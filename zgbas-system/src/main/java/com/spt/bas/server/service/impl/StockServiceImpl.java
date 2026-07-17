package com.spt.bas.server.service.impl;//package com.spt.bas.server.service.impl;
//
//import java.math.BigDecimal;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import javax.persistence.TypedQuery;
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Root;
//
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.domain.Sort.Direction;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.spt.bas.client.constant.BasConstants;
//import com.spt.bas.client.entity.ApplyDeliveryIn;
//import com.spt.bas.client.entity.CtrContract;
//import com.spt.bas.client.entity.CtrProduct;
//import com.spt.bas.client.entity.Stock;
//import com.spt.bas.client.entity.StockDetail;
//import com.spt.bas.client.entity.StockDetailPresell;
//import com.spt.bas.client.vo.ApplyContractAdjustRequestVo;
//import com.spt.bas.client.vo.ApplySellWarehouseVo;
//import com.spt.bas.client.vo.BizUserInfor;
//import com.spt.bas.client.vo.CtrConctractInvalidVo;
//import com.spt.bas.client.vo.StockAdjustAuditVo;
//import com.spt.bas.client.vo.StockDetailCancelSellResp;
//import com.spt.bas.client.vo.StockDetailDeleveryInResp;
//import com.spt.bas.client.vo.StockDetailLinkVo;
//import com.spt.bas.client.vo.StockDetailMoveVo;
//import com.spt.bas.client.vo.StockDetailRequest;
//import com.spt.bas.client.vo.StockFlowVo;
//import com.spt.bas.client.vo.StockRequest;
//import com.spt.bas.client.vo.StockSearchVo;
//import com.spt.bas.client.vo.StockVo;
//import com.spt.bas.server.annotation.ServerTransactional;
//import com.spt.bas.server.dao.ApplyDeliveryInDao;
//import com.spt.bas.server.dao.StockDao;
//import com.spt.bas.server.service.ICtrProductService;
//import com.spt.bas.server.service.IStockDetailPresellService;
//import com.spt.bas.server.service.IStockFlowService;
//import com.spt.bas.server.service.IStockService;
//import com.spt.bas.server.stock.service.StockDetailFacade;
//import com.spt.tools.core.exception.ApplicationException;
//import com.spt.tools.core.json.JsonUtil;
//import com.spt.tools.jpa.dao.BaseDao;
//import com.spt.tools.jpa.persistence.WebUtil;
//import com.spt.tools.jpa.service.BaseService;
//
//@Component
//@Transactional(readOnly = true)
//public class StockServiceImpl extends BaseService<Stock> implements IStockService {
//	@Autowired
//	private StockDao stockDao;
//	@Autowired
//	private StockDetailFacade stockDetailFacade;
//	@Autowired
//	private IStockDetailPresellService stockDetailPresellService;
//	@Autowired
//	private ICtrProductService productService;
//	@Autowired
//	private IStockFlowService stockFlowService;
//	@Autowired
//	private ApplyDeliveryInDao applyDeliveryInDao;
//	@Override
//	public BaseDao<Stock> getBaseDao() {
//		return stockDao;
//	}
//
//	@Override
//	public Class<Stock> getEntityClazz() {
//		return Stock.class;
//	}
//
//	@Override
//	public Stock sumPageVo(StockSearchVo queryVo) {
//		String stockStatus = queryVo.getStockStatus();
//		Map<String,Object> searchParams = queryVo.getSearchParams();
//		Specification<Stock> spec = WebUtil.buildSpecification(searchParams);
//		spec = dealFindCondition(spec,stockStatus);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<?> query = cb.createQuery();
//		Root<Stock> root = query.from(Stock.class);
//		CriteriaQuery<?> cq = query.where(spec.toPredicate(root, query, cb)).multiselect(
//				cb.sum(root.get("totalNumber")), cb.sum(root.get("frozenNumber")), cb.sum(root.get("realNumber")));
//		TypedQuery<?> tq = em.createQuery(cq);
//
//		Object[] result = ((Object[]) tq.getSingleResult());
//
//		Stock sum = new Stock();
//		BigDecimal totalNumber = (BigDecimal) result[0];
//		BigDecimal frozenNumber = (BigDecimal) result[1];
//		BigDecimal realNumber = (BigDecimal) result[2];
//		sum.setTotalNumber(totalNumber);
//		sum.setFrozenNumber(frozenNumber);
//		sum.setRealNumber(realNumber);
//		return sum;
//	}
//
//
//
//	@Override
//	public Stock findBrandNumber(String brandNumber, String productAttr, Long enterpriseId) {
//		return stockDao.findBrandNumber(brandNumber, productAttr, enterpriseId);
//	}
//
//	@Override
//	public List<Stock> findStockForzenNumber(String productCd, String brandNumber, Long factoryId, String warehouseName,
//			Long enterpriseId, BigDecimal dealNumber, String productAttr) {
//		return stockDao.findStockForzenNumber(productCd, brandNumber, factoryId, warehouseName, enterpriseId,
//				dealNumber, productAttr);
//	}
//
//	private Stock findEntityByParam(Map<String, Object> queryParams) {
//		Specification<Stock> spec = WebUtil.buildSpecification(queryParams);
//		List<Stock> stockList = this.stockDao.findAll(spec);
//		if (stockList != null && stockList.size() > 0) {
//			return stockList.get(0);
//		}
//		return null;
//	}
//
//	@Override
//	@ServerTransactional
//	public void updateDeliveryStock(StockRequest request, BizUserInfor userInfor) throws ApplicationException {
//		if (BasConstants.APPLY_TYPE_I.equals(request.getApplyType())) {
////			updateDeliveryInStock(request, userInfor);
//		} else if (BasConstants.APPLY_TYPE_O.equals(request.getApplyType())) {
//			updateDeliveryOutStock(request, userInfor);
//		}
//	}
//
//	/**
//	 * 平均价格计算
//	 *
//	 * @param oldTotalNumber
//	 *            原总数量
//	 * @param dealNumber
//	 *            现数量
//	 * @param oldAveragePrice
//	 *            原平均价
//	 * @param dealPrice
//	 *            现单价
//	 * @return
//	 */
//	private BigDecimal averagePrice(BigDecimal oldTotalNumber, BigDecimal dealNumber, BigDecimal oldAveragePrice,
//			BigDecimal dealPrice) {
//		BigDecimal totalPrice = BigDecimal.ZERO;
//		if (oldTotalNumber.compareTo(BigDecimal.ZERO) > 0)
//			totalPrice = oldAveragePrice.multiply(oldTotalNumber).add(dealPrice.multiply(dealNumber));
//		else {
//			totalPrice = dealPrice.multiply(dealNumber);
//		}
//		BigDecimal nowAveragePrice = BigDecimal.ZERO;
//		BigDecimal totalNumber = oldTotalNumber.add(dealNumber);
//		if (totalPrice.compareTo(BigDecimal.ZERO) > 0 && totalNumber.compareTo(BigDecimal.ZERO)>0) {
//			nowAveragePrice = totalPrice.divide(totalNumber, 2, BigDecimal.ROUND_HALF_UP);
//		}else {
//			logger.warn("averagePrice error，totalPrice:{},oldTotalNumber:{},dealNumber:{}",totalPrice,oldTotalNumber,dealNumber);
//		}
//		return nowAveragePrice;
//
//	}
//
//	/**
//	 * 入库仓库：在途仓库入到现货仓库。
//	 *
//	 * @param product
//	 * @param type
//	 * @throws ApplicationException
//	 */
//	private void updateDeliveryInStock(StockDetailRequest request, BizUserInfor userInfor) throws ApplicationException {
//		//入库对应的是采购合同，查询采购合同对应的库存明细
////		BigDecimal detailRealNumber = BigDecimal.ZERO;//可用数量
////		BigDecimal detailFrozenNumber = BigDecimal.ZERO;//冻结数量
////		logger.info("userInfor:{}",JsonUtil.obj2Json(userInfor));
//		ApplyDeliveryIn deliveryIn = applyDeliveryInDao.findOne(userInfor.getApproveId());
//		if (deliveryIn != null) {
//			request.setStockType(deliveryIn.getStockType());
//			request.setSpotType(deliveryIn.getSpotType());
//		}else{
//			request.setStockType(BasConstants.DICT_TYPE_STOCKTYPE_XH);
//			request.setSpotType(BasConstants.DICT_TYPE_SPOTTYPE_S);
//		}
//		String oldWarehouseName = request.getWarehouseName();
//		String newWarehouseName = request.getWarehouseNameNew();
////		List<StockDetail> lstDetail = stockDetailService.findDetailList(request, BasConstants.OPE_BUYSELL_B,false);
////		for (StockDetail detail : lstDetail) {
////			if (oldWarehouseName.equals(detail.getWarehouseName())) {
////				detailRealNumber = detailRealNumber.add(detail.getAvailableNumber());
////				detailFrozenNumber = detailFrozenNumber.add(detail.getFrozenNumber());
////			}
////		}
//		// 获取采购的在途库存，入库的现货仓库
//		// 原仓库总数据
//		List<Stock> lstStockOld = this.stockDao.findStockList(request.getProductCd(), request.getBrandNumber(),
//				request.getFactoryId(), request.getWarehouseName(), request.getEnterpriseId());
//		if (lstStockOld == null || lstStockOld.size() <= 0) {
//			throw new ApplicationException("原仓库库存不存在");
//		}
//		// 原仓库数据
//		Stock stockOld = lstStockOld.get(0);
//
//		// 新仓库总数据
//		List<Stock> lstStockNew = this.stockDao.findStockList(request.getProductCd(), request.getBrandNumber(),
//				request.getFactoryId(), newWarehouseName, request.getEnterpriseId());
//		// 新仓库数据
//		Stock stockNew = null;
//		if (lstStockNew != null && lstStockNew.size() > 0) {
//			stockNew = lstStockNew.get(0);
//		}
//		//----库存流水---
//		StockFlowVo sfVo;
//		if(!request.isBack()){
//			// 获取保存库存流水的Vo
//			sfVo = getStockFlowVo(null, userInfor, BasConstants.APPLY_TYPE_I);
//		}else{
//			sfVo = getStockFlowVo(null, userInfor, BasConstants.OPERATE_TYPE_IC);
//		}
//		sfVo.setContractId(request.getCtrContractId());
//		sfVo.setDealNumber(request.getDealNumber());
//		sfVo.setDealPrice(request.getDealPrice());
//		sfVo.setTotalPrice(request.getDealNumber().multiply(request.getDealPrice()));
//		sfVo.setPreFrozenNumber(BigDecimal.ZERO);
//		sfVo.setPreRealNumber(BigDecimal.ZERO);
//		//---库存流水--end
//
////		request.setOtherStockId(stockOld.getId());
////		request.setOtherStockRemainNumber(stockOld.getRealNumber());
////		request.setOtherStockRemainFrozen(stockOld.getFrozenNumber());
//		// 如果是撮合订单,且没有现货数据,将在途改为现货
//		if (request.getMatchBl() && stockNew == null) {
//			stockOld.setProductAttr(BasConstants.STOCK_PRODUCT_ATTR_N);
//			stockOld = this.save(stockOld);
//			sfVo.setPreFrozenNumber(stockOld.getFrozenNumber());
//			sfVo.setPreRealNumber(stockOld.getRealNumber());
//			this.stockFlowService.saveStockFlow(sfVo, stockOld);
//		}else{
//			if (StringUtils.equals(newWarehouseName, oldWarehouseName)) {
//				//如果新旧仓库一致，入库时，只增加流水，不修改主表数据
//				sfVo.setPreFrozenNumber(stockOld.getFrozenNumber());
//				sfVo.setPreRealNumber(stockOld.getRealNumber());
//				stockFlowService.saveStockFlow(sfVo, stockOld);
//				stockOld = this.save(stockOld);
//				request.setStockId(stockOld.getId());
//				stockDetailFacade.saveDeliveryIn(request);
//			}else {
//				BigDecimal oriFrozenNew=BigDecimal.ZERO,oriRealNew=BigDecimal.ZERO;
//				BigDecimal oriFrozenOld = stockOld.getFrozenNumber();
//				BigDecimal oriRealOld = stockOld.getRealNumber();
//				//如果新旧仓库不一致，减少旧仓库数量，增加新仓库数量，如果新仓库记录不存在，则新增一条
//				if (stockNew == null) {
//					//如果新仓库对应的库存数据不存在，新建一条
//					stockNew = new Stock();
//					stockNew.setProductAttr(BasConstants.STOCK_PRODUCT_ATTR_N);
//					stockNew.setProductCd(request.getProductCd());
//					stockNew.setProductName(request.getProductName());
//					stockNew.setBrandNumber(request.getBrandNumber());
//					stockNew.setFactoryId(request.getFactoryId());
//					stockNew.setFactoryName(request.getFactoryName());
//					stockNew.setWarehouseId(request.getWarehouseIdNew());
//					stockNew.setWarehouseName(newWarehouseName);
//					stockNew.setEnterpriseId(request.getEnterpriseId());
//					stockNew.setAveragePrice(stockOld.getAveragePrice());
//					stockNew = this.save(stockNew);
//				}else{
//					oriFrozenNew = stockNew.getFrozenNumber();
//					oriRealNew = stockNew.getRealNumber();
//					stockNew.setAveragePrice(averagePrice(stockNew.getTotalNumber(),request.getDealNumber(),stockNew.getAveragePrice(),request.getDealPrice()));
//				}
//				// 当前入库数量
////				BigDecimal curInNumber = request.getDealNumber();
//				BigDecimal oldFrozenNumber = stockOld.getFrozenNumber();
//				BigDecimal oldRealNumber = stockOld.getRealNumber();
//				BigDecimal newFrozenNumber = stockNew.getFrozenNumber();
//				BigDecimal newRealNumber = stockNew.getRealNumber();
//				if (!request.isBack()) {
//					//正常入库
//					// 当前入库数量>=旧冻结数量,新冻结=新冻结数量+旧冻结，在途冻结=0，
//					//
////					if (curInNumber.compareTo(detailFrozenNumber) >= 0) {
////						//先扣除冻结数量，再扣除可用数量
////						BigDecimal remainNumber = curInNumber.subtract(detailFrozenNumber);
////						stockNew.setFrozenNumber(newFrozenNumber.add(detailFrozenNumber));
////						stockNew.setRealNumber(newRealNumber.add(remainNumber));
////						stockOld.setFrozenNumber(oldFrozenNumber.subtract(detailFrozenNumber));
////						stockOld.setRealNumber(oldRealNumber.subtract(remainNumber));
////					} else {
////						// 当前入库数量<旧冻结数量,在途冻结=原在途冻结-入库数量
////						BigDecimal remainNumber = oldFrozenNumber.subtract(curInNumber);
////						stockOld.setFrozenNumber(remainNumber);
////						stockNew.setFrozenNumber(newFrozenNumber.add(curInNumber));
////					}
////					stockNew.setTotalNumber(stockNew.getFrozenNumber().add(stockNew.getRealNumber()));
////					stockOld.setTotalNumber(stockOld.getFrozenNumber().add(stockOld.getRealNumber()));
////					stockNew = this.save(stockNew);
////					sfVo.setPreFrozenNumber(oriFrozenNew);
////					sfVo.setPreRealNumber(oriRealNew);
////					stockFlowService.saveStockFlow(sfVo, stockNew);
//
////					request.setOtherStockId(stockNew.getId());
//					//在途，入库减少
//					sfVo.setOperationType(BasConstants.OPERATE_TYPE_IS);
//					sfVo.setAddSub(BasConstants.STOCK_NUMBER_SUB);
//
//
//
//					request.setStockId(stockOld.getId());
//					StockDetailDeleveryInResp resp = stockDetailFacade.saveDeliveryIn(request);
//					stockNew.setFrozenNumber(newFrozenNumber.add(resp.getFrozenNumber()));
//					stockNew.setRealNumber(newRealNumber.add(resp.getRealNumber()));
//					stockOld.setFrozenNumber(oldFrozenNumber.subtract(resp.getFrozenNumber()));
//					stockOld.setRealNumber(oldRealNumber.subtract(resp.getRealNumber()));
//
//					stockNew.setTotalNumber(stockNew.getFrozenNumber().add(stockNew.getRealNumber()));
//					stockOld.setTotalNumber(stockOld.getFrozenNumber().add(stockOld.getRealNumber()));
//					stockNew = this.save(stockNew);
//					sfVo.setPreFrozenNumber(oriFrozenNew);
//					sfVo.setPreRealNumber(oriRealNew);
//					stockFlowService.saveStockFlow(sfVo, stockNew);
//
//					stockOld = this.save(stockOld);
//				} else {
//					// 入库撤回
//					// 如果冻结数量不够，剩余部分从可用数量扣除
//
////					request.setOtherStockId(stockNew.getId());
//					request.setStockId(stockOld.getId());
//					StockDetailDeleveryInResp resp = stockDetailFacade.saveDeliveryIn(request);
//
//					//如果库存明细撤回的是冻结数量，库存主表也是撤回冻结，反之亦然！
//					stockNew.setFrozenNumber(newFrozenNumber.subtract(resp.getFrozenNumber()));
//					stockOld.setFrozenNumber(oldFrozenNumber.add(resp.getFrozenNumber()));
//					stockNew.setRealNumber(newRealNumber.subtract(resp.getRealNumber()));
//					stockOld.setRealNumber(oldRealNumber.add(resp.getRealNumber()));
//
//
////					if (curInNumber.compareTo(newFrozenNumber) > 0) {
////						stockNew.setFrozenNumber(BigDecimal.ZERO);
////						BigDecimal remainNumber = curInNumber.subtract(newFrozenNumber);
////						stockNew.setRealNumber(newRealNumber.subtract(remainNumber));
////						stockOld.setFrozenNumber(oldFrozenNumber.add(newFrozenNumber));
////						stockOld.setRealNumber(oldRealNumber.add(remainNumber));
////					} else {
////						stockNew.setFrozenNumber(newFrozenNumber.subtract(curInNumber));
////						stockOld.setFrozenNumber(oldFrozenNumber.add(newFrozenNumber));
////					}
//					stockNew.setTotalNumber(stockNew.getFrozenNumber().add(stockNew.getRealNumber()));
//					stockOld.setTotalNumber(stockOld.getFrozenNumber().add(stockOld.getRealNumber()));
//					stockNew = this.save(stockNew);
//					sfVo.setPreFrozenNumber(oriFrozenNew);
//					sfVo.setPreRealNumber(oriRealNew);
//					stockFlowService.saveStockFlow(sfVo, stockNew);
//
//					//在途，入库增加
//					sfVo.setOperationType(BasConstants.OPERATE_TYPE_IB);
//					sfVo.setAddSub(BasConstants.STOCK_NUMBER_ADD);
//
//					stockOld = this.save(stockOld);
//				}
//
//				//添加库存流水
//				sfVo.setPreFrozenNumber(oriFrozenOld);
//				sfVo.setPreRealNumber(oriRealOld);
//				this.stockFlowService.saveStockFlow(sfVo, stockOld);
//			}
//		}
//
//
//
//	}
//
//	private Stock findStock(CtrProduct product) {
////		Map<String, Object> queryParams = new HashMap<String, Object>();
////		queryParams.put("EQS_productCd", product.getProductCd());// 商品类型
////		queryParams.put("EQL_factoryId", product.getFactoryId());// 厂商
////		queryParams.put("EQS_brandNumber", product.getBrandNumber());// 牌号
////		queryParams.put("EQS_warehouseName", product.getWarehouseName());
////		queryParams.put("EQL_enterpriseId", product.getEnterpriseId());
//		// 采购库存保存
////		queryParams.put("EQS_productAttr", BasConstants.STOCK_PRODUCT_ATTR_P);
////		Stock stock = this.findEntityByParam(queryParams);
//		List<Stock> stockList = stockDao.findStockList(product.getProductCd(), product.getBrandNumber(),
//				product.getFactoryId(), product.getWarehouseName(), product.getEnterpriseId());
//		if (stockList.size() > 0) {
//			return stockList.get(0);
//		}
//		return null;
//	}
//
//	/**
//	 * 采购申请库存更新
//	 *
//	 * @param product
//	 * @param type
//	 */
//	public void updateBuyStock(CtrProduct product, BizUserInfor userInfor,String applyType) throws ApplicationException {
////		Map<String, Object> queryParams = new HashMap<String, Object>();
////		queryParams.put("EQS_productCd", product.getProductCd());// 商品类型
////		queryParams.put("EQL_factoryId", product.getFactoryId());// 厂商
////		queryParams.put("EQS_brandNumber", product.getBrandNumber());// 牌号
////		queryParams.put("EQS_warehouseName", product.getWarehouseName());
////		queryParams.put("EQL_enterpriseId", product.getEnterpriseId());
//		// 采购库存保存
////		queryParams.put("EQS_productAttr", BasConstants.STOCK_PRODUCT_ATTR_P);
//		Stock stock = findStock(product);
//		BigDecimal dealPrice = product.getDealPrice();
//		BigDecimal dealNumber = product.getDealNumber();
//		CtrProduct prodSell = null;
//		if (stock == null) {
//			// 新增库存表数据
//			stock = new Stock();
//			stock.setProductAttr(BasConstants.STOCK_PRODUCT_ATTR_P);
//			stock.setProductCd(product.getProductCd());
//			stock.setProductName(product.getProductName());
//			stock.setBrandNumber(product.getBrandNumber());
//			stock.setFactoryId(product.getFactoryId());
//			stock.setFactoryName(product.getFactoryName());
//			stock.setWarehouseName(product.getWarehouseName());
//			stock.setAveragePrice(dealPrice);
//			stock.setTotalNumber(dealNumber);// 数量
//			stock.setEnterpriseId(product.getEnterpriseId());
//			if(applyType.equals(BasConstants.APPLY_TYPE_B)||applyType.equals(BasConstants.APPLY_TYPE_MB)||applyType.equals(BasConstants.APPLY_TYPE_RB)){
//				stock.setRealNumber(dealNumber);// 可用数量
//			}
//		} else {
//			// 原平均价格
//			BigDecimal oldAveragePrice = stock.getAveragePrice();
//			BigDecimal oldTotalNumber = stock.getTotalNumber();
//			if (oldTotalNumber.compareTo(BigDecimal.ZERO) >= 0 && dealNumber.compareTo(BigDecimal.ZERO) > 0) {
//				stock.setAveragePrice(averagePrice(oldTotalNumber, dealNumber, oldAveragePrice, dealPrice));
//			}
//			stock.setTotalNumber(dealNumber.add(oldTotalNumber));// 数量
//			if(applyType.equals(BasConstants.APPLY_TYPE_B)||applyType.equals(BasConstants.APPLY_TYPE_MB)||applyType.equals(BasConstants.APPLY_TYPE_RB)){
//				stock.setRealNumber(dealNumber.add(stock.getRealNumber()));// 可用数量
//			}else{//预售采购
//				stock.setFrozenNumber(stock.getFrozenNumber().add(dealNumber));//冻结数量
//				stock.setPresellNumber(stock.getPresellNumber().subtract(dealNumber));//预售数量
//				//更新预售库存明细
//				stockDetailPresellService.updatePresellDetail(product.getDealNumber(), userInfor.getSellProductId());
//				prodSell = productService.getEntity(userInfor.getSellProductId());
//			}
//		}
//		this.save(stock);
//		// 保存库存流水
//		StockFlowVo sfVo = getStockFlowVo(product, userInfor, applyType);
//		this.stockFlowService.saveStockFlow(sfVo, stock);
//		StockDetailRequest request = StockDetailRequest.build(product);
//		request.setStockId(stock.getId());
//		request.setApplyType(applyType);
//		request.setApplyId(userInfor.getApproveId());
//		request.setSellContractId(userInfor.getSellContractId());
//		// 保存库存明细数据
//		StockDetail detail = stockDetailFacade.saveBuy(request, userInfor);
//		product.setStockDetailId(detail.getId());
//		if (prodSell != null) {
//			prodSell.setStockDetailId(detail.getId());
//			productService.save(prodSell);
//		}
//	}
//
//	/**
//	 * 销售申请库存更新
//	 *
//	 * @param product
//	 * @param type
//	 */
//	public void updateSellStock(CtrProduct product, boolean isBack, StockDetailLinkVo linkVo, BizUserInfor userInfor,String applyType)
//			throws ApplicationException {
//		ApplySellWarehouseVo vo = new ApplySellWarehouseVo();
//		vo.setProductCode(product.getProductCd());
//		vo.setBrandNumber(product.getBrandNumber());
//		vo.setFactoryId(product.getFactoryId());
//		vo.setWarehouseName(product.getWarehouseName());
//		vo.setEnterpriseId(product.getEnterpriseId());
//		List<Stock> stockList = findStockByVo(vo);
////		List<Stock> stockList = stockDao.findStockList(product.getProductCd(), product.getBrandNumber(),
////				product.getFactoryId(), product.getWarehouseName(), product.getEnterpriseId());
//		// 卖的数量
//		BigDecimal dealNumber = product.getDealNumber();
//		if (stockList != null && stockList.size() > 0) {
//			Stock stock = stockList.get(0);
//			// 库存可用数量
//			BigDecimal stockRealNumber = stock.getRealNumber();
//			// 冻结数量
//			BigDecimal stockFrozenNumber = stock.getFrozenNumber();
//
//			StockFlowVo sfVo = null;
//			if (isBack) {
//				//销售驳回，这里不是作废
//				if (stockFrozenNumber.compareTo(dealNumber) >= 0) {
//					// 如果冻结数量>可卖数量，则减少冻结数量，增加可用数量
//					stockFrozenNumber = stockFrozenNumber.subtract(dealNumber);
//					stockRealNumber = stockRealNumber.add(dealNumber);
//					stock.setRealNumber(stockRealNumber);
//					stock.setFrozenNumber(stockFrozenNumber);
//					this.save(stock);
//				} else {
//					// 销售数量大于现货，则需要减少在途数量
//					stock.setRealNumber(stockRealNumber.add(stockRealNumber));
//					stock.setFrozenNumber(BigDecimal.ZERO);
//					this.save(stock);
//					dealNumber = dealNumber.subtract(stockRealNumber);
//					if (stockList.size() > 1) {
//						stock = stockList.get(1);
//						stockRealNumber = stock.getRealNumber();
//						stockFrozenNumber = stock.getFrozenNumber();
//						stock.setRealNumber(stockRealNumber.add(dealNumber));
//						stock.setFrozenNumber(stockFrozenNumber.subtract(dealNumber));
//						this.save(stock);
//					}
//				}
//				stockFlowService.deleteOnBack(userInfor.getApproveId(), applyType);
//
//			} else {
//				if (stockRealNumber.compareTo(dealNumber) >= 0) {
//					// 如果库存数量>可卖数量
//					stockRealNumber = stockRealNumber.subtract(dealNumber);
//					stock.setRealNumber(stockRealNumber);
//					stock.setFrozenNumber(stockFrozenNumber.add(dealNumber));
//					this.save(stock);
//				} else {
//					// 销售数量大于现货，则需要减少在途数量
//					stock.setRealNumber(BigDecimal.ZERO);
//					stock.setFrozenNumber(stockFrozenNumber.add(stockRealNumber));
//					this.save(stock);
//					dealNumber = dealNumber.subtract(stockRealNumber);
//					if (stockList.size() > 1) {
//						stock = stockList.get(1);
//						stockRealNumber = stock.getRealNumber();
//						stockFrozenNumber = stock.getFrozenNumber();
//						stock.setRealNumber(stockRealNumber.subtract(dealNumber));
//						stock.setFrozenNumber(stockFrozenNumber.add(dealNumber));
//						this.save(stock);
//					}
//				}
//				// 保存库存流水
//				sfVo = getStockFlowVo(product, userInfor, applyType);
//				stockFlowService.saveStockFlow(sfVo, stock);
//			}
//
//
//			StockDetailRequest request =StockDetailRequest.build(product);
//			request.setApplyId(userInfor.getApproveId());
//			request.setApproveId(userInfor.getApproveId());
//			request.setLinkContractId(linkVo.getLinkContractId());
//			request.setLinkDetailId(linkVo.getStockDetailId());
//			request.setStockContractId(linkVo.getStockContractId());
//			request.setBack(isBack);
//			request.setApplyType(applyType);
//			stockDetailFacade.saveSell(request);
////			stockDetailService.saveSell(product, isBack, stock, linkVo);
//
//		} else {
//			// new RuntimeException(cause)
//
//		}
//	}
//
//	/**
//	 * 出库之后.总数量totalNumber-出库数量，冻结数量-出库数量
//	 *
//	 * @param request
//	 * @param type
//	 */
//	private void updateDeliveryOutStock(StockRequest request, BizUserInfor userInfor) throws ApplicationException {
//		List<Stock> stockList = stockDao.findStockList(request.getProductCd(), request.getBrandNumber(),
//				request.getFactoryId(), request.getWarehouseNameNew(), request.getEnterpriseId());
//		// 实际出库数量
//		BigDecimal dealNumber = request.getDealNumber();
//		if (dealNumber.compareTo(BigDecimal.ZERO)<=0) {
//			//如果操作数量小于等于0，不处理库存
//			return;
//		}
//		if (stockList != null && stockList.size() > 0) {
//			Stock stock = stockList.get(0);
//
//			BigDecimal stockTotalNumber = stock.getTotalNumber();
//			// 冻结数量
//			BigDecimal stockFrozenNumber = stock.getFrozenNumber();
//			if (!request.isBack()) {
//				// 冻结数量>出库数量，冻结数量足够，直接扣除当前冻结数量
//				if (stockFrozenNumber.compareTo(dealNumber) >= 0) {
//					// 如果库存数量>可卖数量
//					stockTotalNumber = stockTotalNumber.subtract(dealNumber);
//					stock.setTotalNumber(stockTotalNumber);
//					stock.setFrozenNumber(stockFrozenNumber.subtract(dealNumber));
//					this.save(stock);
//				} else {
//					// 出库数量>现货数量，冻结数量不足，扣除下一笔的冻结数量
//					stock.setFrozenNumber(BigDecimal.ZERO);
//					BigDecimal totalNumber = stockTotalNumber.subtract(dealNumber);
//					if (totalNumber.compareTo(BigDecimal.ZERO)<0) {
//						throw new ApplicationException("库存数量不足，操作失败！");
//					}
//					stock.setTotalNumber(totalNumber);
//					this.save(stock);
//					dealNumber = dealNumber.subtract(stockFrozenNumber);
//					if (stockList.size() > 1) {
//						stock = stockList.get(1);
//						stockTotalNumber = stock.getTotalNumber();
//						stockFrozenNumber = stock.getFrozenNumber();
//						stock.setRealNumber(stockTotalNumber.subtract(dealNumber));
//						stock.setFrozenNumber(stockFrozenNumber.subtract(dealNumber));
//						this.save(stock);
//					}
//				}
//			}else {
//				//出库撤回
//				// 增加冻结数量、总数量
//				stockTotalNumber = stockTotalNumber.add(dealNumber);
//				stock.setTotalNumber(stockTotalNumber);
//				stock.setFrozenNumber(stockFrozenNumber.add(dealNumber));
//				this.save(stock);
//			}
//
//			// 保存库存流水
//			StockFlowVo sfVo;
//			if (!request.isBack()) {
//				sfVo = getStockFlowVo(null, userInfor, BasConstants.APPLY_TYPE_O);
//			}else {
//				sfVo = getStockFlowVo(null, userInfor, BasConstants.OPERATE_TYPE_OB);
//			}
//			sfVo.setContractId(request.getCtrContractId());
//			sfVo.setDealNumber(request.getDealNumber());
//			sfVo.setDealPrice(request.getDealPrice());
//			sfVo.setTotalPrice(request.getDealNumber().multiply(request.getDealPrice()));
//			this.stockFlowService.saveStockFlow(sfVo, stock);
//
//			request.setStockId(stock.getId());
//			request.setApplyNo(userInfor.getApproveNo());
////			product.setStockRemainNumber(stock.getRealNumber());
////			product.setStockRemainFrozen(stock.getFrozenNumber());
////			stockDetailFacade.saveDeliveryOut(request);
//		} else {
//			if(dealNumber.compareTo(BigDecimal.ZERO)>0){
//				throw new ApplicationException("无库存出库");
//			}
//		}
//	}
//
//	@Override
//	public List<Stock> findStockByVo(ApplySellWarehouseVo vo) {
//		Map<String, Object> queryParams = new HashMap<String, Object>();
//		if (StringUtils.isNotBlank(vo.getProductCode())) {
//			queryParams.put("EQS_productCd", vo.getProductCode());// 商品类型
//		}
//		if (vo.getFactoryId() != null && vo.getFactoryId() > 0) {
//			queryParams.put("EQL_factoryId", vo.getFactoryId());// 厂商
//		}
//		if (StringUtils.isNotBlank(vo.getBrandNumber())) {
//			queryParams.put("EQS_brandNumber", vo.getBrandNumber());// 牌号
//		}
//		if (StringUtils.isNotBlank(vo.getWarehouseName())) {
//			queryParams.put("EQS_warehouseName", vo.getWarehouseName());
//		}
//		if (StringUtils.isNotBlank(vo.getProductAttr())) {
//			queryParams.put("EQS_productAttr", vo.getProductAttr());
//		}
////		queryParams.put("GTM_realNumber", BigDecimal.ZERO);
//		Specification<Stock> spec = WebUtil.buildSpecification(queryParams);
//		List<Stock> stockList = this.stockDao.findAll(spec);
//
//		return stockList;
//	}
//
//	@Override
//	public StockVo findDealNumber(ApplySellWarehouseVo vo) {
//		List<Object> list =  stockDao.findDealNumber(vo.getProductCode(), vo.getBrandNumber(), vo.getFactoryId(),vo.getWarehouseName(),vo.getEnterpriseId());
//		BigDecimal totalFrozenNumber = BigDecimal.ZERO;
//		BigDecimal totalRealNumber = BigDecimal.ZERO;
//		for (Iterator<Object> iterator = list.iterator(); iterator.hasNext(); ) {
//		       Object[] rows = (Object[]) iterator.next();
//		       BigDecimal frozenNumber = (BigDecimal)rows[0];
//		       BigDecimal realNumber = (BigDecimal)rows[1];
//		       totalFrozenNumber = totalFrozenNumber.add(frozenNumber);
//		       totalRealNumber = totalRealNumber.add(realNumber);
//		}
//		StockVo stockVo = new StockVo(totalFrozenNumber,totalRealNumber);
//		return stockVo;
//	}
//
//	@Override
//	public void deliveryInStockByMatch(List<CtrProduct> lstProd, Long contractId, BizUserInfor userInfor)
//			throws ApplicationException {
//		for (CtrProduct product : lstProd) {
//			StockRequest request = StockRequest.build(product);
//			// 采购
//			request.setApplyType(BasConstants.APPLY_TYPE_I);
//			request.setContractType(BasConstants.CONTRACT_TYPE_B);
//			request.setMatchBl(true);
//			request.setCtrContractId(contractId);
//			request.setApplyId(null);
//			updateDeliveryStock(request, userInfor);
//		}
//
//	}
//
//	@Override
//	@ServerTransactional
//	public void cancelContract(List<CtrContract> contractList, CtrConctractInvalidVo vo) throws ApplicationException {
//		if (contractList != null) {
//			if (contractList.size() == 1) {
//				CtrContract contract = contractList.get(0);
//				String source = contract.getSource();
//				List<CtrProduct> productList = productService.findByContractId(contract.getId());
//				if (BasConstants.APPLY_TYPE_B.equals(source)) {
//					// 采购
//					for (CtrProduct product : productList) {
//						cancelBuyProduct(product, vo);
//					}
//				} else if(BasConstants.APPLY_TYPE_S.equals(source) && !contract.getSource().equals(BasConstants.APPLY_TYPE_L)){
//					// 销售还原数据
//					for (CtrProduct product : productList) {
//						cancelSellProduct(product, product.getProductAttr(), vo);
//					}
//				}
//			} else {
//				// 多条，则为撮合业务
//				for (CtrContract contract : contractList) {
//					// 先撤销销售数据，再撤销入库数据，最后撤销采购数据
//					String applyType = contract.getContractType();
//					List<CtrProduct> productList = productService.findByContractId(contract.getId());
//					if (BasConstants.APPLY_TYPE_S.equals(applyType) && !contract.getSource().equals(BasConstants.APPLY_TYPE_L)) {
//						// 销售还原数据
//						for (CtrProduct product : productList) {
//							cancelSellProduct(product, BasConstants.STOCK_PRODUCT_ATTR_N, vo);
//						}
//					} else {
//						// 先把入库的数据还原，再撤销买家数据
//						for (CtrProduct product : productList) {
//							cancelDeliveryInAndBuyProduct(product, vo);
//						}
//
//					}
//				}
//			}
//
//		}
//
//	}
//
//	private void cancelDeliveryInAndBuyProduct(CtrProduct product, CtrConctractInvalidVo vo)
//			throws ApplicationException {
//		BigDecimal dealNumber = product.getDealNumber();
//		// 查询现货数据，在途入库
//		List<Stock> stockList = stockDao.findStock4CancelBuy(product.getProductCd(), product.getBrandNumber(),
//				product.getFactoryId(), product.getEnterpriseId(), dealNumber, product.getCtrContractId()+"",product.getStockDetailId());
//		if (stockList != null && stockList.size() == 1) {
//			Stock stock = stockList.get(0);
//			// 修改库存
//			BigDecimal curRealNumber = stock.getRealNumber().subtract(dealNumber);
//			// 还存在库存数量，修改平均价
//			if (curRealNumber.compareTo(BigDecimal.ZERO) > 0) {
//				stock.setAveragePrice(averageSubPrice(stock.getTotalNumber(), dealNumber, stock.getAveragePrice(),
//						product.getDealPrice()));
//			}
//			stock.setTotalNumber(stock.getTotalNumber().subtract(dealNumber));
//			stock.setRealNumber(curRealNumber);
//			stockDao.save(stock);
//			// 修改库存明细数据
//			stockFlowService.cancelContract(stock,dealNumber, vo, product.getCtrContractId(), BasConstants.APPLY_TYPE_B);
//			stockDetailFacade.cancelDeliveryInAndBuyProduct(product);
////			List<StockDetail> detailList = stockDetailService.findByBuyContractId(product.getCtrContractId() + "");
////			if (detailList != null && detailList.size() > 0) {
////				for (StockDetail detail : detailList) {
////					if (StringUtils.isNotBlank(detail.getSellContractId())) {
////						throw new ApplicationException("该采购已存在销售申请");
////					}
////					BigDecimal availableNumber = detail.getAvailableNumber();
////					if (availableNumber.compareTo(dealNumber) >= 0) {
////						BigDecimal avail = availableNumber.subtract(dealNumber);
////						detail.setAvailableNumber(avail);
////						detail.setDeliveryInNumber(avail);
////						dealNumber = BigDecimal.ZERO;
////
////					} else {
////						dealNumber = dealNumber.subtract(availableNumber);
////						detail.setAvailableNumber(BigDecimal.ZERO);
////						detail.setDeliveryInNumber(BigDecimal.ZERO);
////					}
////					this.stockDetailService.save(detail);
////					// 更新历史数据
////					this.stockDetailHisService.insertHisByCancel(stock, detail, product, BasConstants.APPLY_TYPE_B, vo);
////					if (dealNumber.compareTo(BigDecimal.ZERO) == 0) {
////						break;
////					}
////				}
////			}
//		} else {
//			throw new ApplicationException("该撮合订单已出库或移库");
//		}
//	}
//
//	/** 撤销销售 */
//	private void cancelSellProduct(CtrProduct product, String productAttr, CtrConctractInvalidVo vo)
//			throws ApplicationException {
//		List<StockDetailCancelSellResp> lstResp = stockDetailFacade.cancelSellProduct(product);
//		for (StockDetailCancelSellResp resp : lstResp) {
//
//			if (resp != null) {
//				BigDecimal fixNumber = resp.getDealNumber();
//				Stock stock = stockDao.findOne(resp.getStockId());
//				if (stock.getFrozenNumber().compareTo(fixNumber) >= 0) {
//					// 冻结数量足够
//					// 修改库存
//					BigDecimal curFrozenlNumber = stock.getFrozenNumber().subtract(fixNumber);
//					// 还存在库存数量，修改平均价
//					if (curFrozenlNumber.compareTo(BigDecimal.ZERO) > 0) {
//						stock.setAveragePrice(averageSubPrice(stock.getTotalNumber(), fixNumber,
//								stock.getAveragePrice(), product.getDealPrice()));
//					}
//					stock.setFrozenNumber(curFrozenlNumber);
//					stock.setRealNumber(stock.getRealNumber().add(fixNumber));
//				} else {
//					// 冻结数量不够，将当前冻结数量置为0
//					stock.setFrozenNumber(BigDecimal.ZERO);
//					stock.setRealNumber(stock.getRealNumber().add(stock.getFrozenNumber()));
//					// 计算剩余数量
//					fixNumber = stock.getFrozenNumber();
//				}
//				stockDao.save(stock);
//				stockFlowService.cancelContract(stock, fixNumber, vo, product.getCtrContractId(),
//						BasConstants.APPLY_TYPE_S);
//
//			}
//		}
//	}
//
//	/**
//	 * 撤销采购库存，还原数据
//	 *
//	 * @param product
//	 * @throws ApplicationException
//	 */
//	private void cancelBuyProduct(CtrProduct product, CtrConctractInvalidVo vo) throws ApplicationException {
//		BigDecimal dealNumber = product.getDealNumber();
//		List<Stock> stockList = stockDao.findStock4CancelBuy(product.getProductCd(), product.getBrandNumber(),
//				product.getFactoryId(), product.getEnterpriseId(), dealNumber,
//				product.getCtrContractId()+"",product.getStockDetailId());
//		if (stockList != null && stockList.size() == 1) {
//			Stock stock = stockList.get(0);
//			// 修改库存
//			BigDecimal curRealNumber = stock.getRealNumber().subtract(dealNumber);
//			// 还存在库存数量，修改平均价
//			if (curRealNumber.compareTo(BigDecimal.ZERO) > 0) {
//				stock.setAveragePrice(averageSubPrice(stock.getTotalNumber(), dealNumber, stock.getAveragePrice(),
//						product.getDealPrice()));
//			}
//			stock.setTotalNumber(stock.getTotalNumber().subtract(dealNumber));
//			stock.setRealNumber(curRealNumber);
//			stockDao.save(stock);
//			stockFlowService.cancelContract(stock,dealNumber, vo, product.getCtrContractId(), BasConstants.APPLY_TYPE_B);
//			// 修改库存明细数据
//			stockDetailFacade.cancelBuyProduct(product);
//		} else {
//			logger.warn("cancelBuyProduct! product:{}, vo:{}", JsonUtil.obj2Json(product), JsonUtil.obj2Json(vo));
//			throw new ApplicationException("该采购已存在销售申请或入库申请");
//
//		}
//	}
//
//	/**
//	 * 平均价格扣减计算
//	 *
//	 * @param curTotalNumber
//	 *            现总数量
//	 * @param subDealNumber
//	 *            扣减数量
//	 * @param curAveragePrice
//	 *            现平均价
//	 * @param subDealPrice
//	 *            扣减单价
//	 * @return
//	 */
//	private BigDecimal averageSubPrice(BigDecimal totalNumber, BigDecimal subDealNumber, BigDecimal curAveragePrice,
//			BigDecimal subDealPrice) {
//
//		BigDecimal totalPrice = totalNumber.multiply(curAveragePrice).subtract(subDealNumber.multiply(subDealPrice));
//		BigDecimal nowAveragePrice = BigDecimal.ZERO;
//		BigDecimal nowNumber = totalNumber.subtract(subDealNumber);
//		if (totalPrice.compareTo(BigDecimal.ZERO) > 0 && nowNumber.compareTo(BigDecimal.ZERO) > 0) {
//			nowAveragePrice = totalPrice.divide(nowNumber, 2, BigDecimal.ROUND_HALF_UP);
//		}
//		return nowAveragePrice;
//
//	}
//
//	private StockFlowVo getStockFlowVo(CtrProduct product, BizUserInfor userInfor, String applyType) {
//		StockFlowVo sfVo = new StockFlowVo();
//		sfVo.setApplyId(userInfor.getApproveId());
//		sfVo.setOperationType(applyType);
//		if (applyType.equals(BasConstants.APPLY_TYPE_B) || applyType.equals(BasConstants.APPLY_TYPE_I) || applyType.equals(BasConstants.APPLY_TYPE_A)
//				|| applyType.equals(BasConstants.OPERATE_TYPE_SC)|| applyType.equals(BasConstants.OPERATE_TYPE_OB)) {
//			sfVo.setAddSub(BasConstants.STOCK_NUMBER_ADD);
//		} else {
//			sfVo.setAddSub(BasConstants.STOCK_NUMBER_SUB);
//		}
//		if (product != null) {
//			sfVo.setContractId(product.getCtrContractId());
//			sfVo.setDealNumber(product.getDealNumber());
//			sfVo.setDealPrice(product.getDealPrice());
//			sfVo.setTotalPrice(product.getTotalPrice());
//		}
//		sfVo.setCreatedUserId(userInfor.getBizUserId());
//		sfVo.setCreatedUserName(userInfor.getBizUserName());
//		return sfVo;
//	}
//
//	/**
//	 * 移库更新库存
//	 * */
//	@Override
//	public Stock updateStock(StockDetail entity, StockDetailMoveVo changeVo) {
//		BigDecimal moveAvailableNumber = changeVo.getMoveRealNumber();//改变的可用数量
//		BigDecimal moveFrozenNumber = changeVo.getMoveFrozenNumber();//改变的冻结数量
//		BigDecimal movetotalNumber = moveAvailableNumber.add(moveFrozenNumber);
//		BigDecimal movetotalPrice = movetotalNumber.multiply(entity.getDealPrice());
//		//更新原仓库库存
//		Stock oldStock = stockDao.findOne(entity.getStockId());
//		oldStock.setRealNumber(oldStock.getRealNumber().subtract(moveAvailableNumber));
//		oldStock.setFrozenNumber(oldStock.getFrozenNumber().subtract(moveFrozenNumber));
//		oldStock.setTotalNumber(oldStock.getTotalNumber().subtract(movetotalNumber));
//		//oldStock.setTotalPrice(oldStock.getTotalPrice().subtract(movetotalPrice));
//		//更新现仓库库存
//		Map<String, Object> queryParams = new HashMap<String, Object>();
//		queryParams.put("EQS_productCd", entity.getProductCd());
//		queryParams.put("EQL_factoryId", entity.getFactoryId());
//		queryParams.put("EQS_brandNumber", entity.getBrandNumber());
//		queryParams.put("EQS_warehouseName", changeVo.getWarehouseName());
//		queryParams.put("EQL_enterpriseId", entity.getEnterpriseId());
//		Stock newStock = this.findEntityByParam(queryParams);
//		if(newStock == null){
//			newStock = new Stock();
//			BeanUtils.copyProperties(entity, newStock);
//			newStock.setTotalNumber(movetotalNumber);
//			newStock.setAveragePrice(entity.getDealPrice());
//			newStock.setFrozenNumber(moveFrozenNumber);
//			newStock.setId(0l);
//			newStock.setWarehouseName(changeVo.getWarehouseName());
//			newStock.setRealNumber(moveAvailableNumber);
//			newStock.setTotalPrice(movetotalPrice);
//		}else{
//			newStock.setFrozenNumber(newStock.getFrozenNumber().add(moveFrozenNumber));
//			newStock.setRealNumber(newStock.getRealNumber().add(moveAvailableNumber));
//			newStock.setTotalNumber(newStock.getTotalNumber().add(movetotalNumber));
//			newStock.setTotalPrice(newStock.getTotalPrice().add(movetotalPrice));
//			newStock.setAveragePrice(averagePrice(newStock.getTotalNumber(),movetotalNumber,newStock.getAveragePrice(),entity.getDealPrice()));
//		}
//		oldStock = stockDao.save(oldStock);
//		newStock = stockDao.save(newStock);
//		//保存库存流水
//		stockFlowService.insert2Flow(oldStock, newStock, entity,changeVo);
//		return newStock;
//	}
//
//	/**
//	 * 预售审批完成产生库存扣减策略：
//	 *  现货库存不为空，先从现货里边减，可用减到0，再从在途里边减，若在途库存为空，则新增一条在途库存
//	 * 	现货库存为空，直接从在途库存里边扣减，若在途库存为空，则新增在途库存
//	 * */
//	@Override
//	@ServerTransactional
//	public void updatePresellStock(CtrProduct product, BizUserInfor userInfor) throws ApplicationException {
//		Stock stock = findStock(product);
//		BigDecimal dealNumber = product.getDealNumber();
//		//总数量=冻结+可用-预售
//		if(stock == null){
//			stock = newStockP(product);
//			stock.setPresellNumber(dealNumber);
//			stock.setTotalNumber(dealNumber.negate());
//		}else{
//			stock.setPresellNumber(dealNumber);
//			stock.setTotalNumber(stock.getFrozenNumber().add(stock.getRealNumber()).subtract(stock.getPresellNumber()));
//		}
//		stock = stockDao.save(stock);
//		// 保存在途库存流水
//		StockFlowVo sfVo = getStockFlowVo(product, userInfor, BasConstants.APPLY_TYPE_L);
//		this.stockFlowService.saveStockFlow(sfVo, stock);
//
//		//保存预售库存明细
//		stockDetailPresellService.savePresellDetail(product, userInfor);
//	}
//
//	private Stock newStockP(CtrProduct product){
//		Stock stock = new Stock();
//		stock.setProductAttr(BasConstants.STOCK_PRODUCT_ATTR_P);
//		stock.setProductCd(product.getProductCd());
//		stock.setProductName(product.getProductName());
//		stock.setBrandNumber(product.getBrandNumber());
//		stock.setFactoryId(product.getFactoryId());
//		stock.setFactoryName(product.getFactoryName());
//		stock.setWarehouseName(product.getWarehouseName());
//		stock.setEnterpriseId(product.getEnterpriseId());
//		return stock;
//	}
//
//	@Override
//	public Page<Stock> findPageStock(StockSearchVo queryVo) {
//		Sort sort = new Sort(Direction.DESC, "id");
//		String stockStatus = queryVo.getStockStatus();
//		PageRequest pageRequest = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows(),sort);
//		Specification<Stock> spe = WebUtil.buildSpecification(queryVo.getSearchParams());
//		spe = dealFindCondition(spe,stockStatus);
//		Page<Stock> page = getBaseDao().findAll(spe, pageRequest);
//		// sort属性无法反序列化，下面代码重新组装page对象，去掉sort属性
//		PageRequest pageRequest_new = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows());
//		Page<Stock> pageVo = new PageImpl<>(page.getContent(), pageRequest_new, page.getTotalElements());
//		return pageVo;
//	}
//
//	private Specification<Stock> dealFindCondition(Specification<Stock> spe,String stockStatus){
//
//		if(StringUtils.isNotBlank(stockStatus)){
//			if ("C".equals(stockStatus)){
//				Specification<Stock> spec_froze = WebUtil.buildSpecification("GTM_frozenNumber", BigDecimal.ZERO);
//				Specification<Stock> spec_real = WebUtil.buildSpecification("GTM_realNumber", BigDecimal.ZERO);
//				Specification<Stock> specNum = Specification.where(spec_froze).or(spec_real);
//				spe=Specification.where(spe).and(specNum);
//			}
//			if ("A".equals(stockStatus)){
//				Specification<Stock> spec_froze = WebUtil.buildSpecification("EQM_frozenNumber", BigDecimal.ZERO);
//				Specification<Stock> spec_real = WebUtil.buildSpecification("EQM_realNumber", BigDecimal.ZERO);
//				Specification<Stock> specNum = Specification.where(spec_froze).and(spec_real);
//				spe=Specification.where(spe).and(spec_froze).and(specNum);
//			}
//		}
//		return spe;
//	}
//
//	@Override
//	@ServerTransactional
//	public void updateByAdjust(StockAdjustAuditVo vo, StockDetail detail,String operationType) {
//		String type = vo.getType();
//		BigDecimal diffNumber = vo.getDifferentNumber();
//		Stock stock = stockDao.findOne(detail.getStockId());
//		if(type.equals("F")){
//			stock.setFrozenNumber(stock.getFrozenNumber().add(diffNumber.negate()));
//		}else{
//			stock.setRealNumber(stock.getRealNumber().add(diffNumber.negate()));
//		}
//		stock.setTotalNumber(stock.getFrozenNumber().add(stock.getRealNumber()));
//		stock = stockDao.save(stock);
//		stockFlowService.saveAdjust(stock, detail,operationType,vo);
//	}
//
//	@Override
//	@ServerTransactional
//	public void cancelPresellProduct(CtrProduct product, CtrConctractInvalidVo vo) throws ApplicationException{
//		//预售库存明细数据还原
//		StockDetailPresell presell = stockDetailPresellService.findByCtrProductId(product.getId());
//		BigDecimal presellNumber = presell.getPresellNumber();
//
//		Stock stock = stockDao.findOne(presell.getStockId());
//		stock.setPresellNumber(stock.getPresellNumber().subtract(presellNumber));
//		stock.setTotalNumber(stock.getTotalNumber().add(presellNumber));
////		stock.setFrozenNumber(BigDecimal.ZERO);
//		stock = stockDao.save(stock);
//		//预售撤销流水
//		stockFlowService.cancelContract(stock, presell.getPresellNumber(), vo, presell.getContractId(), BasConstants.APPLY_TYPE_L);
//	}
//
//	@Override
//	@ServerTransactional
//	public void cancelPreBuyContract(CtrProduct product,CtrConctractInvalidVo vo) throws ApplicationException{
//		Stock stock = findStock(product);
//		BigDecimal dealNumber = product.getDealNumber();
//		stock.setFrozenNumber(stock.getFrozenNumber().subtract(dealNumber));
//		stock.setPresellNumber(stock.getPresellNumber().add(dealNumber));
//		stock.setTotalNumber(stock.getTotalNumber().subtract(dealNumber));
//		stock = stockDao.save(stock);
//		//预售撤销流水
//		stockFlowService.cancelContract(stock, dealNumber, vo, product.getCtrContractId(), BasConstants.APPLY_TYPE_B);
//
//		//更新库存明细记录
//		stockDetailFacade.cancelPreBuyContract(product, vo);
//	}
//
//	@Override
//	@ServerTransactional
//	public void updateByContractAdjust(ApplyContractAdjustRequestVo vo,Long stockId){
//		Stock stock = stockDao.findOne(stockId);
//
//		//差值
//		BigDecimal dealNumber = vo.getDealNumber();
//
//		if(vo.getContractType().equals(BasConstants.CONTRACT_TYPE_B)){
//			if (vo.getDealNumber().compareTo(BigDecimal.ZERO)>0) {
//				//采购数量调大
//			}else {
//				//采购数量调小
//			}
//			stock.setRealNumber(stock.getRealNumber().add(vo.getDealNumber()));
//		}else{
//			if(vo.getIsback()){
//				//还原原库存主表信息
//				stock.setFrozenNumber(stock.getFrozenNumber().subtract(dealNumber));
//				stock.setRealNumber(stock.getRealNumber().add(dealNumber));
//			}else{
//				stock.setFrozenNumber(stock.getFrozenNumber().add(dealNumber));
//				stock.setRealNumber(stock.getRealNumber().subtract(dealNumber));
////				if(vo.getOperationType().equals( BasConstants.OPERATE_TYPE_CA)){
////					stock.setRealNumber(stock.getRealNumber().add(dealNumber));
////					stock.setFrozenNumber(stock.getFrozenNumber().subtract(dealNumber));
////				}else{
////					stock.setRealNumber(stock.getRealNumber().subtract(dealNumber));
////					stock.setFrozenNumber(stock.getFrozenNumber().add(dealNumber));
////				}
//			}
//		}
//		stock.setTotalNumber(stock.getRealNumber().add(stock.getFrozenNumber()));
//		stock = stockDao.save(stock);
//
//		//添加库存流水
//		StockFlowVo sfVo = new StockFlowVo();
//		String addSub = StringUtils.equals(vo.getOperationType(), BasConstants.OPERATE_TYPE_CA) ? BasConstants.STOCK_NUMBER_ADD : BasConstants.STOCK_NUMBER_SUB;
//		sfVo.setAddSub(addSub);
//		sfVo.setApplyId(vo.getApplyId());
//		sfVo.setContractId(vo.getContractId());
//		sfVo.setContractNo(vo.getContractNo());
//		sfVo.setCreatedUserId(vo.getUserId());
//		sfVo.setCreatedUserName(vo.getUserName());
//		sfVo.setDealNumber(dealNumber.abs());
//		sfVo.setOperationType(vo.getOperationType());
//		stockFlowService.saveStockFlow(sfVo, stock);
//	}
//}
