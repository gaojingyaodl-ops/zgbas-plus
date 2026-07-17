package com.spt.bas.report.server.dao;

import com.spt.bas.report.client.vo.RptBudgetSettlementTotalVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

import java.util.List;

@MyBatisDao
public interface RptBudgetSettlementTotalMapper {
	
	List<RptBudgetSettlementTotalVo> findSettlementTotalList(RptBudgetSettlementTotalVo vo);

	RptBudgetSettlementTotalVo findDCTotalList(RptBudgetSettlementTotalVo vo);
	// 查询ID 用来修改汇总标识
	List<Long> findSettleListId(RptBudgetSettlementTotalVo vo);

}
