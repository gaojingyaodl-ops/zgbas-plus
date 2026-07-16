package com.spt.bas.client.vo;

import java.math.BigDecimal;

/**
 * @author MoonLight
 * @version 1.0
 * @description
 * @date 2025/6/16 17:01
 */
public class SettlementCommissionVo {
    private BigDecimal commissionRate;

    private BigDecimal commissionRate2;

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    public BigDecimal getCommissionRate2() {
        return commissionRate2;
    }

    public void setCommissionRate2(BigDecimal commissionRate2) {
        this.commissionRate2 = commissionRate2;
    }

    public SettlementCommissionVo() {
    }

    public SettlementCommissionVo(BigDecimal commissionRate, BigDecimal commissionRate2) {
        this.commissionRate = commissionRate;
        this.commissionRate2 = commissionRate2;
    }

    public SettlementCommissionVo(BigDecimal commissionRate2) {
        this.commissionRate2 = commissionRate2;
    }
}
