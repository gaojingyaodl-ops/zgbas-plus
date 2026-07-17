package com.spt.bas.server.service.impl;

import java.util.List;

import com.spt.tools.core.exception.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyInternalBuyDetail;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.vo.ApplyInternalBuyVo;
import com.spt.bas.client.vo.ApplyProductDetailSaveVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.ApplyInternalBuyDetailDao;
import com.spt.bas.server.service.IApplyInternalBuyDetailService;
import com.spt.bas.server.service.IApplyProductDetailService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class ApplyInternalBuyDetailServiceImpl extends BaseService<ApplyInternalBuyDetail> implements IApplyInternalBuyDetailService {
	@Autowired
	private ApplyInternalBuyDetailDao applyInternalBuyDetailDao;
	@Autowired
	private IApplyProductDetailService applyProductDetailService;
	
	@Override
	public BaseDao<ApplyInternalBuyDetail> getBaseDao() {
		return applyInternalBuyDetailDao;
	}
	
	@Override
	public Class<ApplyInternalBuyDetail> getEntityClazz() {
		return ApplyInternalBuyDetail.class;
	}

	@Override
	@ServerTransactional
	public void saveDetail(Long interId, StockDetail stockDetail,ApplyInternalBuyVo interVo) throws ApplicationException {
		Long enterpriseId = stockDetail.getEnterpriseId();
		ApplyInternalBuyDetail ointerDetail = new ApplyInternalBuyDetail();
		ointerDetail.setApplyInternalBuyId(interId);
		ointerDetail.setDetailType(BasConstants.CONTRACTADJUSTDETAILTYPE_O);
		ointerDetail.setEnterpriseId(enterpriseId);
		ointerDetail.setStockDetailId(stockDetail.getId());
		ointerDetail.setShipperMatchUserId(stockDetail.getBizUserId());
		ointerDetail.setShipperMatchUserName(stockDetail.getBizUserName());
		ointerDetail = applyInternalBuyDetailDao.save(ointerDetail);
		ApplyProductDetailSaveVo vo = new ApplyProductDetailSaveVo();
		vo.setApplyType(BasConstants.APPLY_TYPE_F);
		vo.setApplyId(ointerDetail.getId());
		//保存原库存商品明细
		applyProductDetailService.saveBystockDetail(vo, stockDetail.getId());
		
		ApplyInternalBuyDetail ninterDetail = new ApplyInternalBuyDetail();
		ninterDetail.setApplyInternalBuyId(interId);
		ninterDetail.setDetailType(BasConstants.CONTRACTADJUSTDETAILTYPE_N);
		ninterDetail.setEnterpriseId(enterpriseId);
		ninterDetail.setStockDetailId(stockDetail.getId());
		
		ninterDetail = applyInternalBuyDetailDao.save(ninterDetail);
		//保存交易商品明细
		vo.setApplyId(ninterDetail.getId());
		vo.setEnterpriseId(enterpriseId);
		applyProductDetailService.saveDetailBatch(interVo.getLstInsert(), interVo.getLstUpdate(), interVo.getLstDelete(), vo);
	}
	
	

	@Override
	public List<ApplyInternalBuyDetail> findByApplyInternalBuyId(Long interId) {
		// TODO Auto-generated method stub
		return applyInternalBuyDetailDao.findByApplyInternalBuyId(interId);
	}

	@Override
	public List<ApplyInternalBuyDetail> findByStockDetailId(Long stockDetailId) {
		// TODO Auto-generated method stub
		return applyInternalBuyDetailDao.findByStockDetailId(stockDetailId);
	}
	
	@Override
	@ServerTransactional
	public void saveNewDetail(Long interId,ApplyInternalBuyVo interVo) throws ApplicationException {
		ApplyInternalBuyDetail ninterDetail = applyInternalBuyDetailDao.findByApplyInternalBuyIdAndDetailType(interId, BasConstants.CONTRACTADJUSTDETAILTYPE_N);
		ApplyProductDetailSaveVo vo = new ApplyProductDetailSaveVo();
		vo.setApplyId(ninterDetail.getId());
		vo.setApplyType(BasConstants.APPLY_TYPE_F);
		vo.setEnterpriseId(ninterDetail.getEnterpriseId());
		applyProductDetailService.saveDetailBatch(interVo.getLstInsert(), interVo.getLstUpdate(), interVo.getLstDelete(), vo);
	}
	
}

