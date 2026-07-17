package com.spt.bas.report.server.service.impl;

import com.spt.bas.report.client.vo.RptBudgetSettlementTotalVo;
import com.spt.bas.report.server.dao.RptBudgetSettlementTotalMapper;
import com.spt.bas.report.server.service.IRptBudgetSettlementTotalVoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RptBudgetSettlementTotalVoServiceImpl implements IRptBudgetSettlementTotalVoService {
	@Autowired
	private RptBudgetSettlementTotalMapper budgetSettlementTotalMapper;
	@Override
	public List<RptBudgetSettlementTotalVo> findSettlementTotalList(RptBudgetSettlementTotalVo vo) {
		return budgetSettlementTotalMapper.findSettlementTotalList(vo);
	}

	@Override
	public RptBudgetSettlementTotalVo findDCTotalList(RptBudgetSettlementTotalVo vo) {
		return  budgetSettlementTotalMapper.findDCTotalList(vo);
	}

	@Override
	public List<Long> findSettleListId(RptBudgetSettlementTotalVo vo) {
		return  budgetSettlementTotalMapper.findSettleListId(vo);
	}

}
