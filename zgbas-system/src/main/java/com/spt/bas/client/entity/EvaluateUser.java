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
 * 考核人员清单表
 */
@Entity
@Table(name = "t_evaluate_user")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EvaluateUser extends IdEntity {

    /**
     * 年月
     */
    private String evaluateMonth;

    /**
     * 考评日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date evaluateDate;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 上级评语
     */
    private String evaluateRemark;

    /**
     * 评分
     */
    private Integer score;

    /**
     * 申诉标记
     */
    private String appealFlag;

    /**
     * 状态，0-未考评，1-已考评，2-已审核，3-已完成
     */
    private String status;

    /**
     * 考核人所属分部简码
     */
    private String branchCd;

    /**
     * 考核人ID
     */
    private Long assessmentUserId;

    /**
     * 考核人名称
     */
    private String assessmentUserName;

    public String getEvaluateMonth() {
        return evaluateMonth;
    }

    public void setEvaluateMonth(String evaluateMonth) {
        this.evaluateMonth = evaluateMonth;
    }

    public Date getEvaluateDate() {
        return evaluateDate;
    }

    public void setEvaluateDate(Date evaluateDate) {
        this.evaluateDate = evaluateDate;
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

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getEvaluateRemark() {
        return evaluateRemark;
    }

    public void setEvaluateRemark(String evaluateRemark) {
        this.evaluateRemark = evaluateRemark;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getAppealFlag() {
        return appealFlag;
    }

    public void setAppealFlag(String appealFlag) {
        this.appealFlag = appealFlag;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBranchCd() {
        return branchCd;
    }

    public void setBranchCd(String branchCd) {
        this.branchCd = branchCd;
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
