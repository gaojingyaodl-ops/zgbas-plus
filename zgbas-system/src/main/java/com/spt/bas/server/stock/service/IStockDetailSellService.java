package com.spt.bas.server.stock.service;

import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.vo.BizUserInfor;
import com.spt.bas.client.vo.StockDetailRequest;
import com.spt.tools.core.exception.ApplicationException;

public interface IStockDetailSellService {

	/**
	 * 销售，冻结库存数量
	 */
	public void saveSell(StockDetailRequest request) throws ApplicationException;

	public void saveSellComplete(StockDetailRequest request) throws ApplicationException;

	/** 撤销销售合同 */
	void cancelSellProduct(CtrProduct product) throws ApplicationException;

	/** 预售 */
	void savePresell(CtrProduct product, BizUserInfor userInfor) throws ApplicationException;

	/** 预售作废，删除预售库存记录 */
	void cancelPresell(CtrProduct product) throws ApplicationException;
}
