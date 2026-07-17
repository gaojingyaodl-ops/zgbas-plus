package com.spt.bas.server.rocketmq.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ContractMessage {


    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 合同总价
     */
    private BigDecimal totalAmount;

    /**
     * 合同时间
     */
    private Date contractTime;

    /**
     * 对方企业ID
     */
    private Long companyId;

    /**
     * 对方企业名称
     */
    private String companyName;

    /**
     * 我方企业名称
     */
    private String ourCompanyName;

    /**
     * 提货方式
     */
    private String deliveryType;

    /**
     * 付款方式
     */
    private String payType;

    /**
     * 合同数量
     */
    private BigDecimal totalNumber;

    /**
     * 交货方式
     */
    private String deliveryMode;

    /**
     * 交货地址
     */
    private String deliveryAddr;

    /**
     * 配送电话
     */
    private String deliveryPhone;

}
