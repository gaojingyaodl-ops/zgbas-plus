package com.spt.bas.server.stock.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.entity.StockContract;
import com.spt.bas.client.entity.StockContractRela;
import com.spt.bas.client.vo.ApplyContractAdjustRequestVo;
import com.spt.bas.client.vo.StockDetailRequest;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.dao.CtrProductDao;
import com.spt.bas.server.dao.StockContractDao;
import com.spt.bas.server.stock.service.IStockContractRelaService;
import com.spt.bas.server.stock.service.IStockContractService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.exception.InvalidParamException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

@Component
public class StockContractServiceImpl extends BaseService<StockContract> implements IStockContractService {
	@Autowired
	private StockContractDao stockContractDao;
	@Autowired
	private CtrProductDao ctrProductDao;
	@Autowired
	private CtrContractDao ctrContractDao;
	
	@Autowired
	private IStockContractRelaService stockContractRelaService;

	@Override
	public BaseDao<StockContract> getBaseDao() {
		return stockContractDao;
	}

	@Override
	public Class<StockContract> getEntityClazz() {
		return StockContract.class;
	}

	@Override
	public StockContract saveBuy(StockDetailRequest request) {
		StockContract sc = new StockContract();
		sc.setBrandNumber(request.getBrandNumber());
		sc.setBuyContractId(request.getCtrContractId());
		sc.setBuyNumber(request.getDealNumber());
		sc.setBuyProductId(request.getCtrProductId());
		sc.setDealPrice(request.getDealPrice());
		sc.setEnterpriseId(request.getEnterpriseId());
		sc.setFactoryId(request.getFactoryId());
		sc.setFactoryName(request.getFactoryName());
		sc.setProductCd(request.getProductCd());
		sc.setProductName(request.getProductName());
		sc.setWarehouseId(request.getWarehouseId());
		sc.setWarehouseName(request.getWarehouseName());
		sc.setBizUserId(request.getBizUserId());
		sc.setBizUserName(request.getBizUserName());
		return stockContractDao.save(sc);
	}

	@Override
	public void cancelBuy(CtrProduct product) {

		stockContractDao.deleteByBuyProductId(product.getId());
	}

	@Override
	public void saveSell(StockDetailRequest request) throws ApplicationException {
		if (request.getStockContractId() == null || request.getStockContractId() == 0L) {
			throw new InvalidParamException("stockContractId");
		}
		StockContract sc = getEntity(request.getStockContractId());
		BigDecimal sellingNumber = sc.getSellingNumber().add(request.getDealNumber());
		BigDecimal sellTotal = sellingNumber.add(sc.getSellNumber());

		if (sc.getBuyNumber().compareTo(sellTotal)<0) {
			throw new InvalidParamException("剩余数量不足");
		}


		sc.setSellingNumber(sellingNumber);
		stockContractDao.save(sc);
	}

	@Override
	public void saveSellBack(StockDetailRequest request) throws ApplicationException {
		if (request.getStockContractId() == null || request.getStockContractId() == 0L) {
			throw new InvalidParamException("stockContractId");
		}
		StockContract sc = getEntity(request.getStockContractId());
		sc.setSellingNumber(sc.getSellingNumber().subtract(request.getDealNumber()));
		stockContractDao.save(sc);
	}

	@Override
	public void cancelSell(CtrProduct product) throws ApplicationException {
		StockContractRela rela = stockContractRelaService.findSellByCtrProductId(product.getId());
		if (rela == null) {
			return;
		}
		StockContract sc = getEntity(rela.getStockContractId());
		if (sc != null) {
			BigDecimal sellNumber = sc.getSellNumber().subtract(product.getDealNumber());
			if (sellNumber.compareTo(BigDecimal.ZERO) < 0) {
				throw new InvalidParamException("已销售数量不足，无法作废");
			}
			sc.setSellNumber(sc.getSellNumber().subtract(product.getDealNumber()));
			stockContractDao.save(sc);
		}
		stockContractRelaService.delete(rela.getId());
	}

	@Override
	public void saveSellComplete(StockDetailRequest request) throws ApplicationException {
		sell(request, request.getMatchBl());
	}
	private StockContract sell(StockDetailRequest request,boolean direct) throws ApplicationException {
		if (request.getStockContractId() == null || request.getStockContractId() == 0L) {
			throw new InvalidParamException("stockContractId");
		}
		StockContract sc = getEntity(request.getStockContractId());
		if(!direct) {
			BigDecimal subtract = sc.getSellingNumber().subtract(request.getDealNumber());
			//预售采购合同库存 销售中的数量进行判断
			if (subtract.compareTo(BigDecimal.ZERO) < 0) {
				subtract = BigDecimal.ZERO;
			}
			sc.setSellingNumber(subtract);
			//sc.setSellingNumber(sc.getSellingNumber().subtract(request.getDealNumber()));
		}
		sc.setSellNumber(sc.getSellNumber().add(request.getDealNumber()));
		sc = stockContractDao.save(sc);
		saveRela(request, StockContractRela.RELATYPE_SELL);
		return sc;
	}

	@Override
	public void saveDeliveryIn(StockDetailRequest request) throws ApplicationException {
		if (request.getStockContractId() == null || request.getStockContractId() == 0L) {
			throw new InvalidParamException("stockContractId");
		}
		StockContract sc = getEntity(request.getStockContractId());
		sc.setDeliveryInNumber(sc.getDeliveryInNumber().add(request.getDealNumber()));
		stockContractDao.save(sc);
		saveRela(request, StockContractRela.RELATYPE_IN);
	}

	@Override
	public void cancelDeliveryIn(StockDetailRequest request) throws ApplicationException {
		if (request.getStockContractId() == null || request.getStockContractId() == 0L) {
			throw new InvalidParamException("stockContractId");
		}
		StockContract sc = getEntity(request.getStockContractId());
		sc.setDeliveryInNumber(sc.getDeliveryInNumber().subtract(request.getDealNumber()));
		stockContractDao.save(sc);
		List<StockContractRela> lstRela = stockContractRelaService.findByApproveId(request.getApproveId());
		lstRela.forEach(rela->{
			try {
				stockContractRelaService.delete(rela.getId());
			} catch (ApplicationException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void saveDeliveryOut(StockDetailRequest request) throws ApplicationException {
		if (request.getStockContractId() == null || request.getStockContractId() == 0L) {
			throw new InvalidParamException("stockContractId");
		}
		StockContract sc = getEntity(request.getStockContractId());
		sc.setDeliveryOutNumber(sc.getDeliveryOutNumber().add(request.getDealNumber()));
		stockContractDao.save(sc);
		saveRela(request, StockContractRela.RELATYPE_OUT);
	}

	@Override
	public void cancelDeliveryOut(StockDetailRequest request) throws ApplicationException {
		if (request.getStockContractId() == null || request.getStockContractId() == 0L) {
			throw new InvalidParamException("stockContractId");
		}
		StockContract sc = getEntity(request.getStockContractId());
		sc.setDeliveryOutNumber(sc.getDeliveryOutNumber().subtract(request.getDealNumber()));
		stockContractDao.save(sc);
		List<StockContractRela> lstRela = stockContractRelaService.findByApproveId(request.getApproveId());
		lstRela.forEach(rela->{
			try {
				stockContractRelaService.delete(rela.getId());
			} catch (ApplicationException e) {
				e.printStackTrace();
			}
		});
	}

	/** 合同调整 */
	@Override
	public StockContract[] saveAdjust(ApplyContractAdjustRequestVo request) throws ApplicationException {
		if (request.getOldStockContractId() == null || request.getOldStockContractId() == 0L) {
			throw new InvalidParamException("stockContractId");
		}
		StockContract[] objs =new StockContract[2];
		StockContract sc = getEntity(request.getOldStockContractId());
		// 差值
		BigDecimal diffNumber = request.getNewDealNumber().subtract(request.getOldDealNumber());
		if (request.getContractType().equals(BasConstants.CONTRACT_TYPE_B)) {
			// 采购合同调整
			if (diffNumber.compareTo(BigDecimal.ZERO) > 0) {
				// 数量调大
				sc.setBuyNumber(sc.getBuyNumber().add(diffNumber));
			} else {
				// 数量调小
				sc.setBuyNumber(sc.getBuyNumber().add(diffNumber));
			}
			sc.setDealPrice(request.getDealPrice());
			sc = stockContractDao.save(sc);
			objs[0] = sc;
			objs[1] = sc;
			return objs;
		} else {
			// 销售合同调整
			
			if (request.getNewStockContractId() != null
					&& !request.getNewStockContractId().equals(request.getOldStockContractId())) {
				//销售重新选货源
				CtrProduct product = ctrProductDao.findOne(request.getCtrProductId());
				//撤销原销售
				cancelSell(product);
				
				//关联新合同库存
				StockDetailRequest reqSell =new StockDetailRequest();
				reqSell.setStockContractId(request.getNewStockContractId());
				reqSell.setDealNumber(request.getNewDealNumber());
				reqSell.setEnterpriseId(product.getEnterpriseId());
				reqSell.setCtrContractId(product.getCtrContractId());
				reqSell.setCtrProductId(product.getId());
				StockContract sc_new = sell(reqSell,true);
				
				objs[0] = sc;
				objs[1] = sc_new;
				return objs;
//				updateSell(request.getOldStockContractId(), request.getCtrProductId(),diffNumber.negate());
//				return updateSell(request.getStockContractId(),request.getCtrProductId(), diffNumber);
				
			}else {
				//销售原货源调整
				sc = updateSell(request.getOldStockContractId(),request.getCtrProductId(), diffNumber);
				objs[0] = sc;
				objs[1] = sc;
				return objs;
			}
		}
	}
	
	private StockContract updateSell(Long stockContractId,Long ctrProductId , BigDecimal diffNumber) throws ApplicationException {
		StockContract sc = getEntity(stockContractId);
		// 调整后的数量不能小于已出库数量
		BigDecimal sellNumber = sc.getSellNumber().add(diffNumber);
		if (sellNumber.compareTo(sc.getDeliveryOutNumber()) < 0) {
			throw new InvalidParamException("调整后销售数量不能小于已出库数量");
		}
		
		sc.setSellNumber(sc.getSellNumber().add(diffNumber));
		StockContractRela rela = stockContractRelaService.findSellByCtrProductId(ctrProductId);
		if (rela != null) {
			rela.setRelaNum(rela.getRelaNum().add(diffNumber));
			stockContractRelaService.save(rela);
		}
		return stockContractDao.save(sc);
	}

	private void saveRela(StockDetailRequest request, String relaType) throws ApplicationException {
		StockContractRela relaVo = new StockContractRela();
		relaVo.setApproveId(request.getApproveId());
		relaVo.setContractId(request.getCtrContractId());
		relaVo.setCtrProductId(request.getCtrProductId());
		relaVo.setEnterpriseId(request.getEnterpriseId());
		relaVo.setRelaNum(request.getDealNumber());
		relaVo.setRelaType(relaType);
		relaVo.setStockContractId(request.getStockContractId());
		stockContractRelaService.saveDetailRela(relaVo);
	}

	@Override
	public List<StockContract> findByBuyContractId(Long buyContractId) {
		List<StockContract> stockContracts = stockContractDao.findByBuyContractId(buyContractId);
		return stockContracts;
	}

//	@Override
//	public Page<StockContractVo> findPageStockContractList(StockContractSearchVo queryVo) {
//		Map<String, Object> params = queryVo.getSearchParams();
//		String contractNo = (String)params.get("LIKES_contractNo");
//		params.put("LIKES_contractNo", null);
//		Specification<StockContract> spec = WebUtil.buildSpecification(queryVo.getSearchParams());
//		Sort sort = new Sort(Direction.DESC, "id");
//		if(StringUtils.isNotBlank(contractNo)){
//			Specification<StockContract> spece_contractNo = new Specification<StockContract>() {
//
//				/**
//				 * 
//				 */
//				private static final long serialVersionUID = 1L;
//
//				@Override
//				public Predicate toPredicate(Root<StockContract> root, CriteriaQuery<?> query,
//						CriteriaBuilder cb) {
//					Subquery<CtrContract> sq = query.subquery(CtrContract.class);
//					Root<StockContract> sqc = sq.correlate(root);
//					Join<StockContract, CtrContract> sqo = sqc.join("contract");
//					
//					Path<String> expression = sqo.get("contractNo");
//					Predicate predicate1 = cb.like(expression, "%"+contractNo+"%");
//					Predicate predicate = cb.and(predicate1);
//					sq.select(sqo).where(predicate);
//					return cb.exists(sq);
//				}
//			};
//			spec = Specification.where(spec).and(spece_contractNo);
//		} else {
//			spec = Specification.where(spec);
//		}
//		PageRequest pageRequest = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows(), sort);
//		Page<StockContract> page = getBaseDao().findAll(spec, pageRequest);
//		ArrayList<StockContractVo> result = new ArrayList<>();
//		for (StockContract stockContract : page.getContent()) {
//			StockContractVo stockContractVo = new StockContractVo();
//			String contractNoById = ctrContractDao.findContractNoById(stockContract.getBuyContractId());
//			BeanUtils.copyProperties(stockContract, stockContractVo);
//			stockContractVo.setContractNo(contractNoById);
//			result.add(stockContractVo);
//		}
//		PageRequest pageRequest_new = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows());
//		Page<StockContractVo> pageVo = new PageImpl<>(result, pageRequest_new, page.getTotalElements());
//		return pageVo;
//	}
	
	/**
	 * 销售选择合同库存(关联合同)
	 * 1.已签约
	 * 2.可销售数量大于0
	 */
//	@Override
//	public Page<StockContractVo> findPageVo(StockContractSearchVo queryVo) {
//		Sort sort = new Sort(Direction.DESC, "id");
//		PageRequest pageRequest = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows(), sort);
//		Map<String, Object> searchParams = queryVo.getSearchParams();
//		Specification<StockContract> spec = WebUtil.buildSpecification(searchParams);
//		//关联合同
//		Specification<StockContract> spec_contract = new Specification<StockContract>() {
//			private static final long serialVersionUID = 6955756235753587919L;
//			@Override
//			public Predicate toPredicate(Root<StockContract> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//
//				Subquery<CtrContract> sq = query.subquery(CtrContract.class);
//				Root<StockContract> sqc = sq.correlate(root);
//				Join<StockContract, CtrContract> sqo = sqc.join("ctrContract");
//				Predicate predicate = cb.notEqual(sqo.get("contractStatus"), BasConstants.CONTRACTSTATUS_B);
//				Expression<BigDecimal> sum = cb.sum(root.get("sellingNumber"),root.get("sellNumber"));
//				Predicate greaterThan = cb.greaterThan(root.get("buyNumber"), sum);
//				Predicate pred = cb.and(predicate,greaterThan);
//				sq.select(sqo).where(pred);
//				return cb.exists(sq);
//			}
//		};
//		spec = Specification.where(spec).and(spec_contract);
//		Page<StockContract> page = getBaseDao().findAll(spec, pageRequest);
//		List<StockContractVo> lstVo = new ArrayList<>();
//		for (StockContract entity : page.getContent()) {
//			StockContractVo vo = new StockContractVo();
//			entity2Vo(entity, vo);
//			lstVo.add(vo);
//		}
//		PageRequest pageRequest_new = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows());
//		Page<StockContractVo> pageVo = new PageImpl<>(lstVo, pageRequest_new, page.getTotalElements());
//		return pageVo;
//	}
	
//	private void entity2Vo(StockContract entity ,StockContractVo vo){
//		try {
//			PropertyUtils.copyProperties(vo, entity);
//			Long buyContractId = entity.getBuyContractId();
//			if (buyContractId != null) {
//				CtrContract contract =	ctrContractService.getEntity(entity.getBuyContractId());
//				vo.setOurCompanyName(contract.getOurCompanyName());
//				vo.setQualityStandard(contract.getQualityStandard());
//				vo.setRemainNumber(entity.getBuyNumber().subtract(entity.getSellNumber()).subtract(entity.getSellingNumber()));
//				Long buyProductId = entity.getBuyProductId();
//				if (buyProductId != null) {
//					CtrProduct ctrProduct = ctrProductService.getEntity(buyProductId);
//					vo.setWrapSpecs(ctrProduct.getWrapSpecs());
//					vo.setWarehousePos(ctrProduct.getWarehousePos());
//					vo.setProductAttr(ctrProduct.getProductAttr());
//					vo.setWarehousePrice(ctrProduct.getWarehousePrice());
//				}
//				if (contract.getQualityStandard() == null) {
//					vo.setQualityStandard(BasConstants.QUALITY_Y);
//				}
//			}
//		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
//			e.printStackTrace();
//		}
//	}
	
//	private void saveRela(StockRequest request, String relaType) throws ApplicationException {
//		StockContractRela relaVo = new StockContractRela();
//		relaVo.setApproveId(request.getApplyNo());
//		relaVo.setContractId(request.getCtrContractId());
//		relaVo.setEnterpriseId(request.getEnterpriseId());
//		relaVo.setRelaNum(request.getDealNumber());
//		relaVo.setRelaType(relaType);
//		relaVo.setStockContractId(request.getStockContractId());
//		stockContractRelaService.saveDetailRela(relaVo);
//	}
}
