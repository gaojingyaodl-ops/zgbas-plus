package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyImportBuy;
import com.spt.bas.client.entity.ApplyProductDetail;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.util.ContractCfsUtil;
import com.spt.bas.client.vo.ApplyImportBuyVo;
import com.spt.bas.client.vo.ApplyProductDetailSaveVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.ctr.service.ICtrContractSaveService;
import com.spt.bas.server.dao.ApplyImportBuyDao;
import com.spt.bas.server.service.IApplyImportBuyService;
import com.spt.bas.server.service.IApplyProductDetailService;
import com.spt.bas.server.service.ICtrProductService;
import com.spt.bas.server.util.BasBusinessUtil;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IBsKeySequenceService;
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

@Component("applyImportBuyService")
@Transactional(readOnly = true)
public class ApplyImportBuyServiceImpl extends BaseService<ApplyImportBuy> implements IApplyImportBuyService,IPmService, IPmApproveListener {
	@Autowired
	private ApplyImportBuyDao applyImportBuyDao;
	@Autowired
	private IBsKeySequenceService keySequenceService;
	@Autowired
	private IApplyProductDetailService productDetailService;
	@Autowired
	private ICtrProductService  ctrProductService;
	@Autowired
	private ICtrContractSaveService ctrContractSaveService;
	
	@Override
	public BaseDao<ApplyImportBuy> getBaseDao() {
		return applyImportBuyDao;
	}
	
	@Override
	public Class<ApplyImportBuy> getEntityClazz() {
		return ApplyImportBuy.class;
	}
	
	@Override
	@ServerTransactional
	public void doStepIn(PmApprove approve) throws ApplicationException {
//		ApplyImportBuy buy = applyImportBuyDao.findOne(approve.getBizId());
//		if(buy.getApplyType().equals(BasConstants.APPLY_TYPE_RB)){
//			List<ApplyProductDetail> list = productDetailService.findApplyDetail(buy.getId(), BasConstants.APPLY_TYPE_RB);
//			for (ApplyProductDetail apd : list) {
//				StockDetailPresell presell = stockDetailPresellService.findByCtrProductId(apd.getCtrProductId());
//				presell.setApproveBuyNumber(presell.getApproveBuyNumber().add(apd.getDealNumber()));
//				stockDetailPresellService.save(presell);
//			}
//		}
		
	}
	
	@Override
	@ServerTransactional
	public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
		if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
			ApplyImportBuy buy = applyImportBuyDao.findOne(approve.getBizId());
			List<ApplyProductDetail> list = productDetailService.findApplyDetail(buy.getId(),buy.getApplyType());
			//保存合同主表
			CtrContract entity =  new CtrContract();
			BeanUtils.copyProperties(buy, entity);
			entity.setWarehouseAmount(buy.getWarehouseCost());
			entity.setContractType(BasConstants.CONTRACT_TYPE_B);

			entity.setContractStatus(BasConstants.CONTRACTSTATUS_S);
			entity.setContractAttr(BasConstants.DICT_TYPE_CONTRACTATTR_N);//现货
			entity.setSource(buy.getApplyType());
			entity.setDeliveryDateFrom(buy.getArrivalTime());
			entity.setDeliveryDateTo(buy.getArrivalTime());//到货时间
			entity.setTransportAmount(buy.getTransportCost());//运输费
			entity.setExtraTerm(buy.getPayCondition());
			entity.setBondAmount(buy.getPayBondAmount());
			if(StringUtils.isBlank(entity.getDeliveryAddr())){
				entity.setDeliveryAddr(buy.getObjectivePort());//配送地址
			}
			if(buy.getApplyType().equals(BasConstants.APPLY_TYPE_A)){
				Long sellProductId = list.get(0).getCtrProductId();
				CtrProduct product = ctrProductService.getEntity(sellProductId);
				entity.setLinkContractId(ContractCfsUtil.addContractId(product.getCtrContractId()));
			}
			
			entity = ctrContractSaveService.saveContract(entity, list, approve);
			
			//保存采购申请表中的合同Id
			buy.setContractId(entity.getId());
			buy = applyImportBuyDao.save(buy);
			
		}
		
	}

	@Override
	public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	@ServerTransactional
	public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
		ApplyImportBuy buy = null;
		ApplyProductDetailSaveVo vo = new ApplyProductDetailSaveVo();
		if(pmEntity instanceof ApplyImportBuyVo){
			ApplyImportBuyVo buyVo = (ApplyImportBuyVo) pmEntity;
			//新增进口采购申请
			buy = new ApplyImportBuy();
			BeanUtils.copyProperties(buyVo, buy);
			vo.setApplyType(buy.getApplyType());
			vo.setEnterpriseId(buyVo.getEnterpriseId());
			//生成合同号
			if(buy.getId()== null || buy.getId()==0){
				buy.setContractNo(BasBusinessUtil.composeContractNo(buyVo.getEnterpriseId(), buyVo.getDeptAbbr(), buyVo.getApplyType()));
			}
			buy = applyImportBuyDao.save(buy);
			//新增商品明细
			vo.setApplyId(buy.getId());
			List<ApplyProductDetail> productList = productDetailService.saveDetailBatch(buyVo.getLstInsert(), buyVo.getLstUpdate(), buyVo.getLstDelete(),vo);
//			if(productList==null||productList.size()<=0){
//				productList = productDetailService.findApplyDetail(buy.getId(), vo.getApplyType());
//			}else {
//				logger.warn("找不到商品明细,approveNo:{}",buy.getApproveNo());
//			}
			//计算合同总价及定金比率
			productList = productDetailService.findApplyDetail(buy.getId(), BasConstants.APPLY_TYPE_B);
			BigDecimal totalAmount = BigDecimal.ZERO;
			for (ApplyProductDetail product : productList) {
				totalAmount = totalAmount.add(product.getTotalPrice());
			}
			//BigDecimal bondRate = buy.getBondAmount().divide(totalAmount, 4, RoundingMode.HALF_UP);
			buy.setTotalAmount(totalAmount);
			//buy.setBondRate(bondRate);
			applyImportBuyDao.save(buy);
		}else{
			ApplyImportBuy entity = (ApplyImportBuy) pmEntity;
			buy = applyImportBuyDao.save(entity);
			//保存商品明细中企业id
			vo.setApplyId(buy.getId());
			vo.setEnterpriseId(buy.getEnterpriseId());
			vo.setApplyType(buy.getApplyType());
			productDetailService.saveBatchEnterpriseId(vo);
		}
		return buy;
	}

	@Override
	public String getSubject(IPmEntity pmEntity, PmProcess process) {
		if (pmEntity != null) {
			ApplyImportBuy vo = (ApplyImportBuy) pmEntity;
			List<ApplyProductDetail> list = productDetailService.findApplyDetail(vo.getId(),vo.getApplyType());
			StringBuffer productNameAndBrand = new StringBuffer("");
			BigDecimal totalPrice = BigDecimal.ZERO;
			for (ApplyProductDetail product : list) {
				String dealPrice = NumberUtil.formatNumber(product.getDealPrice(), "#.##");
				String dealNumber = NumberUtil.formatNumber(product.getDealNumber(), "#.###");
				String[] title=product.getProductCd().split("_");
				if(title[0].equals("SL")){
					productNameAndBrand.append(product.getProductName()+"/"+product.getBrandNumber()+"/"+dealPrice+"/"+dealNumber+",");					
				}else{
					productNameAndBrand.append(product.getProductName()+"/"+dealPrice+"/"+dealNumber+",");			
				}	
				totalPrice = totalPrice.add(product.getTotalPrice());
			}
			String productNameAndBrandStr = productNameAndBrand.toString();
			if (productNameAndBrand.length()>0) {
				productNameAndBrandStr = productNameAndBrand.substring(0, productNameAndBrand.length()-1);
			}
			//对方单位 品名 牌号 厂商 数量 合同金额
			String companyName = vo.getCompanyName();
			String totalPriceStr = NumberUtil.formatNumber(totalPrice, "#.##");
			String subject = String.format("%s %s %s",companyName, "["+productNameAndBrandStr+"]",totalPriceStr);
			
			return subject;
		}
		return null;
	}

	@Override
	@ServerTransactional
	public void updateFileId(Long id, String fileId) {
		applyImportBuyDao.updateFileId(id, fileId);
	}

	@Override
	public ApplyImportBuy findByContractId(Long contractId) {
		if (contractId != null) {
			ApplyImportBuy importBuy = applyImportBuyDao.findByContractId(contractId);
			return importBuy;
		}
		return null;
	}

	@Override
	@ServerTransactional
	public void updateApplyStatusC(Long id) {
		applyImportBuyDao.updateStatusC(id);
	}
}

