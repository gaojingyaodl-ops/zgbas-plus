package com.spt.bas.report.client.vo;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/5/23 11:59
 */

public class RptDetailScoreVo {
    /**
     * 评分
     */
    private Integer detailScore;

    private Long evaluateDetailId;

    private Long evaluateUserId;

    public Integer getDetailScore() {
        return detailScore;
    }

    public void setDetailScore(Integer detailScore) {
        this.detailScore = detailScore;
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

    @Override
    public String toString() {
        return "DetailScoreVo{" +
                "detailScore=" + detailScore +
                ", evaluateDetailId=" + evaluateDetailId +
                ", evaluateUserId=" + evaluateUserId +
                '}';
    }
}
