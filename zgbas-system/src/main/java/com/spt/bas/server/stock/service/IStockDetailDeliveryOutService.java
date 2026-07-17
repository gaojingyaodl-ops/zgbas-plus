package com.spt.bas.server.stock.service;

import com.spt.bas.client.vo.StockDetailMoveVo;
import com.spt.bas.client.vo.StockDetailRequest;
import com.spt.tools.core.exception.ApplicationException;

public interface IStockDetailDeliveryOutService {
	/** 出库 */
	void saveDeliveryOut(StockDetailRequest request) throws ApplicationException;

	/** 移库 */
	void changeWarehouse(StockDetailMoveVo changeVo);

}
