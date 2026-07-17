package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.util.ContractCfsUtil;
import com.spt.bas.client.vo.ApplyPresellBuyVo;
import com.spt.bas.client.vo.ApplyProductDetailSaveVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.cache.BsDictUtil;
import com.spt.bas.server.ctr.service.ICtrContractSaveService;
import com.spt.bas.server.dao.ApplyBuyDao;
import com.spt.bas.server.service.*;
import com.spt.bas.server.util.BasBusinessUtil;
import com.spt.bas.server.util.RuleUtil;
import com.spt.bas.server.util.SubjectUtil;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("applyPresellBuyService")
@Transactional(readOnly = true)
public class ApplyPresellBuyServiceImpl extends BaseService<ApplyBuy>
		implements IApplyPresellBuyService, IPmService, IPmApproveListener {
	@Autowired
	private ApplyBuyDao applyBuyDao;
	@Autowired
	private IApplyProductDetailService productDetailService;
	@Autowired
	private ICtrProductService ctrProductService;
	@Autowired
	private ICtrContractSaveService contractSaveService;
	@Autowired
	private IStockDetailPresellService stockDetailPresellService;
	@Autowired
	private IBsCompanyAccountService bsCompanyAccountService;
	@Autowired
	private IApplyBuyService applyBuyService;
	
	@Override
	public BaseDao<ApplyBuy> getBaseDao() {
		return applyBuyDao;
	}
	
	@Override
	@ServerTransactional
	public void doStepIn(PmApprove approve) throws ApplicationException {
		ApplyBuy buy = applyBuyDao.findOne(approve.getBizId());
		List<ApplyProductDetail> list = productDetailService.findApplyDetail(buy.getId(), BasConstants.APPLY_TYPE_A);
		for (ApplyProductDetail apd : list) {
			StockDetailPresell presell = stockDetailPresellService.findByCtrProductId(apd.getCtrProductId());
			presell.setApproveBuyNumber(presell.getApproveBuyNumber().add(apd.getDealNumber()));
			stockDetailPresellService.save(presell);
		}
	}

	@Override
	public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
		if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
			ApplyBuy buy = applyBuyDao.findOne(approve.getBizId());
			List<ApplyProductDetail> list = productDetailService.findApplyDetail(buy.getId(),buy.getApplyType());
			//保存合同主表
			CtrContract entity =  new CtrContract();
			BeanUtils.copyProperties(buy, entity);
			entity.setTransportAmount(buy.getTransportAmount());
			entity.setWarehouseAmount(buy.getWarehouseAmount());
			entity.setContractType(BasConstants.CONTRACT_TYPE_B);

			entity.setContractStatus(BasConstants.CONTRACTSTATUS_S);
			entity.setSource(buy.getApplyType());
			entity.setDeliveryDateFrom(buy.getArrivalTime());
			entity.setDeliveryDateTo(buy.getArrivalTime());//到货时间
			entity.setPayMode(buy.getPayKind());
			entity.setAttachDeliveryTime(buy.getArrivalTimeExt());//交货补充时间
			String contractIds = null;
			for(ApplyProductDetail productDetail:list){
				CtrProduct product = ctrProductService.getEntity(productDetail.getCtrProductId());
				contractIds = ContractCfsUtil.addContractId(contractIds,product.getCtrContractId());
			}
			entity.setLinkContractId(contractIds);
			
			entity = contractSaveService.saveContract(entity, list, approve);
			
			//保存采购申请表中的合同Id
			buy.setContractId(entity.getId());
			buy = applyBuyDao.save(buy);
			
			//如果是来自Saas的订单则需要推送审批完成的信息
			/*String saasContractNo = buy.getSaasContractNo();
			if(StringUtils.isNotEmpty(saasContractNo)){
				SaasApproveStatusVo vo = new SaasApproveStatusVo();
				vo.setApproveNo(approve.getApproveNo());
				vo.setContractNo(saasContractNo);
				vo.setStatus(BasConstants.SAAS_STATUS_1);
				vo.setType(BasConstants.APPLY_TYPE_A);
				SaasApiCallUtil.doRequestSaas(vo,"/open/off/contract/advanceClinchDealRequest");
			}*/

			// 审批完成自动生成盖章申请
			applyBuyService.autoInitiatedSealUsage(buy, approve);
		}
		
	}
	
	@Override
	public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
		ApplyBuy buy = applyBuyDao.findOne(approve.getBizId());
		/*String saasContractNo = buy.getSaasContractNo();
		if(StringUtils.isNotEmpty(saasContractNo)){
			SaasApproveStatusVo vo = new SaasApproveStatusVo();
			vo.setApproveNo(approve.getApproveNo());
			vo.setContractNo(saasContractNo);
			vo.setStatus(BasConstants.SAAS_STATUS_0);
			vo.setType(BasConstants.APPLY_TYPE_B);
			SaasApiCallUtil.doRequestSaas(vo,"/open/off/contract/advanceClinchDealRequest");
		}*/
		List<ApplyProductDetail> list = productDetailService.findApplyDetail(buy.getId(), BasConstants.APPLY_TYPE_A);
		for (ApplyProductDetail apd : list){
			StockDetailPresell presell = stockDetailPresellService.findByCtrProductId(apd.getCtrProductId());
			presell.setApproveBuyNumber(presell.getApproveBuyNumber().subtract(apd.getDealNumber()));
			stockDetailPresellService.save(presell);
		}
	}

	@Override
	public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
		ApplyBuy buy = null;
		ApplyProductDetailSaveVo vo = new ApplyProductDetailSaveVo();
		vo.setApplyType(BasConstants.APPLY_TYPE_A);
		if(pmEntity instanceof ApplyPresellBuyVo){
			ApplyPresellBuyVo buyVo = (ApplyPresellBuyVo) pmEntity;
			//新增采购申请
			buy = new ApplyBuy();
			BeanUtils.copyProperties(buyVo, buy);
			vo.setApplyType(buy.getApplyType());
			//生成合同号
			if(buy.getId()== null || buy.getId()==0){
				String contractNo= BasBusinessUtil.composeContractNo(buyVo.getEnterpriseId(), buyVo.getDeptAbbr(),vo.getApplyType());
				buy.setContractNo(contractNo);
			}
			buy = applyBuyDao.save(mergePayKind(buy));
			//新增商品明细
			vo.setApplyId(buy.getId());
			List<ApplyProductDetail> productList = productDetailService.saveDetailBatch(buyVo.getLstInsert(), buyVo.getLstUpdate(), buyVo.getLstDelete(),vo);
//			if(productList==null||productList.size()<=0){
//				productList = productDetailService.findApplyDetail(buy.getId(), vo.getApplyType());
//			}else {
//				logger.warn("找不到商品明细,approveNo:{}",buy.getApproveNo());
//			}
			//计算合同总价及定金比率
			BigDecimal totalAmount = BigDecimal.ZERO;
			productList = productDetailService.findApplyDetail(buy.getId(), BasConstants.APPLY_TYPE_A);
			for (ApplyProductDetail product : productList) {
				totalAmount = totalAmount.add(product.getTotalPrice());
			}
			BigDecimal bondRate = buy.getBondRate();
			if (bondRate == null) {
				bondRate = BigDecimal.ZERO;
			}
			//定金
			BigDecimal bondAmount = totalAmount.multiply(bondRate);
			buy.setTotalAmount(totalAmount);
			buy.setBondRate(bondRate);
			buy.setBondAmount(bondAmount);
			if (StringUtils.equals(BasConstants.ATTACH_DELIVERY_TIME_K, buy.getArrivalTimeExt())) {
				buy.setArrivalTime(null);
			}
			applyBuyDao.save(buy);
		}else{
			ApplyBuy entity = (ApplyBuy) pmEntity;
			buy = applyBuyDao.save(entity);
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
			ApplyBuy vo = (ApplyBuy) pmEntity;
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
			String subject = String.format("%s %s %s",companyName, "["+productNameAndBrandStr+"]", SubjectUtil.formatMoney(totalPrice, RuleUtil.monetaryUnit));
			
			return subject;
		}
		return null;
	}
	
	private ApplyBuy mergePayKind(ApplyBuy buy) {
		try {
			//判断开票开户行是否存在 
			BsCompanyAccount account = new BsCompanyAccount();
			account.setCompanyId(buy.getCompanyId());
			account.setBankAccount(buy.getReceiveAccount());
			account.setBankName(buy.getReceiveBank());
			//account.setTaxNo(entity.getTaxNo());
			account.setEnterpriseId(buy.getEnterpriseId());
			bsCompanyAccountService.verifyCompanyAccount(account);
			// 转换付款方式
			String deliveryMode = buy.getDeliveryMode();
			// 获取付款方式选项值
			Map<Integer, String> map = new HashMap<Integer, String>();
			List<BsDictData> bsDictData = BsDictUtil.getListByCategory(buy.getEnterpriseId(), BasConstants.BSDICT_BUY_PAYMODE);
			if(!bsDictData.isEmpty()) {
				for (int i = 0; i < bsDictData.size(); i++) {
					String dictName = bsDictData.get(i).getDictName();
					map.put(i, dictName);
				}
			}
			// 合并参数
			Object[] array = new Object[3];
			//BsCompanyAccount companyAccount = bsCompanyAccountDao.findDefaultAccount(buy.getCompanyId(), buy.getEnterpriseId());
			String remark = "(账号未知，等待启用后填入)";
			String bankAccount = buy.getReceiveAccount();//companyAccount.getBankAccount();
			String bankName = buy.getReceiveBank();//companyAccount.getBankName();
			BigDecimal bondAmount = buy.getBondAmount();
			int pay_kind = 0;
			if (StringUtils.isNotBlank(bankName)) {
				remark = "("+bankName+"，账号："+bankAccount+")";
			}
			if (StringUtils.equals(BasConstants.DELIVERY_MODE_XHHK, deliveryMode)) {// 全款
				array[0] = remark;
				pay_kind = 0;
			} else if (StringUtils.equals(BasConstants.DELIVERY_MODE_XKHH, deliveryMode)
					&& bondAmount.compareTo(BigDecimal.ZERO) == 0) {
				Date payFullTime = buy.getPayFullTime();
				String dateStr = DateOperator.formatDate(payFullTime, "yyyy年MM月dd日");
				array[0] = dateStr;
				array[1] = remark;
				pay_kind = 1;
			} else if (StringUtils.equals(BasConstants.DELIVERY_MODE_XKHH, deliveryMode)
					&& bondAmount.compareTo(BigDecimal.ZERO) > 0) {
				Date payBondTime = buy.getPayBondTime();
				String dateStr = DateOperator.formatDate(payBondTime, "yyyy年MM月dd日");
				array[0] = dateStr;
				array[1] = bondAmount;
				array[2] = remark;
				pay_kind = 2;
			} else if (bondAmount.compareTo(BigDecimal.ZERO) > 0) {
				Date payBondTime = buy.getPayBondTime();
				String dateStr = DateOperator.formatDate(payBondTime, "yyyy年MM月dd日");
				array[0] = dateStr;
				array[1] = bondAmount;
				array[2] = remark;
				pay_kind = 2;
			} else {
				Date payFullTime = buy.getPayFullTime();
				String dateStr = DateOperator.formatDate(payFullTime, "yyyy年MM月dd日");
				array[0] = dateStr;
				array[1] = remark;
				pay_kind = 1;
			}
			String mode = MessageFormat.format(map.get(pay_kind), array);
			buy.setPayKind(mode);
			if (bondAmount.compareTo(BigDecimal.ZERO) == 0) {
				buy.setPayBondTime(null);
			}
		} catch (Exception e) {
			logger.error("参数合并异常", e);
		}
		return buy;
		
	}

}
