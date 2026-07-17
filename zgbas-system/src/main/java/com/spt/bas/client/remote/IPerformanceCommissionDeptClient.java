package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.PerformanceCommissionDept;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/performance/dept",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IPerformanceCommissionDeptClient extends BaseClient<PerformanceCommissionDept> {

    @RequestMapping(value = "initPerformanceCommissionDept")
    void initPerformanceCommissionDept(@RequestBody PerformanceCommissionDept performanceCommissionDept);

    @RequestMapping(value = "findPerformanceCommissionDept")
    PerformanceCommissionDept findPerformanceCommissionDept(@RequestBody PerformanceCommissionDept queryVo);
}
