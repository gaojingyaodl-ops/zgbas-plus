package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 考核明细表
 */
@Entity
@Table(name = "t_evaluate_user_detail")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EvaluateUserDetail extends IdEntity {
    private Long evaluateUserId; //	bigint	20 考核人员id
    private Long evaluateItemId; //	bigint	20 考核条目id
    private String evaluateMetrics; //	varchar	20 考核指标
    private Integer weight; //	int	10 权重(分)
    private Integer score; //	int	10 评分
    private String scoreUserId; //	bigint	20 评分人员id
    private String scoreUserName; //	varchar	20 评分人员名称
    private Integer dispOrderNo; //	int	10 序号
    private String status; //	char	1 状态，0-未考评，1-已考评

    public Long getEvaluateUserId() {
        return evaluateUserId;
    }

    public void setEvaluateUserId(Long evaluateUserId) {
        this.evaluateUserId = evaluateUserId;
    }

    public Long getEvaluateItemId() {
        return evaluateItemId;
    }

    public void setEvaluateItemId(Long evaluateItemId) {
        this.evaluateItemId = evaluateItemId;
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

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
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

    public Integer getDispOrderNo() {
        return dispOrderNo;
    }

    public void setDispOrderNo(Integer dispOrderNo) {
        this.dispOrderNo = dispOrderNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
