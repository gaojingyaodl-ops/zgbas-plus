package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.PerformanceCommissionUser;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/performance/user",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IPerformanceCommissionUserClient extends BaseClient<PerformanceCommissionUser> {
    @RequestMapping(value = "initPerformanceCommissionUser")
    void initPerformanceCommissionUser(@RequestBody PerformanceCommissionUser performanceCommissionUser);

    @RequestMapping(value = "findPerformanceCommissionUser")
    PerformanceCommissionUser findPerformanceCommissionUser(@RequestBody PerformanceCommissionUser queryVo);
}
