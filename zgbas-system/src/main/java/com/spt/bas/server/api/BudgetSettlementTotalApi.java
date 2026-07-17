package com.spt.bas.server.api;

import com.spt.bas.client.entity.BudgetSettlementTotal;
import com.spt.bas.server.service.IBudgetSettlementTotalService;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "ctr/budgetSettlementTotal")
public class BudgetSettlementTotalApi extends BaseApi<BudgetSettlementTotal> {
	@Autowired
	private IBudgetSettlementTotalService budgetSettlementTotalService;

	@Override
	public IDataService<BudgetSettlementTotal> getService() {
		return budgetSettlementTotalService;
	}

	@PostMapping("findSettlementPage")
	public Page<BudgetSettlementTotal> findSettlementPage(@RequestBody PageSearchVo searchVo){
		return budgetSettlementTotalService.findSettlementPage(searchVo);
	}

	@PostMapping("sumPageSettlement")
	BudgetSettlementTotal sumPageSettlement(@RequestBody PageSearchVo searchVo){
		return budgetSettlementTotalService.sumPageSettlement(searchVo);
	}

	@PostMapping(value = "createSettleTotal")
	public void createSettleTotal(@RequestBody String summaryDate){
		budgetSettlementTotalService.createSettleTotal(summaryDate);
	}
}

