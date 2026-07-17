package com.spt.bas.report.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 业务经理工作台审批
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RptWorkbenchApprove {
    
    private Long approveId;
    private Long contractId;

    /**
     * 当前审批人
     */
    private String currApproveUserId;
    private String currApproveUserName;
    
    /**
     * 预算类型
     */
    private String budgetName;

    /**
     * 审批标题
     */
    private String subject;

    /**
     * 预算申请状态
     */
    private String budgetStatus;

    /**
     * 预算审批人（审批中：当前审批人，审批完成：最后审批人）
     */
    private String budgetApproveUserName;

    /**
     * 预算最后审批时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd  HH:mm:ss", timezone = "GMT+08:00")
    private Date budgetApproveLastTime;
    /**
     * 供应商双签申请Id
     */
    private Long buySealApproveId;
    /**
     * 供应商预算类型
     */
    private String buySealBudgetName;

    /**
     * 供应商双签申请状态
     */
    private String buySealStatus;

    /**
     * 供应商双签审批人（审批中：当前审批人，审批完成：最后审批人）
     */
    private String buySealApproveUserName;

    /**
     * 供应商双签最后审批时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd  HH:mm:ss", timezone = "GMT+08:00")
    private Date buySealApproveLastTime;
    /**
     * 客户双签申请Id
     */
    private Long sellSealApproveId;
    /**
     * 客户双签预算类型
     */
    private String sellSealBudgetName;
    /**
     * 客户双签申请状态
     */
    private String sellSealStatus;

    /**
     * 客户双签审批人（审批中：当前审批人，审批完成：最后审批人）
     */
    private String sellSealApproveUserName;

    /**
     * 客户双签最后审批时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd  HH:mm:ss", timezone = "GMT+08:00")
    private Date sellSealApproveLastTime;

    /**
     * 供应商付款申请Id
     */
    private Long buyPayApproveId;
    /**
     * 供应商付款预算类型
     */
    private String buyPayBudgetName;

    /**
     * 供应商付款申请状态
     */
    private String buyPayStatus;

    /**
     * 供应商付款审批人（审批中：当前审批人，审批完成：最后审批人）
     */
    private String buyPayApproveUserName;

    /**
     * 供应商付款最后审批时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd  HH:mm:ss", timezone = "GMT+08:00")
    private Date buyPayApproveLastTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd  HH:mm:ss", timezone = "GMT+08:00")
    private Date createdDate;
    
}
