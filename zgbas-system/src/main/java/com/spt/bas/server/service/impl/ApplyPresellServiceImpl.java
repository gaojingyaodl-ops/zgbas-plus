package com.spt.bas.server.service.impl;

import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyPresell;
import com.spt.bas.client.entity.ApplyProductDetail;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.ApplyPresellVo;
import com.spt.bas.client.vo.ApplyProductDetailSaveVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.ctr.service.ICtrContractSaveService;
import com.spt.bas.server.dao.ApplyPresellDao;
import com.spt.bas.server.service.IApplyPresellService;
import com.spt.bas.server.service.IApplyProductDetailService;
import com.spt.bas.server.util.BasBusinessUtil;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.vo.PmApproveCurrVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("applyPresellService")
@Transactional(readOnly = true)
public class ApplyPresellServiceImpl extends BaseService<ApplyPresell> implements IApplyPresellService,IPmService, IPmApproveListener {
	@Autowired
	private ApplyPresellDao applyPresellDao;
	@Autowired
	private IApplyProductDetailService productDetailService;
	@Autowired
	private ICtrContractSaveService contractSaveService;
//	@Autowired
//	private IBsCompanyService bsCompanyService;
	
	@Value("${credit.contract.switch}")
	private Boolean creditSwitch;
	
	@Override
	public BaseDao<ApplyPresell> getBaseDao() {
		return applyPresellDao;
	}
	
	@Override
	public Class<ApplyPresell> getEntityClazz() {
		return ApplyPresell.class;
	}

	@Override
	@ServerTransactional
	public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
		if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
			ApplyPresell sell = applyPresellDao.findOne(approve.getBizId());
			List<ApplyProductDetail> list = productDetailService.findApplyDetail(sell.getId(),BasConstants.APPLY_TYPE_L);
			//保存合同主表
			CtrContract entity = new CtrContract();
			BeanUtils.copyProperties(sell, entity);
			entity.setSource(BasConstants.APPLY_TYPE_L);//预售
			entity.setContractType(BasConstants.CONTRACT_TYPE_S);
			entity.setBondRate(sell.getReceiveRate());
			entity.setBondAmount(sell.getContractAmount());
			entity.setDeliveryDateFrom(sell.getDeliveryTime());
			entity.setDeliveryDateTo(sell.getDeliveryTime());
			entity.setPayFullTime(sell.getReceiveTime());
			entity.setPayBondTime(sell.getReceiveBondTime());
			entity.setTransportAmount(sell.getTransportCost());
			entity.setWarehouseAmount(sell.getWarehouseCost());
			entity.setAttachDeliveryTime(sell.getArrivalTimeExt());
			entity.setPayType(sell.getReceiveType());
			entity.setPayMode(sell.getPayKind());
			entity.setDeliveryAddr(sell.getShippingAddr());
			//添加赊销合同标识
			String deliveryMode = sell.getDeliveryMode();
			if (StringUtils.equals(deliveryMode, BasConstants.DELIVERY_MODE_SX)) {
				entity.setCreditFlg(true);
			}
			entity = contractSaveService.saveContract(entity, list, approve);
			//保存采购申请表中的合同Id
			sell.setContractId(entity.getId());
			applyPresellDao.save(sell);
		}
	}

	@Override
	public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	@ServerTransactional
	public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
		ApplyPresell presell = null;
		ApplyProductDetailSaveVo vo = new ApplyProductDetailSaveVo();
		vo.setApplyType(BasConstants.APPLY_TYPE_L);
		if(pmEntity instanceof ApplyPresellVo){
			ApplyPresellVo presellVo = (ApplyPresellVo) pmEntity;
			presell = new ApplyPresell();
			BeanUtils.copyProperties(presellVo, presell);
			//生成合同号
			if(presell.getId()==0){
//				ComposeContractNoVo comVo= new ComposeContractNoVo();
//				comVo.setApplyType(vo.getApplyType());
//				comVo.setDeptAbbr(presellVo.getDeptAbbr());
//				comVo.setEnterpriseId(presellVo.getEnterpriseId());
//				String contractNo=pmApproveService.composeContractNo(comVo);
				String contractNo= BasBusinessUtil.composeContractNo(presellVo.getEnterpriseId(), presellVo.getDeptAbbr(),vo.getApplyType());
				
				presell.setContractNo(contractNo);
			}
			presell = applyPresellDao.save(mergePayModeXS(presell));
			//新增商品明细
			vo.setApplyId(presell.getId());
			List<ApplyProductDetail> productList = productDetailService.saveDetailBatch(presellVo.getLstInsert(), presellVo.getLstUpdate(), presellVo.getLstDelete(),vo);
//			if(productList==null||productList.size()<=0){
//				productList = productDetailService.findApplyDetail(presell.getId(), BasConstants.APPLY_TYPE_L);
//			}else {
//				logger.warn("找不到商品明细,approveNo:{}",presell.getApproveNo());
//			}
			//计算合同总价及定金比率
			BigDecimal totalAmount = BigDecimal.ZERO;
			productList = productDetailService.findApplyDetail(presell.getId(), BasConstants.APPLY_TYPE_L);
			for (ApplyProductDetail product : productList) {
				totalAmount = totalAmount.add(product.getTotalPrice());
			}
			BigDecimal receiveRate = presell.getReceiveRate();
			if (receiveRate == null) {
				receiveRate = BigDecimal.ZERO;
			}
			BigDecimal contractAmount = totalAmount.multiply(receiveRate);
			presell.setTotalAmount(totalAmount);
			presell.setReceiveRate(receiveRate);
			presell.setContractAmount(contractAmount);
			if (StringUtils.equals(BasConstants.ATTACH_DELIVERY_TIME_K, presell.getArrivalTimeExt())) {
				presell.setDeliveryTime(null);
			}
			applyPresellDao.save(presell);
		}else{
			ApplyPresell entity = (ApplyPresell) pmEntity;
			presell = applyPresellDao.save(entity);
			//保存商品明细中企业id
			vo.setApplyId(presell.getId());
			vo.setEnterpriseId(presell.getEnterpriseId());
			productDetailService.saveBatchEnterpriseId(vo);
		}
		return presell;
	}
	
	@Override
	@ServerTransactional
	public void doStepIn(PmApprove approve) throws ApplicationException {
//		if (creditSwitch != null && creditSwitch) {
//			ApplyPresell sell = applyPresellDao.findOne(approve.getBizId());
//			String deliveryMode = sell.getDeliveryMode();
//
//			// 判断企业授信额度 更新审批中的授信额度
//			BsCompany bsCompany = bsCompanyService.getCompanyCredit(sell.getCompanyId());
//			BigDecimal totalCredit = bsCompany.getTotalCreditAmount() == null ? BigDecimal.ZERO : bsCompany.getTotalCreditAmount();
////			BigDecimal usedCredit = bsCompany.getUsedCreditAmount() == null ? BigDecimal.ZERO : bsCompany.getUsedCreditAmount();
//			BigDecimal approveCredit = bsCompany.getApproveCreditAmount() == null ? BigDecimal.ZERO : bsCompany.getApproveCreditAmount();
////			BigDecimal remainAmount = totalCredit.subtract(usedCredit).subtract(approveCredit);
//			if (StringUtils.equals(deliveryMode, BasConstants.DELIVERY_MODE_SX)) {
////				if (remainAmount.compareTo(sell.getTotalAmount()) < 0) {
////					throw new ApplicationException("该企业剩余可用授信额度不足！！！");
////				}
//				if (totalCredit.compareTo(BigDecimal.ZERO)  <= 0) {
//					logger.error("totalCredit is zero！companyId：{} ", bsCompany.getId());
////					throw new ApplicationException("该企业未开通授信！！！");
//				}
//				BigDecimal realApproveCredit = approveCredit.add(sell.getTotalAmount());
//				bsCompany.setApproveCreditAmount(realApproveCredit);
//				bsCompanyService.save(bsCompany);
//			}
//		}

	}
	
	@Override
	@ServerTransactional
	public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
//		if (creditSwitch != null && creditSwitch) {
//			// 驳回
//			ApplyPresell sell = applyPresellDao.findOne(approve.getBizId());
//			String deliveryMode = sell.getDeliveryMode();
//			// 驳回 更新企业审批中授信额度
//			BsCompany bsCompany = bsCompanyService.getEntity(sell.getCompanyId());
//			BigDecimal approveCredit = bsCompany.getApproveCreditAmount() == null ? BigDecimal.ZERO : bsCompany.getApproveCreditAmount();
//			if (StringUtils.equals(deliveryMode, BasConstants.DELIVERY_MODE_SX)) {
//				BigDecimal realApproveCredit = approveCredit.subtract(sell.getTotalAmount());
//				bsCompany.setApproveCreditAmount(realApproveCredit);
//				bsCompanyService.save(bsCompany);
//			}
//		}
	}
	
	@Override
	public String getSubject(IPmEntity pmEntity, PmProcess process) {
		if (pmEntity != null) {
			ApplyPresell vo = (ApplyPresell) pmEntity;
			List<ApplyProductDetail> list = productDetailService.findApplyDetail(vo.getId(),BasConstants.APPLY_TYPE_L);
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
	public void updateApplyStatus(Long contractId) {
		applyPresellDao.updateApplyStatus(contractId);
	}

	@Override
	public ApplyPresell findByContractId(Long contractId) {
		return applyPresellDao.findByContractId(contractId);
	}

	@Override
	@ServerTransactional
	public void updateFileId(Long id, String fileId) {
		applyPresellDao.updateFileId(id, fileId);
	}
	private ApplyPresell mergePayModeXS(ApplyPresell sell) {
		try {
			// 获取付款方式选项值
			Map<Integer, String> map = new HashMap<Integer, String>();
			List<BsDictData> bsDictData = BsDictUtil.getListByCategory(sell.getEnterpriseId(), BasConstants.BSDICT_SELL_PAYMODE);
			if(!bsDictData.isEmpty()) {
				for (int i = 0; i < bsDictData.size(); i++) {
					String dictName = bsDictData.get(i).getDictName();
					map.put(i, dictName);
				}
			}
			// 合并参数
			Object[] array = new Object[3];
			BsDictData data = BsCompanyOurUtil.getCompanyOurToBsDictData(sell.getEnterpriseId(), sell.getOurCompanyName());
			sell.setOurCompanyName(data.getDictName());
			BigDecimal bondAmount = sell.getContractAmount();
			int pay_mode = 0;
			if(bondAmount.compareTo(BigDecimal.ZERO) == 0) {//全款
				Date receiveFullTime = sell.getReceiveTime();
				String dateStr = DateOperator.formatDate(receiveFullTime, "yyyy年MM月dd日");
				array[0] = dateStr;
				array[1] = data.getRemark();
				pay_mode = 0;
			}else {
				Date receiveBondTime = sell.getReceiveBondTime();
				String dateStr = DateOperator.formatDate(receiveBondTime, "yyyy年MM月dd日");
				array[0] = dateStr;
				array[1] = bondAmount;
				array[2] = data.getRemark();
				pay_mode = 1;
			}
			String mode = MessageFormat.format(map.get(pay_mode), array);
			if (bondAmount.compareTo(BigDecimal.ZERO) == 0) {
				sell.setReceiveBondTime(null);
			}
			sell.setPayKind(mode);
		} catch (Exception e) {
			logger.error("参数合并异常", e);
		}
		return sell;
	}
	
}

