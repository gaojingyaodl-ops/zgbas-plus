package com.spt.bas.server.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.CreditFlowEnum;
import com.spt.bas.client.entity.*;
import com.spt.bas.purchase.wx.client.entity.UserDetail;
import com.spt.bas.purchase.wx.client.remote.IWxUserDetailClient;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.ApplyContractAdjustDao;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.service.*;
import com.spt.bas.server.util.RuleUtil;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import net.sf.cglib.beans.BeanMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.o;

/**
 * 合同调整
 */
@Component("applyContractAdjustService")
@Transactional(readOnly = true)
public class ApplyContractAdjustServiceImpl extends BaseService<ApplyContractAdjust>
		implements IApplyContractAdjustService, IPmService, IPmApproveListener {
	@Autowired
	private ApplyContractAdjustDao applyContractAdjustDao;
	@Autowired
	private CtrContractDao ctrContractDao;
	@Autowired
	private IBsCompanyService bsCompanyService;
	@Autowired
	private IWxUserDetailClient wxUserDetailClient;
	@Autowired
	private ICtrContractService ctrContractService;
	@Autowired
	private IBudgetSettlementService budgetSettlementService;
	@Autowired
	private ICtrContractOphisService contractOphisService;
	@Autowired
	private IApplyDeliveryOutService applyDeliveryOutService;
	@Autowired
	private IApplyDeliveryInService applyDeliveryInService;
	@Autowired
	private IApplyInvoiceService applyInvoiceService;
	@Autowired
	private IApplyInvoiceReceivedService applyInvoiceReceivedService;
	@Autowired
	private IApplyReceiveService applyReceiveService;
	@Autowired
	private IApplyPayService applyPayService;
	@Autowired
	private IBsCompanyAccountService bsCompanyAccountService;
	@Autowired
	private IApplyConfrimReceiptService applyConfrimReceiptService;
	@Autowired
	private IApplyMatchService applyMatchService;
	@Autowired
	private IApplyMatchDetailService applyMatchDetailService;
	@Autowired
	private IPmApproveService pmApproveService;
	@Autowired
	private IAuthOpenFacade authOpenFacade;
	@Autowired
	private IApplyDcsxService applyDcsxService;
	@Autowired
	private ICtrProductService ctrProductService;
	@Autowired
	private IBsCompanyCreditFlowService companyCreditFlowService;

	@Override
	public BaseDao<ApplyContractAdjust> getBaseDao() {
		return applyContractAdjustDao;
	}
	@Override
	public Class<ApplyContractAdjust> getEntityClazz() {
		return ApplyContractAdjust.class;
	}

	@Override
	@ServerTransactional
	public void doStepIn(PmApprove approve) throws ApplicationException {
		ApplyContractAdjust contractAdjust = applyContractAdjustDao.findOne(approve.getBizId());
		CtrContract contract1 = ctrContractDao.findByContractNo(contractAdjust.getSellContractNo());

		if (!StrUtil.isEmpty(contractAdjust.getSettlementType())) {
			// 调整后销售合同金额 200
			BigDecimal sellTotalAmountB = contractAdjust.getSellTotalAmountB();

			// 调整前销售合同金额 100
			BigDecimal sellTotalAmount = contractAdjust.getSellTotalAmount();

			// 调整前后差值 +100
			BigDecimal subtract = sellTotalAmountB.subtract(sellTotalAmount);

			BsCompany bsCompany = bsCompanyService.getEntity(contractAdjust.getSellCompanyIdB());
			// 总额度
			BigDecimal totalCredit = bsCompany.getTotalCreditAmount() == null ? BigDecimal.ZERO
					: bsCompany.getTotalCreditAmount();

			if (totalCredit.compareTo(BigDecimal.ZERO) <= 0) {
				logger.error("totalCredit is zero！companyId：{} ", bsCompany.getId());
			}

			// 当前在审批中额度
			BigDecimal approveCredit = bsCompany.getApproveCreditAmount() == null ? BigDecimal.ZERO
					: bsCompany.getApproveCreditAmount();

			// 审批中额度加上差值
			approveCredit = approveCredit.add(subtract);

			// 已使用额度
			BigDecimal usedCredit = bsCompany.getUsedCreditAmount();

			// 当前使用额度
			BigDecimal realApproveCredit = approveCredit.add(usedCredit);
			// 如果审批中额度加本次申请额度加上已使用额度大于总额度 无法发起
			if (realApproveCredit.compareTo(totalCredit) > 0) {
				throw new ApplicationException("申请中的总金额大于总额度,无法发起");
			}

			// 检验字段
			List<CtrContract> contracts = ctrContractService.findApproveByOrder(contract1.getApproveId());
			CtrContract buyContract = null;
			CtrContract sellContract = null;
			for (CtrContract contract : contracts) {
				// 销售
				if (BasConstants.CONTRACT_TYPE_S.equals(contract.getContractType())) {
					sellContract = contract;
				} else if (BasConstants.CONTRACT_TYPE_B.equals(contract.getContractType())) {
					buyContract = contract;
				}
			}


			// 是否已出库
			boolean isDeliveryOut = isDeliveryOut(sellContract.getId());

			logger.info("合同:{},是否出库:{}", sellContract.getContractNo(), isDeliveryOut);

			// 重签销售合同
			boolean needSellSeal = false;
			// 重签采购合同
			boolean needBuySeal = false;

			// 采购===================================================================================
			if (!StrUtil.equals(buyContract.getCompanyName(), contractAdjust.getBuyCompanyNameB())) {
//				buyContract.setCompanyTitle(contractAdjust.getBuyCompanyNameB());
//				buyContract.setCompanyName(contractAdjust.getBuyCompanyNameB());
//				buyContract.setCompanyId(contractAdjust.getBuyCompanyIdB());
				needBuySeal = true;
			}
			if (buyContract.getTotalAmount().compareTo(contractAdjust.getBuyTotalAmountB()) != 0) {
//				buyContract.setTotalAmount(contractAdjust.getBuyTotalAmountB());
				needBuySeal = true;
			}
			if (!StrUtil.isEmpty(contractAdjust.getBuyContractFileId())) {
//				buyContract.setBuyContentFileId(contractAdjust.getBuyContractFileId());
				needBuySeal = true;
			}

			// 销售====================================================================================
			if (!StrUtil.equals(sellContract.getCompanyName(), contractAdjust.getSellCompanyNameB())) {
//				sellContract.setCompanyTitle(contractAdjust.getSellCompanyNameB());
//				sellContract.setCompanyName(contractAdjust.getSellCompanyNameB());
//				sellContract.setCompanyId(contractAdjust.getSellCompanyIdB());
			}
			if (sellContract.getTotalAmount().compareTo(contractAdjust.getSellTotalAmountB()) != 0) {
//				sellContract.setTotalAmount(contractAdjust.getSellTotalAmountB());
				needSellSeal = true;
			}
			contractAdjust.setServiceAmountB(contractAdjust.getServiceAmountB() == null ? BigDecimal.ZERO : contractAdjust.getServiceAmountB());
			if (sellContract.getServiceAmount().compareTo(contractAdjust.getServiceAmountB()) != 0) {
//				sellContract.setServiceAmount(contractAdjust.getServiceAmountB());
				needSellSeal = true;
			}
			if (sellContract.getPremium().compareTo(contractAdjust.getPremiumB()) != 0) {
//				sellContract.setPremium(contractAdjust.getPremiumB());
				needSellSeal = true;
			}
			if (!StrUtil.isEmpty(contractAdjust.getSellContractFileId())) {
//				sellContract.setSellContentFileId(contractAdjust.getSellContractFileId());
			}
			if (!StrUtil.isEmpty(sellContract.getSettlementType())) {
//				sellContract.setPayFullTime(contractAdjust.getPayFullTimeB());
//				sellContract.setCreditCycle(contractAdjust.getCreditDays().longValue());
				needSellSeal = true;
			}
			// 我方抬头修改 需校验双签合同
			if (!StrUtil.isEmpty(contractAdjust.getSellContractFileId()) && !StrUtil.isEmpty(contractAdjust.getBuyContractFileId()) && contract1.getBusinessTypeDcsx()!=null) {
//				buyContract.setOurCompanyName(contractAdjust.getOurCompanyNameB());
			}else{
//				sellContract.setOurCompanyName(contractAdjust.getOurCompanyNameB());
//				buyContract.setOurCompanyName(contractAdjust.getOurCompanyNameB());
			}
			if (needSellSeal && needBuySeal) {
				if (StrUtil.isEmpty(contractAdjust.getSellContractFileId()) || StrUtil.isEmpty(contractAdjust.getBuyContractFileId())) {
					throw new ApplicationException("需重新上传双签合同");
				}
			} else if (needSellSeal && !needBuySeal) {
				if (StrUtil.isEmpty(contractAdjust.getSellContractFileId())) {
					throw new ApplicationException("需重新上传销售合同");
				}
			}else if (!needSellSeal && needBuySeal) {
				if (StrUtil.isEmpty(contractAdjust.getBuyContractFileId())) {
					throw new ApplicationException("需重新上传采购合同");
				}
			}

			bsCompany.setApproveCreditAmount(approveCredit);

			bsCompanyService.save(bsCompany);
			// 同步更新userdetail表
			UserDetail userDetail = wxUserDetailClient.findByCompanyIdAndIsBindTrue(bsCompany.getId());
			if (userDetail != null) {
				userDetail.setTotalCreditAmount(totalCredit);
				userDetail.setUsedCreditAmount(bsCompany.getUsedCreditAmount().add(realApproveCredit));
				userDetail.setAvailableCreditAmount(totalCredit.subtract(userDetail.getUsedCreditAmount()));
				wxUserDetailClient.save(userDetail);
			}
		}
	}

	/**
	 * 是否完成出库
	 * @param contractId
	 * @return true:已出库  false: 未出库
	 */
	private boolean isDeliveryOut(Long contractId) throws ApplicationException {
		List<ApplyDeliveryOut> byContractIdNoStatusB = applyDeliveryOutService.findByContractIdNoStatusB(contractId);
		logger.info("ApplyDeliveryOutList:{}", byContractIdNoStatusB.size());
		return !byContractIdNoStatusB.isEmpty();
	}

	/**
	 * 判断是否开始业务流程
	 * @param contractId
	 * @return
	 */
	private boolean checkIsStart(Long contractId) {
		CtrContract ctrContract = ctrContractService.getEntity(contractId);
		List<CtrContract> contracts = ctrContractService.findApproveByOrder(ctrContract.getApproveId());
		boolean isStart = false;
		for (CtrContract contract1 : contracts) {
			if (contract1.getDealedAmount().compareTo(BigDecimal.ZERO) > 0) {
				isStart = true;
				break;
			}
			if (contract1.getWarehouseNumber().compareTo(BigDecimal.ZERO) > 0) {
				isStart = true;
				break;
			}
			if (contract1.getBilledAmount().compareTo(BigDecimal.ZERO) > 0) {
				isStart = true;
				break;
			}
		}
		return isStart;
	}

	@Override
	@ServerTransactional
	public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
		ApplyContractAdjust contractAdjust = applyContractAdjustDao.findOne(approve.getBizId());
		CtrContract contract = ctrContractDao.findByContractNo(contractAdjust.getSellContractNo());
		// 驳回后调整授信额度
		if (!StrUtil.isEmpty(contractAdjust.getSettlementType())) {
			// 调整后销售合同金额 200
			BigDecimal sellTotalAmountB = contractAdjust.getSellTotalAmountB();

			// 调整前销售合同金额 100
			BigDecimal sellTotalAmount = contractAdjust.getSellTotalAmount();

			// 调整前后差值 +100
			BigDecimal subtract = sellTotalAmountB.subtract(sellTotalAmount);

			BsCompany bsCompany = bsCompanyService.getEntity(contractAdjust.getSellCompanyIdB());

			// 总额度
			BigDecimal totalCredit = bsCompany.getTotalCreditAmount() == null ? BigDecimal.ZERO
					: bsCompany.getTotalCreditAmount();

			// 当前在审批中额度
			BigDecimal approveCredit = bsCompany.getApproveCreditAmount() == null ? BigDecimal.ZERO
					: bsCompany.getApproveCreditAmount();

			// 审批中额度加上差值
			approveCredit = approveCredit.subtract(subtract);

			// 已使用额度
			BigDecimal usedCredit = bsCompany.getUsedCreditAmount();

			// 当前使用额度
			BigDecimal realApproveCredit = approveCredit.add(usedCredit);

			bsCompany.setApproveCreditAmount(approveCredit);

			bsCompanyService.save(bsCompany);
			// 同步更新userdetail表
			UserDetail userDetail = wxUserDetailClient.findByCompanyIdAndIsBindTrue(bsCompany.getId());
			if (userDetail != null) {
				userDetail.setTotalCreditAmount(totalCredit);
				userDetail.setUsedCreditAmount(bsCompany.getUsedCreditAmount().add(realApproveCredit));
				userDetail.setAvailableCreditAmount(totalCredit.subtract(userDetail.getUsedCreditAmount()));
				wxUserDetailClient.save(userDetail);
			}
		}
	}

	@Override
	@ServerTransactional
	public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
		if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {

			ApplyContractAdjust contractAdjust = applyContractAdjustDao.findOne(approve.getBizId());
			// 已开始业务流程的合同无法进行调整
			CtrContract byContractNo = ctrContractService.findByContractNo(contractAdjust.getSellContractNo());
//			if (checkIsStart(byContractNo.getId())) {
//				throw new ApplicationException("该预算已经开始业务流程,无法进行合同调整审批,请驳回");
//			}
			//更新中游合同数据
			ApplyCtrDCSX byDCSXApprove = applyDcsxService.findByDCSXApproveId(byContractNo.getApproveId());
			if(byDCSXApprove!=null){
				byDCSXApprove.setTotalNumber(contractAdjust.getTotalNumberB());
				BigDecimal totalNumber = contractAdjust.getTotalNumberB();
				BigDecimal dealPrice = byDCSXApprove.getDealPrice();
				BigDecimal sum = totalNumber.multiply(dealPrice);
				byDCSXApprove.setTotalAmount(sum);
			}
			List<CtrProduct> ctrProducts = ctrProductService.findByContractId(byContractNo.getId());
			for (CtrProduct ctrProduct : ctrProducts) {
				ctrProduct.setDealNumber(contractAdjust.getTotalNumberB());
				ctrProductService.save(ctrProduct);
			}
			List<CtrProduct> ctrProducts1 = ctrProductService.findByContractId(byContractNo.getId()-1);
			   ctrProducts1.get(0).setDealNumber(contractAdjust.getTotalNumberB());
				ctrProductService.save(ctrProducts1.get(0));
			//更新授信额度
			CtrContract contract = ctrContractDao.findByContractNo(contractAdjust.getSellContractNo());

			// 更新合同字段
			updateContractAdjust(byContractNo.getId(),contractAdjust, approve);

			// 添加历史
			contractOphisService.addHis(BasConstants.APPLY_TYPE_C, contract.getId(), approve, new Date());
		}
	}

	/**
	 * 更新合同字段
	 * @param contractId
	 */
	private void updateContractAdjust(Long contractId,ApplyContractAdjust contractAdjust, PmApprove approve) throws ApplicationException {
		CtrContract ctrContract = ctrContractService.getEntity(contractId);
		List<CtrContract> contracts = ctrContractService.findApproveByOrder(ctrContract.getApproveId());
		CtrContract buyContract = null;
		CtrContract sellContract = null;
		for (CtrContract contract : contracts) {
			// 销售
			if (BasConstants.CONTRACT_TYPE_S.equals(contract.getContractType())) {
				sellContract = contract;
			} else if (BasConstants.CONTRACT_TYPE_B.equals(contract.getContractType())) {
				buyContract = contract;
			}
		}

		// 是否已收完全款
		boolean recoveryFullFlg = sellContract.getDealedAmount().compareTo(sellContract.getTotalAmount()) >= 0;

		// 是否已出库
		boolean isDeliveryOut = isDeliveryOut(sellContract.getId());

		logger.info("合同:{},是否出库:{}", sellContract.getContractNo(), isDeliveryOut);

		// 采购===================================================================================
		if (!StrUtil.equals(buyContract.getCompanyName(), contractAdjust.getBuyCompanyNameB())) {
			buyContract.setCompanyTitle(contractAdjust.getBuyCompanyNameB());
			buyContract.setCompanyName(contractAdjust.getBuyCompanyNameB());
			buyContract.setCompanyId(contractAdjust.getBuyCompanyIdB());
		}
		if(buyContract.getTotalAmount().compareTo(contractAdjust.getBuyTotalAmountB()) != 0){
			buyContract.setTotalAmount(contractAdjust.getBuyTotalAmountB());
		}
		if(buyContract.getDealPrice().compareTo(contractAdjust.getBuyUnitPriceB()) != 0){
			buyContract.setDealPrice(contractAdjust.getBuyUnitPriceB());
		}
		if (!StrUtil.isEmpty(contractAdjust.getBuyContractFileId())) {
			buyContract.setBuyContentFileId(contractAdjust.getBuyContractFileId());
		}
		if (!StrUtil.isEmpty(sellContract.getSettlementType())) {
			buyContract.setPayFullTime(contractAdjust.getBuyPayFullTimeB());
			buyContract.setDeliveryDateTo(contractAdjust.getBuyDeliveryDateB());
		}

		// 销售====================================================================================
		if (!StrUtil.equals(sellContract.getCompanyName(), contractAdjust.getSellCompanyNameB())) {
			sellContract.setCompanyTitle(contractAdjust.getSellCompanyNameB());
			sellContract.setCompanyName(contractAdjust.getSellCompanyNameB());
			sellContract.setCompanyId(contractAdjust.getSellCompanyIdB());
		}
		if (sellContract.getTotalAmount().compareTo(contractAdjust.getSellTotalAmountB()) != 0) {
			sellContract.setTotalAmount(contractAdjust.getSellTotalAmountB());
		}
		if(sellContract.getDealPrice().compareTo(contractAdjust.getSellUnitPriceB()) != 0){
			sellContract.setDealPrice(contractAdjust.getSellUnitPriceB());
		}
		contractAdjust.setServiceAmountB(contractAdjust.getServiceAmountB() == null ? BigDecimal.ZERO : contractAdjust.getServiceAmountB());
		if (sellContract.getServiceAmount().compareTo(contractAdjust.getServiceAmountB()) != 0) {
			sellContract.setServiceAmount(contractAdjust.getServiceAmountB());
		}
		if (sellContract.getPremium().compareTo(contractAdjust.getPremiumB()) != 0) {
			sellContract.setPremium(contractAdjust.getPremiumB());
		}
		if (!StrUtil.isEmpty(contractAdjust.getSellContractFileId())) {
			sellContract.setSellContentFileId(contractAdjust.getSellContractFileId());
		}
		if (!StrUtil.isEmpty(sellContract.getSettlementType())) {
			sellContract.setPayFullTime(contractAdjust.getPayFullTimeB());
			sellContract.setDeliveryDateTo(contractAdjust.getSellDeliveryDateB());
			sellContract.setCreditCycle(contractAdjust.getCreditDays().longValue());
		}
		// 我方抬头修改
		if (!StrUtil.isEmpty(contractAdjust.getOurCompanyNameB())) {
			sellContract.setOurCompanyName(contractAdjust.getOurCompanyNameB());
			buyContract.setOurCompanyName(contractAdjust.getOurCompanyNameB());
		}
		if (contractAdjust.getTotalNumberB() != null) {
			sellContract.setTotalNumber(contractAdjust.getTotalNumberB());
			buyContract.setTotalNumber(contractAdjust.getTotalNumberB());
		}

		// 合同数量修改
		if (sellContract.getTotalNumber().compareTo(contractAdjust.getTotalNumberB()) != 0) {
			ApplyMatchDetail byContractNo = applyMatchDetailService.findByContractNo(contractAdjust.getSellContractNo());
			ApplyMatch entity = applyMatchService.getEntity(byContractNo.getApplyMatchId());
			entity.setDealNumber(contractAdjust.getTotalNumberB());
			applyMatchService.save(entity);
		}
		// 合同数量修改
			
		ctrContractService.save(buyContract);
		ctrContractService.save(sellContract);
		
		BudgetSettlement bySellContractId = budgetSettlementService.getBySellContractId(sellContract.getId());
		budgetSettlementService.updateSettlement(bySellContractId, buyContract, sellContract);
		
		List<ApplyDeliveryOut> applyDeliveryOutList = applyDeliveryOutService.findByContractId(sellContract.getId()); // 出库申请
		if(applyDeliveryOutList != null && applyDeliveryOutList.size() > 0) {
			for (ApplyDeliveryOut applyDeliveryOut : applyDeliveryOutList) {
				PmApprove pmApprove = pmApproveService.findApproveNoByApproveId(applyDeliveryOut.getApproveId());
				String subject = pmApprove.getSubject();
				pmApprove.setSubject(subject.replaceAll(applyDeliveryOut.getCompanyName(),contractAdjust.getSellCompanyNameB()));
				pmApproveService.save(pmApprove);
				
				applyDeliveryOut.setCompanyName(contractAdjust.getSellCompanyNameB());
				applyDeliveryOut.setCompanyId(contractAdjust.getSellCompanyIdB());
				applyDeliveryOutService.save(applyDeliveryOut);
				
			}
		}
		List<ApplyDeliveryIn> applyDeliveryInList = applyDeliveryInService.findByContractId(buyContract.getId());// 入库申请
		if(applyDeliveryInList != null && applyDeliveryInList.size() > 0) {
			for (ApplyDeliveryIn applyDeliveryIn : applyDeliveryInList) {
				PmApprove pmApprove = pmApproveService.findApproveNoByApproveId(applyDeliveryIn.getApproveId());
				String subject = pmApprove.getSubject();
				pmApprove.setSubject(subject.replaceAll(applyDeliveryIn.getCompanyName(),contractAdjust.getBuyCompanyNameB()));
				pmApproveService.save(pmApprove);
				
				applyDeliveryIn.setCompanyName(contractAdjust.getBuyCompanyNameB());
				applyDeliveryIn.setCompanyId(contractAdjust.getBuyCompanyIdB());
				applyDeliveryInService.save(applyDeliveryIn);
			}
		}
		List<ApplyInvoice> applyInvoiceList = applyInvoiceService.findByContractId(sellContract.getId());// 开票申请
		if(applyInvoiceList != null && applyInvoiceList.size() > 0) {
			for (ApplyInvoice applyInvoice : applyInvoiceList) {
				PmApprove pmApprove = pmApproveService.findApproveNoByApproveId(applyInvoice.getApproveId());
				String subject = pmApprove.getSubject();
				pmApprove.setSubject(subject.replaceAll(applyInvoice.getCompanyName(),contractAdjust.getSellCompanyNameB()));
				pmApproveService.save(pmApprove);
				
				applyInvoice.setTotalAmount(contractAdjust.getSellTotalAmountB());
				applyInvoice.setOurCompanyName(contractAdjust.getOurCompanyNameB());
				applyInvoice.setCompanyName(contractAdjust.getSellCompanyNameB());
				applyInvoice.setCompanyId(contractAdjust.getSellCompanyIdB());
				applyInvoiceService.save(applyInvoice);
			}
		}
		List<ApplyInvoiceReceived> applyInvoiceReceivedList = applyInvoiceReceivedService.findByContractId(buyContract.getId());// 收票申请
		if(applyInvoiceReceivedList != null && applyInvoiceReceivedList.size() > 0) {
			for (ApplyInvoiceReceived applyInvoiceReceived : applyInvoiceReceivedList) {
				PmApprove pmApprove = pmApproveService.findApproveNoByApproveId(applyInvoiceReceived.getApproveId());
				String subject = pmApprove.getSubject();
				pmApprove.setSubject(subject.replaceAll(applyInvoiceReceived.getCompanyName(),contractAdjust.getBuyCompanyNameB()));
				pmApproveService.save(pmApprove);
				
				applyInvoiceReceived.setTotalAmount(contractAdjust.getBuyTotalAmountB());
				applyInvoiceReceived.setOurCompanyName(contractAdjust.getOurCompanyNameB());
				applyInvoiceReceived.setCompanyName(contractAdjust.getBuyCompanyNameB());
				applyInvoiceReceived.setCompanyId(contractAdjust.getBuyCompanyIdB());
				applyInvoiceReceivedService.save(applyInvoiceReceived);
			}
		}
		BigDecimal sellTotalAmountB = contractAdjust.getSellTotalAmountB();
		BigDecimal sellTotalAmount = contractAdjust.getSellTotalAmount();
		BigDecimal sellTotalAmountSubtract = sellTotalAmountB.subtract(sellTotalAmount);
		List<ApplyReceive> applyReceiveList = applyReceiveService.findByContractId(sellContract.getId());// 收款申请
		if(applyReceiveList != null && applyReceiveList.size() > 0) {
			for (ApplyReceive applyReceive : applyReceiveList) {
				PmApprove pmApprove = pmApproveService.findApproveNoByApproveId(applyReceive.getApproveId());
				String subject = pmApprove.getSubject();
				pmApprove.setSubject(subject.replaceAll(applyReceive.getCompanyName(),contractAdjust.getSellCompanyNameB()));
				pmApproveService.save(pmApprove);
				
				applyReceive.setTotalAmount(contractAdjust.getSellTotalAmountB());
				applyReceive.setUnpayedAmount(applyReceive.getUnpayedAmount().add(sellTotalAmountSubtract));
				applyReceive.setOurCompanyName(contractAdjust.getOurCompanyNameB());
				applyReceive.setCompanyName(contractAdjust.getSellCompanyNameB());
				applyReceive.setCompanyId(contractAdjust.getSellCompanyIdB());
				applyReceiveService.save(applyReceive);
			}
		}
		BigDecimal buyTotalAmountB = contractAdjust.getBuyTotalAmountB();
		BigDecimal buyTotalAmount = contractAdjust.getBuyTotalAmount();
		BigDecimal buyTotalAmountSubtract = buyTotalAmountB.subtract(buyTotalAmount);
		List<ApplyPay> applyPayList = applyPayService.findByContractId(buyContract.getId());// 付款申请
		if(applyPayList != null && applyPayList.size() > 0) {
			Long buyCompanyIdB = contractAdjust.getBuyCompanyIdB();
			List<BsCompanyAccount> companyAccounts = bsCompanyAccountService.findByCompanyId(buyCompanyIdB);
			BsCompanyAccount companyAccount = null;
			if(companyAccounts != null && companyAccounts.size() > 0) {
				for (BsCompanyAccount companyAccountDB : companyAccounts) {
					if(companyAccountDB.getDefaultFlg()){
						companyAccount = companyAccountDB;
						break;
					}
				}
				if(companyAccount == null){
					companyAccount = companyAccounts.get(0);
				}
			}
			
			for (ApplyPay applyPay : applyPayList) {
				PmApprove pmApprove = pmApproveService.findApproveNoByApproveId(applyPay.getApproveId());
				String subject = pmApprove.getSubject();
				pmApprove.setSubject(subject.replaceAll(applyPay.getCompanyName(),contractAdjust.getBuyCompanyNameB()));
				pmApproveService.save(pmApprove);
				
				if(companyAccount != null) {
					applyPay.setBankAccount(companyAccount.getBankAccount());
					applyPay.setBankName(companyAccount.getBankName());
				} else {
					applyPay.setBankAccount(null);
					applyPay.setBankName(null);
				}
				applyPay.setTotalAmount(contractAdjust.getBuyTotalAmountB());
				applyPay.setUnpayedAmount(applyPay.getUnpayedAmount().add(buyTotalAmountSubtract));
				applyPay.setOurCompanyName(contractAdjust.getOurCompanyNameB());
				applyPay.setCompanyName(contractAdjust.getBuyCompanyNameB());
				applyPay.setCompanyId(contractAdjust.getBuyCompanyIdB());
				applyPayService.save(applyPay);
			}
		}
		List<ApplyConfirmReceipt> confirmReceiptList = applyConfrimReceiptService.findByContractId(sellContract.getId());// 确认收货单
		if(confirmReceiptList != null && confirmReceiptList.size() > 0) {
			for (ApplyConfirmReceipt applyConfirmReceipt : confirmReceiptList) {
				PmApprove pmApprove = pmApproveService.findApproveNoByApproveId(applyConfirmReceipt.getApproveId());
				String subject = pmApprove.getSubject();
				pmApprove.setSubject(subject.replaceAll(applyConfirmReceipt.getCompanyName(),contractAdjust.getSellCompanyNameB()));
				pmApproveService.save(pmApprove);

				applyConfirmReceipt.setCompanyName(contractAdjust.getSellCompanyNameB());
				applyConfirmReceipt.setCompanyId(contractAdjust.getSellCompanyIdB());
				applyConfrimReceiptService.save(applyConfirmReceipt);
			}
		}

		// 更新客户授信额度
		String settlementType = sellContract.getSettlementType();
		if (StringUtils.isNotBlank(settlementType) && sellTotalAmount.compareTo(sellTotalAmountB) != 0) {
			Long sellCompanyId = contractAdjust.getSellCompanyId();
			Long sellCompanyIdB = contractAdjust.getSellCompanyIdB();

			BsCompany company = bsCompanyService.getEntity(sellCompanyId);
			if (Objects.equals(sellCompanyId, sellCompanyIdB)) {
				companyCreditFlowService.updateUsedCreditAmount(approve.getApproveNo(), company, sellTotalAmountB.subtract(sellTotalAmount), CreditFlowEnum.CJ);
			} else {
				companyCreditFlowService.updateUsedCreditAmount(approve.getApproveNo(), company, sellTotalAmount.negate(), CreditFlowEnum.CJ);

				BsCompany companyAdjust = bsCompanyService.getEntity(sellCompanyIdB);
				companyCreditFlowService.updateUsedCreditAmount(approve.getApproveNo(), companyAdjust, sellTotalAmountB, CreditFlowEnum.CJ);
			}
		}
	}

	@Override
	public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
		// TODO Auto-generated method stub

	}

	@Override
	@ServerTransactional
	public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
		if (pmEntity != null) {
			ApplyContractAdjust entity = (ApplyContractAdjust) pmEntity;
			PmApprove entity1 = pmApproveService.getEntity(entity.getApproveId());
			if(entity1 != null){
				//SysDept deptByUserId = adminOpenFacade.findDeptByUserId(entity1.getCreateUserId());
				SysDeptSdk deptByUserId = authOpenFacade.findDeptByUserId(entity1.getCreateUserId());
				entity.setDeptId(deptByUserId.getDeptId());
			}
			return save(entity);

		}
		return null;
	}

	@Override
	public String getSubject(IPmEntity pmEntity, PmProcess process) {
		if (pmEntity != null) {
			 ApplyContractAdjust applyContractAdjust = (ApplyContractAdjust) pmEntity;
			final BigDecimal totalNumberB = applyContractAdjust.getTotalNumberB();// 总数量
			final String ourCompanyNameB = applyContractAdjust.getOurCompanyNameB();//我方
			final String buyCompanyNameB = applyContractAdjust.getBuyCompanyNameB();//上游公司
			final String buyUnitPriceB = SubjectUtil.formatMoney(applyContractAdjust.getBuyUnitPriceB(), RuleUtil.monetaryUnit);//采购合同单价
			final String buyTotalAmountB = SubjectUtil.formatMoney(applyContractAdjust.getBuyTotalAmountB(), RuleUtil.monetaryUnit);//采购合同总价
			final String buyWarehouseAmountB = SubjectUtil.formatMoney(applyContractAdjust.getBuyWarehouseAmountB(), RuleUtil.monetaryUnit);//采购仓储费
			final String buyTransformAmountB = SubjectUtil.formatMoney(applyContractAdjust.getBuyTransformAmountB(), RuleUtil.monetaryUnit);//采购运输费
			final Date buyPayFullTimeB = applyContractAdjust.getBuyPayFullTimeB();//约定付全款日期
			final Date payFullTimeB = applyContractAdjust.getPayFullTimeB();//约定收全款日期
			final String sellCompanyNameB = applyContractAdjust.getSellCompanyNameB();//下游公司
			final String premiumB = SubjectUtil.formatMoney(applyContractAdjust.getPremiumB(), RuleUtil.monetaryUnit);//加价
			final String sellWarehouseAmountB = SubjectUtil.formatMoney(applyContractAdjust.getSellWarehouseAmountB(), RuleUtil.monetaryUnit);//下游仓储费
			final String sellTransformAmountB = SubjectUtil.formatMoney(applyContractAdjust.getSellTransformAmountB(), RuleUtil.monetaryUnit);//下游运输费
			final String lossAmountB = SubjectUtil.formatMoney(applyContractAdjust.getLossAmountB(), RuleUtil.monetaryUnit);//损耗
			final String sellUnitPriceB = SubjectUtil.formatMoney(applyContractAdjust.getSellUnitPriceB(), RuleUtil.monetaryUnit);//销售合同单价
			final String sellTotalAmountB = SubjectUtil.formatMoney(applyContractAdjust.getSellTotalAmountB(), RuleUtil.monetaryUnit);//销售合同金额
			final String serviceAmountB = SubjectUtil.formatMoney(applyContractAdjust.getServiceAmountB(), RuleUtil.monetaryUnit);//服务合同金额
			 final Date buyDeliveryDateB = applyContractAdjust.getBuyDeliveryDateB();//收货日期
			final Date sellDeliveryDateB = applyContractAdjust.getSellDeliveryDateB(); //收货日期
			final String buyCompanyTitleB = applyContractAdjust.getBuyCompanyTitleB();//上游公司使用抬头
			final String sellCompanyTitleB = applyContractAdjust.getSellCompanyTitleB();//下游公司使用抬头
			String sub=applyContractAdjust.getSellContractNo();
			 if(totalNumberB!=null&&applyContractAdjust.getTotalNumber()!=null&&StringUtils.equals(totalNumberB.toString(),applyContractAdjust.getTotalNumber().toString())==false){
                    sub=sub+"，总数量:"+applyContractAdjust.getTotalNumber()+"-修改后总数量："+totalNumberB;
			 }if(ourCompanyNameB!=null&&applyContractAdjust.getOurCompanyName()!=null&&StringUtils.equals(ourCompanyNameB,applyContractAdjust.getOurCompanyName())==false){
				sub=sub+"，我方:"+applyContractAdjust.getOurCompanyName()+"-修改后我方："+ourCompanyNameB;
			} if(buyCompanyNameB!=null&&applyContractAdjust.getBuyCompanyName()!=null&&StringUtils.equals(buyCompanyNameB,applyContractAdjust.getBuyCompanyName())==false){
				sub=sub+"，上游公司:"+applyContractAdjust.getBuyCompanyName()+"-修改后上游公司："+buyCompanyNameB;
			} if(buyUnitPriceB!=null&&applyContractAdjust.getBuyUnitPrice()!=null&&StringUtils.equals(buyUnitPriceB.toString(),applyContractAdjust.getBuyUnitPrice().toString())==false){
				sub=sub+"，采购合同单价:"+applyContractAdjust.getBuyUnitPrice()+"-修改采购合同单价："+buyUnitPriceB;
			} if(buyTotalAmountB!=null&&applyContractAdjust.getBuyTotalAmount()!=null&&StringUtils.equals(buyTotalAmountB.toString(),applyContractAdjust.getBuyTotalAmount().toString())==false){
				sub=sub+"，采购合同总价:"+applyContractAdjust.getBuyTotalAmount()+"-修改后采购合同总价："+buyTotalAmountB;
			} if(buyWarehouseAmountB!=null&&applyContractAdjust.getBuyWarehouseAmount()!=null&&StringUtils.equals(buyWarehouseAmountB.toString(),applyContractAdjust.getBuyWarehouseAmount().toString())==false){
				sub=sub+"，采购仓储费:"+applyContractAdjust.getBuyWarehouseAmount()+"-修改后采购仓储费："+buyWarehouseAmountB;
			} if(buyTransformAmountB!=null&&applyContractAdjust.getBuyTransformAmount()!=null&&StringUtils.equals(buyTransformAmountB.toString(),applyContractAdjust.getBuyTransformAmount().toString())==false){
				sub=sub+"，采购运输费:"+applyContractAdjust.getBuyTransformAmount()+"-修改后采购运输费："+buyTransformAmountB;
			} if(buyPayFullTimeB!=null&&applyContractAdjust.getBuyPayFullTime()!=null&&StringUtils.equals(buyPayFullTimeB.toString(),applyContractAdjust.getBuyPayFullTime().toString())==false){
				sub=sub+"，约定付全款日期:"+applyContractAdjust.getBuyPayFullTime()+"-修改后约定付全款日期："+buyPayFullTimeB;
			}if(payFullTimeB!=null&&applyContractAdjust.getPayFullTime()!=null&&StringUtils.equals(payFullTimeB.toString(),applyContractAdjust.getPayFullTime().toString())==false){
				sub=sub+"，约定收全款日期:"+applyContractAdjust.getPayFullTime()+"-修改后约定收全款日期："+sellCompanyNameB;
			}if(sellCompanyNameB!=null&&applyContractAdjust.getSellCompanyName()!=null&&StringUtils.equals(sellCompanyNameB,applyContractAdjust.getSellCompanyName())==false){
				sub=sub+"，下游公司:"+applyContractAdjust.getSellCompanyName()+"-修改后下游公司："+sellCompanyNameB;
			} if(premiumB!=null&&applyContractAdjust.getPremium()!=null&&StringUtils.equals(premiumB.toString(),applyContractAdjust.getPremium().toString())==false){
				sub=sub+"，加价:"+applyContractAdjust.getPremium()+"-修改后加价："+premiumB;
			} if(sellWarehouseAmountB!=null&&applyContractAdjust.getSellWarehouseAmount()!=null&&StringUtils.equals(sellWarehouseAmountB.toString(),applyContractAdjust.getSellWarehouseAmount().toString())==false){
				sub=sub+"，下游仓储费:"+applyContractAdjust.getSellWarehouseAmount()+"-修改后下游仓储费："+sellWarehouseAmountB;
			} if(sellTransformAmountB!=null&&applyContractAdjust.getSellTransformAmount()!=null&&StringUtils.equals(sellTransformAmountB.toString(),applyContractAdjust.getSellTransformAmount().toString())==false){
				sub=sub+"，下游运输费:"+applyContractAdjust.getSellTransformAmount()+"-修改后下游运输费："+sellTransformAmountB;
			} if(lossAmountB!=null&&applyContractAdjust.getLossAmount()!=null&&StringUtils.equals(lossAmountB.toString(),applyContractAdjust.getLossAmount().toString())==false){
				sub=sub+"，损耗:"+applyContractAdjust.getLossAmount()+"-修改后损耗："+lossAmountB;
			} if(sellUnitPriceB!=null&&applyContractAdjust.getSellUnitPrice()!=null&&StringUtils.equals(sellUnitPriceB.toString(),applyContractAdjust.getSellUnitPrice().toString())==false){
				sub=sub+"，销售合同单价:"+applyContractAdjust.getSellUnitPrice()+"-修改后销售合同单价："+sellUnitPriceB;
			} if(sellTotalAmountB!=null&&applyContractAdjust.getSellTotalAmount()!=null&&StringUtils.equals(sellTotalAmountB.toString(),applyContractAdjust.getSellTotalAmount().toString())==false){
				sub=sub+"，销售合同金额:"+applyContractAdjust.getSellTotalAmount()+"-修改后销售合同金额："+sellTotalAmountB;
			} if(serviceAmountB !=null&&applyContractAdjust.getServiceAmount()!=null&&StringUtils.equals(serviceAmountB.toString(),applyContractAdjust.getServiceAmount().toString())==false){
				sub=sub+"，服务合同金额:"+applyContractAdjust.getServiceAmount()+"-修改后服务合同金额："+serviceAmountB ;
			} if(buyDeliveryDateB !=null&&applyContractAdjust.getBuyDeliveryDate()!=null&&StringUtils.equals(buyDeliveryDateB.toString(),applyContractAdjust.getBuyDeliveryDate().toString())==false){
				sub=sub+"，收货日期:"+applyContractAdjust.getBuyDeliveryDate()+"-修改后收货日期："+buyDeliveryDateB ;
			} if(sellDeliveryDateB !=null&&applyContractAdjust.getSellDeliveryDate()!=null&&StringUtils.equals(sellDeliveryDateB.toString(),applyContractAdjust.getSellDeliveryDate().toString())==false){
				sub=sub+"，收货日期:"+applyContractAdjust.getSellDeliveryDate()+"-修改后收货日期："+sellDeliveryDateB ;
			} if(buyCompanyTitleB !=null&&applyContractAdjust.getBuyCompanyTitle()!=null&&StringUtils.equals(buyCompanyTitleB,applyContractAdjust.getBuyCompanyTitle())==false){
				sub=sub+"，上游公司抬头:"+applyContractAdjust.getBuyCompanyTitle()+"-修改后上游公司抬头："+buyCompanyTitleB ;
			} if(sellCompanyTitleB !=null&&applyContractAdjust.getSellCompanyTitle()!=null&&StringUtils.equals(sellCompanyTitleB,applyContractAdjust.getSellCompanyTitle())==false){
				sub=sub+"，下游公司使用抬头:"+applyContractAdjust.getSellCompanyTitle()+"-修改后下游公司使用抬头："+sellCompanyTitleB ;
			}
			return  sub;
		}
		return null;
	}

	@Override
	@ServerTransactional
	public void updateSellFileId(Long id, String fileId) {
		applyContractAdjustDao.updateSellFileId(id, fileId);
	}

	@Override
	@ServerTransactional
	public void updateBuyFileId(Long id, String fileId) {
		applyContractAdjustDao.updateBuyFileId(id, fileId);
	}

	@Override
	@ServerTransactional
	public void updateFileId(Long id, String fileId) {
		applyContractAdjustDao.updateFileId(id, fileId);
	}
}
