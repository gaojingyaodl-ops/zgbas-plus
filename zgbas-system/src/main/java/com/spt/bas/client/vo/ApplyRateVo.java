package com.spt.bas.client.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2024/3/6 10:34
 */
@Data
public class ApplyRateVo {

    /**
     * 公司id
     */
    private Long companyId;

    /**
     *
     */
    private BigDecimal interestRate;

    /**
     *
     */
    private BigDecimal rate;

    /**
     *
     */
    private Long userId;

    /**
     *
     */
    private String nickName;

}
