package com.spt.bas.server.stock.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.StockContract;
import com.spt.bas.client.entity.StockContractRela;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.vo.StockContractRelaVo;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.dao.StockContractDao;
import com.spt.bas.server.dao.StockContractRelaDao;
import com.spt.bas.server.stock.service.IStockContractRelaService;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;

@Component
public class StockContractRelaServiceImpl extends BaseService<StockContractRela> implements IStockContractRelaService {
	@Autowired
	private StockContractRelaDao stockContractRelaDao;
	@Autowired
	private StockContractDao stockContractDao;
	@Autowired
	private CtrContractDao ctrContractDao;

	@Override
	public BaseDao<StockContractRela> getBaseDao() {
		return stockContractRelaDao;
	}

	@Override
	public Class<StockContractRela> getEntityClazz() {
		return StockContractRela.class;
	}

	@Override
	public StockContractRela findStockContractId(Long contractId, String relaType, Long stockContractId) {
		List<StockContractRela> list = findAll(contractId, relaType, stockContractId);
		if (list.size()>0) {
			return list.get(0);
		}
		return null;
	}
	
	@Override
	public List<StockContractRela> findByContractId(Long contractId, String relaType) {
		return findAll(contractId, relaType, null);
	}
	
	private List<StockContractRela> findAll(Long contractId, String relaType, Long stockContractId) {
		Specification<StockContractRela> spec = (root, query, cb) -> {
			Predicate p1 = cb.equal(root.get("contractId"), contractId);
			Predicate p2 = cb.equal(root.get("relaType"), relaType);
			Predicate presult = cb.and(p1, p2);
			if (stockContractId != null) {
				Predicate p3 = cb.equal(root.get("stockContractId"), stockContractId);
				presult = cb.and(presult, p3);
			}
			return presult;
		};
		return stockContractRelaDao.findAll(spec);
	}
	
	@Override
	public long countRela(Long stockContractId, String relaType) {
		return stockContractRelaDao.countByStockContractIdAndRelaType(stockContractId, relaType);
	}

	@Override
	public List<StockContractRela> findByApproveId(Long approveId) {
		if (approveId == null || approveId==0L) {
			return new ArrayList<>(0);
		}
		return stockContractRelaDao.findByApproveId(approveId);
	}

	@Override
	public List<StockContractRela> findCtrProductId(String relaType, Long ctrProductId) {
		return stockContractRelaDao.findByRelaTypeAndCtrProductId(relaType, ctrProductId);
	}
	
	@Override
	public StockContractRela findSellByCtrProductId(Long ctrProductId) {
		List<StockContractRela> list = stockContractRelaDao.findByRelaTypeAndCtrProductId(StockContractRela.RELATYPE_SELL, ctrProductId);
		if (list.size()>0) {
			return list.get(0);
		}
		return null;
	}
	
	@Override
	public StockContractRela findSellByStockContractId(Long stockContractId) {
		List<StockContractRela> list = stockContractRelaDao.findByRelaTypeAndStockContractId(StockContractRela.RELATYPE_SELL, stockContractId);
		if (list.size()>0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public StockContractRela saveDetailRela(StockContractRela request) throws ApplicationException {
		StockContractRela rela = findStockContractId(request.getContractId(), request.getRelaType(),
				request.getStockContractId());
		if (rela == null) {
			rela = new StockContractRela();
			rela.setApproveId(request.getApproveId());
			rela.setContractId(request.getContractId());
			rela.setCtrProductId(request.getCtrProductId());
			rela.setEnterpriseId(request.getEnterpriseId());
			rela.setRelaNum(request.getRelaNum());
			rela.setRelaType(request.getRelaType());
			rela.setStockContractId(request.getStockContractId());
		} else {
//			rela.setContractId(request.getContractId());
//			rela.setCtrProductId(request.getCtrProductId());
			rela.setRelaNum(rela.getRelaNum().add(request.getRelaNum()));
		}
		return save(rela);
	}

	/** 销售/出库作废，删除对应的库存明细关联记录 */
	@Override
	public BigDecimal deleteDetailRela(StockDetail detail, Long contractId, BigDecimal number, String relaType)
			throws ApplicationException {
		BigDecimal fixNumber = BigDecimal.ZERO;
		StockContractRela rela = findStockContractId(contractId, relaType, detail.getStockContractId());
		if (rela == null) {
			return fixNumber;
		}
		if (rela.getRelaNum() != null && rela.getRelaNum().compareTo(number) > 0) {
			rela.setRelaNum(rela.getRelaNum().subtract(number));
			fixNumber = number;
			save(rela);
		} else {
			fixNumber = rela.getRelaNum();
			delete(rela.getId());
		}
		return fixNumber;
	}

	@Override
	public Page<StockContractRelaVo> findStockContractRela(PageSearchVo searchVo) {
		Sort sort = Sort.by(Direction.DESC, "id");
		PageRequest pageRequest = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows(), sort);
		Specification<StockContractRela> spe = WebUtil.buildSpecification(searchVo.getSearchParams());
		Page<StockContractRela> page = getBaseDao().findAll(spe, pageRequest);
		List<StockContractRela> relaList = page.getContent();
		List<StockContractRelaVo> listVo = new ArrayList<>();
		for (StockContractRela rela : relaList) {
			Long contractId = rela.getContractId();
			CtrContract ctrContract = ctrContractDao.findOne(contractId);
			StockContract stockContract = stockContractDao.findOne(rela.getStockContractId());
			StockContractRelaVo vo = new StockContractRelaVo();
			BeanUtils.copyProperties(rela, vo);
			if (ctrContract != null) {
				vo.setContractNo(ctrContract.getContractNo());
			}
			vo.setProductName(stockContract.getProductName());
			vo.setBrandNumber(stockContract.getBrandNumber());
			vo.setFactoryName(stockContract.getFactoryName());
			vo.setWarehouseName(stockContract.getWarehouseName());
			listVo.add(vo);
		}
		// sort属性无法反序列化，下面代码重新组装page对象，去掉sort属性
		PageRequest pageRequest_new = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
		Page<StockContractRelaVo> pageVo = new PageImpl<>(listVo, pageRequest_new, page.getTotalElements());
		return pageVo;
	}

}
