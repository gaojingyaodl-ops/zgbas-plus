package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApiExternalHis;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(qualifier = "apiExternalHisClient", name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/api/external", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface IApiExternalHisClient extends BaseClient<ApiExternalHis> {

    @PostMapping(value = "addExternalHis")
    void addExternalHis(@RequestBody ApiExternalHis externalHis);
}

