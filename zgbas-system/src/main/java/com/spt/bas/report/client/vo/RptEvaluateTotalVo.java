package com.spt.bas.report.client.vo;

import java.math.BigDecimal;

/**
 * 考核汇总
 */
public class RptEvaluateTotalVo {
    private String evaluateMonth; //	varchar(20)	考核年月
    private String deptName; //	varchar(20)	考核部门
    private Integer totalUsers; //	init(10)	考核人数
    private Integer evaluateNums; //	int(10)	已考评
    private Integer approveNums; //	init(10)	已审核
    private Integer confirmNums; //	init(10)	已确认
    private BigDecimal avgScore; //	decimal(10,2)	平均得分
    private Integer appealNums; //	init(10)	申诉数量

    public RptEvaluateTotalVo() {
    }

    public RptEvaluateTotalVo(String evaluateMonth, String deptName, Integer totalUsers, Integer evaluateNums, Integer approveNums, Integer confirmNums, BigDecimal avgScore, Integer appealNums) {
        this.evaluateMonth = evaluateMonth;
        this.deptName = deptName;
        this.totalUsers = totalUsers;
        this.evaluateNums = evaluateNums;
        this.approveNums = approveNums;
        this.confirmNums = confirmNums;
        this.avgScore = avgScore;
        this.appealNums = appealNums;
    }

    @Override
    public String toString() {
        return "EvaluateTotalVo{" +
                "evaluateMonth='" + evaluateMonth + '\'' +
                ", deptName='" + deptName + '\'' +
                ", totalUsers=" + totalUsers +
                ", evaluateNums=" + evaluateNums +
                ", approveNums=" + approveNums +
                ", confirmNums=" + confirmNums +
                ", avgScore=" + avgScore +
                ", appealNums=" + appealNums +
                '}';
    }

    public String getEvaluateMonth() {
        return evaluateMonth;
    }

    public void setEvaluateMonth(String evaluateMonth) {
        this.evaluateMonth = evaluateMonth;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Integer getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Integer totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Integer getEvaluateNums() {
        return evaluateNums;
    }

    public void setEvaluateNums(Integer evaluateNums) {
        this.evaluateNums = evaluateNums;
    }

    public Integer getApproveNums() {
        return approveNums;
    }

    public void setApproveNums(Integer approveNums) {
        this.approveNums = approveNums;
    }

    public Integer getConfirmNums() {
        return confirmNums;
    }

    public void setConfirmNums(Integer confirmNums) {
        this.confirmNums = confirmNums;
    }

    public BigDecimal getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(BigDecimal avgScore) {
        this.avgScore = avgScore;
    }

    public Integer getAppealNums() {
        return appealNums;
    }

    public void setAppealNums(Integer appealNums) {
        this.appealNums = appealNums;
    }
}
