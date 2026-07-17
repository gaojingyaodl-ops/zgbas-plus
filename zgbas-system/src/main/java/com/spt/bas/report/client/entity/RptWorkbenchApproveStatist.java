package com.spt.bas.report.client.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 业务经理工作台审批统计
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RptWorkbenchApproveStatist {

    /**
     * 标题Code
     */
    private String labelCode;
    
    /**
     * 标题Name
     */
    private String labelName;

    /**
     * 预算数量
     */
    private Long budgetCount;

    /**
     * 供应商双签数量
     */
    private Long buySealCount;

    /**
     * 客户双签数量
     */
    private Long sellSealCount;

    /**
     * 供应商付款数量
     */
    private Long buyPayCount;
    
    private Long count;

    public RptWorkbenchApproveStatist(String labelCode, String labelName, Long budgetCount) {
        this.labelCode = labelCode;
        this.labelName = labelName;
        this.budgetCount = budgetCount;
    }
}
