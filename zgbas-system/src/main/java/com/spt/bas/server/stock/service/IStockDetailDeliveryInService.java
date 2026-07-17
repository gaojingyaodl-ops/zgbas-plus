package com.spt.bas.server.stock.service;

import com.spt.bas.client.vo.StockDetailDeleveryInResp;
import com.spt.bas.client.vo.StockDetailRequest;
import com.spt.tools.core.exception.ApplicationException;

public interface IStockDetailDeliveryInService {

	/**
	 * 入库明细保存
	 * 
	 * @param product
	 */
	StockDetailDeleveryInResp saveDeliveryIn(StockDetailRequest request) throws ApplicationException;
}
