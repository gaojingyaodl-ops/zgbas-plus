package com.spt.bas.report.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 *      决算统计-
 * </p>
 */
public class RptFinalAccountReportNew {

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 赊销标识
     */
    private Boolean matchCreditFlg;

    /**
     * 销售合同ID
     */
    private Long sellContractId;

    /**
     * 销售合同号
     */
    private String sellContractNo;

    /**
     * 采购合同ID
     */
    private Long buyContractId;

    /**
     * 采购合同号
     */
    private String buyContractNo;

    /**
     * 采购企业ID
     */
    private Long buyCompanyId;

    /**
     * 采购企业名称
     */
    private String buyCompanyName;

    /**
     * 销售企业ID
     */
    private Long sellCompanyId;

    /**
     * 销售企业名称
     */
    private String sellCompanyName;

    /**
     * 采购我方抬头
     */
    private String buyOurCompanyName;

    /**
     * 销售我方抬头
     */
    private String sellOurCompanyName;

    /**
     * 品名
     */
    private String productsName;

    /**
     * 采购业务员ID
     */
    private Long buyMatchUserId;

    /**
     * 采购业务员
     */
    private String buyMatchUserName;

    /**
     * 销售业务员ID
     */
    private Long sellMatchUserId;

    /**
     * 销售业务员
     */
    private String sellMatchUserName;

    /**
     * 结算单状态	I-进行中， B-违约，D-已完成
     */
    private String status;
    private String settlementStatus;

    /**
     * 数量
     */
    private BigDecimal dealNumber;

    /**
     * 销售单价
     */
    private BigDecimal sellPrice;

    /**
     * 采购单价
     */
    private BigDecimal buyPrice;

    /**
     * 采购合同总额
     */
    private BigDecimal buyTotalAmount;

    /**
     * 销售合同总额
     */
    private BigDecimal sellTotalAmount;

    /**
     * 交货日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date deliveryTime;

    /**
     * 预计结算日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payFullTime;

    /**
     * 收款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date receiveDate;

    /**
     * 约定付款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date appointPayDate;

    /**
     * 实际收货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date confirmReceiptDate;

    /**
     * 收票日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date receiveBillDate;

    /**
     * 汇总日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date summaryDate;

    /**
     * 结算日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date settlementDate;

    /**
     * 合同日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date contractTime;

    /**
     * 运费合计
     */
    private BigDecimal transportAmount;

    /**
     * 仓储费合计
     */
    private BigDecimal warehouseAmount;

    /**
     * 出库费用
     */
    private BigDecimal deliveryFee;

    /**
     * 逾期天数
     */
    private Long breachDay;

    /**
     * 逾期罚息
     */
    private BigDecimal breachAmount;

    /**
     * 合同账期
     */
    private Long creditCycle;

    /**
     * 金融服务费
     */
    private BigDecimal financialServiceAmount;

    /**
     * 保险费率
     */
    private BigDecimal insuranceRate;

    /**
     * 增值税税后差价
     */
    private BigDecimal vatSpreadAmount;

    /**
     * 增值税
     */
    private BigDecimal vatAmount;

    /**
     * 印花税
     */
    private BigDecimal printAmount;

    /**
     * 附加税
     */
    private BigDecimal surchargeAmount;

    /**
     * 税金及附加
     */
    private BigDecimal taxesSurchargesAmount;

    /**
     * 税后差价收入
     */
    private BigDecimal afterTaxSpreadAmount;

    /**
     * 销售团队负责人分成
     */
    private BigDecimal sellHeadCommissionAmount;

    /**
     * 采购团队负责人分成
     */
    private BigDecimal buyHeadCommissionAmount;

    /**
     * 销售人员分成
     */
    private BigDecimal sellMatchAmount;

    /**
     * 采购人员分成
     */
    private BigDecimal buyMatchAmount;

    /**
     * 企业账号ID
     */
    private Long enterpriseId;

    /**
     * 唯一标识
     */
    private String settlementCode;

    /**
     * 全部收款标识
     */
    private Boolean receiveFlg = false;

    /**
     * 全部收货确认标识
     */
    private Boolean confirmFlg = false;

    /**
     * 全部收票标识
     */
    private Boolean billFlg = false;

    /**
     * 新：结算状态
     */
    private String settleStatus;

    /**
     * 汇总标识
     */
    private Boolean settleTotalFlg = false;

    /**
     * 是否有效
     */
    private Boolean enableFlg = false;

    /**
     * 采购业务员上级主管
     */
    private Long buyHeadUserId;

    /**
     * 销售业务员上级主管
     */
    private Long sellHeadUserId;

    /**
     * 代采赊销业务类型
     */
    private String businessTypeDcsx;

    private Long approveId;
    
    private String approveNo;
    
    private Long deptId;

    private String deptName;

    /**
     * 合同状态
     * 采购：N-新增，A-审批中，S-已签约(已盖章)，F1-已付款，G1-已收货，V1-已收票，D-完成，B-已审批，C-作废 W-等待
     * 销售：N-新增，A-审批中，S-已签约(已盖章)，F2-已收款，G2-已发货，V2-已开票，D-完成，B-已审批，C-作废 W-等待
     */
    private String contractStatus;

    


    public Date getContractTime() {
        return contractTime;
    }

    public void setContractTime(Date contractTime) {
        this.contractTime = contractTime;
    }

    public Boolean getEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(Boolean enableFlg) {
        this.enableFlg = enableFlg;
    }

    public Boolean getSettleTotalFlg() {
        return settleTotalFlg;
    }

    public void setSettleTotalFlg(Boolean settleTotalFlg) {
        this.settleTotalFlg = settleTotalFlg;
    }

    public String getSettleStatus() {
        return settleStatus;
    }

    public void setSettleStatus(String settleStatus) {
        this.settleStatus = settleStatus;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Boolean getMatchCreditFlg() {
        return matchCreditFlg;
    }

    public void setMatchCreditFlg(Boolean matchCreditFlg) {
        this.matchCreditFlg = matchCreditFlg;
    }

    public String getBusinessTypeDcsx() {
        return businessTypeDcsx;
    }

    public void setBusinessTypeDcsx(String businessTypeDcsx) {
        this.businessTypeDcsx = businessTypeDcsx;
    }

    public Long getSellContractId() {
        return sellContractId;
    }

    public void setSellContractId(Long sellContractId) {
        this.sellContractId = sellContractId;
    }

    public String getSellContractNo() {
        return sellContractNo;
    }

    public void setSellContractNo(String sellContractNo) {
        this.sellContractNo = sellContractNo;
    }

    public Long getBuyContractId() {
        return buyContractId;
    }

    public void setBuyContractId(Long buyContractId) {
        this.buyContractId = buyContractId;
    }

    public String getBuyContractNo() {
        return buyContractNo;
    }

    public void setBuyContractNo(String buyContractNo) {
        this.buyContractNo = buyContractNo;
    }

    public Long getBuyCompanyId() {
        return buyCompanyId;
    }

    public void setBuyCompanyId(Long buyCompanyId) {
        this.buyCompanyId = buyCompanyId;
    }

    public String getBuyCompanyName() {
        return buyCompanyName;
    }

    public void setBuyCompanyName(String buyCompanyName) {
        this.buyCompanyName = buyCompanyName;
    }

    public Long getSellCompanyId() {
        return sellCompanyId;
    }

    public void setSellCompanyId(Long sellCompanyId) {
        this.sellCompanyId = sellCompanyId;
    }

    public String getSellCompanyName() {
        return sellCompanyName;
    }

    public void setSellCompanyName(String sellCompanyName) {
        this.sellCompanyName = sellCompanyName;
    }

    public String getBuyOurCompanyName() {
        return buyOurCompanyName;
    }

    public void setBuyOurCompanyName(String buyOurCompanyName) {
        this.buyOurCompanyName = buyOurCompanyName;
    }

    public String getSellOurCompanyName() {
        return sellOurCompanyName;
    }

    public void setSellOurCompanyName(String sellOurCompanyName) {
        this.sellOurCompanyName = sellOurCompanyName;
    }

    public String getProductsName() {
        return productsName;
    }

    public void setProductsName(String productsName) {
        this.productsName = productsName;
    }

    public Long getBuyMatchUserId() {
        return buyMatchUserId;
    }

    public void setBuyMatchUserId(Long buyMatchUserId) {
        this.buyMatchUserId = buyMatchUserId;
    }

    public String getBuyMatchUserName() {
        return buyMatchUserName;
    }

    public void setBuyMatchUserName(String buyMatchUserName) {
        this.buyMatchUserName = buyMatchUserName;
    }

    public Long getSellMatchUserId() {
        return sellMatchUserId;
    }

    public void setSellMatchUserId(Long sellMatchUserId) {
        this.sellMatchUserId = sellMatchUserId;
    }

    public String getSellMatchUserName() {
        return sellMatchUserName;
    }

    public void setSellMatchUserName(String sellMatchUserName) {
        this.sellMatchUserName = sellMatchUserName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getDealNumber() {
        return dealNumber;
    }

    public void setDealNumber(BigDecimal dealNumber) {
        this.dealNumber = dealNumber;
    }

    public BigDecimal getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(BigDecimal sellPrice) {
        this.sellPrice = sellPrice;
    }

    public BigDecimal getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(BigDecimal buyPrice) {
        this.buyPrice = buyPrice;
    }

    public BigDecimal getBuyTotalAmount() {
        return buyTotalAmount;
    }

    public void setBuyTotalAmount(BigDecimal buyTotalAmount) {
        this.buyTotalAmount = buyTotalAmount;
    }

    public BigDecimal getSellTotalAmount() {
        return sellTotalAmount;
    }

    public void setSellTotalAmount(BigDecimal sellTotalAmount) {
        this.sellTotalAmount = sellTotalAmount;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Date getPayFullTime() {
        return payFullTime;
    }

    public void setPayFullTime(Date payFullTime) {
        this.payFullTime = payFullTime;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    public Date getAppointPayDate() {
        return appointPayDate;
    }

    public void setAppointPayDate(Date appointPayDate) {
        this.appointPayDate = appointPayDate;
    }

    public Date getConfirmReceiptDate() {
        return confirmReceiptDate;
    }

    public void setConfirmReceiptDate(Date confirmReceiptDate) {
        this.confirmReceiptDate = confirmReceiptDate;
    }

    public Date getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(Date settlementDate) {
        this.settlementDate = settlementDate;
    }

    public BigDecimal getTransportAmount() {
        return transportAmount;
    }

    public void setTransportAmount(BigDecimal transportAmount) {
        this.transportAmount = transportAmount;
    }

    public BigDecimal getWarehouseAmount() {
        return warehouseAmount;
    }

    public void setWarehouseAmount(BigDecimal warehouseAmount) {
        this.warehouseAmount = warehouseAmount;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public Long getBreachDay() {
        return breachDay;
    }

    public void setBreachDay(Long breachDay) {
        this.breachDay = breachDay;
    }

    public BigDecimal getBreachAmount() {
        return breachAmount;
    }

    public void setBreachAmount(BigDecimal breachAmount) {
        this.breachAmount = breachAmount;
    }

    public Long getCreditCycle() {
        return creditCycle;
    }

    public void setCreditCycle(Long creditCycle) {
        this.creditCycle = creditCycle;
    }

    public BigDecimal getFinancialServiceAmount() {
        return financialServiceAmount;
    }

    public void setFinancialServiceAmount(BigDecimal financialServiceAmount) {
        this.financialServiceAmount = financialServiceAmount;
    }

    public BigDecimal getInsuranceRate() {
        return insuranceRate;
    }

    public void setInsuranceRate(BigDecimal insuranceRate) {
        this.insuranceRate = insuranceRate;
    }

    public BigDecimal getVatSpreadAmount() {
        return vatSpreadAmount;
    }

    public void setVatSpreadAmount(BigDecimal vatSpreadAmount) {
        this.vatSpreadAmount = vatSpreadAmount;
    }

    public BigDecimal getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(BigDecimal vatAmount) {
        this.vatAmount = vatAmount;
    }

    public BigDecimal getPrintAmount() {
        return printAmount;
    }

    public void setPrintAmount(BigDecimal printAmount) {
        this.printAmount = printAmount;
    }

    public BigDecimal getSurchargeAmount() {
        return surchargeAmount;
    }

    public void setSurchargeAmount(BigDecimal surchargeAmount) {
        this.surchargeAmount = surchargeAmount;
    }

    public BigDecimal getTaxesSurchargesAmount() {
        return taxesSurchargesAmount;
    }

    public void setTaxesSurchargesAmount(BigDecimal taxesSurchargesAmount) {
        this.taxesSurchargesAmount = taxesSurchargesAmount;
    }

    public BigDecimal getAfterTaxSpreadAmount() {
        return afterTaxSpreadAmount;
    }

    public void setAfterTaxSpreadAmount(BigDecimal afterTaxSpreadAmount) {
        this.afterTaxSpreadAmount = afterTaxSpreadAmount;
    }

    public BigDecimal getSellHeadCommissionAmount() {
        return sellHeadCommissionAmount;
    }

    public void setSellHeadCommissionAmount(BigDecimal sellHeadCommissionAmount) {
        this.sellHeadCommissionAmount = sellHeadCommissionAmount;
    }

    public BigDecimal getBuyHeadCommissionAmount() {
        return buyHeadCommissionAmount;
    }

    public void setBuyHeadCommissionAmount(BigDecimal buyHeadCommissionAmount) {
        this.buyHeadCommissionAmount = buyHeadCommissionAmount;
    }

    public BigDecimal getSellMatchAmount() {
        return sellMatchAmount;
    }

    public void setSellMatchAmount(BigDecimal sellMatchAmount) {
        this.sellMatchAmount = sellMatchAmount;
    }

    public BigDecimal getBuyMatchAmount() {
        return buyMatchAmount;
    }

    public void setBuyMatchAmount(BigDecimal buyMatchAmount) {
        this.buyMatchAmount = buyMatchAmount;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getSettlementCode() {
        return settlementCode;
    }

    public void setSettlementCode(String settlementCode) {
        this.settlementCode = settlementCode;
    }

    public Boolean getReceiveFlg() {
        return receiveFlg;
    }

    public void setReceiveFlg(Boolean receiveFlg) {
        this.receiveFlg = receiveFlg;
    }

    public Boolean getConfirmFlg() {
        return confirmFlg;
    }

    public void setConfirmFlg(Boolean confirmFlg) {
        this.confirmFlg = confirmFlg;
    }

    public Boolean getBillFlg() {
        return billFlg;
    }

    public void setBillFlg(Boolean billFlg) {
        this.billFlg = billFlg;
    }

    public Long getBuyHeadUserId() {
        return buyHeadUserId;
    }

    public void setBuyHeadUserId(Long buyHeadUserId) {
        this.buyHeadUserId = buyHeadUserId;
    }

    public Long getSellHeadUserId() {
        return sellHeadUserId;
    }

    public void setSellHeadUserId(Long sellHeadUserId) {
        this.sellHeadUserId = sellHeadUserId;
    }

    public Date getReceiveBillDate() {
        return receiveBillDate;
    }

    public void setReceiveBillDate(Date receiveBillDate) {
        this.receiveBillDate = receiveBillDate;
    }

    public Date getSummaryDate() {
        return summaryDate;
    }

    public void setSummaryDate(Date summaryDate) {
        this.summaryDate = summaryDate;
    }

    public Long getApproveId() {
        return approveId;
    }

    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public String getApproveNo() {
        return approveNo;
    }

    public void setApproveNo(String approveNo) {
        this.approveNo = approveNo;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public String getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(String settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}
