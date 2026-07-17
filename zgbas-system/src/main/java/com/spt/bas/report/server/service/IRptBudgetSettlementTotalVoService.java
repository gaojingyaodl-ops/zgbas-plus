package com.spt.bas.report.server.service;

import com.spt.bas.report.client.vo.RptBudgetSettlementTotalVo;

import java.util.List;

public interface IRptBudgetSettlementTotalVoService {
	
	List<RptBudgetSettlementTotalVo> findSettlementTotalList(RptBudgetSettlementTotalVo vo);
	RptBudgetSettlementTotalVo findDCTotalList(RptBudgetSettlementTotalVo vo);

	// 查询ID 用来修改汇总标识
	List<Long> findSettleListId(RptBudgetSettlementTotalVo vo);
}
