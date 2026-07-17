package com.spt.bas.server.stock.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyInternalBuyDetail;
import com.spt.bas.client.entity.ApplyProductDetail;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.vo.BizUserInfor;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.StockDetailDao;
import com.spt.bas.server.service.IApplyProductDetailService;
import com.spt.bas.server.service.IStockDetailHisService;
import com.spt.bas.server.stock.service.IStockDetailInternalBuyService;
/** 内部采购 */
@Component
@Transactional(readOnly = true)
public class StockDetailInternalBuyServiceImpl implements IStockDetailInternalBuyService {
	@Autowired
	private StockDetailDao stockDetailDao;
	@Autowired
	private IStockDetailHisService stockDetailHisService;
	@Autowired
	private IApplyProductDetailService productDetailService;

	@ServerTransactional
	public StockDetail updataByInternalBuy(ApplyInternalBuyDetail detail,BizUserInfor userInfo) {
		StockDetail nstockDetail = null;
		if(detail!=null){
			List<ApplyProductDetail> list = productDetailService.findApplyDetail(detail.getId(), BasConstants.APPLY_TYPE_F);
			ApplyProductDetail productDetail = list.get(0);
			BigDecimal dealNumber = productDetail.getDealNumber();
			
			StockDetail ostockDetail = stockDetailDao.findOne(detail.getStockDetailId());
			ostockDetail.setAvailableNumber(ostockDetail.getAvailableNumber().subtract(dealNumber));	
			//添加流水
			stockDetailHisService.saveAdjust(ostockDetail, dealNumber, BasConstants.OPERATE_TYPE_FS, userInfo.getApproveId(), null);
			
			//添加新库存及流水
			nstockDetail = new StockDetail();
			nstockDetail.setAvailableNumber(dealNumber);
			nstockDetail.setBizUserId(userInfo.getBizUserId());
			nstockDetail.setBizUserName(userInfo.getBizUserName());
			nstockDetail.setBrandNumber(ostockDetail.getBrandNumber());
			nstockDetail.setBuyCompanyId(ostockDetail.getBuyCompanyId());
			nstockDetail.setBuyCompanyName(ostockDetail.getBuyCompanyName());
			nstockDetail.setBuyContractId(ostockDetail.getBuyContractId());
			nstockDetail.setCtrProductId(ostockDetail.getCtrProductId());
			nstockDetail.setDealPrice(productDetail.getDealPrice());
			if(StringUtils.equals(ostockDetail.getProductAttr(), BasConstants.STOCK_PRODUCT_ATTR_N)){
				nstockDetail.setDeliveryInNumber(dealNumber);
			}
			nstockDetail.setEnterpriseId(ostockDetail.getEnterpriseId());
			nstockDetail.setFactoryId(ostockDetail.getFactoryId());
			nstockDetail.setFactoryName(ostockDetail.getFactoryName());
			nstockDetail.setProductAttr(productDetail.getProductAttr());
			nstockDetail.setProductCd(productDetail.getProductCd());
			nstockDetail.setProductName(productDetail.getProductName());
			nstockDetail.setStockId(ostockDetail.getStockId());
			nstockDetail.setWarehouseId(ostockDetail.getWarehouseId());
			nstockDetail.setWarehouseName(ostockDetail.getWarehouseName());
			nstockDetail.setSource(BasConstants.APPLY_TYPE_F);//内部交易
			nstockDetail = stockDetailDao.save(nstockDetail);
			stockDetailHisService.saveAdjust(nstockDetail, dealNumber, BasConstants.OPERATE_TYPE_FA, userInfo.getApproveId(), null);
		}
		return nstockDetail;
	}

	@ServerTransactional
	public void doBackInternalBuy(StockDetail nDetail, Long oStockDetailId,Long applyId) {
		BigDecimal dealNumber = nDetail.getAvailableNumber();
		StockDetail oDetail = stockDetailDao.findOne(oStockDetailId);
		oDetail.setAvailableNumber(oDetail.getAvailableNumber().add(dealNumber));
		oDetail = stockDetailDao.save(oDetail);
		stockDetailHisService.saveAdjust(oDetail, dealNumber, BasConstants.OPERATE_TYPE_TA, applyId, null);
		
		nDetail.setAvailableNumber(BigDecimal.ZERO);
		nDetail = stockDetailDao.save(nDetail);
		stockDetailHisService.saveAdjust(nDetail, dealNumber, BasConstants.OPERATE_TYPE_TS, applyId, null);
	}
	
}
