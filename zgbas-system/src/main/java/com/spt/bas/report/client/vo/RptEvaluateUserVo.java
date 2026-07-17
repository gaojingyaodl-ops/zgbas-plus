package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/5/27 09:52
 */

public class RptEvaluateUserVo {

    private String evaluateMonth; //	varchar	20 年月
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date evaluateDate; //	datetime	0 考评日期
    private Long userId; //	bigint	20 用户id
    private String userName; //	varchar	20 用户姓名
    private Long deptId; //	bigint	20 部门id
    private String deptName; //	varchar	50 部门名称
    private String evaluateRemark; //	varchar	500 上级评语
    private Integer score; //	int	10 评分
    private String appealFlag; //	char	1 申诉标记
    private String status; //	char	1 状态，0-未考评，1-已考评，2-已审核，3-已完成
    private Long assessmentUserId;// 上级id
    private String assessmentUserName;// 上级名字 / 评分人名字

    private Long id;
    private Date createdDate;
    private Date updatedDate;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

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

    @Override
    public String toString() {
        return "EvaluateUserVo{" +
                "evaluateMonth='" + evaluateMonth + '\'' +
                ", evaluateDate=" + evaluateDate +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", deptId=" + deptId +
                ", deptName='" + deptName + '\'' +
                ", evaluateRemark='" + evaluateRemark + '\'' +
                ", score=" + score +
                ", appealFlag='" + appealFlag + '\'' +
                ", status='" + status + '\'' +
                ", assessmentUserId=" + assessmentUserId +
                ", assessmentUserName='" + assessmentUserName + '\'' +
                ", id=" + id +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                '}';
    }
}
