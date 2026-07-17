package com.spt.bas.report.client.vo;

/**
 * 合同统计实体
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/6/16 15:54
 */

public class RptToDoStatisticsVo {

    /**
     * 进项
     */
    private RptIndexCommonVo buyNoBill;
    /**
     * 销售
     */
    private RptIndexCommonVo sellNoBill;

    /**
     * 待我审批个数
     */
    private Integer approvalWithMeCount;

    public RptIndexCommonVo getBuyNoBill() {
        return buyNoBill;
    }

    public void setBuyNoBill(RptIndexCommonVo buyNoBill) {
        this.buyNoBill = buyNoBill;
    }

    public RptIndexCommonVo getSellNoBill() {
        return sellNoBill;
    }

    public void setSellNoBill(RptIndexCommonVo sellNoBill) {
        this.sellNoBill = sellNoBill;
    }

    public Integer getApprovalWithMeCount() {
        return approvalWithMeCount;
    }

    public void setApprovalWithMeCount(Integer approvalWithMeCount) {
        this.approvalWithMeCount = approvalWithMeCount;
    }

    @Override
    public String toString() {
        return "ToDoStatisticsVo{" +
                "buyNoBill=" + buyNoBill +
                ", sellNoBill=" + sellNoBill +
                ", approvalWithMeCount=" + approvalWithMeCount +
                '}';
    }
}
