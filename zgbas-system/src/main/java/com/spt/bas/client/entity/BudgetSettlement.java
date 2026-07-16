package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 *    预算结算表
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-25 11:47
 */
@Entity
@Table(name = "t_budget_settlement")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BudgetSettlement extends IdEntity {
    private static final long serialVersionUID = -3247807057892452904L;
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
     * 销售商品ID
     */
    private Long sellProductId;
    /**
     * 品名
     */
    private String productName;
    /**
     * 牌号
     */
    private String brandNumber;
    /**
     * 厂商
     */
    private String factoryName;
    /**
     * 合同签订日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date contractTime;
    /**
     * 预计结算日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payFullTime;

    /**
     * 采购合同约定付款日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date buyPayFullTime;

    /**
     * 实际结算日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date realPayFullTime;
    /**
     * 交货日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date deliveryTime;
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
     * 加价
     */
    private BigDecimal premium;
    /**
     * 运费
     */
    private BigDecimal transportPrice;

    /**
     * 采购端运费
     */
    private BigDecimal buyTransportAmount;

    /**
     * 仓储费
     */
    private BigDecimal warehousePrice;

    /**
     * 采购端仓储费
     */
    private BigDecimal buyWarehouseAmount;

    /**
     * 逾期罚息
     */
    private BigDecimal breachAmount;
    /**
     * 利润
     */
    private BigDecimal marginAmount;
    /**
     * 印花税
     */
    private BigDecimal printAmount;
    /**
     * 增值税
     */
    private BigDecimal vatAmount;
    /**
     * 采购提成
     */
    private BigDecimal buyCommissionAmount;
    /**
     * 销售提成
     */
    private BigDecimal sellCommissionAmount;
    /**
     * 管理提成
     */
    private BigDecimal manageCommissionAmount;
    /**
     * 服务费
     */
    private BigDecimal serveAmount;
    /**
     * 公司提成
     */
    private BigDecimal companyCommissionAmount;
    /**
     * 服务费率
     */
    private BigDecimal serveRate;
    /**
     * 违约费率
     */
    private BigDecimal breachRate;
    /**
     * 保费费率
     */
    private BigDecimal insuranceRate;
    /**
     * 业务提成比率
     */
    private BigDecimal businessCommissionRate;
    /**
     * 采购提成比率
     */
    private BigDecimal buyCommissionRate;

    /**
     * 销售提成比率
     */
    private BigDecimal sellCommissionRate;
    /**
     * 管理提成比率
     */
    private BigDecimal manageCommissionRate;

    /**
     * 公司比率
     */
    private BigDecimal companyCommissionRate;

    /**
     * 企业账号ID
     */
    private Long enterpriseId;

    /**
     * 赊销标识
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean creditFlg;

    /**
     * 结算方式 赊销 0：一票制 1：两票制
     */
    private String settlementType;

    /**
     * 唯一标识
     */
    private String settlementCode;

    /**
     * 预算决算状态 0：进行中  1：逾期中 2：逾期（决算结束）3：违约中 4：结束
     */
    private String budgetStatus;

    /**
     * 预算完全结束状态 考虑所有的flg为true
     * 0：未结束 1：结束
     */
    private String budgetFinishStatus;
    /**
     * 逾期天数
     */
    private Long overdueDays;

    /**
     * 利润
     */
    private BigDecimal profit;

    /**
     * 毛利
     */
    private BigDecimal grossProfit;

    /**
     * 利润率
     */
    private BigDecimal grossProfitRate;

    /**
     * 营销留存
     */
    private BigDecimal marketingRetention;

    /**
     * 营销留存比率
     */
    private BigDecimal marketingRetentionRate;

    /**
     * 赊销天数
     */
    private Long creditCycle;

    /**
     * 附加税
     */
    private BigDecimal surtax;

    /**
     * 净利
     */
    private BigDecimal netProfit;

    /**
     * 资金成本
     */
    private BigDecimal capitalCost;

    /**
     * 保险成本
     */
    private BigDecimal insuranceCost;

    /**
     * 预算类型
     */
    private String processCode;

    /**
     * 托盘日期
     */
    private Integer palletDay;

    /**
     * 托盘费用
     */
    private BigDecimal palletAmount;

    /**
     * 采购负责人提成
     */
    private BigDecimal buyDirectorCommissionAmount;
    /**
     * 销售负责人提成
     */
    private BigDecimal sellDirectorCommissionAmount;

    /**
     * 采购负责人id
     */
    private Long buyDirectorUserId;

    /**
     * 销售负责人id
     */
    private Long sellDirectorUserId;

    /**
     * 利润率
     */
    private BigDecimal marginRate;

    /**
     * 下游退款
     */
    private BigDecimal sellRefund = BigDecimal.ZERO;

    /**
     * 上游退款
     */
    private BigDecimal buyRefund = BigDecimal.ZERO;

    /**
     * 物流补偿款
     */
    private BigDecimal logisticsRefund = BigDecimal.ZERO;


    /**
     *合同模式
     */
    private String  contractModel;

    public String getContractModel() {
        return contractModel;
    }

    public void setContractModel(String contractModel) {
        this.contractModel = contractModel;
    }

    public Long getSellContractId() {
        return sellContractId;
    }
    public void setSellContractId(Long sellContractId) {
        this.sellContractId = sellContractId;
    }
    public Long getBuyContractId() {
        return buyContractId;
    }
    public void setBuyContractId(Long buyContractId) {
        this.buyContractId = buyContractId;
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
    public BigDecimal getPremium() {
        return premium;
    }
    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }
    public BigDecimal getTransportPrice() {
        return transportPrice;
    }
    public void setTransportPrice(BigDecimal transportPrice) {
        this.transportPrice = transportPrice;
    }
    public BigDecimal getWarehousePrice() {
        return warehousePrice;
    }
    public void setWarehousePrice(BigDecimal warehousePrice) {
        this.warehousePrice = warehousePrice;
    }
    public BigDecimal getInsuranceRate() {
        return insuranceRate;
    }
    public void setInsuranceRate(BigDecimal insuranceRate) {
        this.insuranceRate = insuranceRate;
    }
    public BigDecimal getBreachAmount() {
        return breachAmount;
    }
    public void setBreachAmount(BigDecimal breachAmount) {
        this.breachAmount = breachAmount;
    }
    public BigDecimal getMarginAmount() {
        return marginAmount;
    }
    public void setMarginAmount(BigDecimal marginAmount) {
        this.marginAmount = marginAmount;
    }
    public BigDecimal getPrintAmount() {
        return printAmount;
    }
    public void setPrintAmount(BigDecimal printAmount) {
        this.printAmount = printAmount;
    }
    public BigDecimal getVatAmount() {
        return vatAmount;
    }
    public void setVatAmount(BigDecimal vatAmount) {
        this.vatAmount = vatAmount;
    }
    public BigDecimal getBuyCommissionAmount() {
        return buyCommissionAmount;
    }
    public void setBuyCommissionAmount(BigDecimal buyCommissionAmount) {
        this.buyCommissionAmount = buyCommissionAmount;
    }
    public BigDecimal getSellCommissionAmount() {
        return sellCommissionAmount;
    }
    public void setSellCommissionAmount(BigDecimal sellCommissionAmount) {
        this.sellCommissionAmount = sellCommissionAmount;
    }
    public BigDecimal getManageCommissionAmount() {
        return manageCommissionAmount;
    }
    public void setManageCommissionAmount(BigDecimal manageCommissionAmount) {
        this.manageCommissionAmount = manageCommissionAmount;
    }
    public BigDecimal getServeAmount() {
        return serveAmount;
    }
    public void setServeAmount(BigDecimal serveAmount) {
        this.serveAmount = serveAmount;
    }
    public BigDecimal getCompanyCommissionAmount() {
        return companyCommissionAmount;
    }
    public void setCompanyCommissionAmount(BigDecimal companyCommissionAmount) {
        this.companyCommissionAmount = companyCommissionAmount;
    }
    public BigDecimal getServeRate() {
        return serveRate;
    }
    public void setServeRate(BigDecimal serveRate) {
        this.serveRate = serveRate;
    }
    public BigDecimal getBreachRate() {
        return breachRate;
    }
    public void setBreachRate(BigDecimal breachRate) {
        this.breachRate = breachRate;
    }
    public BigDecimal getBusinessCommissionRate() {
        return businessCommissionRate;
    }
    public void setBusinessCommissionRate(BigDecimal businessCommissionRate) {
        this.businessCommissionRate = businessCommissionRate;
    }
    public BigDecimal getBuyCommissionRate() {
        return buyCommissionRate;
    }
    public void setBuyCommissionRate(BigDecimal buyCommissionRate) {
        this.buyCommissionRate = buyCommissionRate;
    }
    public BigDecimal getSellCommissionRate() {
        return sellCommissionRate;
    }
    public void setSellCommissionRate(BigDecimal sellCommissionRate) {
        this.sellCommissionRate = sellCommissionRate;
    }
    public BigDecimal getManageCommissionRate() {
        return manageCommissionRate;
    }
    public void setManageCommissionRate(BigDecimal manageCommissionRate) {
        this.manageCommissionRate = manageCommissionRate;
    }
    public Long getEnterpriseId() {
        return enterpriseId;
    }
    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }
    public String getSellContractNo() {
        return sellContractNo;
    }
    public void setSellContractNo(String sellContractNo) {
        this.sellContractNo = sellContractNo;
    }
    public String getBuyContractNo() {
        return buyContractNo;
    }
    public void setBuyContractNo(String buyContractNo) {
        this.buyContractNo = buyContractNo;
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
    public String getFactoryName() {
        return factoryName;
    }
    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }
    public Date getContractTime() {
        return contractTime;
    }
    public void setContractTime(Date contractTime) {
        this.contractTime = contractTime;
    }
    public Date getPayFullTime() {
        return payFullTime;
    }
    public void setPayFullTime(Date payFullTime) {
        this.payFullTime = payFullTime;
    }
    public Date getRealPayFullTime() {
        return realPayFullTime;
    }
    public void setRealPayFullTime(Date realPayFullTime) {
        this.realPayFullTime = realPayFullTime;
    }
    public Date getDeliveryTime() {
        return deliveryTime;
    }
    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
    public Long getSellProductId() {
        return sellProductId;
    }
    public void setSellProductId(Long sellProductId) {
        this.sellProductId = sellProductId;
    }
    public Boolean getCreditFlg() {
        return creditFlg;
    }
    public void setCreditFlg(Boolean creditFlg) {
        this.creditFlg = creditFlg;
    }
    public String getSettlementCode() {
        return settlementCode;
    }
    public void setSettlementCode(String settlementCode) {
        this.settlementCode = settlementCode;
    }

    public String getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(String settlementType) {
        this.settlementType = settlementType;
    }

    public String getBudgetStatus() {
        return budgetStatus;
    }

    public void setBudgetStatus(String budgetStatus) {
        this.budgetStatus = budgetStatus;
    }

    public String getBudgetFinishStatus() {
        return budgetFinishStatus;
    }

    public void setBudgetFinishStatus(String budgetFinishStatus) {
        this.budgetFinishStatus = budgetFinishStatus;
    }
    public Long getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(Long overdueDays) {
        this.overdueDays = overdueDays;
    }

    public BigDecimal getCompanyCommissionRate() {
        return companyCommissionRate;
    }

    public void setCompanyCommissionRate(BigDecimal companyCommissionRate) {
        this.companyCommissionRate = companyCommissionRate;
    }

    public BigDecimal getGrossProfitRate() {
        return grossProfitRate;
    }

    public void setGrossProfitRate(BigDecimal grossProfitRate) {
        this.grossProfitRate = grossProfitRate;
    }

    public BigDecimal getBuyTransportAmount() {
        return buyTransportAmount;
    }

    public void setBuyTransportAmount(BigDecimal buyTransportAmount) {
        this.buyTransportAmount = buyTransportAmount;
    }

    public BigDecimal getBuyWarehouseAmount() {
        return buyWarehouseAmount;
    }

    public void setBuyWarehouseAmount(BigDecimal buyWarehouseAmount) {
        this.buyWarehouseAmount = buyWarehouseAmount;
    }

    public BigDecimal getMarketingRetention() {
        return marketingRetention;
    }

    public void setMarketingRetention(BigDecimal marketingRetention) {
        this.marketingRetention = marketingRetention;
    }

    public BigDecimal getMarketingRetentionRate() {
        return marketingRetentionRate;
    }

    public void setMarketingRetentionRate(BigDecimal marketingRetentionRate) {
        this.marketingRetentionRate = marketingRetentionRate;
    }

    public Long getCreditCycle() {
        return creditCycle;
    }

    public void setCreditCycle(Long creditCycle) {
        this.creditCycle = creditCycle;
    }

    public BigDecimal getSurtax() {
        return surtax;
    }

    public void setSurtax(BigDecimal surtax) {
        this.surtax = surtax;
    }

    public BigDecimal getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(BigDecimal netProfit) {
        this.netProfit = netProfit;
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

    public String getProcessCode() {
        return processCode;
    }

    public void setProcessCode(String processCode) {
        this.processCode = processCode;
    }

    public Integer getPalletDay() {
        return palletDay;
    }

    public void setPalletDay(Integer palletDay) {
        this.palletDay = palletDay;
    }

    public BigDecimal getPalletAmount() {
        return palletAmount;
    }

    public void setPalletAmount(BigDecimal palletAmount) {
        this.palletAmount = palletAmount;
    }

    public Date getBuyPayFullTime() {
        return buyPayFullTime;
    }

    public void setBuyPayFullTime(Date buyPayFullTime) {
        this.buyPayFullTime = buyPayFullTime;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    public BigDecimal getGrossProfit() {
        return grossProfit;
    }

    public void setGrossProfit(BigDecimal grossProfit) {
        this.grossProfit = grossProfit;
    }

    public BigDecimal getBuyDirectorCommissionAmount() {
        return buyDirectorCommissionAmount;
    }

    public void setBuyDirectorCommissionAmount(BigDecimal buyDirectorCommissionAmount) {
        this.buyDirectorCommissionAmount = buyDirectorCommissionAmount;
    }

    public BigDecimal getSellDirectorCommissionAmount() {
        return sellDirectorCommissionAmount;
    }

    public void setSellDirectorCommissionAmount(BigDecimal sellDirectorCommissionAmount) {
        this.sellDirectorCommissionAmount = sellDirectorCommissionAmount;
    }

    public Long getBuyDirectorUserId() {
        return buyDirectorUserId;
    }

    public void setBuyDirectorUserId(Long buyDirectorUserId) {
        this.buyDirectorUserId = buyDirectorUserId;
    }

    public Long getSellDirectorUserId() {
        return sellDirectorUserId;
    }

    public void setSellDirectorUserId(Long sellDirectorUserId) {
        this.sellDirectorUserId = sellDirectorUserId;
    }

    public BigDecimal getMarginRate() {
        return marginRate;
    }

    public void setMarginRate(BigDecimal marginRate) {
        this.marginRate = marginRate;
    }

    public BigDecimal getSellRefund() {
        return sellRefund;
    }

    public void setSellRefund(BigDecimal sellRefund) {
        this.sellRefund = sellRefund;
    }

    public BigDecimal getBuyRefund() {
        return buyRefund;
    }

    public void setBuyRefund(BigDecimal buyRefund) {
        this.buyRefund = buyRefund;
    }

    public BigDecimal getLogisticsRefund() {
        return logisticsRefund;
    }

    public void setLogisticsRefund(BigDecimal logisticsRefund) {
        this.logisticsRefund = logisticsRefund;
    }
}
