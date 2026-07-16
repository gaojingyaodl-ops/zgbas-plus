package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 考核人员管理
 */
@Entity
@Table(name = "t_evaluate_user_manage")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EvaluateUserManage extends IdEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户姓名
     */
    private String userName;
   
    /**
     * 上级评语
     */
    private String remark;

    /**
     * 考核人ID
     */
    private Long assessmentUserId;

    /**
     * 考核人名称
     */
    private String assessmentUserName;

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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getAssessmentUserId() {
        return assessmentUserId;
    }

    public void setAssessmentUserId(Long assessmentUserId) {
        this.assessmentUserId = assessmentUserId;
    }

    public String getAssessmentUserName() {
        return assessmentUserName;
    }

    public void setAssessmentUserName(String assessmentUserName) {
        this.assessmentUserName = assessmentUserName;
    }
}
