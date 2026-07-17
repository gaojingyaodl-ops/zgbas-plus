package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsConfig;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bs/bsConfig",url=BasConstants.SERVER_URL,configuration=FeignConfig.class)
public interface IBsConfigClient extends BaseClient<BsConfig> {
    @PostMapping(value = "findConfigMessageList")
    List<String> findConfigMessageList(@RequestBody Long enterpriseId);

    @PostMapping(value = "getBsConfigList")
    List<BsConfig> getBsConfigList(@RequestBody Long enterpriseId);
}

