package com.spt.bas.server.rocketmq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 信息队列传输订单信息实体
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderMessage {


    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 新状态
     */
    private String newStatus;

    /**
     * 支付方式
     */
    private String paymentMethod;

}
