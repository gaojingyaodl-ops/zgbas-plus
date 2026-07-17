package com.spt.bas.server.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;

import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.entity.Stock;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.entity.StockDetailHis;
import com.spt.bas.client.vo.StockDetailHisVo;
import com.spt.bas.client.vo.StockDetailMoveVo;
import com.spt.bas.client.vo.StockDetailRequest;
import com.spt.bas.client.vo.StockRequest;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

public interface IStockDetailHisService extends IBaseService<StockDetailHis> {

	void saveBuy(StockDetail detail, StockDetailRequest request)throws ApplicationException ;

	void saveDeliveryIn(StockDetail detail, StockDetailRequest request, String bizType, boolean isBack)throws ApplicationException ;

	void saveSell(StockDetailRequest request, StockDetail detail)throws ApplicationException ;

	void saveDeliveryOut(StockDetail detail, StockDetailRequest request, String operateType)throws ApplicationException ;
	/**
	 * 审批驳回，删除新增的流水
	 * @param detail
	 * @param product
	 */
	void deleteSell(StockDetail detail, StockDetailRequest request);
	/**
	 * 取消库存历史
	 * @param stock
	 * @param detail
	 * @param product
	 */
	void insertHisByCancel(StockDetail detail, BigDecimal fixNumber, CtrProduct product, String applyType)throws ApplicationException ;

	void saveMoveHis(StockDetail entity, StockDetailMoveVo changeVo, String type);

	void savePresell(StockDetail detail, CtrProduct product, Stock stock)throws ApplicationException ;

	void updateContractId(Long detailId, Long applyId, Long contractId, String operationType);

	public Page<StockDetailHisVo> findPageVo(PageSearchVo searchVo);

	void saveAdjust(StockDetail detail, BigDecimal diff, String operationType, Long applyId, Long contractId);
}

