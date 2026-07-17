package com.spt.bas.report.client.vo;

import java.math.BigDecimal;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/5/11 11:17
 */

public class RptStockVirtualVo {

    private Long id;

    /**
     * 编号
     */
    private String stockVirtualNo;
    /**
     * 类型
     */
    private String contractType;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 品名
     */
    private String productName;

    /**
     * 厂商
     */
    private String factoryName;

    /**
     * 牌号
     */
    private String brandNumber;

    /**
     * 数量
     */
    private BigDecimal dealNumber;

    /**
     * 含税单价
     */
    private BigDecimal dealPrice;

    /**
     * 总价
     */
    private BigDecimal totalAmount;
    /**
     * 总价，带金额格式化
     */
    private String  totalAmountStr;

    /**
     * 交货日期
     */
    private String deliveryDate;

    /**
     * 付全款日期
     */
    private String payFullTime;
    /**
     * 发布时间
     */
    private String publicTime;

    /**
     * 交货方式
     */
    private String deliveryType;

    /**
     * 发布日期
     */
    private String arrivalTimeExt;

    /**
     * 状态
     */
    private String status;

    /**
     * 交货地点
     */
    private String deliveryAddr;

    /**
     * 业务员
     */
    private String matchUserName;

    public String getTotalAmountStr() {
        return totalAmountStr;
    }

    public void setTotalAmountStr(String totalAmountStr) {
        this.totalAmountStr = totalAmountStr;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStockVirtualNo() {
        return stockVirtualNo;
    }

    public void setStockVirtualNo(String stockVirtualNo) {
        this.stockVirtualNo = stockVirtualNo;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
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

    public BigDecimal getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(BigDecimal dealPrice) {
        this.dealPrice = dealPrice;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getPayFullTime() {
        return payFullTime;
    }

    public void setPayFullTime(String payFullTime) {
        this.payFullTime = payFullTime;
    }

    public String getPublicTime() {
        return publicTime;
    }

    public void setPublicTime(String publicTime) {
        this.publicTime = publicTime;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getArrivalTimeExt() {
        return arrivalTimeExt;
    }

    public void setArrivalTimeExt(String arrivalTimeExt) {
        this.arrivalTimeExt = arrivalTimeExt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeliveryAddr() {
        return deliveryAddr;
    }

    public void setDeliveryAddr(String deliveryAddr) {
        this.deliveryAddr = deliveryAddr;
    }

    public String getMatchUserName() {
        return matchUserName;
    }

    public void setMatchUserName(String matchUserName) {
        this.matchUserName = matchUserName;
    }
}
