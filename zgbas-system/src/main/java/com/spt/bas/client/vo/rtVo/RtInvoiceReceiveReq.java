package com.spt.bas.client.vo.rtVo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 融拓推送收票信息Vo
 *
 * @Author: gaojy
 * @create 2022/4/8 16:55
 * @version: 1.0
 * @description:
 */
public class RtInvoiceReceiveReq extends RtBaseReq{
    /**
     * 申请单号
     */
    private String applyNo;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 发票金额
     */
    private BigDecimal invoiceAmount;

    /**
     * 进项发票号码
     */
    private String inInvoiceNo;

    /**
     * 进项发票日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date inInvoiceDate;

    /**
     * 进项记账凭证号
     */
    private String inBillNo;

    /**
     * 发票抬头
     */
    private String invoiceCompanyName;

    /**
     * 已付金额
     */
    private BigDecimal payedAmount;

    /**
     * 合同总价
     */
    private BigDecimal totalAmount;

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getInInvoiceNo() {
        return inInvoiceNo;
    }

    public void setInInvoiceNo(String inInvoiceNo) {
        this.inInvoiceNo = inInvoiceNo;
    }

    public Date getInInvoiceDate() {
        return inInvoiceDate;
    }

    public void setInInvoiceDate(Date inInvoiceDate) {
        this.inInvoiceDate = inInvoiceDate;
    }

    public String getInBillNo() {
        return inBillNo;
    }

    public void setInBillNo(String inBillNo) {
        this.inBillNo = inBillNo;
    }

    public String getInvoiceCompanyName() {
        return invoiceCompanyName;
    }

    public void setInvoiceCompanyName(String invoiceCompanyName) {
        this.invoiceCompanyName = invoiceCompanyName;
    }

    public BigDecimal getPayedAmount() {
        return payedAmount;
    }

    public void setPayedAmount(BigDecimal payedAmount) {
        this.payedAmount = payedAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
