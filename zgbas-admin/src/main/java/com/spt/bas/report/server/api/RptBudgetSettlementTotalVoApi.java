package com.spt.bas.report.server.api;

import com.spt.bas.report.client.vo.RptBudgetSettlementTotalVo;
import com.spt.bas.report.server.service.IRptBudgetSettlementTotalVoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/budget/settlementTotal")
public class RptBudgetSettlementTotalVoApi {

	@Autowired
	private IRptBudgetSettlementTotalVoService budgetSettlementTotalService;
	
	@PostMapping("findSettlementTotalList")
	public List<RptBudgetSettlementTotalVo> findSettlementTotalList(@RequestBody RptBudgetSettlementTotalVo vo){
		return budgetSettlementTotalService.findSettlementTotalList(vo);
	}

	@PostMapping("findDCTotalList")
	public RptBudgetSettlementTotalVo findDCTotalList(@RequestBody RptBudgetSettlementTotalVo vo){
		return budgetSettlementTotalService.findDCTotalList(vo);
	}

	@PostMapping("findSettleListId")
	public List<Long> findSettleListId(@RequestBody RptBudgetSettlementTotalVo vo){
		return budgetSettlementTotalService.findSettleListId(vo);
	}
}
