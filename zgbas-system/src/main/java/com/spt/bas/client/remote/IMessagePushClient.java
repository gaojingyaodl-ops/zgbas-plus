package com.spt.bas.client.remote;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.vo.MessagePushVo;
import com.spt.bas.client.vo.api.RespVo;
import com.spt.tools.http.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author: gaojy
 * @create 2022/3/18 18:08
 * @version: 1.0
 * @description:
 */
@FeignClient(name = BasConstants.SERVER_NAME,path= BasConstants.SERVER_NAME+"/push/message",url=BasConstants.SERVER_URL,configuration= FeignConfig.class)
public interface IMessagePushClient {

    @PostMapping(value="sendMessage")
    RespVo<?> sendMessage(@RequestBody MessagePushVo messagePushVo);
}
