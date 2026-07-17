package com.spt.bas.server.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.vo.BasStockDetailVo;
import com.spt.bas.client.vo.DeliveryOutChangeVo;
import com.spt.bas.client.vo.StockDetailRequest;
import com.spt.bas.client.vo.StockDetailSearchVo;
import com.spt.bas.client.vo.StockDetailVo;
import com.spt.bas.client.vo.WarehouseAndInNumberVo;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.dao.CtrProductDao;
import com.spt.bas.server.dao.StockDetailDao;
import com.spt.bas.server.service.ICtrContractService;
import com.spt.bas.server.service.IStockDetailService;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class StockDetailServiceImpl extends BaseService<StockDetail> implements IStockDetailService {
	@Autowired
	private StockDetailDao stockDetailDao;
	@Autowired
	private ICtrContractService contractService;
	@Autowired
	private CtrContractDao contractDao;
	@Autowired
	private CtrProductDao ctrProductDao;
	
	@Override
	public BaseDao<StockDetail> getBaseDao() {
		return stockDetailDao;
	}

	@Override
	public Class<StockDetail> getEntityClazz() {
		return StockDetail.class;
	}
	
	
	@Override
	public StockDetail sumPageVo(StockDetailSearchVo queryVo) {
		Specification<StockDetail> spec = dealWithCondition(queryVo);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<?> query = cb.createQuery();
		Root<StockDetail> root = query.from(StockDetail.class);
		CriteriaQuery<?> cq = query.where(spec.toPredicate(root, query, cb)).multiselect(
				cb.sum(root.get("deliveryInNumber")), cb.sum(root.get("deliveryOutNumber")), cb.sum(root.get("availableNumber")));
		TypedQuery<?> tq = em.createQuery(cq);

		Object[] result = ((Object[]) tq.getSingleResult());

		StockDetail sum = new StockDetail();
		BigDecimal deliveryInNumber = (BigDecimal) result[0];
		BigDecimal deliveryOutNumber = (BigDecimal) result[1];
		BigDecimal availableNumber = (BigDecimal) result[2];
//		BigDecimal frozenNumber = (BigDecimal) result[3];
		sum.setDeliveryInNumber(deliveryInNumber);
		sum.setDeliveryOutNumber(deliveryOutNumber);
		sum.setAvailableNumber(availableNumber);
//		sum.setFrozenNumber(frozenNumber);
		return sum;
	}
	
	
	/**
	 * 在出库申请中调用，只已入库才能出库
	 * 查询存在某种商品的库存明细列表，并确定某个销售合同的可出库数量（出库数量不能大于销售合同的数量）
	 * */
	@Override
	public List<WarehouseAndInNumberVo> findWarehoseList(StockDetailSearchVo vo){
//		Long sellContractId = Long.valueOf(vo.getSellContractId());
//		List<CtrContractRela> relaList = contractRelaDao.findBySellProductId(vo.getCtrProductId());
//		List<Long> stockDetailIdList = new ArrayList<>();
//		for (CtrContractRela contractRela : relaList) {
//			CtrProduct product = ctrProductDao.findOne(contractRela.getBuyProductId());
//			Long stockDetailId = product.getStockDetailId();
//			if (stockDetailId != null) {
//				stockDetailIdList.add(stockDetailId);
//			}
//		}
//		Map<String, Object> queryParams = new HashMap<String, Object>();
//		//同一个合同，(品名、牌号、厂商、仓库)都一样时，出库时的最大值会默认错误；增加以下代码，根据id直接判断
//		if (stockDetailIdList != null && stockDetailIdList.size() > 0) {
//			queryParams.put("INL_id_OR_INL_linkStockDetailId", stockDetailIdList);// 库存明细id
//		}
//		if (vo.getFactoryId()!=null && vo.getFactoryId()>0) {
//			queryParams.put("EQL_factoryId", vo.getFactoryId());// 厂商
//		}
//		if (StringUtils.isNotBlank(vo.getBrandNumber())) {
//			queryParams.put("EQS_brandNumber", vo.getBrandNumber());// 牌号
//		}
//		if (StringUtils.isNotBlank(vo.getProductCd())) {
//			queryParams.put("EQS_productCd", vo.getProductCd());// 牌号
//		}
//		if(StringUtils.isNotBlank(vo.getSellContractId())){
//			queryParams.put("LIKES_sellContractId", vo.getSellContractId());
//		}
//		if(StringUtils.isNotBlank(vo.getWarehouseName())){
//			queryParams.put("EQS_warehouseName", vo.getWarehouseName());
//		}
//		if(StringUtils.isNotBlank(vo.getProductAttr())){
//			queryParams.put("EQS_productAttr", vo.getProductAttr());
//		}
//		//queryParams.put("EQS_productAttr", BasConstants.STOCK_PRODUCT_ATTR_N);
//		// 只已入库才能出库，已入库数量>0
//		queryParams.put("GTM_deliveryInNumber", BigDecimal.ZERO);
//		Specification<StockDetail> spec = WebUtil.buildSpecification(queryParams);
//		List<StockDetail> detailList = this.stockDetailDao.findAll(spec);
		
		Specification<StockDetail> spec = (root, query, cb) -> {
			Predicate p_factoryId = cb.equal(root.get("factoryId"), vo.getFactoryId());
			Predicate p_brandNumber = cb.equal(root.get("brandNumber"), vo.getBrandNumber());
			Predicate p_productCd = cb.equal(root.get("productCd"), vo.getProductCd());
			// 入库数量 > 出库数量
			Predicate p_deliveryInNumber = cb.greaterThan(root.get("deliveryInNumber"), root.get("deliveryOutNumber"));
			Predicate p1 = cb.and(p_factoryId, p_brandNumber, p_productCd, p_deliveryInNumber);
			if (StringUtils.isNotBlank(vo.getWarehouseName())) {
				Predicate p_warehouseName = cb.equal(root.get("warehouseName"), vo.getWarehouseName());
				p1 = cb.and(p1, p_warehouseName);
			}
			return p1;
		};
		List<StockDetail> detailList = this.stockDetailDao.findAll(spec);
		List<WarehouseAndInNumberVo> voList = new ArrayList<>();
		List<WarehouseAndInNumberVo> lstSelf  = new ArrayList<>();
		CtrProduct product = ctrProductDao.findOne(vo.getCtrProductId());
		for(StockDetail detail :detailList) {
			WarehouseAndInNumberVo wanVo = new WarehouseAndInNumberVo();
			wanVo.setCurApproveNumber(product.getCurApproveNumber());
			wanVo.setText(detail.getWarehouseName()+"["+detail.getId()+"]");
			wanVo.setWarehouseName(detail.getWarehouseName());
			wanVo.setBuyContractId(detail.getBuyContractId());
			wanVo.setAvailableNumber(detail.getAvailableNumber());
			wanVo.setStockDetailId(detail.getId());
			wanVo.setCtrProductId(detail.getCtrProductId());
			wanVo.setStockContractId(detail.getStockContractId());
			
			wanVo.setBuyCompanyName(detail.getBuyCompanyName());
			wanVo.setBuyBizUserName(detail.getBizUserName());
			wanVo.setProductName(detail.getProductName());
			wanVo.setBrandNumber(detail.getBrandNumber());
			wanVo.setFactoryName(detail.getFactoryName());
			
			if(detail.getProductAttr().equals(BasConstants.STOCK_PRODUCT_ATTR_N)){
				//最大出库数量=已入库数量-已出库数量
				wanVo.setFrozenNumber(detail.getDeliveryInNumber().subtract(detail.getDeliveryOutNumber()));
			}else{
				wanVo.setFrozenNumber(BigDecimal.ZERO);
			}
			
			if (vo.getStockContractId() != null && vo.getStockContractId().equals(detail.getStockContractId())) {
				lstSelf.add(wanVo);
			}else {
				voList.add(wanVo);
			}
		}
		voList.addAll(0, lstSelf);
		return voList;
	}
	
	/**type=buy/sell*/
	@Override
	public List<StockDetail> findDetailList(StockDetailRequest request,String type,boolean isOut){
		String contractId = request.getCtrContractId() + "";
		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("EQS_productCd", request.getProductCd());// 商品类型
		queryParams.put("EQL_factoryId", request.getFactoryId());// 厂商
		queryParams.put("EQS_brandNumber", request.getBrandNumber());// 牌号
		// 原采购仓库
		// queryParams.put("EQS_warehouseName", product.getWarehouseName());
		// 采购库存保存
		if (StringUtils.equals(type, BasConstants.OPE_BUYSELL_B)) {
			queryParams.put("EQS_buyContractId", contractId);
			if (request.getCtrProductId() != null && request.getCtrProductId() > 0) {
				queryParams.put("EQS_ctrProductId", request.getCtrProductId());
			}
			else if (request.getLinkDetailId() != null && request.getLinkDetailId() > 0) {
				queryParams.put("EQL_id", request.getLinkDetailId());
			}
		}else {
			//销售
			if (request.getLinkDetailId() != null && request.getLinkDetailId() > 0) {
				queryParams.put("EQL_id_OR_EQL_linkStockDetailId", request.getLinkDetailId());
			}
			//出库直接根据id查询就可以，注释以下代码
			if (isOut) {
				queryParams.put("EQS_warehouseName", request.getWarehouseNameNew());
			}else {
				queryParams.put("EQS_warehouseName", request.getWarehouseName());
			}
			queryParams.put("EQS_productAttr", "N");//出库默认现货出库
			// 采购库存保存
			queryParams.put("LIKES_sellContractId", "," + contractId + ",");
		}
		// 获取唯一一条明细数据
		Specification<StockDetail> spec = WebUtil.buildSpecification(queryParams);
		// 先找到在途数据，再找到现货数据
		Sort sort = Sort.by(Direction.DESC, "productAttr");
		List<StockDetail> detailList = this.stockDetailDao.findAll(spec, sort);
		return detailList;
	}
	

	
	@Override
	public Page<StockDetailVo> findPageVo(StockDetailSearchVo queryVo) {
		Sort sort = Sort.by(Direction.DESC, "updatedDate");
		Specification<StockDetail> spec = dealWithCondition(queryVo);
		PageRequest pageRequest = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows(), sort);
		Page<StockDetail> page = getBaseDao().findAll(spec, pageRequest);
		
		List<StockDetailVo> voList = new ArrayList<StockDetailVo>();
		for(StockDetail detail:page.getContent()){
			StockDetailVo vo = new StockDetailVo();
			BeanUtils.copyProperties(detail, vo);
			String buyContractId = detail.getBuyContractId();
			CtrContract contract = contractService.getEntity(Long.parseLong(buyContractId));
			vo.setBuyCompanyName(contract.getCompanyName());
			vo.setContractNo(contract.getContractNo());
			voList.add(vo);
		}
		
		PageRequest pageRequestNew = PageRequest.of(queryVo.getPage()-1, queryVo.getRows());
		Page<StockDetailVo> pageVo=new PageImpl<>(voList, pageRequestNew, page.getTotalElements());
		return pageVo;
	}
	
	private Specification<StockDetail> dealWithCondition(StockDetailSearchVo queryVo){
		String status = queryVo.getStatus();
		if (status==null)status="C";
		Map<String, Object> searchParams = queryVo.getSearchParams();
		Specification<StockDetail> spec = WebUtil.buildSpecification(searchParams);
		Specification<StockDetail> spec_avail;
		Specification<StockDetail> spec_froze;
		if(status.equals("C")){//当前
			spec_avail = WebUtil.buildSpecification("GTM_availableNumber", BigDecimal.ZERO);
			spec_froze = WebUtil.buildSpecification("GTM_frozenNumber", BigDecimal.ZERO);
			Specification<StockDetail> specNum = Specification.where(spec_froze).or(spec_avail);
//			Specification<StockDetail> spec4 = Specification.where(spec).and(spec_froze);
			spec = Specification.where(spec).and(specNum);
		}else if(status.equals("A")){//历史
			spec_avail = WebUtil.buildSpecification("EQM_availableNumber", BigDecimal.ZERO);
			spec_froze = WebUtil.buildSpecification("EQM_frozenNumber", BigDecimal.ZERO);
			spec = Specification.where(spec).and(spec_avail).and(spec_froze);
		}
		return spec;
	}
	

	@Override
	public List<StockDetail> findByBuyContractId(String buyContractId) {
		return stockDetailDao.findByBuyContractId(buyContractId);
	}

//	@Override
//	public List<StockDetail> findSellContractId(String sellContractId) {
//		return stockDetailDao.findSellContractId(sellContractId);
//	}
//	@Override
//	public List<StockDetail> findSellContractId(Long stockId,String sellContractId) {
//		return stockDetailDao.findStockIdAndSellContractId(stockId,sellContractId);
//	}

	@Override
	public StockDetail findWarehouseName(String warehouseName) {
		return stockDetailDao.findWarehouseName(warehouseName);
	}

	@Override
	public Page<StockDetail> findByCondition(DeliveryOutChangeVo vo) {
		String productCd = vo.getProductCd();
		String warehouseName = vo.getWarehouseName();
		BigDecimal totalNumber = vo.getTotalNumber();
		String productAttr = vo.getProductAttr();
		Long enterpriseId = vo.getEnterpriseId();
		List<StockDetail> list = stockDetailDao.findByCondition(productCd, warehouseName, totalNumber,productAttr,enterpriseId);
		PageRequest pageRequest = PageRequest.of(vo.getPage() - 1, vo.getRows());
		Page<StockDetail> page = new PageImpl<>(list, pageRequest, vo.getCount());
		return page;
	}

	/** 调整货源中使用 */
	@Override
	public Page<BasStockDetailVo> findPageList(PageSearchVo searchVo) {
		Sort sort = Sort.by(Direction.DESC, "id");
		PageRequest pageRequest = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows(), sort);
		Specification<StockDetail> spec = WebUtil.buildSpecification(searchVo.getSearchParams());
		Page<StockDetail> page = getBaseDao().findAll(spec, pageRequest);
		List<BasStockDetailVo> voList = new ArrayList<>();
		for (StockDetail detail : page.getContent()) {
			BasStockDetailVo vo = new BasStockDetailVo();
			BeanUtils.copyProperties(detail, vo);
			CtrContract contract = contractDao.findOne(Long.valueOf(detail.getBuyContractId()));
			if (contract != null ) {
				String ourCompanyName = contract.getOurCompanyName();
				vo.setOurCompanyName(ourCompanyName);
			}
			voList.add(vo);
		}
		Page<BasStockDetailVo> newPage = new PageImpl<>(voList, pageRequest, page.getTotalElements());
 		return newPage;
	}

	
	
}
