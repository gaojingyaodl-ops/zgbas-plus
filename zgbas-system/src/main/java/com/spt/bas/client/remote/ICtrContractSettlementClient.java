package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContractSettlement;
import com.spt.bas.client.entity.CtrContractSettlementCommission;
import com.spt.bas.client.vo.BudgetSettlementOphisSearchVo;
import com.spt.bas.client.vo.BudgetSettlementOphisVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/contractSettlement",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface ICtrContractSettlementClient extends BaseClient<CtrContractSettlement> {

    @PostMapping(value = "markSettlement")
    void markSettlement(@RequestBody List<Long> settlementIds);

    @PostMapping(value = "refreshSettlement")
    void refreshSettlement(@RequestBody List<Long> settlementIds);

    @PostMapping(value = "updateSettlementOphis")
    void updateSettlementOphis(@RequestBody BudgetSettlementOphisVo ophisVo);

    @PostMapping(value = "sumPageSettlement")
    CtrContractSettlement sumPageSettlement(@RequestBody PageSearchVo searchVo);

    // 修改汇总标识
    @PostMapping(value = "updateSettleTotalFlg")
    void updateSettleTotalFlg(@RequestBody List<Long> settlementId);

    @PostMapping(value = "findIndexPage")
    PageDown<CtrContractSettlement> findIndexPage(@RequestBody BudgetSettlementOphisSearchVo searchVo);

    @PostMapping("sumIndexPage")
    CtrContractSettlement sumIndexPage(@RequestBody BudgetSettlementOphisSearchVo searchVo);

    @PostMapping("findSettlementDetail")
    List<CtrContractSettlementCommission> findSettlementDetail(@RequestBody Long settlementId);

    @PostMapping("finalAccount")
    void finalAccount(@RequestBody CtrContractSettlement settlement);
}

