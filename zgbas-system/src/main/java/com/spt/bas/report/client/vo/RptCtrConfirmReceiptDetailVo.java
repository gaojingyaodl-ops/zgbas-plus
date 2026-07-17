package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 *  确认收货历史信息-详细
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-16 17:24
 */
public class RptCtrConfirmReceiptDetailVo {
    /**
     * 对应的批次编号id，根据此id可以查询到批次详细
     */
    private String confirmReceiptId;

    /**
     * 收货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date deliveryDateTo;

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
     * 该批次的发货金额
     */
    private BigDecimal confirmReceiptAmount;

    /**
     * 该批次的发货数量
     */
    private BigDecimal confirmReceiptNumber;


    /**
     * 客户上传的收货确认单
     */
    private String uploadFileId;

    /**
     * 备注
     */
    private String remark;

    public String getConfirmReceiptId() {
        return confirmReceiptId;
    }

    public void setConfirmReceiptId(String confirmReceiptId) {
        this.confirmReceiptId = confirmReceiptId;
    }

    public Date getDeliveryDateTo() {
        return deliveryDateTo;
    }

    public void setDeliveryDateTo(Date deliveryDateTo) {
        this.deliveryDateTo = deliveryDateTo;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getConfirmReceiptAmount() {
        return confirmReceiptAmount;
    }

    public void setConfirmReceiptAmount(BigDecimal confirmReceiptAmount) {
        this.confirmReceiptAmount = confirmReceiptAmount;
    }

    public BigDecimal getConfirmReceiptNumber() {
        return confirmReceiptNumber;
    }

    public void setConfirmReceiptNumber(BigDecimal confirmReceiptNumber) {
        this.confirmReceiptNumber = confirmReceiptNumber;
    }

    public String getUploadFileId() {
        return uploadFileId;
    }

    public void setUploadFileId(String uploadFileId) {
        this.uploadFileId = uploadFileId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
