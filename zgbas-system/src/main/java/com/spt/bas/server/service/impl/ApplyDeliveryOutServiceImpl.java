package com.spt.bas.server.service.impl;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ExecutorBuilder;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.LogisticsEnum;
import com.spt.bas.client.constant.OwnRegionEnum;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.client.vo.*;
import com.spt.bas.client.vo.api.ApiCode;
import com.spt.bas.report.client.remote.IRptCtrContractReceiveDetailClient;
import com.spt.bas.report.client.vo.RptContractDateSearchVo;
import com.spt.bas.report.client.vo.RptContractDateVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.ctr.service.ICtrContractUpdateService;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.filter.IAutoSealPdfSignFilter;
import com.spt.bas.server.logistics.service.ICtrLogisticsDeliveryService;
import com.spt.bas.server.service.*;
import com.spt.bas.server.stock.service.StockDetailFacade;
import com.spt.bas.server.util.RuleUtil;
import com.spt.bas.server.util.SMSUtils;
import com.spt.bas.server.util.SubjectUtil;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.service.IPmProcessService;
import com.spt.pm.vo.*;
import com.spt.sign.client.remote.ICfcaLogisticsClient;
import com.spt.sign.client.vo.AxqLogisticsVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component("applyDeliveryOutService")
@Transactional(readOnly = true)
public class ApplyDeliveryOutServiceImpl extends BaseService<ApplyDeliveryOut> implements IApplyDeliveryOutService,IPmService, IPmApproveListener {
	ExecutorService executor = ExecutorBuilder.create()
			.setCorePoolSize(4)
			.setMaxPoolSize(10)
			.setWorkQueue(new LinkedBlockingQueue<>(100))
			.build();
	private static final ScheduledExecutorService SCHEDULED_POOL = Executors.newScheduledThreadPool(10);
	@Autowired
	private ApplyDeliveryOutDao applyDeliveryOutDao;
	@Autowired
	private CtrProductDao productDao;
	@Autowired
	private IApplyProductDetailService productDetailService;
	@Autowired
	private StockDetailFacade stockDetailFacade;
	@Autowired
	private IApplyDeliveryService applyDeliveryService;
	@Autowired
	private ICtrContractUpdateService ctrContractUpdateService;
	@Autowired
	private ICtrContractService ctrContractService;
	@Autowired
	private ICtrContractApplyService contractApplyService;
	@Autowired
	private IPmApproveService pmApproveService;
	@Autowired
	private ICtrContractOphisService contractOphisService;
	@Autowired
	private CtrContractDao ctrContractDao;
	@Autowired
	private ApplyDcsxDao applyDcsxDao;
	@Autowired
	private CtrContractApplyDao ctrContractApplyDao;
	@Autowired
	private ICtrContractFeeService ctrContractFeeService;
	@Autowired
	private IStockDetailService stockDetailService;
	@Autowired
	private CtrContractRelaServiceImpl contractRelaService;
	@Autowired
	private ICtrProductFeeService ctrProductFeeService;
	@Autowired
	private ApplyDeliveryDao applyDeliveryDao;
	@Autowired
	private ApplyProductDetailDao applyProductDetailDao;
	@Autowired
	private IAuthOpenFacade authOpenFacade;
	@Autowired
	private IPmProcessService pmProcessService;
	@Autowired
	private IBsCompanyClient bsCompanyClient;
	@Resource
	private CtrLogisticsDao ctrLogisticsDao;
	@Autowired
	private CtrOutInLedgerDao ctrOutInLedgerDao;
	@Resource
	private IAutoSealPdfSignFilter autoSealPdfSignFilter;
	@Autowired
	private IRptCtrContractReceiveDetailClient contractReceiveDetailClient;
	@Autowired
	private ICtrLogisticsDeliveryService logisticsDeliveryService;
	@Resource
	private ICfcaLogisticsClient cfcaLogisticsClient;
	
	@Override
	public BaseDao<ApplyDeliveryOut> getBaseDao() {
		return applyDeliveryOutDao;
	}

	@Override
	public Class<ApplyDeliveryOut> getEntityClazz() {
		return ApplyDeliveryOut.class;
	}

	@Override
	@ServerTransactional
	public void updateFileId(Long id, String fileId) {
		applyDeliveryOutDao.updateFileId(id, fileId);
	}

	@Override
	@ServerTransactional
	public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
		if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
			ApplyDeliveryOut entity = applyDeliveryOutDao.findOne(approve.getBizId());
			List<ApplyProductDetail> lstDetail = productDetailService.findApplyDetail(entity.getId(), BasConstants.APPLY_TYPE_O);
			Long contractId = entity.getContractId();
			CtrContract contract = ctrContractDao.findOne(contractId);
			//当前实际出库的数量
			BigDecimal curRealOutNumber = BigDecimal.ZERO;
			BigDecimal dealAmount = BigDecimal.ZERO;
			
			for(ApplyProductDetail apd:lstDetail) {
				if(apd.getCurNumber().compareTo(BigDecimal.ZERO)>0){
					CtrProduct product = productDao.findOne(apd.getCtrProductId());
					product.setWarehouseNumber(product.getWarehouseNumber().add(apd.getCurNumber()));
					curRealOutNumber = curRealOutNumber.add(apd.getCurNumber());
					//审批结束，更新当前审批数量为0
					product.setCurApproveNumber(BigDecimal.ZERO);

					productDao.save(product);

					// 是否背靠背业务
					boolean isBB = false;
					if (BasConstants.BUSINESS_TYPE_ZY_BB.equals(contract.getBusinessType()) || BasConstants.BUSINESS_TYPE_ZY_TP.equals(contract.getBusinessType())) {
						isBB = true;
					}
					// 如果不是背靠背业务 不修改原逻辑
					if (!isBB) {
						StockDetailRequest request = StockDetailRequest.build(product);
						request.setApproveId(approve.getId());
						request.setLinkDetailId(apd.getStockDetailId());
						request.setStockContractId(apd.getStockContractId());
						request.setApplyType(BasConstants.APPLY_TYPE_O);
						request.setCtrContractId(contractId);
						request.setApplyId(entity.getId());
						request.setDealNumber(apd.getCurNumber());
						stockDetailFacade.saveDeliveryOut(request);
						//出库审批完成生成提货单
						applyDeliveryService.saveDelivery(apd, approve, entity);
					}
					apd.setApplyDeliveryOutId(entity.getId());
					BigDecimal curNumber = apd.getCurNumber();
					BigDecimal dealPrice = product.getDealPrice();
					dealAmount = dealAmount.add(curNumber.multiply(dealPrice));
				}
			}
			CtrContractApply contractApply = ctrContractApplyDao.findByCtrContractId(entity.getContractId());

			// 适配历史审批中的审批单添加操作记录：过段时间可以去除
			Date createdDate = approve.getCreatedDate();
			if (createdDate != null) {
				String format = DateUtil.format(createdDate, "yyyy-MM-dd");
				if (BasConstants.NEW_ADD_HIS_START_DATE.compareTo(format) >= 0) {
					// 添加合同操作记录
					contractOphisService.addHis(BasConstants.APPLY_TYPE_O, contractId, approve,entity.getWarehouseOutDate());
				}
			}

			//更新合同仓储费,运输费
			List<ApplyDeliveryOut> contractNoList = applyDeliveryOutDao.findByContractNo(entity.getContractNo());
			contractNoList.add(entity);
			//合同表的运费字段为多次出库的累加
			BigDecimal tAmount = getDefaultValue(entity.getTransportAmount());
			BigDecimal wAmount = getDefaultValue(entity.getWarehouseAmount());
			BigDecimal stevedorage = getDefaultValue(entity.getStevedorage());
			for (ApplyDeliveryOut applyDeliveryOut : contractNoList) {
				if(StringUtils.equals("D",applyDeliveryOut.getStatus())){
					BigDecimal transportAmount = getDefaultValue(applyDeliveryOut.getTransportAmount());
					BigDecimal warehouseAmount = getDefaultValue(applyDeliveryOut.getWarehouseAmount());
					BigDecimal stevedorageAmount = getDefaultValue(applyDeliveryOut.getStevedorage());
					tAmount = tAmount.add(transportAmount);
					wAmount = wAmount.add(warehouseAmount);
					stevedorage = stevedorage.add(stevedorageAmount);
				}
			}
			Boolean tpFlg = false;
			// 托盘预算特殊逻辑：合同添加托盘利息
			if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP,contract.getBusinessType())) {
				tpFlg = true;
				BigDecimal applyWarehouseNumber = contractApply.getApplyWarehouseNumber() == null ? BigDecimal.ZERO
						: contractApply.getApplyWarehouseNumber();
				BigDecimal totalNumber = contract.getTotalNumber();
				BigDecimal warehouseNumber = contract.getWarehouseNumber();
				if (warehouseNumber.add(applyWarehouseNumber).compareTo(totalNumber) >= 0) {
					// 全部入库 添加实际收全款日期

					RptContractDateSearchVo search = new RptContractDateSearchVo();
					List<String> sellContractNoList = new ArrayList<>();
					sellContractNoList.add(contract.getContractNo());
					search.setSellContractNoList(sellContractNoList);
					List<RptContractDateVo> sellReceiveDateList = contractReceiveDetailClient.selectSellReceiveDateList(search);
					Map<String, Date> sellReceiveDateMap = sellReceiveDateList.stream()
							.collect(Collectors.toMap(RptContractDateVo::getContractNo, RptContractDateVo::getSellReceiveDate));

					Date sellReceiveDate = sellReceiveDateMap.get(contract.getContractNo());
					// 收款日期最大值是否在当前收款日期之后
					if(sellReceiveDate != null){
						contract.setRealPayFullTime(sellReceiveDate);
					}
				}
			}
			contract.setWarehouseAmount(wAmount);
			contract.setTransportAmount(tAmount);
			contract.setStevedorage(stevedorage);
			ctrContractUpdateService.addWarehouseNumber(contractId, curRealOutNumber,approve.getApproveNo(), entity.getWarehouseOutDate());
		    ctrContractDao.save(contract);

			//更新合同出库时间
			Date realWarehoseDate = contractApply.getRealWarehoseDate();
			Date warehouseOutDate = entity.getWarehouseOutDate();
			if (realWarehoseDate == null || warehouseOutDate.after(realWarehoseDate)) {
				contractApply.setRealWarehoseDate(warehouseOutDate);
				contractApplyService.save(contractApply);
			}
			//保存系统仓储费
			BigDecimal feeAmount = getFellAmount(entity,lstDetail);
			ctrContractFeeService.saveWarehouseFee(contract, feeAmount, false);

			//更新合同出/入库费用
			ctrContractUpdateService.updateContractDeliveryFee(entity.getDeliveryOutFee(), entity.getContractId());
			// 出库发送人保赊销 20220324修改为：上游付款完成，发送赊销申请
//			if (checkCanSend(contract)) {
//				executor.execute(() -> piccServiceClient.applyCredit(contract));
//			}

			//收货确认通知
			//合同盖章签署通知
			sendNotifyMessage(contract);

			// 自营出库关联采购合同
			contractRelaService.saveCtrContractReal3(approve, lstDetail, contract);

			// 出库完成时增加出入库台账报表
			addStandingOut(entity);

			// 更新提货日期
			updateLogisticsNum(entity, curRealOutNumber);

			// 托盘业务补充协议处理
			if (tpFlg) {
				handelTpProtocolBusiness(approve, contract);
				handelDcTpProtocolBusiness(approve,entity,contract,curRealOutNumber,dealAmount);
			}
			
			// 出库审批完成后，自动生成发货单 （记录第一次出库日期，第一次出库生成发货单）
			Date shippingDate = contract.getShippingDate();
			if (Objects.isNull(shippingDate)) {
				generateShippingFile(entity, contract, curRealOutNumber, approve);
			}
			
		}
	}

	/**
	 * 刷新发货单
	 * @param contractNo
	 */
	@Override
	public void refreshShippingFile(String contractNo){
		List<CtrContract> contractList = new ArrayList<>();
		if (StringUtils.isNotEmpty(contractNo)) {
			CtrContract contract = ctrContractDao.findByContractNo(contractNo);
			contractList.add(contract);
		} else {
			contractList = ctrContractDao.findByShippingDateNotNUll();
		}

		if (CollectionUtils.isNotEmpty(contractList)) {
			for (CtrContract contract : contractList) {
				List<ApplyDeliveryOut> applyDeliveryOutList = applyDeliveryOutDao.findByContractNo(contract.getContractNo());
				ApplyDeliveryOut applyDeliveryOut;
				if (CollectionUtils.isNotEmpty(applyDeliveryOutList)) {
					applyDeliveryOutList = applyDeliveryOutList.stream()
							.sorted(Comparator.comparing(ApplyDeliveryOut::getId))
							.collect(Collectors.toList());
					applyDeliveryOut = applyDeliveryOutList.get(0);
				} else {
					continue;
				}
				List<ApplyProductDetail> lstDetail = productDetailService.findApplyDetail(applyDeliveryOut.getId(), BasConstants.APPLY_TYPE_O);
				//当前实际出库的数量
				BigDecimal curRealOutNumber = BigDecimal.ZERO;
				for(ApplyProductDetail apd:lstDetail) {
					if(apd.getCurNumber().compareTo(BigDecimal.ZERO)>0){
						curRealOutNumber = curRealOutNumber.add(apd.getCurNumber());
					}
				}
				Long approveId = applyDeliveryOut.getApproveId();
				if (Objects.isNull(approveId)) {
					continue;
				}
				PmApprove approve = pmApproveService.getEntity(applyDeliveryOut.getApproveId());
				if (Objects.isNull(approve)) {
					continue;
				}
				
				generateShippingFile(applyDeliveryOut, contract, curRealOutNumber, approve);
			}
		}
		
		
		

		
	}
	/**
	 * 生成发货单
	 * @param contract
	 */
	public void generateShippingFile(ApplyDeliveryOut entity, CtrContract contract, BigDecimal curRealOutNumber, PmApprove approve){
		SCHEDULED_POOL.schedule(() -> {
			CtrLogisticsReqVo reqVo = new CtrLogisticsReqVo();
			reqVo.setApplyDeliveryOut(entity);
			reqVo.setContractId(entity.getContractId());
			reqVo.setContractNo(entity.getContractNo());
			reqVo.setLogisticsEnum(LogisticsEnum.SHIPPING_FILE);
			reqVo.setCurrNumber(curRealOutNumber);
			reqVo.setBizUserName(approve.getCreateUserName());

			CtrLogistics ctrLogistics = ctrLogisticsDao.findByContractId(contract.getId());
			reqVo.setLogistics(ctrLogistics);
			LogisticsEnum logisticsEnum = reqVo.getLogisticsEnum();
			reqVo = logisticsEnum.compositeLogistics(reqVo);

			AxqLogisticsVo vo = new AxqLogisticsVo();
			Map<String, String> paramMap = reqVo.getParamMap();
			vo.setParamMap(paramMap);
			vo.setOurCompanyName(reqVo.getSignCompanyName());
			vo.setLogisticsNo(reqVo.getLogisticsNo());
			vo.setTemplateCode(logisticsEnum.getLogisticsTemplate());
			vo.setContractNo(reqVo.getContractNo());


			String cfcaContractNo = "";
			try {
				vo = cfcaLogisticsClient.axqLogistics(vo);
				if (Boolean.TRUE.equals(vo.getSuccessFlg())) {
					cfcaContractNo = vo.getCfcaContractNo();
				}
			} catch (Exception e) {
				logger.error("发货单生成失败 error", e);
			}

			try {
				vo.setCfcaContractNo(cfcaContractNo);
				vo.setOurCompanyName(reqVo.getSignCompanyName());
				vo.setSignType(BasConstants.CFCA_SEAL_TYPE.SEAL_TYPE_LGS);
				vo.setSignLocation("buyerSignLocation");
				vo = cfcaLogisticsClient.axqSignLogistics(vo);

				if (Boolean.TRUE.equals(vo.getSuccessFlg())) {
					String cfcaFileId = vo.getCfcaFileId();
					// 更新合同发货日期
					contract.setShippingDate(entity.getWarehouseOutDate());
					contract.setShippingFileId(cfcaFileId);
					ctrContractDao.save(contract);
				}

			} catch (Exception e) {
				logger.error("发货单盖章失败 error", e);
			}
		}, 5, TimeUnit.SECONDS);

		
		
	}

	public CtrLogistics verifyParams(CtrLogisticsReqVo reqVo) throws ApplicationException {
		Long contractId = reqVo.getContractId();
		if (Objects.isNull(contractId)) {
			throw new ApplicationException("参数缺失，无法生成单据!");
		}
		CtrLogistics ctrLogistics = ctrLogisticsDao.findByContractId(contractId);
		if (Objects.isNull(ctrLogistics)) {
			throw new ApplicationException("物流单据数据缺失，无法生成单据!");
		}
		return ctrLogistics;
	}

	/**
	 * 处理托盘业务
	 */
	public void handelTpProtocolBusiness(PmApprove pmApprove, CtrContract sellContract) {
		BigDecimal totalNumber = sellContract.getTotalNumber();
		BigDecimal warehouseNumber = sellContract.getWarehouseNumber();
		BigDecimal tpInterest = sellContract.getTpInterest();
		BigDecimal approveTpInterest = sellContract.getApproveTpInterest();
		BigDecimal totalAmount = sellContract.getTotalAmount().add(tpInterest).subtract(approveTpInterest);
		BigDecimal receiveTotalAmount = sellContract.getDealedAmount();
		Boolean updateFlg = false;
		if (receiveTotalAmount.compareTo(totalAmount) >= 0 && warehouseNumber.compareTo(totalNumber) >= 0) {
			// 出库完成，且收款完成
			updateFlg = true;
		}

		if (updateFlg) {
			// 下游销售单价（提货完成后结算） = (上游采购总价 + SUM[托盘利息])/数量
			CtrContract buyContract = ctrContractService.findBuyContractBySellContractId(sellContract.getId());
			if (Objects.nonNull(buyContract)) {
				BigDecimal newSellTotalAmount = buyContract.getTotalAmount().add(tpInterest);
				BigDecimal newSellDealPrice = newSellTotalAmount.divide(totalNumber, 2, BigDecimal.ROUND_HALF_UP);
				sellContract.setSettlementDealPrice(newSellDealPrice);
				sellContract.setSettlementTotalAmount(newSellTotalAmount);
				ctrContractDao.save(sellContract);
				// 自动签署补充协议
				autoSignProtocolFile(pmApprove,sellContract,buyContract);
			}
		}

	}

	/**
	 * 自动签署补充协议
	 * @param pmApprove
	 * @param sellContract
	 */
	public void autoSignProtocolFile(PmApprove pmApprove, CtrContract sellContract,CtrContract buyContract){
		SCHEDULED_POOL.schedule(() -> {
			autoSealPdfSignFilter.generateProtocolSealPDFSign(pmApprove, sellContract, buyContract);
			String resultFileId = autoSealPdfSignFilter.successSignProtocolFileByKeyword(sellContract.getCfcaProtocolFileNo(), sellContract.getContractNo());
			sellContract.setCfcaProtocolFileNo(sellContract.getCfcaProtocolFileNo());
			sellContract.setProtocolFileId(resultFileId);
			ctrContractDao.save(sellContract);
		}, 5, TimeUnit.SECONDS);
	}

	/**
	 * 处理中游托盘业务
	 * @param entity
	 * @param contract
	 * @param curRealOutNumber
	 * @param dealAmount
	 */
	public void handelDcTpProtocolBusiness(PmApprove pmApprove,ApplyDeliveryOut entity, CtrContract contract, BigDecimal curRealOutNumber, BigDecimal dealAmount){
		CtrContract buyContract = ctrContractDao.findByApproveIdAndContractType(contract.getApproveId(), BasConstants.CONTRACT_TYPE_B);
		BigDecimal warehouseNumber = contract.getWarehouseNumber();
		BigDecimal totalNumber = contract.getTotalNumber();
		Boolean allDeliveryOutFlg = false;
		if (warehouseNumber.compareTo(totalNumber) >= 0) {
			// 已全部出库完成
			allDeliveryOutFlg = true;
		}
		BigDecimal approveDeliveryAmount = contract.getApproveDeliveryAmount();
		BigDecimal subtract = approveDeliveryAmount.subtract(dealAmount);
		if (subtract.compareTo(BigDecimal.ZERO) <= 0) {
			subtract = BigDecimal.ZERO;
		}
		contract.setApproveDeliveryAmount(subtract);
		// 中游合同增加托盘利息
		ApplyCtrDCSX dcsxContract = applyDcsxDao.findByDCSXApproveIdAndBusinessType(contract.getApproveId(),BasConstants.BUSINESS_TYPE_ZY_TP);
		if (Objects.nonNull(dcsxContract)) {

			// 计算中游托盘利息
			Integer tpDays = entity.getTpDays();
			if (tpDays == null) {
				tpDays = 0;
			}
			BigDecimal dcsxZfRate = dcsxContract.getZfRate();
			BigDecimal newDcsxZfInterest = entity.getBuyDealPrice()
					.multiply(dcsxZfRate)
					.multiply(curRealOutNumber)
					.multiply(new BigDecimal(tpDays))
					.setScale(2, BigDecimal.ROUND_HALF_UP);
			// 中游总的托盘利息（包含本次）
			BigDecimal dcsxZfInterest = dcsxContract.getZfInterest();
			BigDecimal dcsxTotalZfInterest = dcsxZfInterest.add(newDcsxZfInterest);
			dcsxContract.setZfInterest(dcsxTotalZfInterest);
			// 计算中游结算价格
			if (allDeliveryOutFlg) {
				BigDecimal newDcsxTotalAmount = contract.getTpInterest().subtract(dcsxTotalZfInterest).add(buyContract.getTotalAmount());
				BigDecimal newDcsxDealPrice = newDcsxTotalAmount.divide(totalNumber, 3, BigDecimal.ROUND_HALF_UP);

				dcsxContract.setSettlementTotalAmount(newDcsxTotalAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
				dcsxContract.setSettlementDealPrice(newDcsxDealPrice);

				// 自动签署中游补充协议
				autoSignDcsxProtocolFile(pmApprove,dcsxContract);

			}

			applyDcsxDao.save(dcsxContract);
		}
	}

	/**
	 * 自动签署中游补充协议
	 * @param pmApprove
	 */
	public void autoSignDcsxProtocolFile(PmApprove pmApprove, ApplyCtrDCSX dcsxContract){
		SCHEDULED_POOL.schedule(() -> {
            try {
                autoSealPdfSignFilter.generateProtocolSealPDFSignDCSX(pmApprove, dcsxContract);
				String resultFileId = autoSealPdfSignFilter.successSignDcsxProtocolFileByKeyword(dcsxContract.getCfcaProtocolFileNo(),dcsxContract.getContractNo());
				dcsxContract.setCfcaProtocolFileNo(dcsxContract.getCfcaProtocolFileNo());
				dcsxContract.setProtocolFileId(resultFileId);
				applyDcsxDao.save(dcsxContract);
			} catch (ApplicationException e) {
				throw new RuntimeException(e);
			}
		}, 5, TimeUnit.SECONDS);
	}

	private BigDecimal getDefaultValue(BigDecimal value){
		return Objects.isNull(value) ? BigDecimal.ZERO : value;
	}

	/**
	 * 检验是否可以发送人保
	 * @param ctrContract
	 * @return
	 */
	private Boolean checkCanSend(CtrContract ctrContract) {
		if (ctrContract.getSettlementType() == null) {
			logger.info("该合同不是赊销合同不发送人保,contract:{}", ctrContract);
			return false;
		}
		if (!ctrContract.getWarehouseFlg()) {
			logger.info("该合同还没有出库完成,不发送人保,contract:{}", ctrContract);
			return false;
		}
		return true;
	}


    /**
     * 发起审批，更新当前审批数量字段
     */
    @Override
    @ServerTransactional
    public void doStepIn(PmApprove approve) throws ApplicationException {
        ApplyDeliveryOut entity = applyDeliveryOutDao.findOne(approve.getBizId());
        CtrContractApply contractApply = ctrContractApplyDao.findByCtrContractId(entity.getContractId());
        Long contractId = entity.getContractId();
        List<ApplyProductDetail> lstDetail = productDetailService.findApplyDetail(entity.getId(), BasConstants.APPLY_TYPE_O);
        //该合同中总的审核中的数量
        BigDecimal curApplyNumberTotal = BigDecimal.ZERO;
        BigDecimal curNumber = BigDecimal.ZERO;
        BigDecimal applyDealAmount = BigDecimal.ZERO;
        CtrContractApplyVo vo = new CtrContractApplyVo();
        for (ApplyProductDetail apd : lstDetail) {
            CtrProduct product = productDao.findOne(apd.getCtrProductId());
            curNumber = product.getCurApproveNumber().add(apd.getCurNumber());
            if (curNumber.compareTo(BigDecimal.ZERO) < 0) {
                curNumber = BigDecimal.ZERO;
            }
            product.setCurApproveNumber(curNumber);
            productDao.save(product);
            curApplyNumberTotal = curApplyNumberTotal.add(apd.getCurNumber());
            applyDealAmount = applyDealAmount.add(apd.getCurNumber().multiply(product.getDealPrice()));
        }
        vo.setDealNumber(curApplyNumberTotal);
		CtrContract ctrContract = ctrContractDao.findOne(entity.getContractId());
        CtrContract buyContract = ctrContractService.findBuyContractBySellContractId(ctrContract.getId());
		BigDecimal warehouseNumberTotal =  ctrContract.getTotalNumber();
		if (buyContract != null){
			// 实际入库数量
			BigDecimal buyWarehouseNumber = buyContract.getWarehouseNumber();
			logger.info("实际入库数量:{}", buyWarehouseNumber);
			warehouseNumberTotal = buyWarehouseNumber;
		}

		BigDecimal applyWarehouseNumber = contractApply.getApplyWarehouseNumber() == null ? BigDecimal.ZERO
				: contractApply.getApplyWarehouseNumber();
		BigDecimal applyNumber = vo.getDealNumber().add(applyWarehouseNumber);
		logger.info("applyNumber:{}", applyNumber);
		BigDecimal subtract = warehouseNumberTotal.subtract(applyNumber);
		BigDecimal number = warehouseNumberTotal.subtract(applyWarehouseNumber);
		if(subtract.compareTo(BigDecimal.ZERO) < 0){
			throw new ApplicationException("申请数量有误，剩余可申请数量为："+number+"吨!");
		}
		// 托盘预算特殊逻辑：出库货款必须已支付且剩余货款必须是剩余未提货总额的10%
		if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP,ctrContract.getBusinessType())) {
			BigDecimal receiveTotalAmount = ctrContract.getDealedAmount();
			BigDecimal tpInterest = ctrContract.getTpInterest();

			// 托盘合同单价（采购合同单价）
			BigDecimal dealPrice = buyContract.getDealPrice();

			// 占用货款
			BigDecimal approveDeliveryAmount = ctrContract.getApproveDeliveryAmount();
			// subtract ：总数量-出库数量（已出库+当前出库）
			if(subtract.compareTo(BigDecimal.ZERO) == 0){
				// 最后一次提货 必须验证所有应收款都已收全（货款、托盘利息）
				BigDecimal buyTotalAmount = buyContract.getTotalAmount();
				// 总托盘利息
				BigDecimal curTpInterest = tpInterest.add(entity.getTpInterest());
				// 采购货款+实际托盘利息
				BigDecimal add = buyTotalAmount.add(curTpInterest);
				// receiveTotalAmount 已收款总金额
				if (receiveTotalAmount.subtract(add).compareTo(BigDecimal.ZERO) < 0) {
					throw new ApplicationException("最后一次出库已收货款有误,(货款+实际托盘利息)未收全，需再收金额：" + add.subtract(receiveTotalAmount) + "元");
				}
			} else {
				// subtract:当前剩余未出库数量; multiply未出库10%货款
				BigDecimal multiply = subtract.multiply(dealPrice).multiply(new BigDecimal(0.1)).setScale(2, BigDecimal.ROUND_HALF_UP);
				// 当前已提货款 curUsedDeliveryAmount：当前已提货款(包含本次)=（已出库+出库中+当前出库）数量:amount*销售单价 applyNumber : 出库数量（已出库+当前出库）
				BigDecimal curUsedDeliveryAmount = applyNumber.multiply(dealPrice).add(tpInterest).add(entity.getTpInterest()).setScale(2, BigDecimal.ROUND_DOWN);
				// 剩余货款（去除掉本次货款后的）
				BigDecimal remainingDeliveryAmount = receiveTotalAmount.subtract(curUsedDeliveryAmount).setScale(2, BigDecimal.ROUND_HALF_UP);
				if (remainingDeliveryAmount.compareTo(multiply) < 0) {
					throw new ApplicationException("已收货款有误，剩余货款不足10%，需再收金额：" + multiply.subtract(remainingDeliveryAmount) + "元");
				}
			}
			ctrContract.setTpInterest(ctrContract.getTpInterest().add(entity.getTpInterest()));
			ctrContract.setApproveDeliveryAmount(approveDeliveryAmount.add(applyDealAmount));
			ctrContractService.save(ctrContract);
		}

		vo.setApplyType(BasConstants.APPLY_TYPE_O);
		vo.setContractId(contractId);
		vo.setTransportAmount(entity.getTransportAmount());
		vo.setWarehouseAmount(entity.getWarehouseAmount());
		vo.setStevedorage(entity.getStevedorage());
		contractApplyService.updateCtrContractApply(vo);
		contractOphisService.addHis(BasConstants.APPLY_TYPE_O, contractId, approve,entity.getWarehouseOutDate());
	}

	@Override
	public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
		//申请单作废，还原出库数据，合同状态改成入库
		ApplyDeliveryOut entity = applyDeliveryOutDao.findOne(vo.getBizId());
		Long contractId = entity.getContractId();
		//当前实际出库的数量
		BigDecimal curRealOutNumber = BigDecimal.ZERO;
		BigDecimal applyDealAmount = BigDecimal.ZERO;
		List<ApplyProductDetail> lstDetail = productDetailService.findApplyDetail(entity.getId(), BasConstants.APPLY_TYPE_O);
		for(ApplyProductDetail apd:lstDetail) {
			CtrProduct product = productDao.findOne(apd.getCtrProductId());
			product.setWarehouseNumber(product.getWarehouseNumber().subtract(apd.getCurNumber()));
			productDao.save(product);
			curRealOutNumber = curRealOutNumber.add(apd.getCurNumber());

			StockDetailRequest request = StockDetailRequest.build(product);
			request.setApplyId(entity.getId());
			request.setApproveId(vo.getApproveId());
			request.setLinkDetailId(apd.getStockDetailId());
			request.setStockContractId(apd.getStockContractId());

			request.setApplyType(BasConstants.APPLY_TYPE_O);
			request.setCtrContractId(contractId);
			request.setBack(true);
			request.setDealNumber(apd.getCurNumber());
			// stockDetailFacade.saveDeliveryOut(request);
			applyDealAmount = applyDealAmount.add(apd.getCurNumber().multiply(product.getDealPrice()));
		}

		//合同状态改成已收货
		Date deliveryOutDate = applyDeliveryOutDao.findLastDelivery(entity.getContractId());
		//合同状态改成已收货
		PmApprove approve = pmApproveService.getEntity(entity.getApproveId());
		if (StringUtils.equals(BasConstants.APPROVE_STATUS_D,approve.getStatus())) {
			ctrContractUpdateService.addWarehouseNumber(contractId, curRealOutNumber.negate(), approve.getApproveNo(), entity.getWarehouseOutDate());
		}
//		CtrContractOphisRequest request=new CtrContractOphisRequest();
//		request.setApplyType(BasConstants.APPLY_TYPE_O);
//		request.setCancel(true);
//		request.setCtrContractId(contractId);
//		request.setRemark(approve.getSubject());
//		request.setCreateUserId(vo.getUserId());
//		request.setCreateUserName(vo.getUserName());
//		request.setApproveId(vo.getApproveId());
//		request.setContractGroup("CTR");
//		contractOphisService.addHis(request);
		//作废提货单，该动作在提货单服务中实现
		applyDeliveryDao.updateDelivery(BasConstants.PRIN_CANCEL, entity.getId());
		//更新CtrContractApply中数据

		CtrContractApplyVo applyVo = new CtrContractApplyVo();
		applyVo.setDealNumber(curRealOutNumber.negate());
		applyVo.setContractId(entity.getContractId());
		applyVo.setApplyType(BasConstants.APPLY_TYPE_O);
		applyVo.setRealDate(deliveryOutDate);
		applyVo.setTransportAmount(getDefaultValue(entity.getTransportAmount()).negate());
		applyVo.setWarehouseAmount(getDefaultValue(entity.getWarehouseAmount()).negate());
		applyVo.setStevedorage(getDefaultValue(entity.getStevedorage()).negate());
		contractApplyService.updateCtrContractApply(applyVo);
		//授信合同作废
		CtrContract contract = ctrContractService.getEntity(contractId);
		if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP,contract.getBusinessType())) {
			BigDecimal approveDeliveryAmount = contract.getApproveDeliveryAmount();
			BigDecimal subtract = approveDeliveryAmount.subtract(applyDealAmount);
			if (subtract.compareTo(BigDecimal.ZERO) <= 0) {
				subtract = BigDecimal.ZERO;
			}
			BigDecimal tpInterest = contract.getTpInterest().subtract(entity.getTpInterest());
			if (tpInterest.compareTo(BigDecimal.ZERO) < 0) {
				tpInterest = BigDecimal.ZERO;
			}
			contract.setTpInterest(tpInterest);
			contract.setApproveDeliveryAmount(subtract.setScale(2,BigDecimal.ROUND_HALF_UP));
			ctrContractService.save(contract);
			if (StringUtils.equals(BasConstants.APPROVE_STATUS_D,approve.getStatus())) {
				ApplyCtrDCSX dcsxContract = applyDcsxDao.findByDCSXApproveIdAndBusinessType(contract.getApproveId(),BasConstants.BUSINESS_TYPE_ZY_TP);
				if (Objects.nonNull(dcsxContract)) {
					BigDecimal dcsxTpRate = dcsxContract.getZfRate();
					// 计算中游托盘利息
					Integer tpDays = entity.getTpDays();
					if (tpDays == null) {
						tpDays = 0;
					}
					BigDecimal newDcsxTpInterest = entity.getBuyDealPrice()
							.multiply(dcsxTpRate)
							.multiply(curRealOutNumber)
							.multiply(new BigDecimal(tpDays))
							.setScale(2, BigDecimal.ROUND_HALF_UP);
					// 中游总的托盘利息（包含本次）
					BigDecimal dcsxTotalTpInterest = dcsxContract.getZfInterest().subtract(newDcsxTpInterest);
					dcsxContract.setZfInterest(dcsxTotalTpInterest);
					applyDcsxDao.save(dcsxContract);
				}
			}
		}

		//删除系统仓储费
		BigDecimal feeAmount = getFellAmount(entity,lstDetail);
		ctrContractFeeService.saveWarehouseFee(contract, feeAmount, true);
		//更新合同实际运输仓储费
		ctrProductFeeService.saveContractRealAmount(contractId);

		//更新合同出/入库费用
		BigDecimal deliveryOptionFee = Objects.isNull(entity.getDeliveryOutFee()) ? BigDecimal.ZERO : entity.getDeliveryOutFee();
		ctrContractUpdateService.updateContractDeliveryFee(deliveryOptionFee.negate(), entity.getContractId());
		contract.getWarehouseAmount();
		BigDecimal warehouse_amount = Objects.isNull(entity.getWarehouseAmount()) ? BigDecimal.ZERO : entity.getWarehouseAmount();
		BigDecimal transport_amount = Objects.isNull(entity.getTransportAmount()) ? BigDecimal.ZERO : entity.getTransportAmount();
		if (StringUtils.equals(BasConstants.APPROVE_STATUS_D,approve.getStatus())) {
			ctrContractUpdateService.updateDeliveryAmount(contractId, warehouse_amount.negate(), transport_amount.negate(), BigDecimal.ZERO);
		}

		// 更新提货日期
		updateLogisticsNum(entity, curRealOutNumber.negate());
	}

	/**
	 * 驳回，将当前审批数量字段设为0
	 */
	@Override
	@ServerTransactional
	public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
		ApplyDeliveryOut entity = applyDeliveryOutDao.findOne(approve.getBizId());
		Long contractId = entity.getContractId();
		List<CtrProduct> productList = productDao.findByCtrContractId(contractId);
		List<ApplyProductDetail> appProdcutList =productDetailService.findApplyDetail(entity.getId(), BasConstants.APPLY_TYPE_O);
		BigDecimal totalAmount = BigDecimal.ZERO;
		BigDecimal applyDealAmount = BigDecimal.ZERO;
		BigDecimal dealNumber = BigDecimal.ZERO;
		for (CtrProduct product : productList) {
			//出入库数量
			totalAmount = totalAmount.add(product.getDealNumber());
			BigDecimal curApproveNumber = product.getCurApproveNumber();
			BigDecimal curApproveNumberSum = BigDecimal.ZERO;
			for(ApplyProductDetail detail:appProdcutList){
				if (detail.getCtrProductId()!=null && detail.getCtrProductId().equals(product.getId())) {
					curApproveNumberSum=curApproveNumber.subtract(detail.getCurNumber());
					if (curApproveNumberSum.compareTo(BigDecimal.ZERO) < 0) {
						curApproveNumberSum = BigDecimal.ZERO;
					}
					dealNumber = dealNumber.add(detail.getCurNumber());
					product.setCurApproveNumber(curApproveNumberSum);
					applyDealAmount = applyDealAmount.add(detail.getCurNumber().multiply(product.getDealPrice()));
					break;
				}
				applyDealAmount = applyDealAmount.add(detail.getCurNumber().multiply(product.getDealPrice()));

			}
			productDao.save(product);
		}
		ApplyDeliveryOut deliveryIn= applyDeliveryOutDao.findOne(approve.getBizId());
		Date deliveryOutDate = applyDeliveryOutDao.findLastDelivery(entity.getContractId());
		//更新CtrContractApply中数据
		CtrContractApplyVo vo = new CtrContractApplyVo();
		vo.setDealNumber(dealNumber.negate());
		vo.setContractId(deliveryIn.getContractId());
		vo.setApplyType(BasConstants.APPLY_TYPE_O);
		vo.setRealDate(deliveryOutDate);
		vo.setTransportAmount(getDefaultValue(entity.getTransportAmount()).negate());
		vo.setWarehouseAmount(getDefaultValue(entity.getWarehouseAmount()).negate());
		vo.setStevedorage(getDefaultValue(entity.getStevedorage()).negate());
		contractApplyService.updateCtrContractApply(vo);
		CtrContract contract = ctrContractService.getEntity(contractId);
		if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP,contract.getBusinessType())) {
			BigDecimal approveDeliveryAmount = contract.getApproveDeliveryAmount();
			BigDecimal subtract = approveDeliveryAmount.subtract(applyDealAmount);
			if (subtract.compareTo(BigDecimal.ZERO) <= 0) {
				subtract = BigDecimal.ZERO;
			}
			BigDecimal tpInterest = contract.getTpInterest().subtract(entity.getTpInterest());
			if (tpInterest.compareTo(BigDecimal.ZERO) < 0) {
				tpInterest = BigDecimal.ZERO;
			}
			contract.setTpInterest(tpInterest);
			contract.setApproveDeliveryAmount(subtract.setScale(2,BigDecimal.ROUND_HALF_UP));
			ctrContractService.save(contract);
		}
	}

	@Override
	@ServerTransactional
	public void doRetrieve(PmApproveRetrieveVo vo) throws ApplicationException {
		ApplyDeliveryOut entity = applyDeliveryOutDao.findOne(vo.getBizId());
		Long contractId = entity.getContractId();
		//当前实际出库的数量
		BigDecimal curRealOutNumber = BigDecimal.ZERO;
		BigDecimal applyDealAmount = BigDecimal.ZERO;
		List<ApplyProductDetail> lstDetail = productDetailService.findApplyDetail(entity.getId(), BasConstants.APPLY_TYPE_O);
		for(ApplyProductDetail apd:lstDetail) {
			CtrProduct product = productDao.findOne(apd.getCtrProductId());
			product.setWarehouseNumber(product.getWarehouseNumber().subtract(apd.getCurNumber()));
			productDao.save(product);
			curRealOutNumber = curRealOutNumber.add(apd.getCurNumber());
			applyDealAmount = applyDealAmount.add(apd.getCurNumber().multiply(product.getDealPrice()));
		}
		CtrContract contract = ctrContractService.getEntity(contractId);
		if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP,contract.getBusinessType())) {
			BigDecimal approveDeliveryAmount = contract.getApproveDeliveryAmount();
			BigDecimal subtract = approveDeliveryAmount.subtract(applyDealAmount);
			if (subtract.compareTo(BigDecimal.ZERO) <= 0) {
				subtract = BigDecimal.ZERO;
			}
			BigDecimal tpInterest = contract.getTpInterest().subtract(entity.getTpInterest());
			if (tpInterest.compareTo(BigDecimal.ZERO) < 0) {
				tpInterest = BigDecimal.ZERO;
			}
			contract.setTpInterest(tpInterest);
			contract.setApproveDeliveryAmount(subtract.setScale(2,BigDecimal.ROUND_HALF_UP));
			ctrContractService.save(contract);
		}

	}

	@Override
	@ServerTransactional
	public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
		ApplyDeliveryOut out = null;
		ApplyProductDetailSaveVo saveVo = new ApplyProductDetailSaveVo();
		saveVo.setApplyType(BasConstants.APPLY_TYPE_O);
		if(pmEntity instanceof ApplyDeliveryOutVo){
			ApplyDeliveryOutVo vo = (ApplyDeliveryOutVo) pmEntity;
			out = new ApplyDeliveryOut();
			BeanUtils.copyProperties(vo, out);
			if(out.getId()==0){
				out.setApplyNo(composeApplyNo(out.getContractNo(), out.getContractId()));
			}
			CtrContract contract = ctrContractService.getEntity(out.getContractId());
			out.setDeptId(contract.getDeptId());
			out.setConfirmFlg(BasConstants.CONFIRM_FLG_NOT);
			out.setBusinessType(contract.getBusinessTypeDcsx());
			CtrContractApply contractApply = ctrContractApplyDao.findByCtrContractId(vo.getContractId());
			BigDecimal applyTransportAmount = contractApply.getApplyTransportAmount();
			BigDecimal applyWarehouseAmount = contractApply.getApplyWarehouseAmount();
			BigDecimal applyStevedorage = contractApply.getApplyStevedorage();
			BigDecimal transportAmount = getDefaultValue(vo.getTransportAmount());
			BigDecimal warehouseAmount = getDefaultValue(vo.getWarehouseAmount());
			BigDecimal stevedorage = getDefaultValue(vo.getStevedorage());
			BigDecimal approveTransportAmount = getDefaultValue(contract.getApproveTransportAmount());
			BigDecimal approveWarehouseAmount = getDefaultValue(contract.getApproveWarehouseAmount());
			BigDecimal approveStevedorage = getDefaultValue(contract.getApproveStevedorage());
			// 出库总运费
			BigDecimal outFreightTotalAmount = applyTransportAmount.add(applyWarehouseAmount).add(applyStevedorage).add(transportAmount).add(warehouseAmount).add(stevedorage);
			// 预算总运费
			BigDecimal conFreightTotalAmount = approveTransportAmount.add(approveWarehouseAmount).add(approveStevedorage);

			if(outFreightTotalAmount.compareTo(conFreightTotalAmount) > 0) {
				// 运费超出添加标识
				out.setFeeExceedFlg(true);
				out.setFeeExceedAmount(outFreightTotalAmount.subtract(conFreightTotalAmount));
			} else {
				out.setFeeExceedFlg(false);
				out.setFeeExceedAmount(BigDecimal.ZERO);
			}
			SysDeptSdk sysDeptSdk = authOpenFacade.findDeptById(contract.getDeptId());
			out.setOwnRegion(Objects.nonNull(sysDeptSdk) && Objects.nonNull(OwnRegionEnum.getRegionEnumByName(sysDeptSdk.getDeptName()))
					? Objects.requireNonNull(OwnRegionEnum.getRegionEnumByName(sysDeptSdk.getDeptName())).getRegionCode()
					: "");
			out = applyDeliveryOutDao.save(out);
			saveVo.setApplyId(out.getId());
			saveVo.setEnterpriseId(out.getEnterpriseId());
			productDetailService.saveDetailBatch(vo.getLstInsert(), vo.getLstUpdate(), vo.getLstDelete(), saveVo);
		}else{
			ApplyDeliveryOut entity = (ApplyDeliveryOut) pmEntity;
			entity.setConfirmFlg(BasConstants.CONFIRM_FLG_NOT);
			out = applyDeliveryOutDao.save(entity);
			saveVo.setApplyId(entity.getId());
			saveVo.setEnterpriseId(entity.getEnterpriseId());
			productDetailService.saveBatchEnterpriseId(saveVo);
		}
		return out;
	}

	@Override
	public String getSubject(IPmEntity pmEntity, PmProcess process) {
		if (pmEntity != null) {
			ApplyDeliveryOut entity = (ApplyDeliveryOut) pmEntity;
			List<ApplyProductDetail> list = productDetailService.findApplyDetail(entity.getId(), BasConstants.APPLY_TYPE_O);
			BigDecimal sumNumber = BigDecimal.ZERO;
			for (ApplyProductDetail applyProductDetail : list) {
				sumNumber = sumNumber.add(applyProductDetail.getDealNumber());
			}
			CtrContract contract = ctrContractService.findByContractNo(entity.getContractNo());
			String companyName = entity.getCompanyName();
			String companyName1 = RuleUtil.companyNameSubString(companyName);
			String companyName2 = RuleUtil.companyNameSubString(contract.getOurCompanyName());
			String type = DictUtil.getValue(BasConstants.DICT_TYPE_DELIVERYOUT_TYPE, entity.getWarehouseOutType());
			String companyTitle = "";
			if (StringUtils.isNotBlank(companyName1) && StringUtils.isNotBlank(companyName2)) {
				companyTitle = companyName1 + "-" + companyName2;
			}
			String outMessage = contract.getProductsName() + "/" + sumNumber;
			// this.overageOutVerify(entity, contract, list);
			entity = applyDeliveryOutDao.save(entity);
			String overageVerify = Boolean.TRUE.equals(entity.getOverageFlg()) ? "[超额]" : "";
			String feeExceed = Boolean.TRUE.equals(entity.getFeeExceedFlg()) ? "[运费超出]" : "";
			String subjectPrefix = overageVerify + feeExceed + entity.getContractNo();
			return SubjectUtil.formatSubject(subjectPrefix, outMessage, companyTitle, type);
		}
		return "";
	}

//	private void overageOutVerify(ApplyDeliveryOut entity, CtrContract contract, List<ApplyProductDetail> productDetialList){
//		try{
//			entity.setOverageFlg(false);
//			entity.setOverageMessage("");
//			List<CtrContract> contractList = ctrContractService.findByApproveId(contract.getApproveId());
//			if (CollectionUtils.isNotEmpty(contractList)){
//				CtrContract buyContract = contractList.stream().filter(c -> StringUtils.equals(BasConstants.CONTRACT_TYPE_B, c.getContractType())).findAny().orElse(null);
//				CtrContract sellContract = contractList.stream().filter(c -> StringUtils.equals(BasConstants.CONTRACT_TYPE_S, c.getContractType())).findAny().orElse(null);
//				if (Objects.isNull(buyContract) || Objects.isNull(sellContract)) {
//					return;
//				}
//				CtrContractApply contractApply = ctrContractApplyDao.findByCtrContractId(entity.getContractId());
//				if (Boolean.FALSE.equals(sellContract.getMatchCreditFlg()) && StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, buyContract.getBusinessType())) {
//					BigDecimal currOutNumber = productDetialList.stream().map(ApplyProductDetail::getCurNumber).reduce(BigDecimal.ZERO, BigDecimal::add);
//					BigDecimal totalAmount = sellContract.getTotalAmount();
//					BigDecimal totalNumber = sellContract.getTotalNumber();
//					BigDecimal receiveAmount = sellContract.getDealedAmount();
//					BigDecimal warehouseNumber = contractApply.getApplyWarehouseNumber();
//					BigDecimal receiveAmountRatio = receiveAmount.divide(totalAmount, 4, RoundingMode.HALF_UP);
//					BigDecimal maxOutNumber = totalNumber.multiply(receiveAmountRatio).setScale(2, RoundingMode.HALF_UP);
//					BigDecimal compareValue = warehouseNumber.add(currOutNumber).subtract(maxOutNumber);
//					if (compareValue.compareTo(BigDecimal.ZERO) > 0) {
//						String message = String.format("超额：收款%s，最大可出库数量%s吨，超出%s吨。",
//								receiveAmountRatio.multiply(new BigDecimal(100)).setScale(2, RoundingMode.UP) + "%",
//								maxOutNumber.setScale(3, RoundingMode.UP),
//								compareValue.setScale(3, RoundingMode.UP));
//						entity.setOverageFlg(true);
//						entity.setOverageMessage(message);
//						applyDeliveryOutDao.save(entity);
//					}
//				}
//			}
//		}catch (Exception e){
//			logger.error("overageOutVerify error", e);
//		}
//	}

	@Override
	public List<ApplyDeliveryOut> findByContractId(Long contractId) {
		return applyDeliveryOutDao.findByContractId(contractId);
	}

	@Override
	@ServerTransactional
	public void updateApplyStatus(Long contractId) {
		applyDeliveryOutDao.updateApplyStatus(contractId);
	}

	private String composeApplyNo(String contractNo, Long contractId) {
		List<ApplyDeliveryOut> list = applyDeliveryOutDao.findByContractId(contractId);
		String fmt = String.format("-%d", list.size() + 1);
		contractNo = contractNo.replaceAll("\\D", "");
		return LogisticsEnum.DELIVERY_OUT.getLogisticsPrefix() + contractNo + fmt;
	}

	/**
	 * 计算系统仓储费
	 * @param deliveryOut
	 * @param productDetailList
	 * @return
	 */
	private BigDecimal getFellAmount(ApplyDeliveryOut deliveryOut,List<ApplyProductDetail> productDetailList) {
		BigDecimal fellAmount = BigDecimal.ZERO;
		Date deliveryOutDate = deliveryOut.getCreatedDate();
		for (ApplyProductDetail apd : productDetailList) {
			CtrProduct product = productDao.findOne(apd.getCtrProductId());
			if (apd.getStockDetailId() == null) {
				continue;
			}
			StockDetail stockDetail = stockDetailService.getEntity(apd.getStockDetailId());
			Date deliveryInDate = stockDetail.getCreatedDate();
			DateOperator.formatDate(new Date(), DateOperator.FORMAT_STR);
			//仓储费单件
			BigDecimal warehousePrice = product.getWarehousePrice();
			warehousePrice = warehousePrice == null ? new BigDecimal(5) : warehousePrice;
			//数量
			BigDecimal dealNumber = apd.getCurNumber();
			//在库天数 出库日期-入库日期
			Long compareDays = DateOperator.compareDays(formatterDate(deliveryInDate), formatterDate(deliveryOutDate));
			compareDays = compareDays <= 0L ? 1L : compareDays;
			//系统仓储费=库存日期天数*仓储费单价*数量
			fellAmount = fellAmount.add(new BigDecimal(compareDays).multiply(warehousePrice).multiply(dealNumber));
		}
		return fellAmount;
	}

	private Date formatterDate(Date date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(DateOperator.FORMAT_STR);
			String format = sdf.format(date);
			date = sdf.parse(format);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 查询已出库未确认批次信息
	 *
	 * @param contractId
	 * @return
	 */
	@Override
	public List<ApplyProductDetailVo> getUnConfirmDeliveryOut(Long contractId) {
		List<ApplyDeliveryOut> list = applyDeliveryOutDao.findByContractIdAndConfirmFlgAndStatus(contractId, BasConstants.CONFIRM_FLG_NOT,BasConstants.APPROVE_STATUS_D);
		List<Long> ids = new ArrayList<>();
		Map<Long, String> userIdName = new HashMap<>();
		// 取每个出库批次的申请人
		for (ApplyDeliveryOut applyDeliveryOut : list) {
			ids.add(applyDeliveryOut.getId());
			PmApprove entity = pmApproveService.getEntity(applyDeliveryOut.getApproveId());
			Long createUserId = entity.getCreateUserId();
			//SysUser userById = adminOpenFacade.findUserById(createUserId);
			SysUserSdk userById = authOpenFacade.findUserById(createUserId);
			userIdName.put(applyDeliveryOut.getId(), userById.getNickName());
		}
		List<ApplyProductDetail> byApplyIdInAndApplyType = applyProductDetailDao.findByApplyIdInAndApplyType(ids, BasConstants.APPLY_TYPE_O);
		List<ApplyProductDetailVo> result = new ArrayList<>(byApplyIdInAndApplyType.size());
		for (ApplyProductDetail detail : byApplyIdInAndApplyType) {
			ApplyProductDetailVo applyProductDetailVo = new ApplyProductDetailVo();
			for (ApplyDeliveryOut applyDeliveryOut : list) {
				if(detail.getApplyId().equals(applyDeliveryOut.getId())){
					BigDecimal transportAmount=applyDeliveryOut.getTransportAmount()==null?BigDecimal.ZERO:applyDeliveryOut.getTransportAmount();
					BigDecimal warehouseAmount=applyDeliveryOut.getWarehouseAmount()==null?BigDecimal.ZERO:applyDeliveryOut.getWarehouseAmount();
					BigDecimal stevedorage=applyDeliveryOut.getStevedorage()==null?BigDecimal.ZERO:applyDeliveryOut.getStevedorage();
					BigDecimal LogisticsCosts=transportAmount.add(warehouseAmount).add(stevedorage);
					applyProductDetailVo.setLogisticsCosts(LogisticsCosts);
					applyProductDetailVo.setDeliveryOutFee(applyDeliveryOut.getDeliveryOutFee()==null?BigDecimal.ZERO:applyDeliveryOut.getDeliveryOutFee());
					applyProductDetailVo.setTransportAmount(applyDeliveryOut.getTransportAmount()==null?BigDecimal.ZERO:applyDeliveryOut.getTransportAmount());
					applyProductDetailVo.setWarehouseAmount(applyDeliveryOut.getWarehouseAmount()==null?BigDecimal.ZERO:applyDeliveryOut.getWarehouseAmount());
					applyProductDetailVo.setDeliveryAddr(applyDeliveryOut.getDeliveryAddr()==null?"":applyDeliveryOut.getDeliveryAddr());
					applyProductDetailVo.setPlateNumber(applyDeliveryOut.getPlateNumber()==null?"":applyDeliveryOut.getPlateNumber());
					applyProductDetailVo.setContactName(applyDeliveryOut.getContactName()==null?"":applyDeliveryOut.getContactName());
					applyProductDetailVo.setContactPhone(applyDeliveryOut.getContactPhone()==null?"":applyDeliveryOut.getContactPhone());
					applyProductDetailVo.setDriverName(applyDeliveryOut.getDriverName()==null?"":applyDeliveryOut.getDriverName());
					applyProductDetailVo.setDriverPhone(applyDeliveryOut.getDriverPhone()==null?"":applyDeliveryOut.getDriverPhone());
					applyProductDetailVo.setDriverCardNo(applyDeliveryOut.getDriverCardNo()==null?"":applyDeliveryOut.getDriverCardNo());
					applyProductDetailVo.setRemark(applyDeliveryOut.getRemark()==null?"":applyDeliveryOut.getRemark());
					applyProductDetailVo.setDeliveryOutId(applyDeliveryOut.getId()==null?null:applyDeliveryOut.getId());
					break;
				}
			}
			BeanUtils.copyProperties(detail, applyProductDetailVo);
			applyProductDetailVo.setApplyMatcher(userIdName.get(applyProductDetailVo.getApplyId()));
			applyProductDetailVo.setApplyDeliveryOutId(applyProductDetailVo.getApplyId());
			result.add(applyProductDetailVo);
		}
		return result;
	}

	/**
	 * 查询中游已出库未确认批次信息
	 *
	 * @param contractId
	 * @return
	 */
	@Override
	public List<ApplyProductDetailVo> getUnConfirmDeliveryOutDcsx(Long contractId) {
//		List<ApplyDeliveryOut> list = applyDeliveryOutDao.findByContractIdAndConfirmDcsxFlgAndStatus(contractId, BasConstants.CONFIRM_FLG_NOT,BasConstants.APPROVE_STATUS_D);
		List<ApplyDeliveryOut> list = applyDeliveryOutDao.findByContractIdAndStatus(contractId, BasConstants.APPROVE_STATUS_D);
		
		List<Long> ids = new ArrayList<>();
		Map<Long, String> userIdName = new HashMap<>();
		// 取每个出库批次的申请人
		for (ApplyDeliveryOut applyDeliveryOut : list) {
			ids.add(applyDeliveryOut.getId());
			PmApprove entity = pmApproveService.getEntity(applyDeliveryOut.getApproveId());
			Long createUserId = entity.getCreateUserId();
			//SysUser userById = adminOpenFacade.findUserById(createUserId);
			SysUserSdk userById = authOpenFacade.findUserById(createUserId);
			userIdName.put(applyDeliveryOut.getId(), userById.getNickName());
		}
		List<ApplyProductDetail> byApplyIdInAndApplyType = applyProductDetailDao.findByApplyIdInAndApplyType(ids, BasConstants.APPLY_TYPE_O);
		List<ApplyProductDetailVo> result = new ArrayList<>(byApplyIdInAndApplyType.size());
		for (ApplyProductDetail detail : byApplyIdInAndApplyType) {
			ApplyProductDetailVo applyProductDetailVo = new ApplyProductDetailVo();
			BeanUtils.copyProperties(detail, applyProductDetailVo);
			Boolean unConfirmFlg = true;
			for (ApplyDeliveryOut applyDeliveryOut : list) {
				if(detail.getApplyId().equals(applyDeliveryOut.getId())){
					BigDecimal transportAmount=applyDeliveryOut.getTransportAmount()==null?BigDecimal.ZERO:applyDeliveryOut.getTransportAmount();
					BigDecimal warehouseAmount=applyDeliveryOut.getWarehouseAmount()==null?BigDecimal.ZERO:applyDeliveryOut.getWarehouseAmount();
					BigDecimal stevedorage=applyDeliveryOut.getStevedorage()==null?BigDecimal.ZERO:applyDeliveryOut.getStevedorage();
					BigDecimal LogisticsCosts=transportAmount.add(warehouseAmount).add(stevedorage);
					applyProductDetailVo.setLogisticsCosts(LogisticsCosts);
					applyProductDetailVo.setDeliveryOutFee(applyDeliveryOut.getDeliveryOutFee()==null?BigDecimal.ZERO:applyDeliveryOut.getDeliveryOutFee());
					applyProductDetailVo.setTransportAmount(applyDeliveryOut.getTransportAmount()==null?BigDecimal.ZERO:applyDeliveryOut.getTransportAmount());
					applyProductDetailVo.setWarehouseAmount(applyDeliveryOut.getWarehouseAmount()==null?BigDecimal.ZERO:applyDeliveryOut.getWarehouseAmount());
					applyProductDetailVo.setDeliveryAddr(applyDeliveryOut.getDeliveryAddr()==null?"":applyDeliveryOut.getDeliveryAddr());
					applyProductDetailVo.setPlateNumber(applyDeliveryOut.getPlateNumber()==null?"":applyDeliveryOut.getPlateNumber());
					applyProductDetailVo.setContactName(applyDeliveryOut.getContactName()==null?"":applyDeliveryOut.getContactName());
					applyProductDetailVo.setContactPhone(applyDeliveryOut.getContactPhone()==null?"":applyDeliveryOut.getContactPhone());
					applyProductDetailVo.setDriverName(applyDeliveryOut.getDriverName()==null?"":applyDeliveryOut.getDriverName());
					applyProductDetailVo.setDriverPhone(applyDeliveryOut.getDriverPhone()==null?"":applyDeliveryOut.getDriverPhone());
					applyProductDetailVo.setDriverCardNo(applyDeliveryOut.getDriverCardNo()==null?"":applyDeliveryOut.getDriverCardNo());
					applyProductDetailVo.setRemark(applyDeliveryOut.getRemark()==null?"":applyDeliveryOut.getRemark());
					applyProductDetailVo.setDeliveryOutId(applyDeliveryOut.getId()==null?null:applyDeliveryOut.getId());
					BigDecimal confirmDcsxNumber = applyDeliveryOut.getConfirmDcsxNumber();
					BigDecimal curNumber = detail.getCurNumber();
					if(confirmDcsxNumber != null && curNumber != null) {
						// 确认收货数量小于出库数量
						BigDecimal subtract = curNumber.subtract(confirmDcsxNumber);
						if(subtract.compareTo(BigDecimal.ZERO) > 0) {
							unConfirmFlg = true;
							applyProductDetailVo.setCurNumber(subtract);
						} else {
							unConfirmFlg = false;
						}
						
					}
					break;
				}
			}
			applyProductDetailVo.setApplyMatcher(userIdName.get(applyProductDetailVo.getApplyId()));
			applyProductDetailVo.setApplyDeliveryOutId(applyProductDetailVo.getApplyId());
			if(unConfirmFlg) {
				result.add(applyProductDetailVo);
			}
		}
		return result;
	}

	/**
	 * 查询已出库信息
	 *
	 * @param contractId
	 * @return
	 */
	@Override
	public List<ApplyProductDetailVo> getAllDeliveryOut(Long contractId) {
		List<ApplyDeliveryOut> list = applyDeliveryOutDao.findByContractIdAndStatus(contractId,BasConstants.APPROVE_STATUS_D);
		List<Long> ids = new ArrayList<>();
		Map<Long, String> userIdName = new HashMap<>();
		// 取每个出库批次的申请人
		for (ApplyDeliveryOut applyDeliveryOut : list) {
			ids.add(applyDeliveryOut.getId());
			PmApprove entity = pmApproveService.getEntity(applyDeliveryOut.getApproveId());
			Long createUserId = entity.getCreateUserId();
			//SysUser userById = adminOpenFacade.findUserById(createUserId);
			SysUserSdk userById = authOpenFacade.findUserById(createUserId);
			userIdName.put(applyDeliveryOut.getId(), userById.getNickName());
		}
		List<ApplyProductDetail> byApplyIdInAndApplyType = applyProductDetailDao.findByApplyIdInAndApplyType(ids, BasConstants.APPLY_TYPE_O);
		List<ApplyProductDetailVo> result = new ArrayList<>(byApplyIdInAndApplyType.size());
		for (ApplyProductDetail detail : byApplyIdInAndApplyType) {
			ApplyProductDetailVo applyProductDetailVo = new ApplyProductDetailVo();
			for (ApplyDeliveryOut applyDeliveryOut : list) {
				if(detail.getApplyId().equals(applyDeliveryOut.getId())){
					BigDecimal transportAmount=applyDeliveryOut.getTransportAmount()==null?BigDecimal.ZERO:applyDeliveryOut.getTransportAmount();
					BigDecimal warehouseAmount=applyDeliveryOut.getWarehouseAmount()==null?BigDecimal.ZERO:applyDeliveryOut.getWarehouseAmount();
					BigDecimal stevedorage=applyDeliveryOut.getStevedorage()==null?BigDecimal.ZERO:applyDeliveryOut.getStevedorage();
					BigDecimal LogisticsCosts=transportAmount.add(warehouseAmount).add(stevedorage);
					applyProductDetailVo.setLogisticsCosts(LogisticsCosts);
					applyProductDetailVo.setDeliveryOutFee(applyDeliveryOut.getDeliveryOutFee()==null?BigDecimal.ZERO:applyDeliveryOut.getDeliveryOutFee());
					applyProductDetailVo.setTransportAmount(applyDeliveryOut.getTransportAmount()==null?BigDecimal.ZERO:applyDeliveryOut.getTransportAmount());
					applyProductDetailVo.setWarehouseAmount(applyDeliveryOut.getWarehouseAmount()==null?BigDecimal.ZERO:applyDeliveryOut.getWarehouseAmount());
					applyProductDetailVo.setDeliveryAddr(applyDeliveryOut.getDeliveryAddr()==null?"":applyDeliveryOut.getDeliveryAddr());
					applyProductDetailVo.setPlateNumber(applyDeliveryOut.getPlateNumber()==null?"":applyDeliveryOut.getPlateNumber());
					applyProductDetailVo.setContactName(applyDeliveryOut.getContactName()==null?"":applyDeliveryOut.getContactName());
					applyProductDetailVo.setContactPhone(applyDeliveryOut.getContactPhone()==null?"":applyDeliveryOut.getContactPhone());
					applyProductDetailVo.setDriverName(applyDeliveryOut.getDriverName()==null?"":applyDeliveryOut.getDriverName());
					applyProductDetailVo.setDriverPhone(applyDeliveryOut.getDriverPhone()==null?"":applyDeliveryOut.getDriverPhone());
					applyProductDetailVo.setDriverCardNo(applyDeliveryOut.getDriverCardNo()==null?"":applyDeliveryOut.getDriverCardNo());
					applyProductDetailVo.setRemark(applyDeliveryOut.getRemark()==null?"":applyDeliveryOut.getRemark());
					applyProductDetailVo.setDeliveryOutId(applyDeliveryOut.getId()==null?null:applyDeliveryOut.getId());
					applyProductDetailVo.setStevedorage(applyDeliveryOut.getStevedorage()==null?null:applyDeliveryOut.getStevedorage());
					applyProductDetailVo.setOtherFee(applyDeliveryOut.getOtherFee()==null?null:applyDeliveryOut.getOtherFee());
					applyProductDetailVo.setCarrier(applyDeliveryOut.getCarrier()==null?null:applyDeliveryOut.getCarrier());
					break;
				}
			}
			BeanUtils.copyProperties(detail, applyProductDetailVo);
			applyProductDetailVo.setApplyMatcher(userIdName.get(applyProductDetailVo.getApplyId()));
			applyProductDetailVo.setApplyDeliveryOutId(applyProductDetailVo.getApplyId());
			result.add(applyProductDetailVo);
		}
		return result;
	}

	/**
	 * 查询详细
	 *
	 * @param applyDeliveryOutId
	 * @return
	 */
	@Override
	public ApplyProductDetail findByApplyDeliveryOutId(Long applyDeliveryOutId) {
		ApplyProductDetail detail = applyProductDetailDao.findByApplyIdAndApplyType(applyDeliveryOutId, BasConstants.APPLY_TYPE_O);
		return detail;
	}

	@Override
	public ApplyProductDetail findByApplyDeliveryOutApplyNo(String applyNo) {
		ApplyDeliveryOut applyDeliveryOut = applyDeliveryOutDao.findByApplyNo(applyNo);
		ApplyProductDetail detail = applyProductDetailDao.findByApplyIdAndApplyType(applyDeliveryOut.getId(), BasConstants.APPLY_TYPE_O);
		return detail;
	}

	@Override
	public ApplyDeliveryOut findByApplyNo(String applyNo) {
		return applyDeliveryOutDao.findByApplyNo(applyNo);
	}

	@Override
	public ApplyDeliveryOut findEntity(Long approveId) {
		return applyDeliveryOutDao.findEntity(approveId);
	}

	@Override
	public List<ApplyDeliveryOut> findByContractNo2(String contractNo) {
		return applyDeliveryOutDao.findByContractNo2(contractNo);
	}

    /**
     * 发起出库申请
     * @param deliveryOutVo
     * @throws ApplicationException
     */
    @Override
    @ServerTransactional
    public void applyDeliveryOut(ApplyDeliveryOutVo deliveryOutVo) throws ApplicationException {
        try {
            if (StringUtils.isEmpty(deliveryOutVo.getWarehouseOutType())) {
                throw new ApplicationException("出库方式不能为NULL");
            }
            if (deliveryOutVo.getWarehouseOutDate() == null) {
                throw new ApplicationException("出库日期不能为NULL");
            }
            PmApproveSaveVo startVo = new PmApproveSaveVo();
            startVo.setMode(BasConstants.APPROVE_STATUS_A);
            startVo.setStatus(BasConstants.APPROVE_STATUS_A);
            startVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
            deliveryOutVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
            //获取流程
            PmProcessSearchVo searchVo = new PmProcessSearchVo();
            searchVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
            searchVo.setProcessCode(BasConstants.PROCESS_CODE_OUT); //出库流程
            if (deliveryOutVo.getId() == 0) {
                //获取合同号
                deliveryOutVo.setApplyNo(composeApplyNo(deliveryOutVo.getContractNo(), deliveryOutVo.getContractId()));
            }
            //获取撮合字段信息
            List<ApplyProductDetailVo> insertList = deliveryOutVo.getProductJSON();
            deliveryOutVo.setBatchSub(insertList, deliveryOutVo.getLstUpdate(), deliveryOutVo.getLstDelete());

            PmProcess process = pmProcessService.findByProcessCode(searchVo);
            if (process == null) {
                throw new ApplicationException(ApiCode.ERROR_PROCESS_NOTFOUND, "找不到流程记录");
            }
            //通过当前登录人Id 获取申请人信息
            SysUserSdk userById = authOpenFacade.findUserById(deliveryOutVo.getApplyUserId());
            if (userById != null) {
                startVo.setUserId(userById.getUserId());
                startVo.setUserName(userById.getNickName());
                startVo.setProcessId(process.getId());
                startVo.setApproveId(0L);
                deliveryOutVo.setApproveId(0L);//代表新增
            }
            startVo.setBizEntityJson(JsonUtil.obj2Json(deliveryOutVo));
            pmApproveService.startFlow(startVo);
        } catch (ApplicationException e) {
            throw new ApplicationException(e);
        }
    }

	/**
	 * 查询有效的出库审批单
	 *
	 * @param contractId
     */
	@Override
	public List<ApplyDeliveryOut> findByContractIdNoStatusB(Long contractId) {
		return applyDeliveryOutDao.findByContractIdNoStatusB(contractId);
	}

	@Override
	public ApplyDeliveryOut generateApplyNo(Long contractId) {
		ApplyDeliveryOut entity = new ApplyDeliveryOut();
		CtrContract contract = ctrContractService.getEntity(contractId);
		String applyNo = composeApplyNo(contract.getContractNo(), contractId);
		entity.setApplyNo(applyNo);
		return entity;
	}

	private void sendNotifyMessage(CtrContract contract) {
		executor.execute(() -> {
			try {
				if (contract.getContractType().equals(BasConstants.APPLY_TYPE_S)) {
					BsCompany company = bsCompanyClient.getEntity(contract.getCompanyId());
					if (Objects.nonNull(company) && Boolean.TRUE.equals(company.getOpenCfcaFlg())) {
						SMSUtils.sendReceiveGoodsContractNo(company.getCompanyPhone(), contract.getContractNo());
					}
				}
			} catch (Exception e) {
				logger.error("sendNotifyMessage error", e);
			}
		});
	}

	private void addStandingOut(ApplyDeliveryOut out) {
		executor.execute(() -> {
			try {
				// 出库完成时增加出入库台账报表
				CtrContract ctrContractSell = ctrContractDao.findOne(out.getContractId());
				if (Objects.nonNull(ctrContractSell)) {
					CtrContract ctrContractBuy = ctrContractDao.findByApproveIdAndContractType(ctrContractSell.getApproveId(), BasConstants.CONTRACT_TYPE_B);
					if (Objects.nonNull(ctrContractBuy)) {
						CtrOutInLedger ctrOutInLedger = new CtrOutInLedger();
						BeanUtils.copyProperties(ctrContractSell, ctrOutInLedger);
						ctrOutInLedger.setId(null);
						ctrOutInLedger.setOperTime(new Date());
						ctrOutInLedger.setOperation(BasConstants.DICT_OUT_IN_LEDGER_TYPE_3);
						ctrOutInLedger.setPrice(ctrContractSell.getDealPrice());
						ctrOutInLedger.setSourceId(out.getId());
						ctrOutInLedger.setCarrier(out.getCarrier());
						ctrOutInLedger.setDeliveryAddr(out.getDeliveryAddr());
						ctrOutInLedger.setDeliveryPhone(out.getDeliveryPhone());
						ctrOutInLedger.setOtherAmount(out.getStevedorage());
						ctrOutInLedger.setTransportAmount(out.getTransportAmount());
						ctrOutInLedger.setDeliveryOutFee(out.getDeliveryOutFee());
						ctrOutInLedger.setDriverName(out.getDriverName());
						ctrOutInLedger.setDriverPhone(out.getDriverPhone());
						ctrOutInLedger.setDriverCardNo(out.getDriverCardNo());
						ctrOutInLedger.setPlateNumber(out.getPlateNumber());
						// 获取当前出库数量
						ApplyProductDetail applyProductDetail = applyProductDetailDao.findByApplyIdAndApplyType(out.getId(), BasConstants.APPLY_TYPE_O);
						if (Objects.nonNull(applyProductDetail)) {
							ctrOutInLedger.setWarehouseNumber(applyProductDetail.getCurNumber());
						}
						// 计算可提取数量（可提取数量 = 合同数量 - 已出库数量）
						List<CtrOutInLedger> ctrOutLedgerList = ctrOutInLedgerDao.findByOperationAndContractNo(BasConstants.DICT_OUT_IN_LEDGER_TYPE_3, ctrContractSell.getContractNo());
						BigDecimal extractNumber = ctrContractSell.getTotalNumber().subtract(ctrOutInLedger.getWarehouseNumber());
						if (CollectionUtils.isNotEmpty(ctrOutLedgerList)) {
							for (CtrOutInLedger outLedger : ctrOutLedgerList) {
								extractNumber = extractNumber.subtract(outLedger.getWarehouseNumber());
							}
						}
						ctrOutInLedger.setExtractNumber(extractNumber);
						// 获取公司传真
						BsCompany bsCompany = bsCompanyClient.getEntity(ctrContractSell.getCompanyId());
						if (Objects.nonNull(bsCompany)) {
							ctrOutInLedger.setCompanyFax(bsCompany.getCompanyFax());
						}
						// 获取实际合同单号
						List<CtrLogistics> ctrLogisticsList = ctrLogisticsDao.findBySellContractNo(ctrOutInLedger.getContractNo());
						if (CollectionUtils.isNotEmpty(ctrLogisticsList)) {
							ctrOutInLedger.setRealContractNo(ctrLogisticsList.get(0).getSupplierNo());
						}
						ctrOutInLedgerDao.save(ctrOutInLedger);
					}
				}
			} catch (Exception e) {
				logger.error("addStandingOut error", e);
			}
		});
	}

	private void updateLogisticsNum(ApplyDeliveryOut entity, BigDecimal currNum) {
		executor.execute(() -> {
			try {
				CtrLogistics ctrLogistics = ctrLogisticsDao.findByContractId(entity.getContractId());
				if (Objects.nonNull(ctrLogistics)) {
					BigDecimal logisticsNumber = ctrLogistics.getLogisticsNumber();
					ctrLogistics.setLogisticsNumber(logisticsNumber.add(currNum));
					ctrLogistics.setLogisticsDate(entity.getWarehouseOutDate());
					ctrLogisticsDao.save(ctrLogistics);
				}
			} catch (Exception e) {
				logger.error("updateLogisticsNum error", e);
			}
		});
	}
}

