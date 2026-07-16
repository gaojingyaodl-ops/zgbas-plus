package com.spt.pm.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 流程推送配置表
 * @Author: gaojy
 * @create 2022/4/26 10:54
 * @version: 1.0
 * @description:
 */
@Entity
@Table(name = "t_pm_process_push")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PmProcessPush extends IdEntity {
    private static final long serialVersionUID = 222441519875857696L;

    /**
     * 流程ID
     */
    private Long processId;

    /**
     * 流程名称
     */
    private String processName;

    /**
     * 条件内容
     */
    private String conditionValue;

    /**
     * 参照节点
     */
    private Long referNodeId;

    /**
     * 是否有效
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean enableFlg;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 企业账号ID
     */
    private Long enterpriseId;

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getConditionValue() {
        return conditionValue;
    }

    public void setConditionValue(String conditionValue) {
        this.conditionValue = conditionValue;
    }

    public Long getReferNodeId() {
        return referNodeId;
    }

    public void setReferNodeId(Long referNodeId) {
        this.referNodeId = referNodeId;
    }

    public Boolean getEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(Boolean enableFlg) {
        this.enableFlg = enableFlg;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }
}
