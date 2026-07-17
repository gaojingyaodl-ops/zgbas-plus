package com.spt.bas.report.client.vo;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/6/9 09:50
 */

public class RptDetailAndItemRemoteVo {
    /**
     * t_evaluate_user_detail表id
     */
    private Long evaluateDetailId;

    /**
     * t_evaluate_item表 id
     */
    private Long evaluateItemId;

    /**
     * t_evaluate_user表id
     */
    private Long evaluateUserId;

    /**
     * 评分人员 id
     */
    private String scoreUserId;

    /**
     * 评分人员名称
     */
    private String scoreUserName;

    /**
     * 评分
     */
    private Integer detailScore;

    /**
     * 评分详情表状态
     */
    private String detailStatus;

    /**
     * 评分部门 HR-人力，UP-上级
     */
    private String evaluateDept;

    public Long getEvaluateDetailId() {
        return evaluateDetailId;
    }

    public void setEvaluateDetailId(Long evaluateDetailId) {
        this.evaluateDetailId = evaluateDetailId;
    }

    public Long getEvaluateItemId() {
        return evaluateItemId;
    }

    public void setEvaluateItemId(Long evaluateItemId) {
        this.evaluateItemId = evaluateItemId;
    }

    public Long getEvaluateUserId() {
        return evaluateUserId;
    }

    public void setEvaluateUserId(Long evaluateUserId) {
        this.evaluateUserId = evaluateUserId;
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

    public Integer getDetailScore() {
        return detailScore;
    }

    public void setDetailScore(Integer detailScore) {
        this.detailScore = detailScore;
    }

    public String getDetailStatus() {
        return detailStatus;
    }

    public void setDetailStatus(String detailStatus) {
        this.detailStatus = detailStatus;
    }

    public String getEvaluateDept() {
        return evaluateDept;
    }

    public void setEvaluateDept(String evaluateDept) {
        this.evaluateDept = evaluateDept;
    }

    @Override
    public String toString() {
        return "DetailAndItemRemoteVo{" +
                "evaluateDetailId=" + evaluateDetailId +
                ", evaluateItemId=" + evaluateItemId +
                ", evaluateUserId=" + evaluateUserId +
                ", scoreUserId='" + scoreUserId + '\'' +
                ", scoreUserName='" + scoreUserName + '\'' +
                ", detailScore=" + detailScore +
                ", detailStatus='" + detailStatus + '\'' +
                ", evaluateDept='" + evaluateDept + '\'' +
                '}';
    }
}
