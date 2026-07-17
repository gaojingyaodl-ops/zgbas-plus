package com.spt.bas.server.service;

import java.math.BigDecimal;
import java.util.List;

import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.entity.StockDetailPresell;
import com.spt.bas.client.vo.BizUserInfor;
import com.spt.bas.client.vo.StockDetailPresellVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

public interface IStockDetailPresellService extends IBaseService<StockDetailPresell> {

	public void savePresellDetail(CtrProduct product, BizUserInfor userInfor) throws ApplicationException;

	public void updatePresellDetail(BigDecimal dealNumber, Long sellProductId);

	public StockDetailPresell findByCtrProductId(Long productId);
	
	//public Page<StockDetailPresellVo> findApplyPage(PageSearchVo searchVo);
	
	public List<StockDetailPresellVo> findList(Long contractId);
	
}

