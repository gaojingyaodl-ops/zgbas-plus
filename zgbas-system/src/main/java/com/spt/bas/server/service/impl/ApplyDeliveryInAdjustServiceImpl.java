package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyDeliveryInAdjust;
import com.spt.bas.client.entity.ApplyProductDetail;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.vo.ApplyDeliveryInAdjustVo;
import com.spt.bas.client.vo.ApplyDeliveryInVo;
import com.spt.bas.client.vo.ApplyProductDetailSaveVo;
import com.spt.bas.client.vo.ApproveDealRequest;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.ApplyDeliveryInAdjustDao;
import com.spt.bas.server.dao.CtrProductDao;
import com.spt.bas.server.service.IApplyDeliveryInAdjustService;
import com.spt.bas.server.service.IApplyProductDetailService;
import com.spt.bas.server.service.IApproveDealService;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("ApplyDeliveryInAdjustService")
@Transactional(readOnly = true)
public class ApplyDeliveryInAdjustServiceImpl extends BaseService<ApplyDeliveryInAdjust> implements IApplyDeliveryInAdjustService ,IPmService, IPmApproveListener{
	@Autowired
	private ApplyDeliveryInAdjustDao applyDeliveryInAdjustDao;
	@Autowired
	private IApplyProductDetailService productDetailService;
	@Autowired
	private ICtrContractService ctrContractService;
	@Autowired
	private CtrProductDao productDao;
	@Autowired
	private IApproveDealService approveDealService; 
	
	
	@Override
	public BaseDao<ApplyDeliveryInAdjust> getBaseDao() {
		return applyDeliveryInAdjustDao;
	}
	
	@Override
	public Class<ApplyDeliveryInAdjust> getEntityClazz() {
		return ApplyDeliveryInAdjust.class;
	}

	@Override
	public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
//		ApplyDeliveryInAdjust entity = applyDeliveryInAdjustDao.findOne(approve.getBizId());
//		Long contractId = entity.getContractId();
		
		
	}

	@Override
	public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
		ApplyDeliveryInAdjust deliveryIn = null;
		ApplyProductDetailSaveVo vo = new ApplyProductDetailSaveVo();
		vo.setApplyType(BasConstants.APPLY_TYPE_K);
		if(pmEntity instanceof ApplyDeliveryInVo){
			ApplyDeliveryInAdjustVo inVo = (ApplyDeliveryInAdjustVo) pmEntity;
			//新增入库信息
			deliveryIn = new ApplyDeliveryInAdjust();
			BeanUtils.copyProperties(inVo, deliveryIn);
			deliveryIn = applyDeliveryInAdjustDao.save(deliveryIn);
			
			//新增商品明细
			vo.setApplyId(deliveryIn.getId());
			productDetailService.saveDetailBatch(inVo.getLstInsert(), inVo.getLstUpdate(), inVo.getLstDelete(), vo);
		}else{
			ApplyDeliveryInAdjust entity = (ApplyDeliveryInAdjust) pmEntity;
			deliveryIn = applyDeliveryInAdjustDao.save(entity);
			//保存商品明细中企业id
			vo.setApplyId(deliveryIn.getId());
			vo.setEnterpriseId(deliveryIn.getEnterpriseId());
			productDetailService.saveBatchEnterpriseId(vo);
		}
		return deliveryIn;
	}
	
	/**
	 * 发起审批，更新当前审批数量字段，作废原出库申请单
	 */
	@Override
	@ServerTransactional
	public void doStepIn(PmApprove approve) throws ApplicationException {
		
		ApplyDeliveryInAdjust entity = applyDeliveryInAdjustDao.findOne(approve.getBizId());
		Long contractId = entity.getContractId();
		Long processId=approve.getProcessId();
		List<CtrProduct> productList = productDao.findByCtrContractId(contractId);
		//该合同中总的审核中的数量
		BigDecimal curTotalNumber=BigDecimal.ZERO;
		for (CtrProduct product : productList) {
			BigDecimal remainNumber = product.getDealNumber().subtract(product.getWarehouseNumber().add(product.getCurApproveNumber()));
			if(remainNumber.compareTo(BigDecimal.ZERO)>0){
				//更新合同明细的已入库数量
				BigDecimal curNumber=BigDecimal.ZERO;
				Map<String,Object> params = new HashMap<String,Object>();
				params.put("EQL_applyId", entity.getId());
				params.put("EQS_applyType", BasConstants.APPLY_TYPE_I);
				params.put("EQS_productCd", product.getProductCd());
				params.put("EQL_factoryId", product.getFactoryId());
				params.put("EQS_brandNumber", product.getBrandNumber());
				ApplyProductDetail apd = productDetailService.findEntityByParam(params);
				curNumber=product.getCurApproveNumber().add(apd.getCurNumber());
				if (curNumber.compareTo(BigDecimal.ZERO) < 0) {
					curNumber = BigDecimal.ZERO;
				}
				product.setCurApproveNumber(curNumber);
				productDao.save(product);
				
				curTotalNumber = curTotalNumber.add(curNumber);
			}
		}
		
		//获得合同信息
		CtrContract contract=ctrContractService.getEntity(contractId);
		//获得该合同的数量
		BigDecimal totalNumber=contract.getTotalNumber();
		//获得实际入库数量
		BigDecimal warehouseNumber=	contract.getWarehouseNumber();
		//合同总数量等于 实际入库数量
		if(totalNumber.equals(warehouseNumber)){		//已经全部入库
			//删除对应的入库通知
			approveDealService.removeApproveDeal(processId,String.valueOf(contractId));
		}else{
			BigDecimal inNumber=warehouseNumber.add(curTotalNumber);
			if(inNumber.equals(totalNumber)){
				//删除对应的入库通知
				approveDealService.removeApproveDeal(processId,String.valueOf(contractId));
			}else{
				//仍有剩余入库数量时 需修改 摘要上的入库商量
				BigDecimal dealNumber=totalNumber.subtract(inNumber);

				ApproveDealRequest request=new ApproveDealRequest();
				request.setContractId(contractId);
				request.setDealType(BasConstants.APPLY_TYPE_I);
				request.setProcessId(processId);
				request.setTotalAmount(null);
				request.setTotalNumber(dealNumber);
				//修改摘要
				approveDealService.updateSubject(request);
			}
		}
	}

	@Override
	public String getSubject(IPmEntity pmEntity, PmProcess process) {
		if (pmEntity != null) {
			ApplyDeliveryInAdjust vo = (ApplyDeliveryInAdjust) pmEntity;
			List<ApplyProductDetail> list = productDetailService.findApplyDetail(vo.getId(), BasConstants.APPLY_TYPE_K);
			StringBuffer productNameAndBrand = new StringBuffer("");
			BigDecimal sumNumber = BigDecimal.ZERO;
			for (ApplyProductDetail applyProductDetail : list) {
				String realInNumber = NumberUtil.formatNumber(applyProductDetail.getCurNumber(), "#.###");
				String[] title=applyProductDetail.getProductCd().split("_");
				if(title[0].equals("SL")){
					productNameAndBrand.append(applyProductDetail.getProductName()+"/"+applyProductDetail.getBrandNumber()+"/"+applyProductDetail.getWarehouseName()+"/"+realInNumber+",");
				}else{
					productNameAndBrand.append(applyProductDetail.getProductName()+"/"+applyProductDetail.getWarehouseName()+"/"+realInNumber+",");
				}
				sumNumber = sumNumber.add(applyProductDetail.getDealNumber());
			}
			
			String companyName = vo.getCompanyName();
			String sumNumberStr = NumberUtil.formatNumber(sumNumber, "#.###");
			String productNameAndBrandStr = productNameAndBrand.toString();
			if (productNameAndBrand.length()>0) {
				productNameAndBrandStr = productNameAndBrand.substring(0, productNameAndBrand.length()-1);
			}
			String subject = String.format("%s %s %s", "["+productNameAndBrandStr+"]",companyName,sumNumberStr);
			return subject;
		}
		return null;
	}
	
}

