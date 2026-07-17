package com.spt.bas.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyProductDetail;
import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.vo.ApplyDeliveryApplyIdVo;
import com.spt.bas.client.vo.ApplyMatchDetailVo;
import com.spt.bas.client.vo.ApplyMatchVo;
import com.spt.bas.client.vo.ApplyProductDetailSaveVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.cache.FactoryCache;
import com.spt.bas.server.cache.WarehouseCache;
import com.spt.bas.server.dao.ApplyProductDetailDao;
import com.spt.bas.server.dao.StockDetailDao;
import com.spt.bas.server.service.IApplyProductDetailService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Transactional(readOnly = true)
public class ApplyProductDetailServiceImpl extends BaseService<ApplyProductDetail>
		implements IApplyProductDetailService {
	@Autowired
	private ApplyProductDetailDao applyProductDetailDao;
	@Autowired
	private StockDetailDao stockDetailDao;

	@Override
	public BaseDao<ApplyProductDetail> getBaseDao() {
		return applyProductDetailDao;
	}

	@Override
	public Class<ApplyProductDetail> getEntityClazz() {
		return ApplyProductDetail.class;
	}

	@Override
	public List<ApplyProductDetail> findApplyDetail(Long applyId, String applyType) {
		return applyProductDetailDao.findApplyDetail(applyId, applyType);
	}

	@Override
	public List<ApplyProductDetail> saveDetailBatch(List<ApplyProductDetail> lstInsert,
			List<ApplyProductDetail> lstUpdate, List<ApplyProductDetail> lstDelete, ApplyProductDetailSaveVo vo) throws ApplicationException {
		List<ApplyProductDetail> bsList = new ArrayList<>();
		for (ApplyProductDetail entity : lstInsert) {
			if (entity.getEnterpriseId() == null) {
				entity.setEnterpriseId(vo.getEnterpriseId());
			}
			if(StringUtils.isBlank(entity.getBrandNumber())) {
				entity.setBrandNumber(BasConstants.DEFUALT_BRANDNUMBER);
			}
			entity.setApplyId(vo.getApplyId());
			entity.setApplyType(vo.getApplyType());
			if (StringUtils.isNotBlank(entity.getWarehouseName())) {
				entity.setWarehouseName(entity.getWarehouseName().trim());
			}
			// 如果工厂name没值，查询保存工厂Name值
			if (entity.getFactoryId() != null && StringUtils.isBlank(entity.getFactoryName())) {
				entity.setFactoryName(FactoryCache.getFactoryName(entity.getFactoryId()));
			}
			if (entity.getFactoryId() == null) {
				entity.setFactoryId(BasConstants.DEFUALT_FACTORYID);
			}
			if (entity.getWarehouseId() != null && StringUtils.isBlank(entity.getWarehouseName())) {
				entity.setWarehouseName(WarehouseCache.getWarehouseName(entity.getWarehouseId()));
			}
			entity.setTotalPrice(entity.getDealPrice().multiply(entity.getDealNumber()).setScale(2, RoundingMode.HALF_UP));
			ApplyProductDetail product = applyProductDetailDao.save(entity);
			bsList.add(product);
		}
		for (ApplyProductDetail entity : lstUpdate) {
			if (entity.getEnterpriseId() == null) {
				entity.setEnterpriseId(vo.getEnterpriseId());
			}
			if(StringUtils.isBlank(entity.getBrandNumber())) {
				entity.setBrandNumber(BasConstants.DEFUALT_BRANDNUMBER);
			}
			entity.setApplyId(vo.getApplyId());
			entity.setApplyType(vo.getApplyType());
			if (entity.getWarehouseId() != null && StringUtils.isBlank(entity.getWarehouseName())) {
				entity.setWarehouseName(WarehouseCache.getWarehouseName(entity.getWarehouseId()));
			}
			if (entity.getFactoryId() != null && StringUtils.isBlank(entity.getFactoryName())) {
				entity.setFactoryName(FactoryCache.getFactoryName(entity.getFactoryId()));
			}
			if (entity.getFactoryId() == null) {
				entity.setFactoryId(BasConstants.DEFUALT_FACTORYID);
			}
			if (StringUtils.isNotBlank(entity.getWarehouseName())) {
				entity.setWarehouseName(entity.getWarehouseName().trim());
			}
			entity.setTotalPrice(entity.getDealPrice().multiply(entity.getDealNumber()).setScale(2, RoundingMode.HALF_UP));
			ApplyProductDetail product = applyProductDetailDao.save(entity);
			bsList.add(product);
		}
		for (ApplyProductDetail entity : lstDelete) {
			if (entity.getId() == null || entity.getId() == 0L) {
				continue;
			} else {
				delete(entity.getId());
			}
		}
		return bsList;
	}

	@Override
	public ApplyProductDetail findEntityByParam(Map<String, Object> queryParams) {
		Specification<ApplyProductDetail> spec = WebUtil.buildSpecification(queryParams);
		List<ApplyProductDetail> productDetailList = this.applyProductDetailDao.findAll(spec);
		if (productDetailList != null && productDetailList.size() > 0) {
			return productDetailList.get(0);
		}
		return null;
	}

	@Override
	public List<ApplyProductDetail> findApplyId(@RequestBody ApplyDeliveryApplyIdVo vo) {
		return applyProductDetailDao.findApplyId(vo.getApplyId(), vo.getApplyType());
	}

	@Override
	public void saveBatchEnterpriseId(ApplyProductDetailSaveVo vo) {
		List<ApplyProductDetail> productList = findApplyDetail(vo.getApplyId(), vo.getApplyType());
		for (ApplyProductDetail product : productList) {
			product.setEnterpriseId(vo.getEnterpriseId());
			applyProductDetailDao.save(product);
		}

	}

	@Override
	@ServerTransactional
	public void saveBatchByCtrProductList(List<CtrProduct> list,ApplyProductDetailSaveVo vo){
		//删除掉旧数据
		List<ApplyProductDetail> productList = applyProductDetailDao.findApplyId(vo.getApplyId(), vo.getApplyType());
		if(!productList.isEmpty()){
			for (ApplyProductDetail apd : productList) {
				applyProductDetailDao.delete(apd);
			}
		}
		//添加新数据
		if(!list.isEmpty()){
			for (CtrProduct ctrProduct : list) {
				ApplyProductDetail detail = new ApplyProductDetail();
				BeanUtils.copyProperties(ctrProduct, detail);
				detail.setApplyId(vo.getApplyId());
				detail.setApplyType(vo.getApplyType());
				detail.setId(0l);
				detail.setUpdatedDate(null);
				detail.setCreatedDate(null);
				applyProductDetailDao.save(detail);
			}
		}
	}

	@Override
	@ServerTransactional
	public void saveBystockDetail(ApplyProductDetailSaveVo vo, Long stockDetailId) {
		//驳回后若重新选择库存明细，则会生成新的记录，需要将旧记录删除掉
		List<ApplyProductDetail> productList = applyProductDetailDao.findApplyId(vo.getApplyId(), vo.getApplyType());
		if(!productList.isEmpty()){
			for (ApplyProductDetail apd : productList) {
				applyProductDetailDao.delete(apd);
			}
		}
		//添加新数据
		StockDetail detail = stockDetailDao.findOne(stockDetailId);
		ApplyProductDetail productDetail = new ApplyProductDetail();
		BeanUtils.copyProperties(detail, productDetail);
		productDetail.setDealNumber(detail.getAvailableNumber());
		productDetail.setCtrProductId(null);
		productDetail.setApplyId(vo.getApplyId());
		productDetail.setApplyType(vo.getApplyType());
		productDetail.setId(0l);
		productDetail.setUpdatedDate(null);
		productDetail.setCreatedDate(null);
		applyProductDetailDao.save(productDetail);
	}

	@Override
	public List<Object[]> sumApplyDetail(Long id, String applyTypeI) {

		return applyProductDetailDao.sumApplyDetail(id,applyTypeI);
	}

	@Override
 	@ServerTransactional
 	public ApplyProductDetail saveDetailMatch(ApplyMatchDetailVo detailVo,ApplyMatchVo matchVo, ApplyProductDetailSaveVo vo) {
 		ApplyProductDetail productDetail = new ApplyProductDetail();
 		// 先删除已存在的数据(驳回后在申请出现重复的数据)
		// applyProductDetailDao.deleteDetail(vo.getApplyId(),BasConstants.APPLY_TYPE_M);

 		// 这里的applyId 就是 apply_match_detail id
 		productDetail.setApplyId(vo.getApplyId());
 		productDetail.setApplyType(vo.getApplyType());
 		productDetail.setEnterpriseId(vo.getEnterpriseId());
 		productDetail.setProductAttr("N");
 		productDetail.setProductCd(matchVo.getProductCd());
 		productDetail.setProductName(matchVo.getProductName());
 		productDetail.setBrandNumber(matchVo.getBrandNumber());
 		productDetail.setFactoryId(matchVo.getFactoryId());
 		productDetail.setFactoryName(matchVo.getFactoryName());
 		productDetail.setDealNumber(matchVo.getDealNumber());
 		productDetail.setStockContractId(matchVo.getStockContractId());
 		productDetail.setDealPrice(detailVo.getDealPrice());
 		productDetail.setMinDealPrice(detailVo.getMinDealPrice());
 		productDetail.setPremium(detailVo.getPremium());
 		productDetail.setWrapSpecs(matchVo.getWrapSpecs());
 		if(Objects.nonNull(detailVo.getDealPrice())) {
			BigDecimal totalAmount = detailVo.getDealPrice().multiply(matchVo.getDealNumber()).setScale(2, BigDecimal.ROUND_HALF_UP);
			productDetail.setTotalPrice(totalAmount);
		}
 		productDetail= applyProductDetailDao.save(productDetail);
 		return productDetail;
 	}

	@Override
	public List<ApplyProductDetail> findByProductName(String productName) {
		return applyProductDetailDao.findByProductName(productName);
	}

}
