package com.spt.bas.server.stock.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.entity.StockDetailRela;
import com.spt.bas.server.dao.StockDetailRelaDao;
import com.spt.bas.server.stock.service.IStockDetailRelaService;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;

/**
 * 库存明细关联
 * 
 * @author wlddh
 *
 */
@Component
public class StockDetailRelaServiceImpl extends BaseService<StockDetailRela> implements IStockDetailRelaService {
	@Autowired
	private StockDetailRelaDao stockDetailRelaDao;

	@Override
	public BaseDao<StockDetailRela> getBaseDao() {
		return stockDetailRelaDao;
	}

	@Override
	public Class<StockDetailRela> getEntityClazz() {
		return StockDetailRela.class;
	}

	@Override
	public StockDetailRela findRela(Long contractId, String relaType, Long stockDetailId) {
		return stockDetailRelaDao.findByContractIdAndRelaTypeAndStockDetailId(contractId, relaType, stockDetailId);
	}

	@Override
	public void saveDetailRela(StockDetailRela request) throws ApplicationException {
		StockDetailRela rela = findRela(request.getContractId(), request.getRelaType(), request.getStockDetailId());
		if (rela == null) {
			rela = new StockDetailRela();
			rela.setApproveId(request.getApproveId());
			rela.setContractId(request.getContractId());
			rela.setEnterpriseId(request.getEnterpriseId());
			rela.setRelaNum(request.getRelaNum());
			rela.setRelaType(request.getRelaType());
			rela.setStockDetailId(request.getStockDetailId());
		} else {
			rela.setRelaNum(rela.getRelaNum().add(request.getRelaNum()));
		}
		save(rela);
	}

	/** 销售/出库作废，删除对应的库存明细关联记录 */
	@Override
	public BigDecimal deleteDetailRela(StockDetail detail, Long contractId, BigDecimal number, String relaType)
			throws ApplicationException {
		BigDecimal fixNumber = BigDecimal.ZERO;
		StockDetailRela rela = findRela(contractId, relaType, detail.getId());
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

}
