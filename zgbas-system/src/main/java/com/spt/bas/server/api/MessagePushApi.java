package com.spt.bas.server.api;

import com.spt.bas.client.vo.MessagePushVo;
import com.spt.bas.client.vo.api.RespVo;
import com.spt.bas.server.service.IMessagePushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: gaojy
 * @create 2022/3/18 18:04
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping(value = "push/message")
public class MessagePushApi{
    @Autowired
    private IMessagePushService messagePushService;

    @PostMapping(value="sendMessage")
    public RespVo<?> sendMessage(@RequestBody MessagePushVo messagePushVo) {
        return messagePushService.sendMessage(messagePushVo);
    }
}
