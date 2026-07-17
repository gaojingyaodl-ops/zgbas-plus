package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.vo.DistanceResultVo;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/midstream",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IMidstreamClient {

    @GetMapping("/generateRespStr")
    String generateRespStr(@RequestParam(value = "reqStr", required = false) String reqStr,
                                           @RequestParam(value = "oldValue", required = false) String oldValue);
}
