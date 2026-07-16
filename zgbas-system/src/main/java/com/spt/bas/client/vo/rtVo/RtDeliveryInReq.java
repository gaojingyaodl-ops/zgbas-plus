package com.spt.bas.client.vo.rtVo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 融拓推送入库信息Vo
 *
 * @Author: gaojy
 * @create 2022/4/8 16:38
 * @version: 1.0
 * @description:
 */
public class RtDeliveryInReq extends RtBaseReq{
    /**
     * 审批编号
     */
    private String applyNo;

    /**
     * 上家提单号
     */
    private String billNoPre;

    /**
     * 品名
     */
    private String productName;

    /**
     * 牌号
     */
    private String brandNumber;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 承运商
     */
    private String carrier;

    /**
     * 供货商名称
     */
    private String companyName;

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
     * 当前出库数量
     */
    private BigDecimal curNumber;

    /**
     * 数量
     */
    private BigDecimal dealNumber;

    /**
     * 价格
     */
    private BigDecimal dealPrice;

    /**
     * 提货电话
     */
    private String deliveryPhone;

    /**
     * 提货地址
     */
    private String deliveryAddr;

    /**
     * 配送方式
     * 自提ZT、配送PS
     */
    private String deliveryType;

    /**
     * 厂商
     */
    private String factoryName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 库存类型
     */
    private String stockType;

    /**
     * 货权类型
     */
    private String spotType;

    /**
     * 仓位/货位
     */
    private String warehousePosition;

    /**
     * 批号
     */
    private String warehouseBatchNo;

    /**
     * 入库日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date warehouseInDate;

    /**
     * 入库方式
     */
    private String warehouseInType;

    /**
     * 库存性质
     */
    private String warehouseKind;

    /**
     * 仓库单号
     */
    private String warehouseNo;

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    public String getBillNoPre() {
        return billNoPre;
    }

    public void setBillNoPre(String billNoPre) {
        this.billNoPre = billNoPre;
    }

    public String getBrandNumber() {
        return brandNumber;
    }

    public void setBrandNumber(String brandNumber) {
        this.brandNumber = brandNumber;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
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

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStockType() {
        return stockType;
    }

    public void setStockType(String stockType) {
        this.stockType = stockType;
    }

    public String getSpotType() {
        return spotType;
    }

    public void setSpotType(String spotType) {
        this.spotType = spotType;
    }

    public String getWarehousePosition() {
        return warehousePosition;
    }

    public void setWarehousePosition(String warehousePosition) {
        this.warehousePosition = warehousePosition;
    }

    public String getWarehouseBatchNo() {
        return warehouseBatchNo;
    }

    public void setWarehouseBatchNo(String warehouseBatchNo) {
        this.warehouseBatchNo = warehouseBatchNo;
    }

    public Date getWarehouseInDate() {
        return warehouseInDate;
    }

    public void setWarehouseInDate(Date warehouseInDate) {
        this.warehouseInDate = warehouseInDate;
    }

    public String getWarehouseInType() {
        return warehouseInType;
    }

    public void setWarehouseInType(String warehouseInType) {
        this.warehouseInType = warehouseInType;
    }

    public String getWarehouseKind() {
        return warehouseKind;
    }

    public void setWarehouseKind(String warehouseKind) {
        this.warehouseKind = warehouseKind;
    }

    public String getWarehouseNo() {
        return warehouseNo;
    }

    public void setWarehouseNo(String warehouseNo) {
        this.warehouseNo = warehouseNo;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
