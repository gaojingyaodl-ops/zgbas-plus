package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.vo.DistanceResultVo;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/7/6 15:09
 */
@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/baiduMapApi",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface BaiduMapClient {

    @GetMapping("/getTwoDistance")
    DistanceResultVo getTwoDistance(@RequestParam(value = "start", required = false) String start,
                                           @RequestParam(value = "end", required = false) String end);
}
