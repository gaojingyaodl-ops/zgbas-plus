package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BudgetSettlement;
import com.spt.bas.client.vo.BudgetSettlementVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/ctr/budgetSettlement",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IBudgetSettlementClient extends BaseClient<BudgetSettlement> {

	@PostMapping("findBySellContractId")
	BudgetSettlementVo findBySellContractId(@RequestBody Long sellContractId);

	@PostMapping("findBySellContractIdWithAnyStatus")
	BudgetSettlementVo findBySellContractIdWithAnyStatus(@RequestBody Long sellContractId);
}

