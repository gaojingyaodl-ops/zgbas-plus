package com.spt.bas.client.vo;

import lombok.Data;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2024/3/6 10:45
 */
@Data
public class CreditDeliveryVo {

    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 审批id
     */
    private Long approveId;

    /**
     * 终端工厂自提
     */
    private String interestRate;
    /**
     * 备注
     */
    private String remark;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 申请人id
     */
    private Long userId;

    /**
     * 部门id
     */
    private Long deptId;

    /**
     * 申请人名称
     */
    private String nickName;

    /**
     * 终端工厂自提方式（是 1 ，否 0）
     */
    private String creditDelivery;

}
