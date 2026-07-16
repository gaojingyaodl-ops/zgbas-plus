package com.spt.bas.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.bas.client.entity.CtrContractDelivery;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

public class CtrContractDeliveryVo extends CtrContractDelivery {
    private String productName;
    private String brandNumber;
    private BigDecimal dealNumber;
    private BigDecimal dealPrice;
    private BigDecimal dealPriceAmount;
    private Long factoryId; // 厂商ID
    private String factoryName; // 厂商名称
    private String contactAddr; // 详细地址
    private  Long pairId;

    private  String  productStatus;

      private   String deliveryStaus;
    /**
     * 则一对接--则一运单号
     */
    private String waybillCode;
    /**
     * 确认收货状态
     */
    private  String  receiptStatus;
    /**
     * 则一是否推送发车
     */
    private Boolean exitsDepartVehicle = false;
    /**
     * 则一是否推送到车
     */
    private Boolean exitsArriveVehicle = false;

    /**
     * 则一出库日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date zydeliveryDate;

    public Date getZydeliveryDate() {
        return zydeliveryDate;
    }

    public void setZydeliveryDate(Date zydeliveryDate) {
        this.zydeliveryDate = zydeliveryDate;
    }

    public String getReceiptStatus() {
        return receiptStatus;
    }

    public void setReceiptStatus(String receiptStatus) {
        this.receiptStatus = receiptStatus;
    }


    public Boolean getExitsDepartVehicle() {
        return exitsDepartVehicle;
    }

    public void setExitsDepartVehicle(Boolean exitsDepartVehicle) {
        this.exitsDepartVehicle = exitsDepartVehicle;
    }

    public Boolean getExitsArriveVehicle() {
        return exitsArriveVehicle;
    }

    public void setExitsArriveVehicle(Boolean exitsArriveVehicle) {
        this.exitsArriveVehicle = exitsArriveVehicle;
    }

    @Override
    public String getWaybillCode() {
        return waybillCode;
    }

    @Override
    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getContactAddr() {
        return contactAddr;
    }

    public void setContactAddr(String contactAddr) {
        this.contactAddr = contactAddr;
    }

    public String getDeliveryStaus() {
        return deliveryStaus;
    }

    public void setDeliveryStaus(String deliveryStaus) {
        this.deliveryStaus = deliveryStaus;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    public Long getPairId() {
        return pairId;
    }

    public void setPairId(Long pairId) {
        this.pairId = pairId;
    }


    public Long getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

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

    public BigDecimal getDealPriceAmount() {
        return dealPriceAmount;
    }

    public void setDealPriceAmount(BigDecimal dealPriceAmount) {
        this.dealPriceAmount = dealPriceAmount;
    }
}
