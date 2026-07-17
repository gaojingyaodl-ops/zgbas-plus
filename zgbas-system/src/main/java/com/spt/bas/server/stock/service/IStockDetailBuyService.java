package com.spt.bas.server.stock.service;

import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.vo.BizUserInfor;
import com.spt.bas.client.vo.CtrConctractInvalidVo;
import com.spt.bas.client.vo.StockDetailRequest;
import com.spt.tools.core.exception.ApplicationException;

public interface IStockDetailBuyService {
	/** 采购入库 */
	StockDetail saveBuy(StockDetailRequest request, BizUserInfor userInfor) throws ApplicationException;

	/** 撤销采购合同 */
	void cancelBuyProduct(CtrProduct product) throws ApplicationException;

	/** 撤销采购和入库，撮合业务下使用 */
	void cancelDeliveryInAndBuyProduct(CtrProduct product) throws ApplicationException;

	/** 作废关于该预售合同的预售采购合同 */
	public void cancelPreBuyContract(CtrProduct product, CtrConctractInvalidVo vo) throws ApplicationException;
}
