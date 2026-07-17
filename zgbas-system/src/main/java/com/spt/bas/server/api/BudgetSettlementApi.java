package com.spt.bas.server.api;

import com.spt.bas.client.entity.BudgetSettlement;
import com.spt.bas.client.vo.BudgetSettlementVo;
import com.spt.bas.server.service.IBudgetSettlementService;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "ctr/budgetSettlement")
public class BudgetSettlementApi extends BaseApi<BudgetSettlement> {
	@Autowired
	private IBudgetSettlementService budgetSettlementService;

	@Override
	public IBaseService<BudgetSettlement> getService() {
		return budgetSettlementService;
	}

	@PostMapping("findBySellContractId")
	BudgetSettlementVo findBySellContractId(@RequestBody Long sellContractId){
		return budgetSettlementService.findBySellContractId(sellContractId);
	}
	@PostMapping("findBySellContractIdWithAnyStatus")
	BudgetSettlement findBySellContractIdWithAnyStatus(@RequestBody Long sellContractId){
		return budgetSettlementService.findBySellContractIdWithAnyStatus(sellContractId);
	}


}

