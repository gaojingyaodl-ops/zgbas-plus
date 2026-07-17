package com.spt.bas.report.client.entity;

import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-18 17:03
 */
public class RptApplyDeliveryOutDetail {
    /**
     * 合同商品唯一标识
     */
    private Long ctrProductId;

    /**
     * 申请发货数量
     */
    private BigDecimal deliveryOutNumber;

    private String fileId;

    public Long getCtrProductId() {
        return ctrProductId;
    }

    public void setCtrProductId(Long ctrProductId) {
        this.ctrProductId = ctrProductId;
    }

    public BigDecimal getDeliveryOutNumber() {
        return deliveryOutNumber;
    }

    public void setDeliveryOutNumber(BigDecimal deliveryOutNumber) {
        this.deliveryOutNumber = deliveryOutNumber;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
