package com.spt.pm.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 额外条件节点配置
 * @Author: gaojy
 * @create 2022/2/8 14:59
 * @version: 1.0
 * @description:
 */
@Entity
@Table(name = "t_pm_process_auto_step")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PmProcessAutoStep extends IdEntity {

    private static final long serialVersionUID = -2611407579857254700L;

    /**
     * 关联流程ID
     */
    private Long processId;

    /**
     * 条件类型 A-增加步骤;S-减少步骤
     */
    private String conditionType;

    /**
     * 参照流程条件ID
     */
    private Long referConditionId;

    /**
     * 参照流程节点ID
     */
    private Long referNodeId;

    /**
     * 条件
     */
    private String conditionValue;

    /**F
     * 条件名称
     */
    private String conditionName;

    /**
     * 条件节点ID
     */
    private Long autoNodeId;

    /**
     * 偏移量
     */
    private Long autoOffSet;

    /**
     * 自动签时限(分钟)
     */
    private Long autoSignLimit;

    /**
     * 是否有效
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean enableFlg;

    /**
     * 排序号
     */
    private Long dispOrderNo;

    /**
     * 备注
     */
    private String remark;

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }

    public Long getReferConditionId() {
        return referConditionId;
    }

    public void setReferConditionId(Long referConditionId) {
        this.referConditionId = referConditionId;
    }

    public Long getReferNodeId() {
        return referNodeId;
    }

    public void setReferNodeId(Long referNodeId) {
        this.referNodeId = referNodeId;
    }

    public String getConditionValue() {
        return conditionValue;
    }

    public void setConditionValue(String conditionValue) {
        this.conditionValue = conditionValue;
    }

    public String getConditionName() {
        return conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }

    public Long getAutoOffSet() {
        return autoOffSet;
    }

    public void setAutoOffSet(Long autoOffSet) {
        this.autoOffSet = autoOffSet;
    }

    public Boolean getEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(Boolean enableFlg) {
        this.enableFlg = enableFlg;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getAutoNodeId() {
        return autoNodeId;
    }

    public void setAutoNodeId(Long autoNodeId) {
        this.autoNodeId = autoNodeId;
    }

    public Long getDispOrderNo() {
        return dispOrderNo;
    }

    public void setDispOrderNo(Long dispOrderNo) {
        this.dispOrderNo = dispOrderNo;
    }

    public Long getAutoSignLimit() {
        return autoSignLimit;
    }

    public void setAutoSignLimit(Long autoSignLimit) {
        this.autoSignLimit = autoSignLimit;
    }
}
