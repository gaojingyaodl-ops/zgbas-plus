package com.spt.bas.purchase.wx.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购管家报价表
 */
@Entity
@Table(name = "t_buy_quote")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BuyQuote extends IdEntity {
    /**
     * 单号
     */
    private String oddNumber;

    /**
     * 询价表id
     */
    private Long enquiryId;

    /**
     * '数量'
     */
    private BigDecimal dealNumber;

    /**
     * 销售价
     */
    private BigDecimal sellPrice;

    /**
     * 送到日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date arriveDate;

    /**
     * 失效时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+08:00")
    private Date expireTime;

    /**
     * 确定日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+08:00")
    private Date confirmTime;

    /**
     * '业务员名称'
     */
    private String userName;

    /**
     * 业务员id
     */
    private Long userId;

    /**
     * 部门id
     */
    private Long deptId;

    /**
     * '部门名称'
     */
    private String deptName;

    /**
     * 成交标记(0-为成交，1-已成交)
     */
    private String status;

    /**
     * 创建人
     */
    private Long createdBy;

    /**
     * 修改人
     */
    private Long updatedBy;

    /**
     * 是否删除
     */
    private Boolean deleteFlg = false;

    /**
     * 询价编号
     */
    @Transient
    private String enquiryOddNumber;

    /**
     * 询价产品名称（组合）
     */
    @Transient
    private String productName;
    /**
     * 询价账期
     */
    @Transient
    private int paymentDays;

    @Transient
    private String phone;

    public String getOddNumber() {
        return oddNumber;
    }

    public void setOddNumber(String oddNumber) {
        this.oddNumber = oddNumber;
    }

    public Date getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(Date confirmTime) {
        this.confirmTime = confirmTime;
    }

    public Long getEnquiryId() {
        return enquiryId;
    }

    public void setEnquiryId(Long enquiryId) {
        this.enquiryId = enquiryId;
    }

    public BigDecimal getDealNumber() {
        return dealNumber;
    }

    public void setDealNumber(BigDecimal dealNumber) {
        this.dealNumber = dealNumber;
    }

    public BigDecimal getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(BigDecimal sellPrice) {
        this.sellPrice = sellPrice;
    }

    public Date getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(Date arriveDate) {
        this.arriveDate = arriveDate;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
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

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Boolean getDeleteFlg() {
        return deleteFlg;
    }

    public void setDeleteFlg(Boolean deleteFlg) {
        this.deleteFlg = deleteFlg;
    }

    @Override
    public String toString() {
        return "BuyQuote{" +
                "enquiryId=" + enquiryId +
                ", dealNumber='" + dealNumber + '\'' +
                ", sellPrice='" + sellPrice + '\'' +
                ", arriveDate=" + arriveDate +
                ", expireTime=" + expireTime +
                ", userName='" + userName + '\'' +
                ", userId=" + userId +
                ", deptId=" + deptId +
                ", deptName='" + deptName + '\'' +
                ", status='" + status + '\'' +
                ", id=" + id +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                '}';
    }

    @Transient
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    @Transient
    public String getEnquiryOddNumber() {
        return enquiryOddNumber;
    }

    public void setEnquiryOddNumber(String enquiryOddNumber) {
        this.enquiryOddNumber = enquiryOddNumber;
    }
    @Transient
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
    @Transient
    public int getPaymentDays() {
        return paymentDays;
    }

    public void setPaymentDays(int paymentDays) {
        this.paymentDays = paymentDays;
    }
}
