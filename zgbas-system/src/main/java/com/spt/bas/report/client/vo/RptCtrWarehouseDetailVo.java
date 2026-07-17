package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 合同发货详细
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-16 11:09
 */
public class RptCtrWarehouseDetailVo {
    /**
     * 共计已发货金额
     */
    private BigDecimal warehouseTotalAmount;

    /**
     * 共计已发货数量
     */
    private BigDecimal warehouseTotalNumber;

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
     * 发货批次信息
     */
    private List<RptCtrWarehouseDeliveryVo> deliveryList;

    /**
     * 交货日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date deliveryDate;

    public BigDecimal getWarehouseTotalAmount() {
        return warehouseTotalAmount;
    }

    public void setWarehouseTotalAmount(BigDecimal warehouseTotalAmount) {
        this.warehouseTotalAmount = warehouseTotalAmount;
    }

    public BigDecimal getWarehouseTotalNumber() {
        return warehouseTotalNumber;
    }

    public void setWarehouseTotalNumber(BigDecimal warehouseTotalNumber) {
        this.warehouseTotalNumber = warehouseTotalNumber;
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

    public List<RptCtrWarehouseDeliveryVo> getDeliveryList() {
        return deliveryList;
    }

    public void setDeliveryList(List<RptCtrWarehouseDeliveryVo> deliveryList) {
        this.deliveryList = deliveryList;
    }

    public Long getCtrContractId() {
        return ctrContractId;
    }

    public void setCtrContractId(Long ctrContractId) {
        this.ctrContractId = ctrContractId;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
