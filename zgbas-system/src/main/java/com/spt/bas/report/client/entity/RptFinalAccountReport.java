package com.spt.bas.report.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 *      决算统计
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-02-20 09:40
 */
public class RptFinalAccountReport {
    /**
     * 预算编号
     */
    private String approveNo;

    /**
     * 品种/牌号
     */
    private String productName;

    /**
     * 签订日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date contractTime;

    /**
     * 合同数量
     */
    private BigDecimal totalNumber;

    // =====================================================================


    /**
     * 事业部id
     */
    private Long serviceDepartmentId;

    /**
     * 事业部名称
     */
    private String serviceDepartmentName;

    /**
     * 业务部id
     */
    private Long businessDepartmentId;

    /**
     * 业务部名称
     */
    private String businessDepartmentName;

    /**
     * 业务员
     */
    private String matchUserName;

    /**
     * 业务员id
     */
    private Long matchUserId;


    //=========================================================================


    /**
     * 采购合同编号
     */
    private String buyContractNo;

    /**
     * 采购商
     */
    private String buyCompanyName;

    /**
     * 采购单价
     */
    private BigDecimal bdealPrice;

    /**
     * 采购合同金额
     */
    private BigDecimal btotalAmount;

    /**
     * 采购提成(元)
     */
    private BigDecimal buyCommission;


    //=========================================================================


    /**
     * 销售合同编号
     */
    private String sellContractNo;

    /**
     * 终端工厂
     */
    private String sellCompanyName;

    /**
     * 销售单价
     */
    private BigDecimal sdealPrice;

    /**
     * 销售合同金额
     */
    private BigDecimal stotalAmount;

    /**
     * 销售提成(元)
     */
    private BigDecimal sellCommission;

    //=========================================================================

    /**
     * 回款周期（天）
     */
    private Integer creditDays;

    /**
     * 加价*吨数.差价收入(元)
     */
    private BigDecimal premiumAmount;

    /**
     * 资金服务费
     */
    private BigDecimal serviceAmount;

    /**
     * 增值税(元)
     */
    private BigDecimal valueAddedTax;

    /**
     * 服务附加税(元)
     */
    private BigDecimal serviceSurtax;

    /**
     * 销售附加税
     */
    private BigDecimal saleSurtax;

    /**
     * 印花税(元)
     */
    private BigDecimal stampTax;

    /**
     * 资金成本(元)
     */
    private BigDecimal capitalCost;

    /**
     * 保险成本(元)
     */
    private BigDecimal insuranceCost;


    /**
     * 经营性利润
     */
    private BigDecimal marginAmount;

    /**
     * 毛利
     */
    private BigDecimal companyCommissionAmount;

    /**
     * 毛利率
     */
    private BigDecimal companyCommissionRate;

    /**
     * 营销费用
     */
    private BigDecimal marketingExpenses;


    /**
     * 营销留存(元)
     */
    private BigDecimal marketingRetention;

    /**
     * 公司留存(元)
     */
    private BigDecimal companyCommission;

    /**
     * 净利(元)
     */
    private BigDecimal netProfit;

    /**
     * 合同状态
     */
    private String contractStatus;

    public String getApproveNo() {
        return approveNo;
    }

    public void setApproveNo(String approveNo) {
        this.approveNo = approveNo;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Date getContractTime() {
        return contractTime;
    }

    public void setContractTime(Date contractTime) {
        this.contractTime = contractTime;
    }

    public BigDecimal getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(BigDecimal totalNumber) {
        this.totalNumber = totalNumber;
    }

    public Long getServiceDepartmentId() {
        return serviceDepartmentId;
    }

    public void setServiceDepartmentId(Long serviceDepartmentId) {
        this.serviceDepartmentId = serviceDepartmentId;
    }

    public String getServiceDepartmentName() {
        return serviceDepartmentName;
    }

    public void setServiceDepartmentName(String serviceDepartmentName) {
        this.serviceDepartmentName = serviceDepartmentName;
    }

    public Long getBusinessDepartmentId() {
        return businessDepartmentId;
    }

    public void setBusinessDepartmentId(Long businessDepartmentId) {
        this.businessDepartmentId = businessDepartmentId;
    }

    public String getBusinessDepartmentName() {
        return businessDepartmentName;
    }

    public void setBusinessDepartmentName(String businessDepartmentName) {
        this.businessDepartmentName = businessDepartmentName;
    }

    public String getMatchUserName() {
        return matchUserName;
    }

    public void setMatchUserName(String matchUserName) {
        this.matchUserName = matchUserName;
    }

    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public String getBuyContractNo() {
        return buyContractNo;
    }

    public void setBuyContractNo(String buyContractNo) {
        this.buyContractNo = buyContractNo;
    }

    public String getBuyCompanyName() {
        return buyCompanyName;
    }

    public void setBuyCompanyName(String buyCompanyName) {
        this.buyCompanyName = buyCompanyName;
    }

    public BigDecimal getBdealPrice() {
        return bdealPrice;
    }

    public void setBdealPrice(BigDecimal bdealPrice) {
        this.bdealPrice = bdealPrice;
    }

    public BigDecimal getBtotalAmount() {
        return btotalAmount;
    }

    public void setBtotalAmount(BigDecimal btotalAmount) {
        this.btotalAmount = btotalAmount;
    }

    public BigDecimal getBuyCommission() {
        return buyCommission;
    }

    public void setBuyCommission(BigDecimal buyCommission) {
        this.buyCommission = buyCommission;
    }

    public String getSellContractNo() {
        return sellContractNo;
    }

    public void setSellContractNo(String sellContractNo) {
        this.sellContractNo = sellContractNo;
    }

    public String getSellCompanyName() {
        return sellCompanyName;
    }

    public void setSellCompanyName(String sellCompanyName) {
        this.sellCompanyName = sellCompanyName;
    }

    public BigDecimal getSdealPrice() {
        return sdealPrice;
    }

    public void setSdealPrice(BigDecimal sdealPrice) {
        this.sdealPrice = sdealPrice;
    }

    public BigDecimal getStotalAmount() {
        return stotalAmount;
    }

    public void setStotalAmount(BigDecimal stotalAmount) {
        this.stotalAmount = stotalAmount;
    }

    public BigDecimal getSellCommission() {
        return sellCommission;
    }

    public void setSellCommission(BigDecimal sellCommission) {
        this.sellCommission = sellCommission;
    }

    public Integer getCreditDays() {
        return creditDays;
    }

    public void setCreditDays(Integer creditDays) {
        this.creditDays = creditDays;
    }

    public BigDecimal getPremiumAmount() {
        return premiumAmount;
    }

    public void setPremiumAmount(BigDecimal premiumAmount) {
        this.premiumAmount = premiumAmount;
    }

    public BigDecimal getServiceAmount() {
        return serviceAmount;
    }

    public void setServiceAmount(BigDecimal serviceAmount) {
        this.serviceAmount = serviceAmount;
    }

    public BigDecimal getValueAddedTax() {
        return valueAddedTax;
    }

    public void setValueAddedTax(BigDecimal valueAddedTax) {
        this.valueAddedTax = valueAddedTax;
    }

    public BigDecimal getServiceSurtax() {
        return serviceSurtax;
    }

    public void setServiceSurtax(BigDecimal serviceSurtax) {
        this.serviceSurtax = serviceSurtax;
    }

    public BigDecimal getSaleSurtax() {
        return saleSurtax;
    }

    public void setSaleSurtax(BigDecimal saleSurtax) {
        this.saleSurtax = saleSurtax;
    }

    public BigDecimal getStampTax() {
        return stampTax;
    }

    public void setStampTax(BigDecimal stampTax) {
        this.stampTax = stampTax;
    }

    public BigDecimal getCapitalCost() {
        return capitalCost;
    }

    public void setCapitalCost(BigDecimal capitalCost) {
        this.capitalCost = capitalCost;
    }

    public BigDecimal getInsuranceCost() {
        return insuranceCost;
    }

    public void setInsuranceCost(BigDecimal insuranceCost) {
        this.insuranceCost = insuranceCost;
    }

    public BigDecimal getMarginAmount() {
        return marginAmount;
    }

    public void setMarginAmount(BigDecimal marginAmount) {
        this.marginAmount = marginAmount;
    }

    public BigDecimal getCompanyCommissionAmount() {
        return companyCommissionAmount;
    }

    public void setCompanyCommissionAmount(BigDecimal companyCommissionAmount) {
        this.companyCommissionAmount = companyCommissionAmount;
    }

    public BigDecimal getCompanyCommissionRate() {
        return companyCommissionRate;
    }

    public void setCompanyCommissionRate(BigDecimal companyCommissionRate) {
        this.companyCommissionRate = companyCommissionRate;
    }

    public BigDecimal getMarketingExpenses() {
        return marketingExpenses;
    }

    public void setMarketingExpenses(BigDecimal marketingExpenses) {
        this.marketingExpenses = marketingExpenses;
    }

    public BigDecimal getMarketingRetention() {
        return marketingRetention;
    }

    public void setMarketingRetention(BigDecimal marketingRetention) {
        this.marketingRetention = marketingRetention;
    }

    public BigDecimal getCompanyCommission() {
        return companyCommission;
    }

    public void setCompanyCommission(BigDecimal companyCommission) {
        this.companyCommission = companyCommission;
    }

    public BigDecimal getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(BigDecimal netProfit) {
        this.netProfit = netProfit;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }
}
