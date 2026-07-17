package com.spt.bas.server.stock.service;

import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;

import com.spt.bas.client.entity.StockContractRela;
import com.spt.bas.client.entity.StockDetail;
import com.spt.bas.client.vo.StockContractRelaVo;

public interface IStockContractRelaService extends IBaseService<StockContractRela> {

	BigDecimal deleteDetailRela(StockDetail detail, Long contractId, BigDecimal number, String relaType) throws ApplicationException;

	StockContractRela saveDetailRela(StockContractRela request) throws ApplicationException;

	StockContractRela findStockContractId(Long contractId, String relaType, Long stockContractId);

	List<StockContractRela> findCtrProductId(String relaType, Long ctrProductId);

	List<StockContractRela> findByApproveId(Long approveId);

	long countRela(Long stockContractId, String relaType);

	List<StockContractRela> findByContractId(Long contractId, String relaType);

	StockContractRela findSellByCtrProductId(Long ctrProductId);
	
	Page<StockContractRelaVo> findStockContractRela(PageSearchVo searchVo);
	
	StockContractRela findSellByStockContractId(Long stockContractId);
	
}

