package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BasManual;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(qualifier="basManualClient",name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bas/manual",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IBasManualClient extends BaseClient<BasManual> {

    @PostMapping("findAllEnable")
    List<BasManual> findAllEnable();
}
