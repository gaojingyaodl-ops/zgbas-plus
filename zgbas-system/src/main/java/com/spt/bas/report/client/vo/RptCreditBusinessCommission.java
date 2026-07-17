package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.data.vo.DataEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 赊销业务提成
 * @Author: gaojy
 * @create 2022/2/23 17:35
 * @version: 1.0
 * @description:
 */
public class RptCreditBusinessCommission extends DataEntity {
    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 业务类型名称
     */
    private String businessTypeDesc;

    /**
     * 合同号
     */
    private String contractNo;

    /**
     * 合同客户
     */
    private String companyName;

    /**
     * 销售人员
     */
    private String sellMatchUserName;

    /**
     * 采购人员
     */
    private String buyMatchUserName;

    /**
     * 合同数量
     */
    private BigDecimal totalNumber;

    /**
     * 采购单价
     */
    private BigDecimal  buyPrice;

    /**
     * 销售单件
     */
    private BigDecimal sellPrice;

    /**
     * 销售总额
     */
    private BigDecimal sellTotalAmount;

    /**
     * 采购总额
     */
    private BigDecimal buyTotalAmount;

    /**
     * 付款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+08:00")
    private Date payDate;

    /**
     * 收款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+08:00")
    private Date receiveDate;

    /**
     * 约定付款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+08:00")
    private Date appointPayDate;

    /**
     * 实际收货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+08:00")
    private Date confirmReceiptDate;

    /**
     * 金融服务账期
     */
    private Long creditCycle;

    /**
     * 金融服务费
     */
    private BigDecimal financialServiceAmount;

    /**
     * 运输费
     */
    private BigDecimal transportAmount;

    /**
     * 仓储费
     */
    private BigDecimal warehouseAmount;

    /**
     * 逾期天数
     */
    private Long breachDay;

    /**
     * 逾期罚息
     */
    private BigDecimal breachAmount;

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
     * 出库费
     *
     */
    private BigDecimal deliveryFee;

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBusinessTypeDesc() {
        return businessTypeDesc;
    }

    public void setBusinessTypeDesc(String businessTypeDesc) {
        this.businessTypeDesc = businessTypeDesc;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSellMatchUserName() {
        return sellMatchUserName;
    }

    public void setSellMatchUserName(String sellMatchUserName) {
        this.sellMatchUserName = sellMatchUserName;
    }

    public String getBuyMatchUserName() {
        return buyMatchUserName;
    }

    public void setBuyMatchUserName(String buyMatchUserName) {
        this.buyMatchUserName = buyMatchUserName;
    }

    public BigDecimal getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(BigDecimal totalNumber) {
        this.totalNumber = totalNumber;
    }

    public BigDecimal getSellTotalAmount() {
        return sellTotalAmount;
    }

    public void setSellTotalAmount(BigDecimal sellTotalAmount) {
        this.sellTotalAmount = sellTotalAmount;
    }

    public BigDecimal getBuyTotalAmount() {
        return buyTotalAmount;
    }

    public void setBuyTotalAmount(BigDecimal buyTotalAmount) {
        this.buyTotalAmount = buyTotalAmount;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
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

    public BigDecimal getSurchargeAmount() {
        return surchargeAmount;
    }

    public void setSurchargeAmount(BigDecimal surchargeAmount) {
        this.surchargeAmount = surchargeAmount;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }
}
