package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BudgetSettlementTotal;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/ctr/budgetSettlementTotal",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IBudgetSettlementTotalClient extends BaseClient<BudgetSettlementTotal> {

    @PostMapping("findSettlementPage")
    PageDown<BudgetSettlementTotal> findSettlementPage(@RequestBody PageSearchVo searchVo);

    @PostMapping("sumPageSettlement")
    BudgetSettlementTotal sumPageSettlement(@RequestBody PageSearchVo searchVo);

    @PostMapping(value = "createSettleTotal")
    void createSettleTotal(@RequestBody String summaryDate);
}

