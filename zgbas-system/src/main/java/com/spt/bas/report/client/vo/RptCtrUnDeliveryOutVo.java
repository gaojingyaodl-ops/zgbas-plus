package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *  合同未发货详情
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-16 13:40
 */
public class RptCtrUnDeliveryOutVo {
    /**
     * 合同数量
     */
    private BigDecimal totalNumber;

    /**
     * 合同总价
     */
    private BigDecimal totalAmount;


    /**
     * Z：自提；P：配送
     */
    private String deliveryType;

    /**
     * 仓库地址信息
     */
    private String deliveryAddr;

    /**
     * 发货地
     */
    private String deliveryAddrSell;

    /**
     * 仓库地址Id
     */
    private Long warehouseId;

    /**
     * 合同id
     */
    @JsonIgnore
    private Long ctrContractId;

    /**
     * 质量标准  Y-原厂标准，G-过渡料，F-副牌料
     */
    private String qualityStandard;

    /**
     * 未发货商品信息
     */
    private List<RptCtrUnDeliveryOutDetailVo> products;

    public BigDecimal getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(BigDecimal totalNumber) {
        this.totalNumber = totalNumber;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getDeliveryAddr() {
        return deliveryAddr;
    }

    public void setDeliveryAddr(String deliveryAddr) {
        this.deliveryAddr = deliveryAddr;
    }

    public String getDeliveryAddrSell() {
        return deliveryAddrSell;
    }

    public void setDeliveryAddrSell(String deliveryAddrSell) {
        this.deliveryAddrSell = deliveryAddrSell;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getCtrContractId() {
        return ctrContractId;
    }

    public void setCtrContractId(Long ctrContractId) {
        this.ctrContractId = ctrContractId;
    }

    public List<RptCtrUnDeliveryOutDetailVo> getProducts() {
        return products;
    }

    public void setProducts(List<RptCtrUnDeliveryOutDetailVo> products) {
        this.products = products;
    }

    public String getQualityStandard() {
        return qualityStandard;
    }

    public void setQualityStandard(String qualityStandard) {
        this.qualityStandard = qualityStandard;
    }
}
