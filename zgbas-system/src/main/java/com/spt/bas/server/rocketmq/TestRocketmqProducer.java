package com.spt.bas.server.rocketmq;

import cn.hutool.json.JSONUtil;
import com.spt.bas.server.rocketmq.dto.ContractMessage;
import com.spt.bas.server.rocketmq.dto.OrderMessage;
import com.spt.bas.server.rocketmq.tags.ContractTagsEnum;
import com.spt.bas.server.rocketmq.tags.OrderTagsEnum;
import com.spt.bas.server.rocketmq.util.RocketmqSendCallbackBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class TestRocketmqProducer {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private RocketmqCustomProperties rocketmqCustomProperties;

    public void sendString() {
        OrderMessage orderMessage = new OrderMessage();
        orderMessage.setOrderSn("001");
        orderMessage.setPaymentMethod("test");
        orderMessage.setNewStatus("PAID");

        String destination = rocketmqCustomProperties.getDemoStringTopic() + ":"+ OrderTagsEnum.ORDER_CREATE.name();
        //发送订单变更mq消息
        rocketMQTemplate.asyncSend(destination, "这是测试消息", RocketmqSendCallbackBuilder.commonCallback());

    }

    public void send() {

        OrderMessage orderMessage = new OrderMessage();
        orderMessage.setOrderSn("001");
        orderMessage.setPaymentMethod("test");
        orderMessage.setNewStatus("PAID");

        String destination = rocketmqCustomProperties.getDemoOrderTopic() + ":"+ OrderTagsEnum.STATUS_CHANGE.name();
        //发送订单变更mq消息
        rocketMQTemplate.asyncSend(destination, JSONUtil.toJsonStr(orderMessage), RocketmqSendCallbackBuilder.commonCallback());


    }

    public void sendContract() {

        ContractMessage message = new ContractMessage();
        message.setContractNo("001");
        message.setContractTime(new Date());
        message.setCompanyId(10L);
        message.setCompanyName("客户企业名称");
        message.setDeliveryAddr("上海浦东外高桥");
        message.setDeliveryMode("客户自提");
        message.setDeliveryPhone("13333333333");
        message.setDeliveryType("货到付款");
        message.setPayType("商票");
        message.setOurCompanyName("青岛中光");
        String destination = rocketmqCustomProperties.getContractTopic() + ":"+ ContractTagsEnum.SIGN.name();
        //发送订单变更mq消息
        rocketMQTemplate.asyncSend(destination, JSONUtil.toJsonStr(message), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("消息发送成功={}", sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("消息发送失败", throwable);
            }
        });


    }

    void sendDelay() {
        String destination = rocketmqCustomProperties.getDemoOrderTopic() + ":" + OrderTagsEnum.STATUS_CHANGE.name();
        Message<String> message = MessageBuilder.withPayload("Context").build();

        rocketMQTemplate.asyncSend(destination, message, RocketmqSendCallbackBuilder.commonCallback());
        //支持的level如下：level=0 级表示不延时，level=1 表示 延时1s，level=2 表示 延时5s
        //参数四：delayLevel 延时level  messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
        rocketMQTemplate.asyncSend(destination, message, RocketmqSendCallbackBuilder.commonCallback(),1000,3);
    }
}
