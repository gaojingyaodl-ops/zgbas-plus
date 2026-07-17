package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApiRequestHis;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(qualifier = "apiRequestHisClient", name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/api/request", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApiRequestHisClient extends BaseClient<ApiRequestHis> {
    @PostMapping(value = "addZYRequestHis")
    void addZYRequestHis(@RequestBody ApiRequestHis requestHis);

    @PostMapping(value = "addWFQRequestHis")
    void addWFQRequestHis(@RequestBody ApiRequestHis requestHis);

    @PostMapping(value = "addCFCARequestHis")
    void addCFCARequestHis(@RequestBody ApiRequestHis requestHis);

    @PostMapping(value = "addJINXINRequestHis")
    void addJINXINRequestHis(@RequestBody ApiRequestHis requestHis);

    @PostMapping(value = "addRtRequestHis")
    void addRtRequestHis(@RequestBody ApiRequestHis requestHis);
}

