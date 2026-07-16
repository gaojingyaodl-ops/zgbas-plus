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
@Table(name = "t_evaluate_appeal")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EvaluateAppeal extends IdEntity {
    private Long evaluateUserId;//	bigint	20	考核明细id
    private String appealRemark; //	varchar	500	申诉内容
    private String appealStatus; //	char	1	0-未处理，1-已处理

    public Long getEvaluateUserId() {
        return evaluateUserId;
    }

    public void setEvaluateUserId(Long evaluateUserId) {
        this.evaluateUserId = evaluateUserId;
    }

    public String getAppealRemark() {
        return appealRemark;
    }

    public void setAppealRemark(String appealRemark) {
        this.appealRemark = appealRemark;
    }

    public String getAppealStatus() {
        return appealStatus;
    }

    public void setAppealStatus(String appealStatus) {
        this.appealStatus = appealStatus;
    }
}
