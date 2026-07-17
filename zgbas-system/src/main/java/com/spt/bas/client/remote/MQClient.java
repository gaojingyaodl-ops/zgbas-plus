package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/2/28 11:10
 */
@FeignClient(name = BasConstants.SERVER_NAME, path = BasConstants.SERVER_NAME + "/mq/api", url = BasConstants.SERVER_URL, configuration = FeignConfig.class)
public interface MQClient {

    @GetMapping("/test")
    void  test();

}
