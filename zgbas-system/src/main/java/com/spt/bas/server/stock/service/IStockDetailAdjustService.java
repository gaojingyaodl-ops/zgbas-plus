package com.spt.bas.server.stock.service;

import com.spt.bas.client.entity.StockContract;
import com.spt.bas.client.vo.ApplyContractAdjustRequestVo;
import com.spt.bas.client.vo.StockAdjustAuditVo;
import com.spt.tools.core.exception.ApplicationException;

public interface IStockDetailAdjustService {

	public StockContract[] updateByContractAdjust(ApplyContractAdjustRequestVo vo) throws ApplicationException;
	
	public void saveDetailAndHis(StockAdjustAuditVo vo);
}

