package com.spt.bas.server.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

//git.spt.com/team/bas.git


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.entity.StockDetailPresell;
import com.spt.bas.client.vo.BizUserInfor;
import com.spt.bas.client.vo.StockDetailPresellVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.dao.CtrProductDao;
import com.spt.bas.server.dao.StockDetailPresellDao;
import com.spt.bas.server.service.IStockDetailPresellService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

@Component
@Transactional(readOnly = true)
public class StockDetailPresellServiceImpl extends BaseService<StockDetailPresell> implements IStockDetailPresellService {
	@Autowired
	private StockDetailPresellDao stockDetailPresellDao;
	@Autowired
	private CtrContractDao ctrContractDao;
	@Autowired
	private CtrProductDao ctrProductDao;
	@Override
	public BaseDao<StockDetailPresell> getBaseDao() {
		return stockDetailPresellDao;
	}
	
	@Override
	public Class<StockDetailPresell> getEntityClazz() {
		return StockDetailPresell.class;
	}
	
	@Override
	@ServerTransactional
	public void savePresellDetail(CtrProduct product, BizUserInfor userInfor) throws ApplicationException {
		StockDetailPresell detail = new StockDetailPresell();
		BeanUtils.copyProperties(product, detail);
		BigDecimal dealNumber = product.getDealNumber();
//		detail.setStockId(stock.getId());
		detail.setId(null);
		detail.setPresellNumber(dealNumber);
		detail.setContractId(product.getCtrContractId());
		detail.setCtrProductId(product.getId());
		detail = stockDetailPresellDao.save(detail);
	}

	@Override
	@ServerTransactional
	public void updatePresellDetail(BigDecimal dealNumber, Long sellProductId) {
		StockDetailPresell detail = stockDetailPresellDao.findByCtrProductId(sellProductId);
		detail.setApproveBuyNumber(detail.getApproveBuyNumber().subtract(dealNumber));
		detail.setBuyedNumber(detail.getBuyedNumber().add(dealNumber));
		stockDetailPresellDao.save(detail);
	}

	@Override
	public StockDetailPresell findByCtrProductId(Long productId) {
		return stockDetailPresellDao.findByCtrProductId(productId);
	}

	/*@Override
	public Page<StockDetailPresellVo> findApplyPage(PageSearchVo searchVo) {
		Sort sort = new Sort(Direction.DESC, "id");
		PageRequest pageRequest = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows(), sort);
		Specification<StockDetailPresell> spec = WebUtil.buildSpecification(searchVo.getSearchParams());
		//可销售数量大于0
		Specification<StockDetailPresell> spec_presell = new Specification<StockDetailPresell>() {
			private static final long serialVersionUID = -3911518707860552672L;

			@Override
			public Predicate toPredicate(Root<StockDetailPresell> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate predicate = cb.lt(root.get("buyedNumber"), root.get("presellNumber"));
				Expression<BigDecimal> sum = cb.sum(root.get("buyedNumber"),root.get("approveBuyNumber"));
				Predicate greaterThan = cb.greaterThan(root.get("presellNumber"), sum);
				Predicate pred = cb.and(predicate,greaterThan);
				return pred;
			}
		};
		spec = Specification.where(spec).and(spec_presell);
		//关联合同 (合同已签约)
		Specification<StockDetailPresell> spec_contract = new Specification<StockDetailPresell>() {
			private static final long serialVersionUID = -96396417983121598L;

			@Override
			public Predicate toPredicate(Root<StockDetailPresell> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				Subquery<CtrContract> sq = query.subquery(CtrContract.class);
				Root<StockDetailPresell> sqc = sq.correlate(root);
				Join<StockDetailPresell, CtrContract> sqo = sqc.join("ctrContract");
				Predicate predicate = cb.notEqual(sqo.get("contractStatus"), BasConstants.CONTRACTSTATUS_B);
				sq.select(sqo).where(predicate);
				return cb.exists(sq);
			}
		};
		spec = Specification.where(spec).and(spec_contract);
		Page<StockDetailPresell> page = getBaseDao().findAll(spec, pageRequest);
		List<StockDetailPresellVo> voList = new ArrayList<>();
		List<StockDetailPresell> content = page.getContent();
		for (StockDetailPresell presell : content) {
			StockDetailPresellVo vo = new StockDetailPresellVo();
			BeanUtils.copyProperties(presell, vo);
			CtrProduct product = ctrProductDao.findOne(presell.getCtrProductId());
			CtrContract contract = ctrContractDao.findOne(presell.getContractId());
			if (product != null) {
				vo.setWrapSpecs(product.getWrapSpecs());
				vo.setWarehousePos(product.getWarehousePos());
				vo.setWarehousePrice(product.getWarehousePrice());
			}
			if (contract != null) {
				vo.setOurCompanyName(contract.getOurCompanyName());
				if(StringUtils.isNotEmpty(contract.getQualityStandard())){
					vo.setQualityStandard(contract.getQualityStandard());
				}else{
					vo.setQualityStandard(BasConstants.QUALITY_Y);
				}
			}
			voList.add(vo);
		}
		
		PageRequest pageRequest_new = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
		Page<StockDetailPresellVo> pageVo = new PageImpl<>(voList, pageRequest_new, page.getTotalElements());
		return pageVo;
	}*/

	@Override
	public List<StockDetailPresellVo> findList(Long contractId) {
		List<StockDetailPresellVo> voList = new ArrayList<StockDetailPresellVo>();
		List<StockDetailPresell> list = stockDetailPresellDao.findByContractId(contractId);
		for (StockDetailPresell stockDetailPresell : list) {
			StockDetailPresellVo vo = new StockDetailPresellVo();
			BeanUtils.copyProperties(stockDetailPresell, vo);
			Long ctrProductId = stockDetailPresell.getCtrProductId();
			CtrProduct product = ctrProductDao.findOne(ctrProductId);
			CtrContract contract = ctrContractDao.findOne(contractId);
			vo.setQualityStandard(contract.getQualityStandard());
			if (product != null) {
				vo.setWrapSpecs(product.getWrapSpecs());
				vo.setWarehousePos(product.getWarehousePos());
				vo.setWarehousePrice(product.getWarehousePrice());
			}
			voList.add(vo);
		}
		return voList;
	}
	
}

