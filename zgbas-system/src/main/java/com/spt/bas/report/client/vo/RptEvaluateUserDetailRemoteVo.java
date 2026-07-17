package com.spt.bas.report.client.vo;

/**
 *
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/5/20 11:29
 */

public class RptEvaluateUserDetailRemoteVo {

    /**
     * t_evaluate_user_detail表id
     */
    private Long evaluateDetailId;

    /**
     * t_evaluate_user表 id
     */
    private Long evaluateUserId;
    /**
     * 考核项目
     */
    private String evaluateGroup;

    /**
     * 考核指标
     */
    private String evaluateMetrics;

    /**
     * 权重
     */
    private Integer weight;

    /**
     * 指标定义
     */
    private String metricsContent;

    /**
     * 年-月
     */
    private String evaluateMonth;

    /**
     * 评分部门
     */
    private String evaluateDept;

    /**
     * 评分人员 id
     */
    private String scoreUserId;

    /**
     * 评分
     */
    private Integer detailScore;

    /**
     * 评分人员名称
     */
    private String scoreUserName;

    /**
     * 考评日期
     */
    private String evaluateDate;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 部门 id
     */
    private Long deptId;

    /**
     * 评语
     */
    private String evaluateRemark;

    /**
     * 序号
     */
    private Integer dispOrderNo;

    /**
     * evaluateUser表的状态
     */
    private String evaluateUserStatus;
    /**
     * evaluateUserDetail表的状态
     */
    private String evaluateUserDetailStatus;

    public String getEvaluateUserStatus() {
        return evaluateUserStatus;
    }

    public void setEvaluateUserStatus(String evaluateUserStatus) {
        this.evaluateUserStatus = evaluateUserStatus;
    }

    public String getEvaluateUserDetailStatus() {
        return evaluateUserDetailStatus;
    }

    public void setEvaluateUserDetailStatus(String evaluateUserDetailStatus) {
        this.evaluateUserDetailStatus = evaluateUserDetailStatus;
    }

    public Integer getDispOrderNo() {
        return dispOrderNo;
    }

    public void setDispOrderNo(Integer dispOrderNo) {
        this.dispOrderNo = dispOrderNo;
    }

    public String getEvaluateRemark() {
        return evaluateRemark;
    }

    public void setEvaluateRemark(String evaluateRemark) {
        this.evaluateRemark = evaluateRemark;
    }

    public String getEvaluateGroup() {
        return evaluateGroup;
    }

    public void setEvaluateGroup(String evaluateGroup) {
        this.evaluateGroup = evaluateGroup;
    }

    public String getEvaluateMetrics() {
        return evaluateMetrics;
    }

    public void setEvaluateMetrics(String evaluateMetrics) {
        this.evaluateMetrics = evaluateMetrics;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getMetricsContent() {
        return metricsContent;
    }

    public void setMetricsContent(String metricsContent) {
        this.metricsContent = metricsContent;
    }

    public String getEvaluateDept() {
        return evaluateDept;
    }

    public void setEvaluateDept(String evaluateDept) {
        this.evaluateDept = evaluateDept;
    }

    public String getScoreUserId() {
        return scoreUserId;
    }

    public void setScoreUserId(String scoreUserId) {
        this.scoreUserId = scoreUserId;
    }

    public String getScoreUserName() {
        return scoreUserName;
    }

    public void setScoreUserName(String scoreUserName) {
        this.scoreUserName = scoreUserName;
    }

    public String getEvaluateDate() {
        return evaluateDate;
    }

    public void setEvaluateDate(String evaluateDate) {
        this.evaluateDate = evaluateDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getEvaluateDetailId() {
        return evaluateDetailId;
    }

    public void setEvaluateDetailId(Long evaluateDetailId) {
        this.evaluateDetailId = evaluateDetailId;
    }

    public Long getEvaluateUserId() {
        return evaluateUserId;
    }

    public void setEvaluateUserId(Long evaluateUserId) {
        this.evaluateUserId = evaluateUserId;
    }

    public Integer getDetailScore() {
        return detailScore;
    }

    public void setDetailScore(Integer detailScore) {
        this.detailScore = detailScore;
    }

    public String getEvaluateMonth() {
        return evaluateMonth;
    }

    public void setEvaluateMonth(String evaluateMonth) {
        this.evaluateMonth = evaluateMonth;
    }
}
