package com.spt.bas.client.vo.rtVo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 融拓推送出库信息Vo
 *
 * @Author: gaojy
 * @create 2022/4/8 17:03
 * @version: 1.0
 * @description:
 */
public class RtDeliveryOutReq extends RtBaseReq{
    /**
     * 申请单号
     */
    private String applyNo;

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
     * 承运商
     */
    private String carrier;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 是否确认收货状态 0：未确认 1：已确认 2：确认中
     */
    private String confirmFlg;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系地址
     */
    private String contactAddr;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 柜数
     */
    private String countersNumber;

    /**
     * 当前出库数量
     */
    private BigDecimal curNumber;

    /**
     * 数量
     */
    private BigDecimal dealNumber;

    /**
     * 单价
     */
    private BigDecimal dealPrice;

    /**
     * 交货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date deliveryDate;

    /**
     * 配送方式
     */
    private String deliveryType;

    /**
     * 交货方式
     */
    private String deliveryMode;

    /**
     * 仓库/配送电话
     */
    private String deliveryPhone;

    /**
     * 仓库/配送地址
     */
    private String deliveryAddr;

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    public String getBrandNumber() {
        return brandNumber;
    }

    public void setBrandNumber(String brandNumber) {
        this.brandNumber = brandNumber;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getConfirmFlg() {
        return confirmFlg;
    }

    public void setConfirmFlg(String confirmFlg) {
        this.confirmFlg = confirmFlg;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactAddr() {
        return contactAddr;
    }

    public void setContactAddr(String contactAddr) {
        this.contactAddr = contactAddr;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getCountersNumber() {
        return countersNumber;
    }

    public void setCountersNumber(String countersNumber) {
        this.countersNumber = countersNumber;
    }

    public BigDecimal getCurNumber() {
        return curNumber;
    }

    public void setCurNumber(BigDecimal curNumber) {
        this.curNumber = curNumber;
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

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(String deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public String getDeliveryPhone() {
        return deliveryPhone;
    }

    public void setDeliveryPhone(String deliveryPhone) {
        this.deliveryPhone = deliveryPhone;
    }

    public String getDeliveryAddr() {
        return deliveryAddr;
    }

    public void setDeliveryAddr(String deliveryAddr) {
        this.deliveryAddr = deliveryAddr;
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
}
