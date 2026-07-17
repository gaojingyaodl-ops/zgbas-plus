package com.spt.bas.report.client.vo;

import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/5/23 12:00
 */

public class RptDetailSaveScoreVo {
    /**
     * 上级评语
     */
    private String evaluateRemark;
    /**
     * 评分信息
     */
    private List<RptDetailScoreVo> detailScoreArray;

    public String getEvaluateRemark() {
        return evaluateRemark;
    }

    public void setEvaluateRemark(String evaluateRemark) {
        this.evaluateRemark = evaluateRemark;
    }

    public List<RptDetailScoreVo> getDetailScoreArray() {
        return detailScoreArray;
    }

    public void setDetailScoreArray(List<RptDetailScoreVo> detailScoreArray) {
        this.detailScoreArray = detailScoreArray;
    }

    @Override
    public String toString() {
        return "DetailSaveScoreVo{" +
                "evaluateRemark='" + evaluateRemark + '\'' +
                ", detailScoreArray=" + detailScoreArray +
                '}';
    }
}
