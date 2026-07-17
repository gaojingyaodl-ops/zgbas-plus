package com.spt.bas.report.client.vo;

import java.math.BigDecimal;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/11/8 10:36
 */

public class RptBisPmApproveResVo {

    /**
     * 审批编号
     */
    private String approveNo;

    /**
     * 内容
     */
    private BigDecimal dealAmount;

    /**
     * 流程code
     */
    private String processCode;

    /**
     * 费用类别
     */
    private String costType;

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }

    public String getProcessCode() {
        return processCode;
    }

    public void setProcessCode(String processCode) {
        this.processCode = processCode;
    }

    public String getApproveNo() {
        return approveNo;
    }

    public void setApproveNo(String approveNo) {
        this.approveNo = approveNo;
    }

    public BigDecimal getDealAmount() {
        return dealAmount;
    }

    public void setDealAmount(BigDecimal dealAmount) {
        this.dealAmount = dealAmount;
    }
}
