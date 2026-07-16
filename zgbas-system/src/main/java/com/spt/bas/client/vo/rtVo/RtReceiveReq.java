package com.spt.bas.client.vo.rtVo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 融拓收款信息推送Vo
 *
 * @Author: gaojy
 * @create 2022/4/8 17:25
 * @version: 1.0
 * @description:
 */
public class RtReceiveReq extends RtBaseReq{

    /**
     * 申请单号
     */
    private String applyNo;

    /**
     * 票证期限
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date billDueTime;

    /**
     * 逾期罚息
     */
    private BigDecimal breachAmount;

    /**
     * 付款方抬头
     */
    private String companyName;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 到期时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date dueTime;

    /**
     * 我方公司名称
     */
    private String ourCompanyName;

    /**
     * 已收金额
     */
    private BigDecimal payedAmount;

    /**
     * 收款金额
     */
    private BigDecimal receiveAmount;

    /**
     * 已收逾期罚息
     */
    private BigDecimal receiveBreachAmount;

    /**
     * 收款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date receiveDate;

    /**
     * 收款方式 现金-cash，电汇-telTransfer，承兑-accept，信用证-credit
     */
    private String receiveMode;

    /**
     * 收款类型 B-定金，R-尾款，A-全款，S-仓储费，T-运费
     */
    private String receiveType;

    /**
     * 合同总价
     */
    private BigDecimal totalAmount;

    /**
     * 未收金额
     */
    private BigDecimal unpayedAmount;

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    public Date getBillDueTime() {
        return billDueTime;
    }

    public void setBillDueTime(Date billDueTime) {
        this.billDueTime = billDueTime;
    }

    public BigDecimal getBreachAmount() {
        return breachAmount;
    }

    public void setBreachAmount(BigDecimal breachAmount) {
        this.breachAmount = breachAmount;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public Date getDueTime() {
        return dueTime;
    }

    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public BigDecimal getPayedAmount() {
        return payedAmount;
    }

    public void setPayedAmount(BigDecimal payedAmount) {
        this.payedAmount = payedAmount;
    }

    public BigDecimal getReceiveAmount() {
        return receiveAmount;
    }

    public void setReceiveAmount(BigDecimal receiveAmount) {
        this.receiveAmount = receiveAmount;
    }

    public BigDecimal getReceiveBreachAmount() {
        return receiveBreachAmount;
    }

    public void setReceiveBreachAmount(BigDecimal receiveBreachAmount) {
        this.receiveBreachAmount = receiveBreachAmount;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    public String getReceiveMode() {
        return receiveMode;
    }

    public void setReceiveMode(String receiveMode) {
        this.receiveMode = receiveMode;
    }

    public String getReceiveType() {
        return receiveType;
    }

    public void setReceiveType(String receiveType) {
        this.receiveType = receiveType;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getUnpayedAmount() {
        return unpayedAmount;
    }

    public void setUnpayedAmount(BigDecimal unpayedAmount) {
        this.unpayedAmount = unpayedAmount;
    }
}
