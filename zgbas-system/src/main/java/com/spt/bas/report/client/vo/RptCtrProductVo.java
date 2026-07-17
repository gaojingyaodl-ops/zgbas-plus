package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;

/**
 * <p>
 *  合同产品信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-09 09:22
 */
public class RptCtrProductVo {
    /**
     * 品名
     */
    private String productName;

    /**
     * 牌号
     */
    private String brandNumber;

    /**
     * 单位
     */
    private String unit;

    /**
     * 该品种单价
     */
    private BigDecimal dealPrice;

    /**
     * 该品种数量
     */
    private BigDecimal dealNumber;

    /**
     * 该品种的总价
     */
    private BigDecimal dealAmount;

    /**
     * 合同id
     */
    @JsonIgnore
    private Long ctrContractId;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBrandNumber() {
        return brandNumber;
    }

    public void setBrandNumber(String brandNumber) {
        this.brandNumber = brandNumber;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(BigDecimal dealPrice) {
        this.dealPrice = dealPrice;
    }

    public BigDecimal getDealNumber() {
        return dealNumber;
    }

    public void setDealNumber(BigDecimal dealNumber) {
        this.dealNumber = dealNumber;
    }

    public BigDecimal getDealAmount() {
        return dealAmount;
    }

    public void setDealAmount(BigDecimal dealAmount) {
        this.dealAmount = dealAmount;
    }

    public Long getCtrContractId() {
        return ctrContractId;
    }

    public void setCtrContractId(Long ctrContractId) {
        this.ctrContractId = ctrContractId;
    }
}
