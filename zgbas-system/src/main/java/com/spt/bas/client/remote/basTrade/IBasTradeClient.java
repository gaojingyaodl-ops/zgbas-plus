package com.spt.bas.client.remote.basTrade;

import com.spt.bas.client.constant.BasConstants;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 采销中心服务接口
 */
@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/bas/trade",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IBasTradeClient  {

    @PostMapping(value = "test")
    void test();

}
