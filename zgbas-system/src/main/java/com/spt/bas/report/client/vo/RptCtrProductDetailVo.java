package com.spt.bas.report.client.vo;

import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-09 18:32
 */
public class RptCtrProductDetailVo extends RptCtrProductVo {
    /**
     * 厂商
     */
    private String factoryName;

    /**
     * 质量标准  Y-原厂标准，G-过渡料，F-副牌料
     */
    private String qualityStandard;

    /**
     * 已出库/入库 金额 （已出/入库数量*单价）
     */
    private BigDecimal warehouseAmount;

    /**
     * 已确认收货金额 （已确认收货数量*单价）
     */
    private BigDecimal confirmReceiptAmount;

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

    public BigDecimal getWarehouseAmount() {
        return warehouseAmount;
    }

    public void setWarehouseAmount(BigDecimal warehouseAmount) {
        this.warehouseAmount = warehouseAmount;
    }

    public BigDecimal getConfirmReceiptAmount() {
        return confirmReceiptAmount;
    }

    public void setConfirmReceiptAmount(BigDecimal confirmReceiptAmount) {
        this.confirmReceiptAmount = confirmReceiptAmount;
    }
}
