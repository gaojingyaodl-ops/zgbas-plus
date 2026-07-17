package com.spt.bas.report.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单预警
 *
 * @Author MoonLight
 * @Date 2023/10/16 15:02
 * @Version 1.0
 */
public class RptCtrContractWarnReport {
    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 合并编号
     */
    private String contractNo;

    /**
     * 审批单ID
     */
    private Long approveId;

    /**
     * 业务类型
     */
    private String businessTypeDcsx;

    /**
     * 货名
     */
    private String productNames;

    /**
     * 对方企业ID
     */
    private String companyId;

    /**
     * 对方企业名称
     */
    private String companyName;

    /**
     * 我方企业名称
     */
    private String ourCompanyName;

    /**
     * 人保授信额度
     */
    private BigDecimal piccCreditAmount;

    /**
     * 合同数量
     */
    private BigDecimal totalNumber;

    /**
     * 合同金额
     */
    private BigDecimal totalAmount;

    /**
     * 已收款金额
     */
    private BigDecimal receiveAmount;

    /**
     * 待收金额
     */
    private BigDecimal receivableAmount;

    /**
     * 业务员ID
     */
    private Long matchUserId;

    /**
     * 业务员
     */
    private String matchUserName;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 所属区域
     */
    private String owningRegion;

    /**
     * 约定付款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date appointPayFullTime;

    /**
     * 确认收货数量
     */
    private BigDecimal confirmReceiptNumber;

    /**
     * 确认收货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date confirmDate;

    /**
     * 最晚开票日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date latestBillDate;

    /**
     * 合同账期
     */
    private Long creditCycle;

    /**
     * 逾期天数
     */
    private Long breachDays;

    /**
     * 逾期金额
     */
    private BigDecimal breachAmount;

    /**
     * 履约状态
     * 进行中-N
     * 宽限期-B
     * 催告期-D
     * 逾期-S
     * 违约-P
     * 已完成-A
     */
    private String performanceStatus;

    /**
     * 跟进内容
     */
    private String notifyContent;

    /**
     * 资金方（取中游代采方）
     */
    private String fundCompanyName;

    /**
     * 开票状态
     */
    private String invoiceBillName;

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getBusinessTypeDcsx() {
        return businessTypeDcsx;
    }

    public void setBusinessTypeDcsx(String businessTypeDcsx) {
        this.businessTypeDcsx = businessTypeDcsx;
    }

    public String getProductNames() {
        return productNames;
    }

    public void setProductNames(String productNames) {
        this.productNames = productNames;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public BigDecimal getPiccCreditAmount() {
        return piccCreditAmount;
    }

    public void setPiccCreditAmount(BigDecimal piccCreditAmount) {
        this.piccCreditAmount = piccCreditAmount;
    }

    public BigDecimal getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(BigDecimal totalNumber) {
        this.totalNumber = totalNumber;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getReceiveAmount() {
        return receiveAmount;
    }

    public void setReceiveAmount(BigDecimal receiveAmount) {
        this.receiveAmount = receiveAmount;
    }

    public BigDecimal getReceivableAmount() {
        return receivableAmount;
    }

    public void setReceivableAmount(BigDecimal receivableAmount) {
        this.receivableAmount = receivableAmount;
    }

    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public String getMatchUserName() {
        return matchUserName;
    }

    public void setMatchUserName(String matchUserName) {
        this.matchUserName = matchUserName;
    }

    public String getOwningRegion() {
        return owningRegion;
    }

    public void setOwningRegion(String owningRegion) {
        this.owningRegion = owningRegion;
    }

    public Date getAppointPayFullTime() {
        return appointPayFullTime;
    }

    public void setAppointPayFullTime(Date appointPayFullTime) {
        this.appointPayFullTime = appointPayFullTime;
    }

    public BigDecimal getConfirmReceiptNumber() {
        return confirmReceiptNumber;
    }

    public void setConfirmReceiptNumber(BigDecimal confirmReceiptNumber) {
        this.confirmReceiptNumber = confirmReceiptNumber;
    }

    public Date getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(Date confirmDate) {
        this.confirmDate = confirmDate;
    }

    public Long getCreditCycle() {
        return creditCycle;
    }

    public void setCreditCycle(Long creditCycle) {
        this.creditCycle = creditCycle;
    }

    public Long getBreachDays() {
        return breachDays;
    }

    public void setBreachDays(Long breachDays) {
        this.breachDays = breachDays;
    }

    public BigDecimal getBreachAmount() {
        return breachAmount;
    }

    public void setBreachAmount(BigDecimal breachAmount) {
        this.breachAmount = breachAmount;
    }

    public String getPerformanceStatus() {
        return performanceStatus;
    }

    public void setPerformanceStatus(String performanceStatus) {
        this.performanceStatus = performanceStatus;
    }

    public String getNotifyContent() {
        return notifyContent;
    }

    public void setNotifyContent(String notifyContent) {
        this.notifyContent = notifyContent;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getApproveId() {
        return approveId;
    }

    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getFundCompanyName() {
        return fundCompanyName;
    }

    public void setFundCompanyName(String fundCompanyName) {
        this.fundCompanyName = fundCompanyName;
    }

    public String getInvoiceBillName() {
        return invoiceBillName;
    }

    public void setInvoiceBillName(String invoiceBillName) {
        this.invoiceBillName = invoiceBillName;
    }

    public Date getLatestBillDate() {
        return latestBillDate;
    }

    public void setLatestBillDate(Date latestBillDate) {
        this.latestBillDate = latestBillDate;
    }
}
