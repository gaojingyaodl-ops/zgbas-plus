package com.spt.bas.server.rocketmq.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;

@Slf4j
public class RocketmqSendCallback implements SendCallback {

    @Override
    public void onSuccess(SendResult sendResult) {
        // log.info("异步消息发送成功！SendResult={}", sendResult);
    }

    @Override
    public void onException(Throwable throwable) {
        log.error("异步消息发送失败！！！", throwable);
    }
}
