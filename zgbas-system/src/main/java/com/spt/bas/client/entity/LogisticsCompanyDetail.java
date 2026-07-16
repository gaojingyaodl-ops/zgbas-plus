package com.spt.bas.client.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 物流承运商评价明细表
 */
@Entity
@Table(name = "t_logistics_company_detail")
public class LogisticsCompanyDetail extends IdEntity {

    /**
     *承运商ID
     */
    private Long logisticsCompanyId;

    /**
     *出库单号
     */
    private  String deliveryOutNo;

    /**
     *出库日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date deliveryOutDate;

    /**
     *综合评分
     */
    private BigDecimal score;

    /**
     *综合描述
     */
    private  String evaluate;

    public Long getLogisticsCompanyId() {
        return logisticsCompanyId;
    }

    public void setLogisticsCompanyId(Long logisticsCompanyId) {
        this.logisticsCompanyId = logisticsCompanyId;
    }

    public String getDeliveryOutNo() {
        return deliveryOutNo;
    }

    public void setDeliveryOutNo(String deliveryOutNo) {
        this.deliveryOutNo = deliveryOutNo;
    }

    public Date getDeliveryOutDate() {
        return deliveryOutDate;
    }

    public void setDeliveryOutDate(Date deliveryOutDate) {
        this.deliveryOutDate = deliveryOutDate;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(String evaluate) {
        this.evaluate = evaluate;
    }
}
