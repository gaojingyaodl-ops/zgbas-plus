package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;

/**
 * <p>
 *  合同未发货商品详细
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-16 13:42
 */
public class RptCtrUnDeliveryOutDetailVo {

    /**
     * 品名
     */
    private String productName;

    /**
     * 牌号
     */
    private String brandNumber;

    /**
     * 厂商
     */
    private String factoryName;

    /**
     * 质量标准  Y-原厂标准，G-过渡料，F-副牌料
     */
    private String qualityStandard;

    /**
     * 单价
     */
    private BigDecimal dealPrice;

    /**
     * 该品种数量
     */
    private BigDecimal dealNumber;

    /**
     * 该品种的金额
     */
    private BigDecimal dealAmount;

    /**
     * 单位
     */
    private String unit;

    /**
     * 合同商品唯一标识
     */
    private Long ctrProductId;

    /**
     * 可申请发货数量
     */
    private BigDecimal applyNumber;

    /**
     * 采购已入库数量
     */
    @JsonIgnore
    private BigDecimal deliveryInNumber;

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

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getQualityStandard() {
        return qualityStandard;
    }

    public void setQualityStandard(String qualityStandard) {
        this.qualityStandard = qualityStandard;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Long getCtrProductId() {
        return ctrProductId;
    }

    public void setCtrProductId(Long ctrProductId) {
        this.ctrProductId = ctrProductId;
    }

    public BigDecimal getApplyNumber() {
        return applyNumber;
    }

    public void setApplyNumber(BigDecimal applyNumber) {
        this.applyNumber = applyNumber;
    }

    public BigDecimal getDeliveryInNumber() {
        return deliveryInNumber;
    }

    public void setDeliveryInNumber(BigDecimal deliveryInNumber) {
        this.deliveryInNumber = deliveryInNumber;
    }
}
