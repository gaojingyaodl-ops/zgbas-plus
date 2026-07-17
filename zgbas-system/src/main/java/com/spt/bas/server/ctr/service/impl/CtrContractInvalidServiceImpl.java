package com.spt.bas.server.ctr.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.IPmProcessClient;
import com.spt.bas.client.util.ContractCfsUtil;
import com.spt.bas.client.vo.CtrConctractInvalidVo;
import com.spt.bas.client.vo.CtrContractApplyVo;
import com.spt.bas.client.vo.CtrContractOphisRequest;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.ctr.service.ICtrContractInvalidService;
import com.spt.bas.server.dao.ApplyDcsxDao;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.dao.CtrOutInLedgerDao;
import com.spt.bas.server.service.*;
import com.spt.bas.server.stock.service.StockDetailFacade;
import com.spt.pm.dao.PmApproveDao;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.exception.InvalidParamException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class CtrContractInvalidServiceImpl implements ICtrContractInvalidService{
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private ICtrContractService contractService;
	@Autowired
	private CtrContractDao ctrContractDao;
	@Autowired
	private IPmProcessClient processClient;
	@Autowired
	private ICtrProductService productService;
	@Autowired
	private StockDetailFacade stockDetailFacade;
	@Autowired
	private ICtrContractOphisService contractHisService;
	@Autowired
	private ICtrContractApplyService contractApplyService;
	@Autowired
	private ICtrContractRelaService contractRelaService;
	@Autowired
	private IPushContractService pushContractService;
	@Autowired
	private IApplyBuyService applyBuyService;
	@Autowired
	private IApplySellService applySellService;
	@Autowired
	private IApplyDeliveryOutService applyDeliveryOutService;
	@Autowired
	private IApplyReceiveService applyReceiveService;
	@Autowired
	private IApplyMatchDetailService applyMatchDetailService;
	@Autowired
	private IApplyMatchService applyMatchService;
	@Autowired
	private IApplyImportDetailService applyImportDetailService;
	@Autowired
	private IApplyImportService applyImportService;
	@Autowired
	private IApplyInternalBuyService internalBuyService;
	@Autowired
	private IPmApproveService approveService;
	@Autowired
	private IApplyImportBuyService applyImportBuyService;
	@Autowired
	private PmApproveDao pmApproveDao;
	@Autowired
	private ApplyDcsxDao applyDcsxDao;
	@Autowired
	private CtrOutInLedgerDao ctrOutInLedgerDao;
	@Autowired
	private ICtrLogisticsService  ctrLogisticsService;
	@Autowired
	private IInsuranceAmountFlowService insuranceAmountFlowService;
	@Autowired
	private IBsCompanyDcsxService iBsCompanyDcsxService;
	
	@Override
	@ServerTransactional
	public void invalidTheContract(CtrConctractInvalidVo vo) throws ApplicationException {
		if (vo.getId() == null || vo.getId() == 0) {
			throw new InvalidParamException("contractId");
		}
		CtrContract ctr = contractService.getEntity(vo.getId());
		List<CtrContract> contractList = contractService.findApproveByOrder(ctr.getApproveId());
		if (StringUtils.equals(BasConstants.CONTRACTSTATUS_C, ctr.getContractStatus())) {
			logger.info("该合同已作废,contractNo:{}",ctr.getContractNo());
			return;
		}
		
		// 不能作废的情况已从上面方法抛出，下面只考虑可以作废的情况，修改审批及申请的状态
		// 采购合同
		Long invalidApproveId = null;
		for (CtrContract contract : contractList) {
			invalidApproveId = invalidContract(contract, vo);
			//作废物流单据
			ctrLogisticsService.initLogistics(contract.getContractNo());
			//删除出库单
			List<CtrOutInLedger> ctrOutInLedgerList = ctrOutInLedgerDao.findByContractNo(contract.getContractNo());
			if (CollectionUtils.isNotEmpty(ctrOutInLedgerList)) {
				for (CtrOutInLedger ctrOutInLedger : ctrOutInLedgerList) {
					ctrOutInLedgerDao.delete(ctrOutInLedger.getId());
				}
			}
		}
		doWithdrawApply(invalidApproveId, vo.getUserId(), vo.getUserName());
	}
	
	/**合同作废*/
	private Long invalidContract(CtrContract contract,CtrConctractInvalidVo vo) throws ApplicationException {
		//合同对应的申请单id
		Long invalidApproveId = null;
		String businessType = contract.getBusinessType();
		String contractType = contract.getContractType();
		// validateInvalid(contract);
		Long contractId = contract.getId();
		List<CtrProduct> productList = productService.findByContractId(contractId);
		for (CtrProduct product : productList) {
			if (BasConstants.CONTRACT_TYPE_S.equals(contractType)) {
				//销售
				stockDetailFacade.cancelSellProduct(product);
				if (contract.getSource().equals(BasConstants.APPLY_TYPE_L)) {
					stockDetailFacade.cancelPresell(product);
				}
			}else {
				//采购
				if (contract.getSource().equals(BasConstants.APPLY_TYPE_MB)
						|| contract.getSource().equals(BasConstants.APPLY_TYPE_RB)) {
					stockDetailFacade.cancelDeliveryInAndBuyProduct(product);
				}else if (contract.getSource().equals(BasConstants.APPLY_TYPE_A)) {
					//预售采购
					stockDetailFacade.cancelPreBuyContract(product, vo);
				}else {
					stockDetailFacade.cancelBuyProduct(product);
				}
			}
		}

		if (contract.getSource().equals(BasConstants.APPLY_TYPE_B)
				|| contract.getSource().equals(BasConstants.APPLY_TYPE_S)) {
			Long cntRela = contractRelaService.countRela(contractId, contract.getContractType());
			if (cntRela > 0) {
				logger.warn("contractId:{},contractType:{}", contractId, contract.getContractType());
				throw new InvalidParamException("该合同已采购/销售，不能作废");
			}
		}
		if (contract.getContractType().equals(BasConstants.CONTRACTTYPE_BUY)) {
			// 修改关于该采购合同的出库、付款申请状态
			if (contract.getSource().equals(BasConstants.APPLY_TYPE_B)) {
				// 修改采购申请状态
				if (StringUtils.equals(businessType, BasConstants.BUSINESS_TYPE_ZY_JK)) {
					applyImportBuyService.updateApplyStatusC(contractId);
					ApplyImportBuy importBuy = applyImportBuyService.findByContractId(contractId);
					// 修改采购审批状态
					invalidApproveId = importBuy.getApproveId();
				}else {
					applyBuyService.updateApplyStatus(contractId);
					ApplyBuy buy = applyBuyService.findByContractId(contractId);
					// 修改采购审批状态
					if (buy != null) {
						invalidApproveId = buy.getApproveId();
					}
				}
			} else if (contract.getSource().equals(BasConstants.APPLY_TYPE_MB)
					|| contract.getSource().equals(BasConstants.APPLY_TYPE_M)) {
				applyMatchDetailService.updateApplyStatus(contractId);
				ApplyMatchDetail matchDetail = applyMatchDetailService.findByContractId(contractId);
				ApplyMatch match = applyMatchService.getEntity(matchDetail.getApplyMatchId());
				invalidApproveId = match.getApproveId();
			} else if (contract.getSource().equals(BasConstants.APPLY_TYPE_RB)
					|| contract.getSource().equals(BasConstants.APPLY_TYPE_R)) {
				applyImportDetailService.updateApplyStatus(contractId);
				ApplyImportDetail importDetail = applyImportDetailService.findByContractId(contractId);
				ApplyImport importd = applyImportService.getEntity(importDetail.getApplyImportId());
				invalidApproveId = importd.getApproveId();
			} else if (contract.getSource().equals(BasConstants.APPLY_TYPE_A)) {
				//预售采购
				// 修改预售采购申请单状态
				applyBuyService.updateApplyStatus(contractId);
				// 修改审批状态
				ApplyBuy buy = applyBuyService.findByContractId(contractId);
				invalidApproveId = buy.getApproveId();
			} else if (contract.getSource().equals(BasConstants.APPLY_TYPE_F)) {
				internalBuyService.updateApplyStatus(contractId);
				ApplyInternalBuy internalBuy = internalBuyService.findByContractId(contractId);
				invalidApproveId = internalBuy.getApproveId();
			}

		} else {
			updateAllApplyStatusOfSellContract(vo);
			if (contract.getSource().equals(BasConstants.APPLY_TYPE_S)) {
				applySellService.updateApplyStatus(contractId);
				ApplySell sell = applySellService.findByContractId(contractId);
				if (sell != null) {
					invalidApproveId = sell.getApproveId();
				}
			} else if (contract.getSource().equals(BasConstants.APPLY_TYPE_MS)
					|| contract.getSource().equals(BasConstants.APPLY_TYPE_M)) {
				applyMatchDetailService.updateApplyStatus(contractId);
				ApplyMatchDetail matchDetail = applyMatchDetailService.findByContractId(contractId);
				ApplyMatch match = applyMatchService.getEntity(matchDetail.getApplyMatchId());
				invalidApproveId = match.getApproveId();
			} else if (contract.getSource().equals(BasConstants.APPLY_TYPE_RS)
					|| contract.getSource().equals(BasConstants.APPLY_TYPE_R)) {
				applyImportDetailService.updateApplyStatus(contractId);
				ApplyImportDetail importDetail = applyImportDetailService.findByContractId(contractId);
				ApplyImport importd = applyImportService.getEntity(importDetail.getApplyImportId());
				invalidApproveId = importd.getApproveId();
			} else if (contract.getSource().equals(BasConstants.APPLY_TYPE_L)) {
				applySellService.updateApplyStatus(contractId);
				ApplySell sell = applySellService.findByContractId(contractId);
				if (sell != null) {
					invalidApproveId = sell.getApproveId();
				}
			}
			//去除合同推送任务
			pushContractService.removeContractTasks(contract.getContractNo());
		}
		// 撤回盖章申请
		this.invalidSealUsage(contract, vo);
		if (contractId != null) {
			// 作废合同相关审批单
			List<String> statusList = new ArrayList<>();
			statusList.add(BasConstants.APPROVE_STATUS_N);
			statusList.add(BasConstants.APPROVE_STATUS_A);
			List<PmApprove> approveList = pmApproveDao.findApproveByContractIdAndStatusIn(contractId, statusList);
			approveList.forEach(approve -> doWithdrawApply(approve.getId(), vo.getUserId(), vo.getUserName()));

			// 作废中游合同
			List<ApplyCtrDCSX> dcsxList = applyDcsxDao.findByApproveId(contract.getApproveId());
			if (CollectionUtils.isNotEmpty(dcsxList)) {
				dcsxList.forEach(dcsx -> {
					List<PmApprove> approveDcsxList = pmApproveDao.findApproveByContractIdAndStatusIn(dcsx.getId(), statusList);
					approveDcsxList.forEach(approveDcsx -> doWithdrawApply(approveDcsx.getId(), vo.getUserId(), vo.getUserName()));

					dcsx.setDealedAmount(BigDecimal.ZERO);
					dcsx.setBilledAmount(BigDecimal.ZERO);
					dcsx.setWarehouseNumber(BigDecimal.ZERO);
					dcsx.setStatus(BasConstants.CONTRACTSTATUS_C);
					applyDcsxDao.save(dcsx);
				});
			}
		}
		ctrContractDao.updateContractStatusIsC(contractId);
		// 合同作废
		CtrContractApplyVo applyVo = new CtrContractApplyVo();
		applyVo.setContractId(vo.getId());
		applyVo.setApplyType(BasConstants.APPLY_TYPE_K);
		contractApplyService.updateCtrContractApply(applyVo);

		CtrContractOphisRequest request = new CtrContractOphisRequest();
		request.setApplyType(contract.getSource());
		request.setCtrContractId(contractId);
		request.setCancel(true);
		request.setRemark(contract.getProductsName());
		request.setCreateUserId(vo.getUserId());
		request.setCreateUserName(vo.getUserName());
		request.setApproveId(vo.getApproveId());
		contractHisService.addHis(request);
		contractRelaService.invalidContract(contract);

		// 合同作废时，相应的关联合同id要去掉
		removeLinkedContractId(contract);
		// 销售合同作废，判断是否已经参保，如果已参保需要返还保费
		rebackInsurance(contract);
		return invalidApproveId;
	}

	private void invalidSealUsage(CtrContract contract, CtrConctractInvalidVo vo) throws ApplicationException {
		PmProcessSearchVo searchVo = new PmProcessSearchVo(BasConstants.PROCESS_APPLY_SEAL_USAGE_BUSINESS, contract.getEnterpriseId());
		PmProcess process = processClient.findByProcessCode(searchVo);
		// 撤回业务盖章申请
		List<CtrContract> contractList = ctrContractDao.findByApproveId(contract.getApproveId());
		if (CollectionUtils.isNotEmpty(contractList)) {
			for (CtrContract ctrContract : contractList) {
				List<PmApprove> pmApproveList = approveService.findApproveByContractIdAndProcessId(ctrContract.getId(), process.getId());
				if(CollectionUtils.isNotEmpty(pmApproveList)) {
					for (PmApprove pmApprove:pmApproveList) {
						doWithdrawApply(pmApprove.getId(), vo.getUserId(), vo.getUserName());
					}
				}
			}
			// 撤回代采赊销盖章申请
			List<ApplyCtrDCSX> dcsxList = applyDcsxDao.findByApproveId(contract.getApproveId());
			if (CollectionUtils.isNotEmpty(dcsxList)){
				PmProcessSearchVo searchDcsxVo = new PmProcessSearchVo(BasConstants.APPLY_SEAL_USAGE_DCSX, contract.getEnterpriseId());
				PmProcess processDcsx = processClient.findByProcessCode(searchDcsxVo);
				for (ApplyCtrDCSX applyCtrDCSX : dcsxList) {
					List<PmApprove> pmApproveList = approveService.findApproveByContractIdAndProcessId(applyCtrDCSX.getId(), processDcsx.getId());
					if(CollectionUtils.isNotEmpty(pmApproveList)) {
						for (PmApprove pmApprove:pmApproveList) {
							doWithdrawApply(pmApprove.getId(), vo.getUserId(), vo.getUserName());
						}
					}
				}
			}
		}

	}

	private void removeLinkedContractId(CtrContract contract) {
		String linkContractId = contract.getLinkContractId();
		if (StringUtils.isNotBlank(linkContractId)) {
			String[] idstr = linkContractId.split(",");
			for (String linkId : idstr) {
				if (StringUtils.isNotBlank(linkId)) {
					CtrContract linkContract = ctrContractDao.findOne(Long.valueOf(linkId));
					// 删除关联合同的关联id
					String newLinkContractId = ContractCfsUtil.removeContractId(linkContract.getLinkContractId(),
							contract.getId());
					linkContract.setLinkContractId(newLinkContractId);
					ctrContractDao.save(linkContract);
				}
			}
			// 当前合同的关联Id清空
			contract.setLinkContractId(null);
			ctrContractDao.save(contract);
		}
	}
	
	
	/**合同作废验证
	 * @throws InvalidParamException */
	private void validateInvalid(CtrContract ctr) throws InvalidParamException {
		if (ctr.findRealDealedAmount().compareTo(BigDecimal.ZERO) > 0) {
			throw new InvalidParamException("已收/付款，不能作废合同");
		}
		if (ctr.getBilledAmount().compareTo(BigDecimal.ZERO) > 0) {
			throw new InvalidParamException("已开/收票，不能作废合同");
		}
		//撮合采购/进口采购，会同步作废入库
		if (!ctr.getSource().equals(BasConstants.APPLY_TYPE_MB)
				&& !ctr.getSource().equals(BasConstants.APPLY_TYPE_RB)) {
			if (ctr.getWarehouseNumber().compareTo(BigDecimal.ZERO) > 0) {
				throw new InvalidParamException("已出/入库，不能作废合同");
			}
		}
	}
	
	private void updateAllApplyStatusOfSellContract(CtrConctractInvalidVo vo) {
		Long contractId = vo.getId();
		List<ApplyDeliveryOut> deliveryOutList = applyDeliveryOutService.findByContractId(contractId);
		List<ApplyReceive> receiveList = applyReceiveService.findByContractId(contractId);
		if (deliveryOutList.size() > 0) {
			for (ApplyDeliveryOut deliveryOut : deliveryOutList) {
				doWithdrawApply(deliveryOut.getApproveId(), vo.getUserId(), vo.getUserName());
			}
			applyDeliveryOutService.updateApplyStatus(contractId);
		}
		if (receiveList.size() > 0) {
			for (ApplyReceive applyReceive : receiveList) {
				doWithdrawApply(applyReceive.getApproveId(), vo.getUserId(), vo.getUserName());
			}
			applyReceiveService.updateApplyStatus(contractId);
		}
	}

	private void doWithdrawApply(Long approveId, Long userId, String userName) {
		PmApproveWithdrawVo vo = new PmApproveWithdrawVo();
		vo.setApproveId(approveId);
		vo.setUserId(userId);
		vo.setUserName(userName);
		try {
			approveService.doWithdraw(vo);
		} catch (ApplicationException e) {
			logger.error("审批撤回失败", e);
		}
	}
	

	private void cancelPreBuyAndSellContract(Long presellContractId, CtrConctractInvalidVo vo)
			throws ApplicationException {
		// 查询关于预售合同的的预售采购合同
		List<CtrContract> buyContractList = contractService.findByLinkContractIdLink(presellContractId+"");
		logger.info("作废预售 {} 找到预售采购记录 {}", presellContractId, buyContractList.size());
		if (!buyContractList.isEmpty()) {
			for (CtrContract ctr : buyContractList) {
				if (ctr.findRealDealedAmount().compareTo(BigDecimal.ZERO) > 0) {
					throw new InvalidParamException("预售采购已付款，不能作废合同");
				}
				if (ctr.getBilledAmount().compareTo(BigDecimal.ZERO) > 0) {
					throw new InvalidParamException("预售采购已收票，不能作废合同");
				}
				if (ctr.getWarehouseNumber().compareTo(BigDecimal.ZERO) > 0) {
					throw new InvalidParamException("预售采购已入库，不能作废合同");
				}
				invalidContract(ctr, vo);
			}
		}
	}
	private void rebackInsurance(CtrContract contract){
		try{
			// 只有销售合同有保费,且已经参保
			if(contract.getContractType().equals(BasConstants.CONTRACTTYPE_SELL)&&contract.getInsuranceFlag()){
				String ourCompanyName = contract.getOurCompanyName();
				// 获取资金代采方信息
				BsCompanyDcsx bsCompanyDcsx = iBsCompanyDcsxService.findByCompanyName(ourCompanyName);
				// 期初余额
				BigDecimal insuranceAmount = bsCompanyDcsx.getInsuranceAmount()==null?BigDecimal.ZERO:bsCompanyDcsx.getInsuranceAmount();
				// 合同保费
				BigDecimal contractInsuranceAmount = contract.getInsuranceAmount()==null?BigDecimal.ZERO:contract.getInsuranceAmount();
				// 期末余额（期初余额+合同保费）,期初余额是全部出库的时候减过合同保费的数据，作废要加上合同保费
				BigDecimal ultimateAmount = insuranceAmount.add(contractInsuranceAmount);
				// 增加保费作废流水
				InsuranceAmountFlow insuranceAmountFlow = new InsuranceAmountFlow();
				insuranceAmountFlow.setFundCompanyId(bsCompanyDcsx.getId());
				insuranceAmountFlow.setContractId(contract.getId());
				insuranceAmountFlow.setFlowType(BasConstants.DICT_TYPE_INSURANCE_AMFL_C);
				// 流水金额
				insuranceAmountFlow.setFlowAmount(contractInsuranceAmount);
				insuranceAmountFlow.setInitialAmount(insuranceAmount);
				insuranceAmountFlow.setUltimateAmount(ultimateAmount);
				String subject = contract.getContractNo()+","+contract.getTotalAmount()+"元,";
				if(contract.getCreditCycle()!=null&&contract.getCreditCycle()>0){
					subject+=contract.getCreditCycle()+"天";
				}
				insuranceAmountFlow.setSubject(subject);
				insuranceAmountFlow.setLinkApproveId(contract.getApproveId());
				insuranceAmountFlowService.save(insuranceAmountFlow);
				// 修改资金方保费余额
				bsCompanyDcsx.setInsuranceAmount(ultimateAmount);
				iBsCompanyDcsxService.save(bsCompanyDcsx);
			}
		}catch (Exception e){
			logger.error("合同作废，返还保费失败========>", e);
		}
	}
}
