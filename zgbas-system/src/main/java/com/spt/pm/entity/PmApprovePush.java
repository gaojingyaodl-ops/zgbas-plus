package com.spt.pm.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 审批推送记录表
 * @Author: gaojy
 * @create 2022/4/26 11:05
 * @version: 1.0
 * @description:
 */
@Entity
@Table(name = "t_pm_approve_push")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PmApprovePush extends IdEntity {
    private static final long serialVersionUID = 2635206033608283376L;

    /**
     * 审批单ID
     */
    private Long approveId;

    /**
     * 审批单编号
     */
    private String approveNo;

    /**
     * 流程ID
     */
    private Long processId;

    /**
     * 推送类型
     * 1-完成系统自动推送
     */
    private String pushType;

    /**
     * 被推送人ID
     */
    private Long pushToUserId;

    /**
     * 被推送人姓名
     */
    private String pushToUserName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 企业账套ID
     */
    private Long enterpriseId;

    public Long getApproveId() {
        return approveId;
    }

    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public String getApproveNo() {
        return approveNo;
    }

    public void setApproveNo(String approveNo) {
        this.approveNo = approveNo;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    public Long getPushToUserId() {
        return pushToUserId;
    }

    public void setPushToUserId(Long pushToUserId) {
        this.pushToUserId = pushToUserId;
    }

    public String getPushToUserName() {
        return pushToUserName;
    }

    public void setPushToUserName(String pushToUserName) {
        this.pushToUserName = pushToUserName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }
}
