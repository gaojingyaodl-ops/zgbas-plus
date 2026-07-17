package com.spt.bas.report.client.vo;

/**
 * 业绩提成vo
 *
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/6/20 10:33
 */

public class RptPerformanceVo {
    /**
     * 累计未结算提成
     */
    private RptIndexCommonVo noSettlementCommissionSum;

    /**
     * 预计提成
     */
    private RptIndexCommonVo planCommission;

    /**
     * 上个月提成
     */
    private RptIndexCommonVo lastMonthCommission;

    public RptPerformanceVo() {
    }

    public RptPerformanceVo(RptIndexCommonVo noSettlementCommissionSum, RptIndexCommonVo planCommission, RptIndexCommonVo lastMonthCommission) {
        this.noSettlementCommissionSum = noSettlementCommissionSum;
        this.planCommission = planCommission;
        this.lastMonthCommission = lastMonthCommission;
    }

    public RptIndexCommonVo getNoSettlementCommissionSum() {
        return noSettlementCommissionSum;
    }

    public void setNoSettlementCommissionSum(RptIndexCommonVo noSettlementCommissionSum) {
        this.noSettlementCommissionSum = noSettlementCommissionSum;
    }

    public RptIndexCommonVo getPlanCommission() {
        return planCommission;
    }

    public void setPlanCommission(RptIndexCommonVo planCommission) {
        this.planCommission = planCommission;
    }

    public RptIndexCommonVo getLastMonthCommission() {
        return lastMonthCommission;
    }

    public void setLastMonthCommission(RptIndexCommonVo lastMonthCommission) {
        this.lastMonthCommission = lastMonthCommission;
    }
}
