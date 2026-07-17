package com.spt.bas.server.service.impl;

import com.hsoft.push.sdk.vo.PushResponse;
import com.spt.bas.client.vo.MessagePushVo;
import com.spt.bas.client.vo.api.RespVo;
import com.spt.bas.server.service.IMessagePushService;
import com.spt.bas.server.util.SMSUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @Author: gaojy
 * @create 2022/3/18 18:03
 * @version: 1.0
 * @description:
 */
@Component
@Slf4j
public class MessagePushServiceImpl implements IMessagePushService {
    @Override
    public RespVo<?> sendMessage(MessagePushVo messagePushVo) {
        RespVo respVo = new RespVo();
        if (StringUtils.isBlank(messagePushVo.getPhone())){
            respVo.setMessage("手机号不可为空！");
            respVo.setCode(201);
            return respVo;
        }
        if (StringUtils.isBlank(messagePushVo.getMessage())){
            respVo.setMessage("信息不可为空！");
            respVo.setCode(201);
            return respVo;
        }
        try {
            PushResponse pushResponse = SMSUtils.sendVerificationCode(messagePushVo.getPhone(), messagePushVo.getTitle(), messagePushVo.getMessage());
            if (Objects.nonNull(pushResponse)) {
                respVo.setData(pushResponse.getCode());
            }
        } catch (Exception e) {
            log.error("sendMessage error", e);
        }
        return respVo;
    }
}
