package com.spt.bas.server.service;

import com.spt.bas.client.vo.MessagePushVo;
import com.spt.bas.client.vo.api.RespVo;

/**
 * @Author: gaojy
 * @create 2022/3/18 18:00
 * @version: 1.0
 * @description:
 */
public interface IMessagePushService {

    RespVo<?> sendMessage(MessagePushVo messagePushVo);
}
