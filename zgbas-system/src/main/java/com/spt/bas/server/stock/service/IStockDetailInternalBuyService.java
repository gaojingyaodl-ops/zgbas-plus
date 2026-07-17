package com.spt.bas.server.stock.service;

import com.spt.bas.client.entity.ApplyInternalBuyDetail;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.vo.BizUserInfor;

public interface IStockDetailInternalBuyService {

	public StockDetail updataByInternalBuy(ApplyInternalBuyDetail nDetail, BizUserInfor userInfo);

	public void doBackInternalBuy(StockDetail nDetail, Long oStockDetailId, Long applyId);
}
