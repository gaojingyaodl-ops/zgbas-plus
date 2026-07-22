package com.spt.bas.purchase.wx.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购管家询价表
 */
@Entity
@Table(name = "t_buy_enquiry")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BuyEnquiry extends IdEntity {
    /**
     * 单号
     */
    private String oddNumber;
    /**
     * 微信用户唯一标识
     */
    private String openId;

    /**
     * 品名
     */
    private String productName;

    /**
     * 品名cd
     */
    private String productCd;

    /**
     * 牌号
     */
    private String brandNumber;

    /**
     * 数量
     */
    private BigDecimal dealNumber;

    /**
     * 生产厂家
     */
    private String factoryName;

    /**
     * factory表id
     */
    private Long factoryId;

    /**
     * 送到日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date arriveDate;
    /**
     * 交货地
     */
    private String deliveryAddr;

    /**
     * 区域代码
     */
    private String areaCode;

    /**
     * 客户id
     */
    private Long companyId;

    /**
     * 客户名称
     */
    private String companyName;

    /**
     * 人保额度
     */
    private BigDecimal piccCreditAmount;

    /**
     * 失效时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+08:00")
    private Date expireTime;

    /**
     * 账期
     */
    private Integer paymentDays;

    /**
     * 状态(0-为成交，1-已成交)
     */
    private String status = "0";

    /**
     * 询价时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+08:00")
    private Date enquiryTime;

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
     * 有效报价数量
     */
    private Integer effectiveQuoteNum = 0;

    /**
     * 是否有效 0 有效，1 无效
     */
    @Transient
    private int isValidFlag = 0;

    @Transient
    public BigDecimal getSurplusNum() {
        return surplusNum;
    }

    @Transient
    public void setSurplusNum(BigDecimal surplusNum) {
        this.surplusNum = surplusNum;
    }

    /**
     * 剩余数量
     */
    @Transient
    private BigDecimal surplusNum;


    @Transient
    public int getDealOffer() {
        return dealOffer;
    }

    @Transient
    public void setDealOffer(int dealOffer) {
        this.dealOffer = dealOffer;
    }

    /**
     * 是否有成交(控制前端编辑按钮) 0 未成交，1 有成交
     */
    @Transient
    private int dealOffer = 0;

    @Transient
    public int getIsValidFlag() {
        return isValidFlag;
    }

    @Transient
    public void setIsValidFlag(int isValidFlag) {
        this.isValidFlag = isValidFlag;
    }

    public String getOddNumber() {
        return oddNumber;
    }

    public void setOddNumber(String oddNumber) {
        this.oddNumber = oddNumber;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCd() {
        return productCd;
    }

    public void setProductCd(String productCd) {
        this.productCd = productCd;
    }

    public String getBrandNumber() {
        return brandNumber;
    }

    public void setBrandNumber(String brandNumber) {
        this.brandNumber = brandNumber;
    }

    public BigDecimal getDealNumber() {
        return dealNumber;
    }

    public void setDealNumber(BigDecimal dealNumber) {
        this.dealNumber = dealNumber;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public Long getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }

    public Date getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(Date arriveDate) {
        this.arriveDate = arriveDate;
    }


    public String getDeliveryAddr() {
        return deliveryAddr;
    }

    public void setDeliveryAddr(String deliveryAddr) {
        this.deliveryAddr = deliveryAddr;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public BigDecimal getPiccCreditAmount() {
        return piccCreditAmount;
    }

    public void setPiccCreditAmount(BigDecimal piccCreditAmount) {
        this.piccCreditAmount = piccCreditAmount;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Integer getPaymentDays() {
        return paymentDays;
    }

    public void setPaymentDays(Integer paymentDays) {
        this.paymentDays = paymentDays;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getEnquiryTime() {
        return enquiryTime;
    }

    public void setEnquiryTime(Date enquiryTime) {
        this.enquiryTime = enquiryTime;
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

    public Integer getEffectiveQuoteNum() {
        return effectiveQuoteNum;
    }

    public void setEffectiveQuoteNum(Integer effectiveQuoteNum) {
        this.effectiveQuoteNum = effectiveQuoteNum;
    }

    @Override
    public String toString() {
        return "BuyEnquiry{" +
                "openId='" + openId + '\'' +
                ", productName='" + productName + '\'' +
                ", productCd='" + productCd + '\'' +
                ", brandNumber='" + brandNumber + '\'' +
                ", dealNumber=" + dealNumber +
                ", factoryName='" + factoryName + '\'' +
                ", factoryId=" + factoryId +
                ", arriveDate=" + arriveDate +
                ", deliveryAddr='" + deliveryAddr + '\'' +
                ", areaCode='" + areaCode + '\'' +
                ", companyId=" + companyId +
                ", companyName='" + companyName + '\'' +
                ", piccCreditAmount=" + piccCreditAmount +
                ", expireTime=" + expireTime +
                ", paymentDays=" + paymentDays +
                ", status='" + status + '\'' +
                ", enquiryTime=" + enquiryTime +
                ", createdBy=" + createdBy +
                ", updatedBy=" + updatedBy +
                ", deleteFlg=" + deleteFlg +
                ", effectiveQuoteNum=" + effectiveQuoteNum +
                ", isValidFlag=" + isValidFlag +
                ", surplusNum=" + surplusNum +
                ", dealOffer=" + dealOffer +
                '}';
    }
}
