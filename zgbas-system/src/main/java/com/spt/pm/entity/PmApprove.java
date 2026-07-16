package com.spt.pm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 审批信息主表
 */
@Entity
@Table(name = "t_pm_approve")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PmApprove extends IdEntity {

    private static final long serialVersionUID = 8833321958431034250L;

    /**
     * 审批编号
     */
    private String approveNo;

    /**
     * 状态 'N-新增，A-审批中，B-驳回，D-完成'，C-作废
     */
    private String status;

    /**
     * 前审批人,多人用|隔开
     */
    private String currApproverUserId;

    /**
     * 当前步骤名称
     */
    private String currStepName;

    /**
     * 当前审批步骤id
     */
    private String currApproveStepId;

    /**
     * 最近审批时间
     */
    private Date lastApproveDate;

    /**
     * 最近审批人姓名
     */
    private String lastApproveUserName;

    /**
     * 最近审批人
     */
    private Long lastApproveUserId;

    /**
     * 最近审批意见
     */
    private String lastApproveRemark;

    /**
     * 附件id
     */
    private String fileId;

    /**
     * 失效时间
     */
    private Date invalidTime;

    /**
     * 流程名称
     */
    private String processName;

    /**
     * 流程id
     */
    private Long processId;

    /**
     * 条件id
     */
    private Long conditionId;

    /**
     * 业务类型 ; 记录业务实体名
     */
    private String bizType;

    /**
     * 业务id
     */
    private Long bizId;

    /**
     * 创建人id
     */
    private Long createUserId;

    /**
     * 合作业务员Id
     */
    private Long cooperationUserId;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 标题
     */
    private String subject;

    /**
     * 企业帐套ID
     */
    private Long enterpriseId;

    /**
     * 关联查询使用：我审批过
     */
    private List<PmApproveHistory> approveHistories;

    /**
     * 关联查询使用：推送给我
     */
    private List<PmApprovePush> approvePushes;

    /**
     * 更改时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    //@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date updatedDate;

    /**
     * 审批发起时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    /**
     * 部门ID
     */
    private Long deptId;

    private Long companyId;

    private Long contractId;

    /**
     * 自动签时限(分钟)
     */
    private Long autoSignLimit;

    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean enableFlg = true;

    /**
     * 来源：0-内部，1-新审批
     */
    private String source;

    /**
     * 新审批id
     */
    private Long newApproveId;

    /**
     * 外部隐藏
     */
    private String hideOut = "0";

    /**
     * 采销中心标识
     */
    private Boolean tradeFlg; 

    public Long getNewApproveId() {
        return newApproveId;
    }

    public void setNewApproveId(Long newApproveId) {
        this.newApproveId = newApproveId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getApproveNo() {
        return approveNo;
    }

    public void setApproveNo(String approveNo) {
        this.approveNo = approveNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrApproverUserId() {
        return currApproverUserId;
    }

    public void setCurrApproverUserId(String approverUserId) {
        this.currApproverUserId = approverUserId;
    }

    public Date getLastApproveDate() {
        return lastApproveDate;
    }

    public void setLastApproveDate(Date lastApproveDate) {
        this.lastApproveDate = lastApproveDate;
    }

    public Long getLastApproveUserId() {
        return lastApproveUserId;
    }

    public void setLastApproveUserId(Long lastApproveUserId) {
        this.lastApproveUserId = lastApproveUserId;
    }

    public String getLastApproveRemark() {
        return lastApproveRemark;
    }

    public void setLastApproveRemark(String lastApproveRemark) {
        this.lastApproveRemark = lastApproveRemark;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Date getInvalidTime() {
        return invalidTime;
    }

    public void setInvalidTime(Date invalidTime) {
        this.invalidTime = invalidTime;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public Long getConditionId() {
        return conditionId;
    }

    public void setConditionId(Long conditionId) {
        this.conditionId = conditionId;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

    public String getCurrStepName() {
        return currStepName;
    }

    public void setCurrStepName(String stepName) {
        this.currStepName = stepName;
    }

    public String getCurrApproveStepId() {
        return currApproveStepId;
    }

    public void setCurrApproveStepId(String approveStepId) {
        this.currApproveStepId = approveStepId;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getLastApproveUserName() {
        return lastApproveUserName;
    }

    public void setLastApproveUserName(String lastApproveUserName) {
        this.lastApproveUserName = lastApproveUserName;
    }

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "approveId")
    public List<PmApproveHistory> getApproveHistories() {
        return approveHistories;
    }

    public void setApproveHistories(List<PmApproveHistory> approveHistories) {
        this.approveHistories = approveHistories;
    }

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "approveId")
    public List<PmApprovePush> getApprovePushes() {
        return approvePushes;
    }

    public void setApprovePushes(List<PmApprovePush> approvePushes) {
        this.approvePushes = approvePushes;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public Boolean getEnableFlg() {
        return enableFlg;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public void setEnableFlg(Boolean enableFlg) {
        this.enableFlg = enableFlg;
    }

    public Long getAutoSignLimit() {
        return autoSignLimit;
    }

    public void setAutoSignLimit(Long autoSignLimit) {
        this.autoSignLimit = autoSignLimit;
    }

    public Long getCooperationUserId() {
        return cooperationUserId;
    }

    public void setCooperationUserId(Long cooperationUserId) {
        this.cooperationUserId = cooperationUserId;
    }

    public String getHideOut() {
        return hideOut;
    }

    public void setHideOut(String hideOut) {
        this.hideOut = hideOut;
    }

    public Boolean getTradeFlg() {
        return tradeFlg;
    }

    public void setTradeFlg(Boolean tradeFlg) {
        this.tradeFlg = tradeFlg;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}
