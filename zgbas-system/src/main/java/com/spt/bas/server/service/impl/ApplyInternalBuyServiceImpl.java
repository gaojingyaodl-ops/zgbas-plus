package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.ApplyInternalBuyVo;
import com.spt.bas.client.vo.ApplyProductDetailSaveVo;
import com.spt.bas.client.vo.BizUserInfor;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.ctr.service.ICtrContractSaveService;
import com.spt.bas.server.dao.ApplyInternalBuyDao;
import com.spt.bas.server.dao.ApplyInternalBuyDetailDao;
import com.spt.bas.server.dao.StockDetailDao;
import com.spt.bas.server.service.*;
import com.spt.bas.server.stock.service.StockDetailFacade;
import com.spt.bas.server.util.BasBusinessUtil;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.inter.IPmApproveListener;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.inter.IPmService;
import com.spt.pm.service.IPmApproveService;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Component("applyInternalBuyService")
@Transactional(readOnly = true)
public class ApplyInternalBuyServiceImpl extends BaseService<ApplyInternalBuy> implements IApplyInternalBuyService ,IPmService, IPmApproveListener {
	
	@Autowired
	private ApplyInternalBuyDao applyInternalBuyDao;
	@Autowired
	private IApplyInternalBuyDetailService internalBuyDetailService;
	@Autowired
	private IApplyProductDetailService applyProductDetailService;
	@Autowired
	private ApplyInternalBuyDetailDao ApplyInternalBuyDetailDao;
	@Autowired
	private StockDetailDao stockDetailDao;
	@Autowired
	private StockDetailFacade stockDetailFacade;
	@Autowired
	private IStockDetailService stockDetailService;
	@Autowired
	private IPmApproveService approveService;
	@Autowired
	private ICtrContractSaveService contractSaveService;
	@Autowired
	private ICtrContractService contractService;
	@Autowired
	private ICtrProductService ctrProductService;

	@Override
	public BaseDao<ApplyInternalBuy> getBaseDao() {
		return applyInternalBuyDao;
	}
	
	@Override
	public Class<ApplyInternalBuy> getEntityClazz() {
		return ApplyInternalBuy.class;
	}

	@Override
	@ServerTransactional
	public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
		if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
			ApplyInternalBuy internal = applyInternalBuyDao.findOne(approve.getBizId());
			//从A业务员的库存明细里减去，并新增一条B业务员的明细
			ApplyInternalBuyDetail  nDetail = ApplyInternalBuyDetailDao.findByApplyInternalBuyIdAndDetailType(internal.getId(), BasConstants.CONTRACTADJUSTDETAILTYPE_N);
			ApplyInternalBuyDetail  oDetail = ApplyInternalBuyDetailDao.findByApplyInternalBuyIdAndDetailType(internal.getId(), BasConstants.CONTRACTADJUSTDETAILTYPE_O);
			
			List<ApplyProductDetail> list = applyProductDetailService.findApplyDetail(nDetail.getId(),BasConstants.APPLY_TYPE_F);
			//新建内部交易合同
			BizUserInfor userInfo = new BizUserInfor();
			userInfo.setBizUserId(approve.getCreateUserId());
			userInfo.setBizUserName(approve.getCreateUserName());
			userInfo.setApproveNo(approve.getApproveNo());
			userInfo.setApproveId(approve.getId());
			StockDetail nstockDetail = stockDetailFacade.updataByInternalBuy(nDetail,userInfo);
			nDetail.setStockDetailId(nstockDetail.getId());
			nDetail.setShipperMatchUserId(nstockDetail.getBizUserId());
			nDetail.setShipperMatchUserName(nstockDetail.getBizUserName());
			ApplyInternalBuyDetailDao.save(nDetail);
			
			//旧的内部交易明细的库存明细ID查询库存明细
			StockDetail ostockDetail = stockDetailService.getEntity(oDetail.getStockDetailId());
			
			//旧的库存明细的合同ID查询旧的合同
			CtrContract oContract = contractService.getEntity(Long.parseLong(ostockDetail.getBuyContractId()));
			
			
			//保存合同主表
			CtrContract entity =  new CtrContract();
			entity.setApproveId(internal.getApproveId());
			entity.setBusinessNo(internal.getContractNo());
			entity.setContractNo(internal.getContractNo());
			entity.setEnterpriseId(internal.getEnterpriseId());
			entity.setTotalAmount(internal.getTotalAmount());
			entity.setTotalNumber(internal.getTotalNumber());
			entity.setMatchUserId(nstockDetail.getBizUserId());
			entity.setMatchUserName(nstockDetail.getBizUserName());
			entity.setBondRate(oContract.getBondAmount().divide(internal.getTotalAmount(),4, RoundingMode.HALF_UP));
			entity = contractSaveService.saveInterContract(entity,list,approve,oContract);
			
			internal.setContractId(entity.getId());
			applyInternalBuyDao.save(internal);
			
			//修改库存明细合同Id,商品Id,入库数量
			CtrProduct ctrProduct = ctrProductService.findByContractId(entity.getId()).get(0);
			nstockDetail.setBuyContractId(String.valueOf(entity.getId()));
			nstockDetail.setCtrProductId(ctrProduct.getId());
			nstockDetail.setDeliveryInNumber(BigDecimal.ZERO);
			stockDetailService.save(nstockDetail);
			
		}
	}

	@Override
	@ServerTransactional
	public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
		ApplyInternalBuy internalBuy = applyInternalBuyDao.findOne(vo.getBizId());
		List<ApplyInternalBuyDetail> detailList = ApplyInternalBuyDetailDao.findByApplyInternalBuyId(internalBuy.getId());
		StockDetail stockDetail = null;
		ApplyInternalBuyDetail odetail = null;
		for (ApplyInternalBuyDetail detail : detailList) {
			if(detail.getDetailType().equals(BasConstants.CONTRACTADJUSTDETAILTYPE_N)){
				stockDetail = stockDetailDao.findOne(detail.getStockDetailId());
			}else{
				odetail = detail;
			}
		}
		stockDetailFacade.doBackInternalBuy(stockDetail, odetail.getStockDetailId(), internalBuy.getId());
	}
	
	public Long getMatchUserId(IPmEntity pmEntity) {
		if(pmEntity instanceof ApplyInternalBuy){
			ApplyInternalBuy internal = (ApplyInternalBuy)pmEntity;
			ApplyInternalBuyDetail  oldDetail = ApplyInternalBuyDetailDao.findByApplyInternalBuyIdAndDetailType(internal.getId(), BasConstants.CONTRACTADJUSTDETAILTYPE_O);
			if (oldDetail!=null) {
				return oldDetail.getShipperMatchUserId();
			}
		}
		return null;
	};

	@Override
	@ServerTransactional
	public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
		ApplyInternalBuy internalBuy = null;
		ApplyProductDetailSaveVo vo = new ApplyProductDetailSaveVo();
		vo.setApplyType(BasConstants.APPLY_TYPE_F);
		if(pmEntity instanceof ApplyInternalBuyVo){
			ApplyInternalBuyVo interVo = (ApplyInternalBuyVo)pmEntity;
			
			if(interVo.getId() == 0){
				//原库存明细Id
				Long stockDetailId = interVo.getStockDetailId();
				StockDetail stockDetail = stockDetailDao.findOne(stockDetailId);
				
				internalBuy = new ApplyInternalBuy();
				internalBuy.setTotalAmount(interVo.getTotalAmount());
				internalBuy.setTotalNumber(interVo.getTotalNumber());
				internalBuy.setEnterpriseId(stockDetail.getEnterpriseId());
				//生成合同号
				if(internalBuy.getId()== null || internalBuy.getId()==0){
//					ComposeContractNoVo comVo= new ComposeContractNoVo();
//					comVo.setApplyType(vo.getApplyType());
//					comVo.setDeptAbbr(interVo.getDeptAbbr());
//					comVo.setEnterpriseId(interVo.getEnterpriseId());
//					String contractNo=approveService.composeContractNo(comVo);
					String contractNo= BasBusinessUtil.composeContractNo(interVo.getEnterpriseId(), interVo.getDeptAbbr(),vo.getApplyType());
					internalBuy.setContractNo(contractNo);
				}
				
				internalBuy = applyInternalBuyDao.save(internalBuy);
				
				//保存明细数据
				internalBuyDetailService.saveDetail(internalBuy.getId(), stockDetail,interVo);
			}else {
				internalBuy = applyInternalBuyDao.findOne(interVo.getId());
				internalBuy.setTotalAmount(interVo.getTotalAmount());
				internalBuy.setTotalNumber(interVo.getTotalNumber());
				internalBuy = applyInternalBuyDao.save(internalBuy);
				internalBuyDetailService.saveNewDetail(interVo.getId(), interVo);
			}
			
		}else if(pmEntity instanceof ApplyInternalBuy){
			internalBuy = (ApplyInternalBuy)pmEntity;
			internalBuy = applyInternalBuyDao.save(internalBuy);
		}
		return internalBuy;
	}

	@Override
	public String getSubject(IPmEntity pmEntity, PmProcess process) {
		if(pmEntity != null){
			ApplyInternalBuy vo = (ApplyInternalBuy)pmEntity;
			List<ApplyInternalBuyDetail> detailList = ApplyInternalBuyDetailDao.findByApplyInternalBuyId(vo.getId());
			StringBuffer oldData = new StringBuffer("");
			StringBuffer newData = new StringBuffer("");
			for (ApplyInternalBuyDetail detail : detailList) {
				if(detail.getDetailType().equals(BasConstants.CONTRACTADJUSTDETAILTYPE_O)){
					//旧数据
					List<ApplyProductDetail> olist = applyProductDetailService.findApplyDetail(detail.getId(),BasConstants.APPLY_TYPE_F);
					for (ApplyProductDetail apd : olist) {
						String dealPrice = NumberUtil.formatNumber(apd.getDealPrice(), "#.##");
						String dealNumber = NumberUtil.formatNumber(apd.getDealNumber(), "#.###");
						String[] title=apd.getProductCd().split("_");
						if(title[0].equals("SL")){
							oldData.append(apd.getProductName()+"/"+apd.getBrandNumber()+"/"+dealPrice+"/"+dealNumber+",");					
						}else{
							oldData.append(apd.getProductName()+"/"+dealPrice+"/"+dealNumber+",");			
						}	
					}
				}else{
					//新数据
					List<ApplyProductDetail> nlist = applyProductDetailService.findApplyDetail(detail.getId(),BasConstants.APPLY_TYPE_F);
					for (ApplyProductDetail apd : nlist) {
						String dealPrice = NumberUtil.formatNumber(apd.getDealPrice(), "#.##");
						String dealNumber = NumberUtil.formatNumber(apd.getDealNumber(), "#.###");
						String[] title=apd.getProductCd().split("_");
						if(title[0].equals("SL")){
							newData.append(apd.getProductName()+"/"+apd.getBrandNumber()+"/"+dealPrice+"/"+dealNumber+",");					
						}else{
							newData.append(apd.getProductName()+"/"+dealPrice+"/"+dealNumber+",");			
						}	
					}
				}
			}
			String oldDataStr = oldData.toString();
			String newDataStr = newData.toString();
			String subject = String.format("%s", "["+oldDataStr.substring(0, oldDataStr.length()-1)+"]"+"-["+newDataStr.substring(0, newDataStr.length()-1)+"]");
			return subject;
		}
		return null;
	}

	/**
	 * 需还原库存的两种情况：
	 * 1、未销售的，直接作废申请单
	 * 2、部分销售的，还原未销售出去的库存即可
	 * */
	@Override
	@ServerTransactional
	public void doBackInternalBuy() throws ApplicationException{
		//查询在今天产生的库存明细id
		Date endDate = new Date();
		Date startDate = DateOperator.addDays(endDate, -1);
		List<ApplyInternalBuy> list = applyInternalBuyDao.findByUpdatedDateAfterAndUpdatedDateBeforeAndStatus(startDate,endDate, BasConstants.APPROVE_STATUS_D);
		if(!list.isEmpty()){
			for (ApplyInternalBuy interBuy : list) {
				//判断新生成的明细有没有销售，库存可用数量<dealNumber
				ApplyInternalBuyDetail detail = ApplyInternalBuyDetailDao.findByApplyInternalBuyIdAndDetailType(interBuy.getId(),BasConstants.CONTRACTADJUSTDETAILTYPE_N);
				List<ApplyProductDetail> nlist = applyProductDetailService.findApplyDetail(detail.getId(),BasConstants.APPLY_TYPE_F);
				ApplyProductDetail productDetail = nlist.get(0);
				StockDetail stockDetail = stockDetailDao.findOne(detail.getStockDetailId());
				
				//部分销售
				if(productDetail.getDealNumber().compareTo(stockDetail.getAvailableNumber())>0 && stockDetail.getAvailableNumber().compareTo(BigDecimal.ZERO)>0 ){
					//获取原库存明细的id
					ApplyInternalBuyDetail odetail = ApplyInternalBuyDetailDao.findByApplyInternalBuyIdAndDetailType(interBuy.getId(),BasConstants.CONTRACTADJUSTDETAILTYPE_O);
					stockDetailFacade.doBackInternalBuy(stockDetail, odetail.getStockDetailId(), interBuy.getId());
				}
				//未销售
				if(productDetail.getDealNumber().compareTo(stockDetail.getAvailableNumber())==0){
					PmApproveWithdrawVo vo = new PmApproveWithdrawVo();
					vo.setApproveId(interBuy.getApproveId());
					vo.setBizId(interBuy.getId());
					approveService.doWithdraw(vo);
				}
			}
		}
		
	}

	@Override
	@ServerTransactional
	public void updateApplyStatus(Long id) {
		applyInternalBuyDao.updateApplyStatus(id);
	}

	@Override
	public ApplyInternalBuy findByContractId(Long contractId) {
		// TODO Auto-generated method stub
		return applyInternalBuyDao.findByContractId(contractId);
	}
}

