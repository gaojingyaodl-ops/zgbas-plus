package com.spt.bas.report.client.vo;

import java.math.BigDecimal;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/6/16 16:54
 */

public class RptNobillVo {
    /**
     * 总的钱
     */
    private BigDecimal sumTotalMount;

    private BigDecimal sumCount;

    public BigDecimal getSumTotalMount() {
        return sumTotalMount;
    }

    public void setSumTotalMount(BigDecimal sumTotalMount) {
        this.sumTotalMount = sumTotalMount;
    }

    public BigDecimal getSumCount() {
        return sumCount;
    }

    public void setSumCount(BigDecimal sumCount) {
        this.sumCount = sumCount;
    }
}
