package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 合同日期VO
 */
public class RptContractDateVo {

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 上游付款日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date buyPayDate;

    /**
     * 上游收票日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date buyBillDate;

    /**
     * 上游入库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date buyWarehouseDate;

    /**
     * 上游付定金日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date buyPayBondTime;


    /**
     * 下游回款日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date sellReceiveDate;

    /**
     * 下游开票日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date sellBillDate;

    /**
     * 下游出库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date sellWarehouseDate;

    /**
     * 中游开票时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date dcsxReceiveBillDate;

    /**
     * 罚息收款日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date interestAmountReceiveDate;

    /**
     * 中游收款日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date dcsxReceiveDate;

    /**
     * 中游实际付款日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date dcsxPayDate;

    /**
     * 中游收定金日期日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date dcsxReceiveBondTime;

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public Date getBuyPayDate() {
        return buyPayDate;
    }

    public void setBuyPayDate(Date buyPayDate) {
        this.buyPayDate = buyPayDate;
    }

    public Date getBuyBillDate() {
        return buyBillDate;
    }

    public void setBuyBillDate(Date buyBillDate) {
        this.buyBillDate = buyBillDate;
    }

    public Date getBuyWarehouseDate() {
        return buyWarehouseDate;
    }

    public void setBuyWarehouseDate(Date buyWarehouseDate) {
        this.buyWarehouseDate = buyWarehouseDate;
    }

    public Date getBuyPayBondTime() {
        return buyPayBondTime;
    }

    public void setBuyPayBondTime(Date buyPayBondTime) {
        this.buyPayBondTime = buyPayBondTime;
    }

    public Date getSellReceiveDate() {
        return sellReceiveDate;
    }

    public void setSellReceiveDate(Date sellReceiveDate) {
        this.sellReceiveDate = sellReceiveDate;
    }

    public Date getSellBillDate() {
        return sellBillDate;
    }

    public void setSellBillDate(Date sellBillDate) {
        this.sellBillDate = sellBillDate;
    }

    public Date getSellWarehouseDate() {
        return sellWarehouseDate;
    }

    public void setSellWarehouseDate(Date sellWarehouseDate) {
        this.sellWarehouseDate = sellWarehouseDate;
    }

    public Date getDcsxReceiveBillDate() {
        return dcsxReceiveBillDate;
    }

    public void setDcsxReceiveBillDate(Date dcsxReceiveBillDate) {
        this.dcsxReceiveBillDate = dcsxReceiveBillDate;
    }

    public Date getInterestAmountReceiveDate() {
        return interestAmountReceiveDate;
    }

    public void setInterestAmountReceiveDate(Date interestAmountReceiveDate) {
        this.interestAmountReceiveDate = interestAmountReceiveDate;
    }

    public Date getDcsxReceiveDate() {
        return dcsxReceiveDate;
    }

    public void setDcsxReceiveDate(Date dcsxReceiveDate) {
        this.dcsxReceiveDate = dcsxReceiveDate;
    }

    public Date getDcsxPayDate() {
        return dcsxPayDate;
    }

    public void setDcsxPayDate(Date dcsxPayDate) {
        this.dcsxPayDate = dcsxPayDate;
    }

    public Date getDcsxReceiveBondTime() {
        return dcsxReceiveBondTime;
    }

    public void setDcsxReceiveBondTime(Date dcsxReceiveBondTime) {
        this.dcsxReceiveBondTime = dcsxReceiveBondTime;
    }
}
