package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * <p>
 *     查询合同业务操作记录vo
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-13 09:05
 */
public class RptContractOperationVo {

    /**
     * 序号
     */
    private Integer orderNo;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date createdDate;

    /**
     * 操作id
     */
    private Long operationId;

    /**
     * 摘要
     */
    private String subject;

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

}
