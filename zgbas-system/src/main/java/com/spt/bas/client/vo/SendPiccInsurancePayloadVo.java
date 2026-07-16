package com.spt.bas.client.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2024/3/4 17:37
 */
@Data
public class SendPiccInsurancePayloadVo {
    private Long companyId;

    /**
     * 金额
     */
    private BigDecimal appliAmount;

    /**
     * 回款期限
     */
    private Integer paidTerm;
}
