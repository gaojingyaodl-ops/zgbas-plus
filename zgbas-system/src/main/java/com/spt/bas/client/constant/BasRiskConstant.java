package com.spt.bas.client.constant;

import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-01-22 16:16
 */
public interface BasRiskConstant {
    /**
     * 默认服务费率
     */
    BigDecimal DEFAULT_RATE = new BigDecimal("0.0003");

    /**
     * 默认超期服务费率
     */
    BigDecimal DEFAULT_INTEREST_RATE = new BigDecimal("0.001");

    /**
     * 基础赊销额度
     */
    BigDecimal DEFAULT_QUOTA = new BigDecimal("300000");
}
