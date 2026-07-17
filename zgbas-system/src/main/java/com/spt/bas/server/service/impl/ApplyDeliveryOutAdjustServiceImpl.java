package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.*;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.ctr.service.ICtrContractUpdateService;
import com.spt.bas.server.dao.ApplyDeliveryDao;
import com.spt.bas.server.dao.ApplyDeliveryOutAdjustDao;
import com.spt.bas.server.dao.CtrProductDao;
import com.spt.bas.server.service.*;
import com.spt.bas.server.stock.service.StockDetailFacade;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component("applyDeliveryOutAdjustService")
@Transactional(readOnly = true)
public class ApplyDeliveryOutAdjustServiceImpl extends BaseService<ApplyDeliveryOutAdjust> implements IApplyDeliveryOutAdjustService,IPmService, IPmApproveListener {
	@Autowired
	private ApplyDeliveryOutAdjustDao applyDeliveryOutAdjustDao;
	@Autowired
	private CtrProductDao productDao;
	@Autowired
	private IApplyProductDetailService productDetailService;
//	@Autowired
//	private IStockService stockService;
	@Autowired
	private StockDetailFacade stockDetailFacade;
	@Autowired
	private IApplyDeliveryService applyDeliveryService;
	@Autowired
	private ICtrContractUpdateService contractUpdateService;
	@Autowired
	private ICtrContractService ctrContractService;
	@Autowired
	private ApplyDeliveryDao applyDeliveryDao;
	@Autowired
	private IApproveDealService approveDealService;
	@Autowired
	private IPmApproveService approveService;
	@Autowired
	private ICtrContractOphisService contractOphisService;
	@Override
	public BaseDao<ApplyDeliveryOutAdjust> getBaseDao() {
		return applyDeliveryOutAdjustDao;
	}
	
	@Override
	public Class<ApplyDeliveryOutAdjust> getEntityClazz() {
		return ApplyDeliveryOutAdjust.class;
	}

	@Override
	@ServerTransactional
	public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
		if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
			ApplyDeliveryOutAdjust entity = applyDeliveryOutAdjustDao.findOne(approve.getBizId());
			
			//作废调整前的出库申请及提货单
			doCancelOldApply(approve,entity.getOldApplyId());
			
			//更新合同明细及库存
			updateStockByStatus(approve,entity,BasConstants.APPROVE_STATUS_D);
			
			//生成提货单
			ApplyDeliveryOut out = new ApplyDeliveryOut();
			BeanUtils.copyProperties(entity, out);
			applyDeliveryService.insertDelivery(approve,out, BasConstants.APPLY_TYPE_Q);
		}	
		
	}

	@Override
	public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
		ApplyDeliveryOutAdjust entity = applyDeliveryOutAdjustDao.findOne(vo.getBizId());
		PmApprove approve = approveService.getEntity(entity.getApproveId());
		//更新合同明细及库存
		updateStockByStatus(approve,entity,BasConstants.APPROVE_STATUS_C);
	}

	@Override
	public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
		ApplyDeliveryOutAdjust out = null;
		ApplyProductDetailSaveVo saveVo = new ApplyProductDetailSaveVo();
		saveVo.setApplyType(BasConstants.APPLY_TYPE_Q);
		if(pmEntity instanceof ApplyDeliveryOutVo){
			ApplyDeliveryOutAdjustVo vo = (ApplyDeliveryOutAdjustVo) pmEntity;
			out = new ApplyDeliveryOutAdjust();
			BeanUtils.copyProperties(vo, out);
			out = applyDeliveryOutAdjustDao.save(out);
			saveVo.setApplyId(out.getId());
			//保存商品明细
			productDetailService.saveDetailBatch(vo.getLstInsert(), vo.getLstUpdate(), vo.getLstDelete(), saveVo);
		}else{
			ApplyDeliveryOutAdjust entity = (ApplyDeliveryOutAdjust) pmEntity;
			out = applyDeliveryOutAdjustDao.save(entity);
			saveVo.setApplyId(entity.getId());
			saveVo.setEnterpriseId(entity.getEnterpriseId());
			productDetailService.saveBatchEnterpriseId(saveVo);
		}
		return out;
	}
	
	@Override
	@ServerTransactional
	public void doStepIn(PmApprove approve) throws ApplicationException {
		updateCtrProductNumber(approve);
	}
	
	@Override
	public String getSubject(IPmEntity pmEntity, PmProcess process) {
		if (pmEntity != null) {
			ApplyDeliveryOutAdjust vo = (ApplyDeliveryOutAdjust) pmEntity;
			List<ApplyProductDetail> list = productDetailService.findApplyDetail(vo.getId(), BasConstants.APPLY_TYPE_Q);
			StringBuffer productNameAndBrand = new StringBuffer("");
			BigDecimal sumNumber = BigDecimal.ZERO;
			for (ApplyProductDetail applyProductDetail : list) {
				String realOutNumber = NumberUtil.formatNumber(applyProductDetail.getCurNumber(), "#.###");
				String[] title=applyProductDetail.getProductCd().split("_");
				if(title[0].equals("SL")){
					productNameAndBrand.append(applyProductDetail.getProductName()+"/"+applyProductDetail.getBrandNumber()+"/"+applyProductDetail.getWarehouseName()+"/"+realOutNumber+",");
				}else {
					productNameAndBrand.append(applyProductDetail.getProductName()+"/"+applyProductDetail.getWarehouseName()+"/"+realOutNumber+",");
				}
				sumNumber = sumNumber.add(applyProductDetail.getDealNumber());
			}
			String productNameAndBrandStr = productNameAndBrand.toString();
			if (productNameAndBrand.length()>0) {
				productNameAndBrandStr = productNameAndBrand.substring(0, productNameAndBrand.length()-1);
			}
			
			String companyName = vo.getCompanyName();
			String sumNumberStr = NumberUtil.formatNumber(sumNumber, "#.###");
			String subject = String.format("%s %s %s", "["+productNameAndBrandStr+"]",companyName,sumNumberStr);
			return subject;
		}
		return null;
	}
	
	/**
	 * 作废提货单
	 * */
	private void doCancelOldApply(PmApprove approve,Long applyDeliveryOutId) throws ApplicationException{
		List<ApplyDelivery> deliveryList = applyDeliveryDao.findByDeliveryOutApplyId(applyDeliveryOutId);
		for (ApplyDelivery applyDelivery : deliveryList) {
			ApplyDeliveryCancelVo cancelVo = new ApplyDeliveryCancelVo();
			cancelVo.setUserId(approve.getCreateUserId());
			cancelVo.setUserName(approve.getCreateUserName());
			cancelVo.setId(applyDelivery.getId());
			applyDeliveryService.doCancel(cancelVo);
		}
	}
	
	/**
	 *  根据审批状态更新合同明细中的审批中数量或已出库数量，更新待办事项
	 * */
	private List<ApplyProductDetail> updateCtrProductNumber(PmApprove approve){
		ApplyDeliveryOutAdjust entity = applyDeliveryOutAdjustDao.findOne(approve.getBizId());
		List<ApplyProductDetail> productList = productDetailService.findApplyDetail(entity.getId(), BasConstants.APPLY_TYPE_Q);
		//修改合同明细中的审批中数量
		BigDecimal curTotalNumber = BigDecimal.ZERO;
		String approveStatus = approve.getStatus();
		for (ApplyProductDetail product : productList) {
			//当前商品在审批中的数量
			BigDecimal curNumber = BigDecimal.ZERO;
			CtrProduct ctr = productDao.findOne(product.getCtrProductId());
			
			if(StringUtils.equals(approveStatus, BasConstants.APPROVE_STATUS_D)){
				curNumber = ctr.getCurApproveNumber().subtract(product.getCurNumber());
				ctr.setWarehouseNumber(ctr.getWarehouseNumber().add(product.getCurNumber()));
			}else if(StringUtils.equals(approveStatus, BasConstants.APPROVE_STATUS_A)){
				curNumber = ctr.getCurApproveNumber().add(product.getCurNumber());
			}else if(StringUtils.equals(approveStatus, BasConstants.APPROVE_STATUS_C)){
				curNumber = ctr.getCurApproveNumber().subtract(product.getCurNumber());
			}
			if (curNumber.compareTo(BigDecimal.ZERO) < 0) {
				curNumber = BigDecimal.ZERO;
			}
			ctr.setCurApproveNumber(curNumber);
			curTotalNumber = curTotalNumber.add(curNumber);
			productDao.save(ctr);
		}
		
		if(!approveStatus.equals(BasConstants.APPROVE_STATUS_D)){
			//修改待办事项
			Long contractId = entity.getContractId();
			Long processId=approve.getProcessId();
			CtrContract contract=ctrContractService.getEntity(contractId);
			//审批中和已出库的总和
			BigDecimal realTotalNumber = curTotalNumber.add(contract.getWarehouseNumber());
			BigDecimal contractNumber = contract.getTotalNumber();
			if(realTotalNumber.compareTo(contractNumber) >= 0){
				//删除对应的出库通知
				approveDealService.removeApproveDeal(processId,String.valueOf(contractId));
			}else{
				//仍有剩余出库数量时需修改摘要上的出库数量
				BigDecimal dealNumber=contractNumber.subtract(realTotalNumber);	
				ApproveDealRequest request=new ApproveDealRequest();
				request.setContractId(contractId);
				request.setDealType(BasConstants.APPLY_TYPE_O);
				request.setProcessId(processId);
				request.setTotalAmount(null);
				request.setTotalNumber(dealNumber);
				//修改摘要
				approveDealService.updateSubject(request);
			}
		}
		return productList;
	}
	
	/**
	 * 根据审批状态更新库存
	 * @throws ApplicationException 
	 * */
	private void updateStockByStatus(PmApprove approve,ApplyDeliveryOutAdjust entity,String status) throws ApplicationException {
		//更新合同明细
		List<ApplyProductDetail> productList = updateCtrProductNumber(approve);
		
		BizUserInfor userInfor = new BizUserInfor();
		userInfor.setBizUserId(approve.getCreateUserId());
		userInfor.setBizUserName(approve.getCreateUserName());
		userInfor.setApproveId(approve.getId());
		userInfor.setApproveNo(approve.getApproveNo());
		//更新库存数据
		//当前总的出库数量
		BigDecimal curRealOutNumber = BigDecimal.ZERO;
		for (ApplyProductDetail apd : productList) {
			curRealOutNumber = curRealOutNumber.add(apd.getCurNumber());
			CtrProduct product = productDao.findOne(apd.getCtrProductId());
//			StockRequest request = getStockRequest(product,apd,status);
			
			StockDetailRequest request = StockDetailRequest.build(product);
			request.setApplyType(BasConstants.APPLY_TYPE_O);
			if(status.equals(BasConstants.APPROVE_STATUS_C)){
				request.setBack(true);
			}
			request.setApplyId(approve.getId());
			request.setApproveId(approve.getId());
			request.setLinkDetailId(apd.getStockDetailId());
			request.setStockContractId(apd.getStockContractId());
			stockDetailFacade.saveDeliveryOut(request);
//			stockService.updateDeliveryStock(request,userInfor);
		}
		if(status.equals(BasConstants.APPROVE_STATUS_C)){
			curRealOutNumber = curRealOutNumber.negate();
		}
		contractUpdateService.addWarehouseNumber(entity.getContractId(), curRealOutNumber,approve.getApproveNo(), entity.getDeliveryDate());
		contractOphisService.addHis(BasConstants.APPLY_TYPE_Q, entity.getContractId(), approve,null);
	}
}

