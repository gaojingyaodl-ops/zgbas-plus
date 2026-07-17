package com.spt.bas.server.stock.service;

import com.spt.bas.client.entity.StockDetail;
import com.spt.tools.core.exception.ApplicationException;

public interface IStockDetailTransferService {

	void clean(StockDetail detail) throws ApplicationException;

	void refreshRela(StockDetail detail) throws ApplicationException;

	void cleanByContractId(Long contractId) throws ApplicationException;

}
