package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 考核条目表
 */
@Entity
@Table(name = "t_evaluate_item")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EvaluateItem extends IdEntity {
    private static final long serialVersionUID = -2554866099497311201L;
    
    private String evaluateGroup; //	varchar 考核项目	
    private String evaluateMetrics; //	varchar 考核指标
    private Integer weight; //	int 权重(分)	
    private String metricsContent; //	varchar 指标定义	
    private String evaluateDept; //	varchar 评分部门，HR-人力行政部，UP-直接上级	
    private Integer dispOrderNo; //	int 序号	

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

    public Integer getDispOrderNo() {
        return dispOrderNo;
    }

    public void setDispOrderNo(Integer dispOrderNo) {
        this.dispOrderNo = dispOrderNo;
    }
}
