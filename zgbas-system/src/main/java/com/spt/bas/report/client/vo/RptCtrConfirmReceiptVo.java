package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *    确认收货历史信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-16 17:24
 */
public class RptCtrConfirmReceiptVo {
    /**
     * 共计已发货金额
     */
    private BigDecimal confirmReceiptTotalAmount;

    /**
     * 共计已发货数量
     */
    private BigDecimal confirmReceiptTotalNumber;

    /**
     * 合同id
     */
    @JsonIgnore
    private Long ctrContractId;

    /**
     * Z：自提；P：配送
     */
    private String deliveryType;

    /**
     * 发货地
     */
    private String deliveryAddrSell;

    /**
     * 收货地
     */
    private String deliveryAddr;

    /**
     * 仓库id
     */
    private Long warehouseId;

    private List<RptCtrConfirmReceiptDetailVo> confirmReceiptList;

    public BigDecimal getConfirmReceiptTotalAmount() {
        return confirmReceiptTotalAmount;
    }

    public void setConfirmReceiptTotalAmount(BigDecimal confirmReceiptTotalAmount) {
        this.confirmReceiptTotalAmount = confirmReceiptTotalAmount;
    }

    public BigDecimal getConfirmReceiptTotalNumber() {
        return confirmReceiptTotalNumber;
    }

    public void setConfirmReceiptTotalNumber(BigDecimal confirmReceiptTotalNumber) {
        this.confirmReceiptTotalNumber = confirmReceiptTotalNumber;
    }

    public Long getCtrContractId() {
        return ctrContractId;
    }

    public void setCtrContractId(Long ctrContractId) {
        this.ctrContractId = ctrContractId;
    }

    public List<RptCtrConfirmReceiptDetailVo> getConfirmReceiptList() {
        return confirmReceiptList;
    }

    public void setConfirmReceiptList(List<RptCtrConfirmReceiptDetailVo> confirmReceiptList) {
        this.confirmReceiptList = confirmReceiptList;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getDeliveryAddrSell() {
        return deliveryAddrSell;
    }

    public void setDeliveryAddrSell(String deliveryAddrSell) {
        this.deliveryAddrSell = deliveryAddrSell;
    }

    public String getDeliveryAddr() {
        return deliveryAddr;
    }

    public void setDeliveryAddr(String deliveryAddr) {
        this.deliveryAddr = deliveryAddr;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }
}
