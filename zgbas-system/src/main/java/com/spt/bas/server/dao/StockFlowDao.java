package com.spt.bas.server.dao;

import java.util.List;

import com.spt.bas.client.entity.StockFlow;
import com.spt.tools.jpa.dao.BaseDao;

public interface StockFlowDao extends BaseDao<StockFlow> {


	public void deleteByApplyIdAndOperationType(Long applyId, String operationType);
	public List<StockFlow> findByApplyIdAndOperationType(Long applyId, String operationType);
	public List<StockFlow> findByContractIdAndOperationType(Long contractId, String type);
}

