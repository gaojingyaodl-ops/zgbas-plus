package com.spt.bas.server.stock.service;

import java.math.BigDecimal;

import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.entity.StockDetailRela;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

public interface IStockDetailRelaService extends IBaseService<StockDetailRela> {

	/** 销售/出库作废，删除对应的库存明细关联记录 */
	BigDecimal deleteDetailRela(StockDetail detail, Long contractId, BigDecimal number, String relaType)
			throws ApplicationException;

	StockDetailRela findRela(Long contractId, String relaType, Long stockDetailId);

	/** 添加库存关联记录 */
	void saveDetailRela(StockDetailRela request) throws ApplicationException;

}
