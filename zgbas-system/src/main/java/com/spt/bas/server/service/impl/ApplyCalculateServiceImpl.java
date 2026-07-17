package com.spt.bas.server.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCalculate;
import com.spt.bas.client.entity.ApplyImportDetail;
import com.spt.bas.client.entity.ApplyProductDetail;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrContractRela;
import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.entity.StockContract;
import com.spt.bas.client.vo.ApplyCalculateDetailVo;
import com.spt.bas.client.vo.ApplyCalculateFlowVo;
import com.spt.bas.client.vo.ApplyContractAdjustRequestVo;
import com.spt.bas.client.vo.CtrContractOphisRequest;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.ApplyCalculateDao;
import com.spt.bas.server.dao.CtrContractRelaDao;
import com.spt.bas.server.dao.CtrProductDao;
import com.spt.bas.server.dao.StockContractDao;
import com.spt.bas.server.service.IApplyCalculateService;
import com.spt.bas.server.service.IApplyImportDetailService;
import com.spt.bas.server.service.IApplyProductDetailService;
import com.spt.bas.server.service.ICtrContractOphisService;
import com.spt.bas.server.service.ICtrContractRelaService;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.bas.server.service.ICtrContractTextService;
import com.spt.bas.server.stock.service.StockDetailFacade;
import com.spt.pm.service.IBsKeySequenceService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class ApplyCalculateServiceImpl extends BaseService<ApplyCalculate> implements IApplyCalculateService {
	private static final ExecutorService threadPool = Executors.newFixedThreadPool(10);
	@Autowired
	private ApplyCalculateDao applyCalculateDao;
	@Autowired
	private IApplyProductDetailService applyProductDetailService;
	@Autowired
	private IApplyImportDetailService applyImportDetailService;
	@Autowired
	private IBsKeySequenceService bsKeySequenceService;
	@Autowired
	private ICtrContractService ctrContractService;
	@Autowired
	private CtrProductDao ctrProductDao;
	@Autowired
	private StockDetailFacade stockDetailFacade;
	@Autowired
	private ICtrContractTextService ctrContractTextService;
	@Autowired
	private StockContractDao stockContractDao;
	@Autowired
	private CtrContractRelaDao contractRelaDao;
	@Autowired
	private ICtrContractRelaService contractRelaService;
	@Autowired
	private ICtrContractOphisService ctrContractOphisService;
	
	@Override
	public BaseDao<ApplyCalculate> getBaseDao() {
		return applyCalculateDao;
	}
	
	@Override
	public Class<ApplyCalculate> getEntityClazz() {
		return ApplyCalculate.class;
	}
	
	/**
	 * 保存二次结算申请明细
	 */
	@Override
	@ServerTransactional
	public void saveDetail(List<ApplyCalculateDetailVo> detailList) throws ApplicationException {
		if (detailList.isEmpty()) {
			return;
		}
		//生成二次结算单号
		Long enterpriseId = detailList.get(0).getEnterpriseId();
		String nextKey = bsKeySequenceService.getNextKey(BasConstants.KEYSEQUENCE_CALCULATE_NO, enterpriseId);
		for (ApplyCalculateDetailVo detailVo : detailList) {
			Long contractId = detailVo.getContractId();
			CtrContract contract = ctrContractService.getEntity(contractId);
			String contractNo = detailVo.getContractNo();
			Long bizUserId = detailVo.getBizUserId();
			String bizUserName = detailVo.getBizUserName();
			List<ApplyProductDetail> productDetail = detailVo.getLstUpdate();
			BigDecimal updateDealNumber = BigDecimal.ZERO;
			BigDecimal updateDealAmount = BigDecimal.ZERO;
			for (ApplyProductDetail applyProductDetail : productDetail) {
				Long id = applyProductDetail.getId();
				ApplyProductDetail entity = applyProductDetailService.getEntity(id);
				if (entity != null) {
					//原明细数量单价
					BigDecimal oldDealNumber = entity.getDealNumber();
					BigDecimal oldDealPrice = entity.getDealPrice();
					//二次结算差值
					BigDecimal dealNumber = applyProductDetail.getDealNumber().subtract(oldDealNumber);
					BigDecimal dealPrice = applyProductDetail.getDealPrice().subtract(oldDealPrice);
					updateDealNumber = updateDealNumber.add(dealNumber);
					updateDealAmount = updateDealAmount.add(dealPrice);
					
					Long oldProductDetailId = applyProductDetail.getId();
					//保存二次结算明细
					applyProductDetail.setId(null);
					applyProductDetail.setApplyType(BasConstants.APPLY_TYPE_CC);
					ApplyProductDetail save = applyProductDetailService.save(applyProductDetail);
					Long newProductDetailId = save.getId();
					
					//保存二次结算申请明细
					ApplyCalculate calculate = new ApplyCalculate();
					calculate.setContractId(contractId);
					calculate.setContractNo(contractNo);
					calculate.setOldProductDetailId(oldProductDetailId);
					calculate.setNewProductDetailId(newProductDetailId);
					calculate.setDealNumber(dealNumber);
					calculate.setDealPrice(dealPrice);
					calculate.setBizUserId(bizUserId);
					calculate.setBizUserName(bizUserName);
					calculate.setEnterpriseId(entity.getEnterpriseId());
					calculate.setStatus(BasConstants.CONTRACTADJUSTDETAILTYPE_N);
					calculate.setCalculateNo(nextKey);
					calculate.setImportDetailId(detailVo.getId());
					calculate.setPayBondAmount(detailVo.getPayBondAmount());
					calculate.setWarehouseCost(detailVo.getWarehouseCost());
					calculate.setTransportCost(detailVo.getTransportCost());
					calculate.setQingguanFee(detailVo.getQingguanFee());
					calculate.setKaizhengFee(detailVo.getKaizhengFee());
					calculate.setPayBondAmount(detailVo.getPayBondAmount());
					calculate.setWarehouseCost(detailVo.getWarehouseCost());
					calculate.setTransportCost(detailVo.getTransportCost());
					calculate.setQingguanFee(detailVo.getQingguanFee());
					calculate.setKaizhengFee(detailVo.getKaizhengFee());
					calculate.setChengduiFee(detailVo.getChengduiFee());
					calculate.setArrivalTime(detailVo.getArrivalTime());
					calculate.setPayBondTime(detailVo.getPayBondTime());
					calculate.setPayFullTime(detailVo.getPayFullTime());
					if (detailVo.getReceiveBondTime() != null) {
						calculate.setPayBondTime(detailVo.getReceiveBondTime());
					}
					if (detailVo.getReceiveFullTime() != null) {
						calculate.setPayFullTime(detailVo.getReceiveFullTime());
					}
					//calculate.setReceiveBondTime(detailVo.getReceiveBondTime());
					//calculate.setReceiveFullTime(detailVo.getReceiveFullTime());
					calculate.setDailiFee(detailVo.getDailiFee());
					this.save(calculate);
				}
			}
			dealWithAdjust(contract,updateDealNumber,updateDealAmount);
		}
	}
	
	/**
	 * 二次结算审核
	 * 
	 */
	@Override
	@ServerTransactional
	public void doCalculate(ApplyCalculateFlowVo flowVo) throws ApplicationException {
		String calculateNo = flowVo.getCalculateNo();
		String status = flowVo.getStatus();
		List<ApplyCalculate> calculateList = applyCalculateDao.findByCalculateNo(calculateNo);
		for (ApplyCalculate applyCalculate : calculateList) {
			if (StringUtils.equals(BasConstants.APPROVE_STATUS_C, status)) {
				ApplyProductDetail newProductDetail = applyProductDetailService.getEntity(applyCalculate.getNewProductDetailId());
				applyCalculate.setStatus(BasConstants.APPROVE_STATUS_C);
				applyProductDetailService.delete(newProductDetail.getId());
			}else {
				Long contractId = applyCalculate.getContractId();
				CtrContract contract = ctrContractService.getEntity(contractId);
				String contractType = contract.getContractType();
				if (contract.getContractStatus().equals(BasConstants.CONTRACTSTATUS_C)) {
					throw new ApplicationException("该合同已作废，不能二次结算");
				}
				//新货物明细
				ApplyProductDetail newProductDetail = applyProductDetailService.getEntity(applyCalculate.getNewProductDetailId());
				//旧货物明细
				ApplyProductDetail oldProductDetail = applyProductDetailService.getEntity(applyCalculate.getOldProductDetailId());
				CtrProduct product = ctrProductDao.findOne(newProductDetail.getCtrProductId());
				ApplyContractAdjustRequestVo vo = new ApplyContractAdjustRequestVo();
				vo.setContractId(contract.getId());
				vo.setContractNo(contract.getContractNo());
				vo.setUserId(applyCalculate.getBizUserId());
				vo.setUserName(applyCalculate.getBizUserName());
				vo.setContractType(contract.getContractType());
				if (StringUtils.equals(contractType, BasConstants.CONTRACT_TYPE_B)) {
					//采购合同调整
					//vo.setApplyId(contractAdjust.getId());
					
					if(product.getWarehouseNumber().compareTo(newProductDetail.getDealNumber())>0){
						logger.info("二次结算,ctrProductId:{},deliveryInNumber:{},newDealNumber:{}", newProductDetail.getCtrProductId(),
								product.getWarehouseNumber(), newProductDetail.getDealNumber());
						throw new ApplicationException("二次结算后的明细数量不能小于已出/入库数量，已出/入库数量为：" + product.getWarehouseNumber() + "吨！");
					}
					
					vo.setOldDealNumber(product.getDealNumber());
					product.setDealNumber(newProductDetail.getDealNumber());
					product.setDealPrice(newProductDetail.getDealPrice());
					product.setTotalPrice(newProductDetail.getTotalPrice());
					ctrProductDao.save(product);
					
					vo.setOldStockContractId(product.getStockContractId());
					vo.setNewStockContractId(product.getStockContractId());
					vo.setWarehouseName(product.getWarehouseName());
					vo.setNewDealNumber(newProductDetail.getDealNumber());
					vo.setDealPrice(newProductDetail.getDealPrice());
					vo.setCtrProductId(product.getId());
					stockDetailFacade.updateByContractAdjust(vo);
					
					StockContract stockContract = stockContractDao.findOne(product.getStockContractId());
					if (stockContract != null) {
						stockContract.setBuyNumber(product.getDealNumber());
						stockContract.setSellNumber(product.getDealNumber());
						stockContractDao.save(stockContract);
					}
				}else {
					//销售合同调整
					product.setDealNumber(newProductDetail.getDealNumber());
					product.setDealPrice(newProductDetail.getDealPrice());
					product.setTotalPrice(newProductDetail.getTotalPrice());

					vo.setOldDealNumber(product.getDealNumber());
					vo.setNewDealNumber(newProductDetail.getDealNumber());
					vo.setNewStockContractId(product.getStockContractId());
					vo.setOldStockContractId(product.getStockContractId());
					vo.setCtrProductId(product.getId());
					vo.setDealNumber(product.getDealNumber());
					
					CtrContractRela ctrRelaOld = contractRelaDao.findBySellProductIdAndStockContractId(product.getId(), product.getStockContractId());
					if (ctrRelaOld!=null) {
						contractRelaDao.delete(ctrRelaOld);
					}
					contractRelaService.saveCtrContractReal(product.getStockContractId(),product,product.getDealNumber());

					// 未重新选择货源
					vo.setDealNumber(vo.getNewDealNumber().subtract(vo.getOldDealNumber()));
					stockDetailFacade.updateByContractAdjust(vo);
					ctrProductDao.save(product);
						
						
				}
				List<CtrProduct> productList = ctrProductDao.findByCtrContractId(contract.getId());
				BigDecimal dealAmount = BigDecimal.ZERO;
				BigDecimal dealNumber = BigDecimal.ZERO;
				for (CtrProduct ctrProduct : productList) {
					dealAmount = dealAmount.add(ctrProduct.getDealPrice().multiply(ctrProduct.getDealNumber()));
					dealNumber = dealNumber.add(ctrProduct.getDealNumber());
				}
				contract.setTotalAmount(dealAmount.setScale(2,BigDecimal.ROUND_HALF_UP));
				contract.setTotalNumber(dealNumber);
				//contract.setBondRate(dealAmount.divide(dealNumber, 4, BigDecimal.ROUND_HALF_UP));
				if(null != applyCalculate.getImportDetailId() && StringUtils.equals(BasConstants.BUSINESS_TYPE_DL_KZ, contract.getBusinessType())){
					ApplyImportDetail importDetail = applyImportDetailService.getEntity(applyCalculate.getImportDetailId());
					//修改合同二次结算内容
					contract.setWarehouseAmount(applyCalculate.getWarehouseCost());
					contract.setTransportAmount(applyCalculate.getTransportCost());
					contract.setDeliveryDateTo(applyCalculate.getArrivalTime()); //交收货日期
					if (applyCalculate.getPayBondAmount() != null) {
						contract.setBondAmount(applyCalculate.getPayBondAmount());
					}
					contract.setPayBondTime(applyCalculate.getPayBondTime());
					contract.setPayFullTime(applyCalculate.getPayFullTime());
					//修改申请单
					importDetail.setWarehouseCost(applyCalculate.getWarehouseCost());
					importDetail.setTransportCost(applyCalculate.getTransportCost());
					importDetail.setQingguanFee(applyCalculate.getQingguanFee());
					importDetail.setArrivalTime(applyCalculate.getArrivalTime());
					if (StringUtils.equals(BasConstants.CONTRACTTYPE_BUY, importDetail.getContractType())) {
						importDetail.setPayBondAmount(applyCalculate.getPayBondAmount());
						importDetail.setKaizhengFee(applyCalculate.getKaizhengFee());
						importDetail.setChengduiFee(applyCalculate.getChengduiFee());
						importDetail.setPayBondTime(applyCalculate.getPayBondTime());
						importDetail.setPayFullTime(applyCalculate.getPayFullTime());
					}else {
						importDetail.setReceiveBondTime(applyCalculate.getPayBondTime());
						importDetail.setReceiveFullTime(applyCalculate.getPayFullTime());
						importDetail.setDailiFee(applyCalculate.getDailiFee());
					}
					applyImportDetailService.save(importDetail);
				}
				ctrContractService.save(contract);
				saveCalculateHis(contractId,calculateNo,flowVo);
				//修改二次结算申请单状态为已审核
				applyCalculate.setStatus(BasConstants.APPROVE_STATUS_D);
				//修改货物明细数量
				oldProductDetail.setDealNumber(newProductDetail.getDealNumber());
				oldProductDetail.setDealPrice(newProductDetail.getDealPrice());
				oldProductDetail.setTotalPrice(newProductDetail.getTotalPrice());
				applyProductDetailService.save(oldProductDetail);
				applyProductDetailService.delete(newProductDetail.getId());
				// 重新生成电子合同
				saveContractText(contract,productList);
			}

		}
	}
	
	private void dealWithAdjust(CtrContract contract,BigDecimal dealNumber,BigDecimal dealAmount) throws ApplicationException {
		Long contractId = contract.getId();
		BigDecimal totalNumber = contract.getTotalNumber();
		BigDecimal totalAmount = contract.getTotalAmount();
		dealNumber = totalNumber.add(dealNumber);
		dealAmount = totalAmount.add(dealAmount);
		BigDecimal warehouseNumber = contract.getWarehouseNumber();
		if (contract.getContractType().equals(BasConstants.CONTRACT_TYPE_B)) {
			//若该采购合同存在入库，则调整后的合同数量不能小于入库数量
			if (warehouseNumber.compareTo(dealNumber) > 0) {
				throw new ApplicationException("调整后的合同数量不能小于已入库数量，已入库数量为：" + warehouseNumber + "吨！");
			}
			//若该采购合同存在销售，调整后的合同数量不能小于已销售数量
			//获取总已销数量	
			List<StockContract> lstStockContract = stockContractDao.findByBuyContractId(contractId);
//			List<StockDetail> stockDetailList = stockDetailDao.findByBuyContractId(contractId+"");
			BigDecimal totalSelledNumber = BigDecimal.ZERO;
			for (StockContract stockContract : lstStockContract) {
				//已销数量
				BigDecimal selledNumber = stockContract.getSellNumber().add(stockContract.getSellingNumber());
				totalSelledNumber = totalSelledNumber.add(selledNumber);
			}
//			if (totalSelledNumber.compareTo(dealNumber) > 0) {
//				throw new ApplicationException("调整后的合同数量不能小于已销售数量，已销售数量为：" + totalSelledNumber + "吨！");
//			}
			if (contract.findRealDealedAmount().compareTo(dealAmount) > 0) {
				throw new ApplicationException("调整后的合同总价不能小于已付款金额，已付款金额为：" + contract.findRealDealedAmount() + "元！");
			}
			
		} else {
			if (warehouseNumber.compareTo(dealNumber) > 0) {
				throw new ApplicationException("调整后的合同数量不能小于已出库数量，已出库数量为：" + warehouseNumber + "吨！");
			}
			if (contract.findRealDealedAmount().compareTo(dealAmount) > 0) {
				throw new ApplicationException("调整后的合同总价不能小于已收款金额，已收款金额为：" + contract.findRealDealedAmount() + "元！");
			}
		}
	}
	
	/**
	 * 保存二次结算合同操作记录
	 * @param ctrContractId
	 * @param calculateNo
	 */
	private void saveCalculateHis(Long ctrContractId, String calculateNo, ApplyCalculateFlowVo flowVo) {
		List<ApplyCalculate> calculateList = applyCalculateDao.findByCalculateNoAndContractId(calculateNo,
				ctrContractId);
		CtrContract entity = ctrContractService.getEntity(ctrContractId);
		StringBuffer oldData = new StringBuffer("");
		StringBuffer newData = new StringBuffer("");
		Long createUserId = 0L;
		String createUserName = "";
		for (ApplyCalculate calculate : calculateList) {
			createUserId = calculate.getBizUserId();
			createUserName = calculate.getBizUserName();
			Long oldProductDetailId = calculate.getOldProductDetailId();
			Long newProductDetailId = calculate.getNewProductDetailId();
			ApplyProductDetail oldDetail = applyProductDetailService.getEntity(oldProductDetailId);
			ApplyProductDetail newDetail = applyProductDetailService.getEntity(newProductDetailId);
			if (newDetail == null) {
				return;
			}
			oldData.append(oldDetail.getProductName() + "/" + oldDetail.getBrandNumber() + "/"
					+ oldDetail.getDealPrice() + "/" + oldDetail.getDealNumber() + ",");
			newData.append(newDetail.getProductName() + "/" + newDetail.getBrandNumber() + "/"
					+ newDetail.getDealPrice() + "/" + newDetail.getDealNumber() + ",");
		}
		String oldDataStr = oldData.toString();
		String newDataStr = newData.toString();
		String subject = String.format("%s %s", entity.getContractNo(),
				"[" + oldDataStr.substring(0, oldDataStr.length() - 1) + "]" + "-["
						+ newDataStr.substring(0, newDataStr.length() - 1) + "]");
		
		subject = subject + createUserName+"→"+flowVo.getApproveCurUserName();
		CtrContractOphisRequest request = new CtrContractOphisRequest();
		request.setCtrContractId(ctrContractId);
		request.setCancel(false);
		request.setApplyType(BasConstants.APPLY_TYPE_CC);
		request.setRemark(subject);
		request.setCreateUserId(createUserId);
		request.setCreateUserName(createUserName);
		ctrContractOphisService.addHis(request);
	}
	
	private void saveContractText(CtrContract entity, List<CtrProduct> lstProduct) {
		threadPool.execute(new Runnable() {

			@Override
			public void run() {
				// 生成电子合同
				try {
					ctrContractTextService.saveContractText(entity, lstProduct);
				} catch (ApplicationException e) {
					logger.error("生成电子合同异常", e);
				}
			}
		});

	}

	@Override
	public ApplyCalculate findByImportId(ApplyCalculate calculate) {
		List<ApplyCalculate> list = applyCalculateDao.findByImportId(calculate.getImportDetailId(), calculate.getCalculateNo());
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
}

