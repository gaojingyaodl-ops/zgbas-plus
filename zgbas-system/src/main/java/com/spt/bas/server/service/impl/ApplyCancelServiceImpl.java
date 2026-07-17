package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCancel;
import com.spt.bas.client.entity.ApplyCancelDetail;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.vo.ApplyCancelVo;
import com.spt.bas.client.vo.CtrConctractInvalidVo;
import com.spt.bas.client.vo.CtrContractOphisRequest;
import com.spt.bas.client.vo.StockDetailRequest;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.ctr.service.ICtrContractInvalidService;
import com.spt.bas.server.ctr.service.ICtrContractUpdateService;
import com.spt.bas.server.dao.ApplyCancelDao;
import com.spt.bas.server.dao.ApplyCancelDetailDao;
import com.spt.bas.server.service.*;
import com.spt.bas.server.stock.service.StockDetailFacade;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component("applyCancelService")
@Transactional(readOnly = true)
public class ApplyCancelServiceImpl extends BaseService<ApplyCancel>
		implements IApplyCancelService, IPmService, IPmApproveListener {
	@Autowired
	private ApplyCancelDao applyCancelDao;
	@Autowired
	private IApplyCancelDetailService applyCancelDetailService;
	@Autowired
	private ApplyCancelDetailDao applyCancelDetailDao;
	@Autowired
	private IPmApproveService pmApproveService;
	@Autowired
	private ICtrContractInvalidService contractInvalidService;
	@Autowired
	private ICtrProductService ctrProductService;
	@Autowired
	private StockDetailFacade stockDetailFacade;
	@Autowired
	private ICtrContractOphisService contractOphisService;
	@Autowired
	private ICtrContractUpdateService ctrContractUpdateService;
	@Autowired
	private ICtrContractService ctrContractService;

	@Override
	public BaseDao<ApplyCancel> getBaseDao() {
		return applyCancelDao;
	}

	@Override
	public Class<ApplyCancel> getEntityClazz() {
		return ApplyCancel.class;
	}

	@Override
	@ServerTransactional
	public void doStepIn(PmApprove approve) throws ApplicationException {
		 ApplyCancel cancel = applyCancelDao.findOne(approve.getBizId());
		 //作废申请重复发起限制
		 List<ApplyCancelDetail> cancelDetailList = applyCancelDetailDao.findByApplyCancelId(cancel.getId());
		 for (ApplyCancelDetail detail : cancelDetailList) {
			String oldApproveNo = detail.getOldApproveNo();
			if (StringUtils.isNotBlank(oldApproveNo)) {
				List<ApplyCancel> cancelList = applyCancelDao.findByOldApproveNo(oldApproveNo);
				if (cancelList != null && cancelList.size() > 0) {
					throw new ApplicationException("请勿重复申请 !!!");
				}
			}
		}
		//更新合同作废申请标识
		updateApplyCancelFlg(cancel,true);
	}

	@Override
	public void doStepFlow(PmApprove approve, PmApproveStep nextStep) throws ApplicationException {
		if (approve.getStatus().equals(BasConstants.APPROVE_STATUS_D)) {
			// 作废各种数据
			ApplyCancel cancel = applyCancelDao.findOne(approve.getBizId());
			List<ApplyCancelDetail> detailList = applyCancelDetailService.findByApplyCancelId(cancel.getId());
			String cancelType = cancel.getCancelType();
			if (StringUtils.equals(BasConstants.DICT_TYPE_CANCEL_CN, cancelType)) {
				// 合同作废
				String contractId = cancel.getContractId();
				CtrConctractInvalidVo vo = new CtrConctractInvalidVo();
				vo.setId(Long.parseLong(contractId));
				vo.setUserId(approve.getCreateUserId());
				vo.setUserName(approve.getCreateUserName());
				vo.setApproveId(approve.getId());
				contractInvalidService.invalidTheContract(vo);
			} else {
				for (ApplyCancelDetail detail : detailList) {
					Long oldApproveId = detail.getOldApproveId();
					//自动入库的入库作废
					if (oldApproveId==null || oldApproveId==0L && StringUtils.equals(BasConstants.APPLY_TYPE_I, cancelType)) {
						cancelDeliveryIn(Long.valueOf(cancel.getContractId()),approve);
					}else {
						PmApproveWithdrawVo vo=new PmApproveWithdrawVo();
						vo.setApproveId(oldApproveId);
						vo.setApproveIdNew(approve.getId());
						vo.setUserId(approve.getCreateUserId());
						vo.setUserName(approve.getCreateUserName());
						pmApproveService.doWithdraw(vo);
					}
				}

			}
		}

	}

	@Override
	public void doStepBack(PmApproveCurrVo approve, PmApproveStep nextStep) throws ApplicationException {
		ApplyCancel cancel = applyCancelDao.findOne(approve.getBizId());
		//更新合同作废申请标识
		updateApplyCancelFlg(cancel,false);
	}

	@Override
	public void doWithdraw(PmApproveWithdrawVo vo) throws ApplicationException {
		ApplyCancel cancel = applyCancelDao.findOne(vo.getBizId());
		//更新合同作废申请标识
		updateApplyCancelFlg(cancel,false);
	}

	@ServerTransactional
	@Override
	public IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException {
		ApplyCancel cancel = null;

		if (pmEntity instanceof ApplyCancelVo) {
			ApplyCancelVo cancelVo = (ApplyCancelVo) pmEntity;
			// 新增采购申请
			cancel = new ApplyCancel();
			BeanUtils.copyProperties(cancelVo, cancel);
			cancel = applyCancelDao.save(cancel);
			cancelVo.setId(cancel.getId());
			if (cancel.getContractId() != null) {
				CtrContract entity = ctrContractService.getEntity(Long.valueOf(cancel.getContractId()));
				cancel.setContractStatus(entity.getContractStatus());
			}
			//查询所有的明细，删除明细后新增
			List<ApplyCancelDetail> list = this.applyCancelDetailService.findByApplyCancelId(cancel.getId());
			if(list!=null&&list.size()>0){
				cancelVo.setLstDelete(list);
			}
			// 新增商品明细
			List<ApplyCancelDetail> productList = applyCancelDetailService.saveDetailBatch(cancelVo);
			if (productList == null || productList.size() <= 0) {
				productList = applyCancelDetailService.findByApplyCancelId(cancel.getId());
			} else {
				logger.warn("找不到商品明细,approveNo:{}", cancel.getApproveNo());
			}
		} else {
			/*
			 * ApplyCancel entity = (ApplyCancel) pmEntity; buy =
			 * applyCancelDao.save(entity); //保存商品明细中企业id
			 * vo.setApplyId(buy.getId());
			 * vo.setEnterpriseId(buy.getEnterpriseId());
			 * vo.setApplyType(buy.getApplyType());
			 * productDetailService.saveBatchEnterpriseId(vo);
			 */
		}
		return cancel;
	}

	@Override
	public String getSubject(IPmEntity pmEntity, PmProcess process) {
		if (pmEntity != null) {
			ApplyCancel vo = (ApplyCancel) pmEntity;
			List<ApplyCancelDetail> list = applyCancelDetailService.findByApplyCancelId(vo.getId());
			// StringBuffer productNameAndBrand = new StringBuffer("");
			BigDecimal totalNumber = BigDecimal.ZERO;
			String cancelType = vo.getCancelType();
			Boolean numFlg = false;
			if (StringUtils.equals(BasConstants.APPLY_TYPE_I, cancelType) || StringUtils.equals(BasConstants.APPLY_TYPE_O, cancelType)) {
				numFlg = true;
			}else {
				numFlg = false;
			}
			for (ApplyCancelDetail detail : list) {
				BigDecimal cancelAmount = detail.getCancelAmount();
				BigDecimal cancelNum = detail.getCancelNum();
				if (numFlg) {
					totalNumber = totalNumber.add(cancelNum);
				}else {
					totalNumber = totalNumber.add(cancelAmount);
				}
			}
			String subject = String.format("%s %s %s", vo.getContractNo(),
					"[" + DictUtil.getValue("cancelType", cancelType) + "]", totalNumber);
			return subject;
		}
		return null;
	}

	/**
	 * 合同作废业务，删除审批
	 */
	@Override
	@ServerTransactional
	public void delete(Long id) {
		applyCancelDetailDao.deleteByApplyCancelId(id);
		applyCancelDao.delete(id);
	}


	@Override
	public void updateFileId(Long id, String fileId) {
		applyCancelDao.updateFileId(id, fileId);
	}

	private void cancelDeliveryIn(Long contractId,PmApprove approve) throws ApplicationException {
		List<CtrProduct> productList = ctrProductService.findByContractId(contractId);
		for (CtrProduct product : productList) {
			BigDecimal warehouseNumber = product.getDealNumber();
			StockDetailRequest request = new StockDetailRequest();
			request.setApplyType(BasConstants.APPLY_TYPE_I);
			request.setCtrContractId(contractId);
			request.setBack(true);
			//request.setLinkDetailId(apd.getStockDetailId());
			request.setStockContractId(product.getStockContractId());
			//request.setApplyId(apd.getApplyId());
			//request.setWarehouseIdNew(apd.getWarehouseId());
			request.setWarehouseNameNew(product.getWarehouseName());
			request.setWarehouseName(product.getWarehouseName());
			request.setDealNumber(warehouseNumber);
			stockDetailFacade.saveDeliveryIn(request);

			ctrContractUpdateService.addWarehouseNumber(contractId, warehouseNumber.negate(),null, null);
			CtrContractOphisRequest requestOphis=new CtrContractOphisRequest();
			requestOphis.setApplyType(BasConstants.APPLY_TYPE_I);
			requestOphis.setCtrContractId(contractId);
			requestOphis.setCancel(true);
			requestOphis.setRemark(approve.getSubject());
			requestOphis.setCreateUserId(approve.getCreateUserId());
			requestOphis.setCreateUserName(approve.getCreateUserName());
			contractOphisService.addHis(requestOphis);
		}
	}

	private void updateApplyCancelFlg(ApplyCancel cancel,Boolean flg) throws ApplicationException {
		String cancelType = cancel.getCancelType();
		String contractId = cancel.getContractId();
		if (StringUtils.equals(BasConstants.DICT_TYPE_CANCEL_CN, cancelType)) {
			CtrContract ctrContract = ctrContractService.getEntity(Long.valueOf(contractId));
			ctrContract.setApplyCancelFlg(flg);
			ctrContractService.save(ctrContract);
		}
	}

}
