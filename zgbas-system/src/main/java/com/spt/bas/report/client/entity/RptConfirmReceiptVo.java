package com.spt.bas.report.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  合同操作 - 确认收货 传参
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-18 09:48
 */
public class RptConfirmReceiptVo {
    @NotBlank(message = "合同编号不能为空")
    private String contractNo;

    private String deliveryId;

    /**
     * 实际到货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date confirmReceiptDate;

    private List<RptConfirmReceiptDetail> confirmReceiptList;

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public List<RptConfirmReceiptDetail> getConfirmReceiptList() {
        return confirmReceiptList;
    }

    public void setConfirmReceiptList(List<RptConfirmReceiptDetail> confirmReceiptList) {
        this.confirmReceiptList = confirmReceiptList;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public Date getConfirmReceiptDate() {
        return confirmReceiptDate;
    }

    public void setConfirmReceiptDate(Date confirmReceiptDate) {
        this.confirmReceiptDate = confirmReceiptDate;
    }
}
