package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 *  合同发货详细-批次详细
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-16 11:15
 */
public class RptCtrWarehouseDeliveryVo {
    /**
     * 对应的批次编号ID，根据ID可以查询到批次详情
     */
    private String deliveryId;

    /**
     * 发货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date deliveryDateFrom;

    /**
     * 品名
     */
    private String productName;

    /**
     * 牌号
     */
    private String brandNumber;

    /**
     * 该批次的发货金额
     */
    private BigDecimal warehouseAmount;

    /**
     * 该批次发货数量
     */
    private BigDecimal warehouseNumber;

    /**
     * 客户上传的提货单
     */
    private String uploadFileId;

    /**
     *发货凭证或运单附件
     */
    private String fileId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 单位
     */
    private String unit;

    /**
     * 是否确认收货状态 0：未确认 1：已确认 2：确认中
     */
    private String confirmFlg;
    /**
     * 则一运单号
     */
    private String waybillCode;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public Date getDeliveryDateFrom() {
        return deliveryDateFrom;
    }

    public void setDeliveryDateFrom(Date deliveryDateFrom) {
        this.deliveryDateFrom = deliveryDateFrom;
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

    public BigDecimal getWarehouseAmount() {
        return warehouseAmount;
    }

    public void setWarehouseAmount(BigDecimal warehouseAmount) {
        this.warehouseAmount = warehouseAmount;
    }

    public BigDecimal getWarehouseNumber() {
        return warehouseNumber;
    }

    public void setWarehouseNumber(BigDecimal warehouseNumber) {
        this.warehouseNumber = warehouseNumber;
    }

    public String getUploadFileId() {
        return uploadFileId;
    }

    public void setUploadFileId(String uploadFileId) {
        this.uploadFileId = uploadFileId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getConfirmFlg() {
        return confirmFlg;
    }

    public void setConfirmFlg(String confirmFlg) {
        this.confirmFlg = confirmFlg;
    }
}
