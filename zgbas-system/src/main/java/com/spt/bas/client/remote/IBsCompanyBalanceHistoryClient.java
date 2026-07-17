package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompanyBalanceHistory;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bsCompanyBalance/history",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IBsCompanyBalanceHistoryClient extends BaseClient<BsCompanyBalanceHistory> {

    @PostMapping("findByCompanyId")
    List<BsCompanyBalanceHistory> findByCompanyId(@RequestParam("companyId") Long companyId);

    @GetMapping("updateLastBalance")
    public void updateLastBalance(@RequestParam("companyId") Long companyId, @RequestParam("lastBalance") BigDecimal lastBalance);

    @GetMapping("updateLastBalanceStatus")
    public void updateLastBalanceStatus(@RequestParam("companyId") Long companyId,@RequestParam("changeType") String changeType);
}