package com.spt.bas.server.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;

import com.spt.bas.client.entity.Stock;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.entity.StockFlow;
import com.spt.bas.client.vo.CtrConctractInvalidVo;
import com.spt.bas.client.vo.StockAdjustAuditVo;
import com.spt.bas.client.vo.StockDetailMoveVo;
import com.spt.bas.client.vo.StockFlowVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.jpa.service.IBaseService;

public interface IStockFlowService extends IBaseService<StockFlow> {

	public void saveStockFlow(StockFlowVo sfVo, Stock stock);

	public void updateContractId(Long applyId, Long contractId, String operationType);

	public void insert2Flow(Stock oldStock, Stock newStock, StockDetail entity, StockDetailMoveVo changeVo);

	void cancelContract(Stock stock, BigDecimal fixNumber, CtrConctractInvalidVo vo, Long contractId, String applyType);

	public Page<StockFlowVo> findPageVo(PageSearchVo queryVo);

	void deleteOnBack(Long applyId, String operationType);

	public void saveAdjust(Stock stock, StockDetail detail, String operationType, StockAdjustAuditVo vo);

}

