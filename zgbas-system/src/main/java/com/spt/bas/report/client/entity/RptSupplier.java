package com.spt.bas.report.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

public class RptSupplier extends IdEntity {

    /**
     * 企业ID
     */
    private Long companyId;
    /** 企业名称 */
    private String companyName;

    /** 客户区域 */
    private String companyArea;
    /** 省份代码 */
    private String provinceCode;
    /** 省份名称 */
    private String provinceName;
    /** 注册资金 */
    private String registerCapital;
    /** 供应商准入白名单。W-白名单 G-灰名单 B-黑名单 */
    private String supplierRating;
    private String supplierRatingName;
    /** 供应商分类（A类：60分以上；B类：50-60分；C类：40-50分；D类：40分以下） */
    private String supplierGrade;
    private String supplierLevel; // 供应商级别。{'A级-龙头供应商': 'A', 'B级-扶持供应商': 'B', 'C级-小规模供应商': 'C'},
    /** 供应商配送 */
    private String supplierDelivery = "0"; // 是否可以供应商配送
    /** 创建时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date createdDate;
    
    /** 常用牌号 */
    private String commonBrandNumber;
    /** 交易单数 */
    private Integer tradeCount;
    /** 交易吨数 */
    private Integer tradeTonnes;
    private BigDecimal tradeTonnesSell;
    private BigDecimal tradeTonnesBuy;

    /** 逾期天数 - 合同总的逾期天数 */
    private Long breachDays;

    /** 采购单价 */
    private BigDecimal buyUnitPrice;
    /** 采购总价 */
    private BigDecimal buyTotalAmount;
    /**
     * 销售单价
     */
    private BigDecimal sellUnitPrice;
    /** 销售总价 */
    private BigDecimal sellTotalAmount;
    /** 下游加价 */
    private BigDecimal premium;
    /** 毛利率 */
    private BigDecimal grossProfitMargin;
    
    /** 最近成交时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date lastContractTime; 				// 成交时间

    /** 首次成交时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date firstContractTime;

    /** 业务区域 企业领用人所属部门 */
    private Long deptId;
    private String deptName;

    /** 业务员ID  企业领用人 */
    private Long matchUserId;

    /** 业务员名称 */
    private String matchUserName;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyArea() {
        return companyArea;
    }

    public void setCompanyArea(String companyArea) {
        this.companyArea = companyArea;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getRegisterCapital() {
        return registerCapital;
    }

    public void setRegisterCapital(String registerCapital) {
        this.registerCapital = registerCapital;
    }

    public String getSupplierRating() {
        return supplierRating;
    }

    public void setSupplierRating(String supplierRating) {
        this.supplierRating = supplierRating;
    }

    public String getSupplierGrade() {
        return supplierGrade;
    }

    public void setSupplierGrade(String supplierGrade) {
        this.supplierGrade = supplierGrade;
    }

    public String getSupplierDelivery() {
        return supplierDelivery;
    }

    public void setSupplierDelivery(String supplierDelivery) {
        this.supplierDelivery = supplierDelivery;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCommonBrandNumber() {
        return commonBrandNumber;
    }

    public void setCommonBrandNumber(String commonBrandNumber) {
        this.commonBrandNumber = commonBrandNumber;
    }

    public Integer getTradeCount() {
        return tradeCount;
    }

    public void setTradeCount(Integer tradeCount) {
        this.tradeCount = tradeCount;
    }

    public Integer getTradeTonnes() {
        return tradeTonnes;
    }

    public void setTradeTonnes(Integer tradeTonnes) {
        this.tradeTonnes = tradeTonnes;
    }

    public BigDecimal getBuyUnitPrice() {
        return buyUnitPrice;
    }

    public void setBuyUnitPrice(BigDecimal buyUnitPrice) {
        this.buyUnitPrice = buyUnitPrice;
    }

    public BigDecimal getBuyTotalAmount() {
        return buyTotalAmount;
    }

    public void setBuyTotalAmount(BigDecimal buyTotalAmount) {
        this.buyTotalAmount = buyTotalAmount;
    }

    public BigDecimal getSellUnitPrice() {
        return sellUnitPrice;
    }

    public void setSellUnitPrice(BigDecimal sellUnitPrice) {
        this.sellUnitPrice = sellUnitPrice;
    }

    public BigDecimal getSellTotalAmount() {
        return sellTotalAmount;
    }

    public void setSellTotalAmount(BigDecimal sellTotalAmount) {
        this.sellTotalAmount = sellTotalAmount;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public Date getLastContractTime() {
        return lastContractTime;
    }

    public void setLastContractTime(Date lastContractTime) {
        this.lastContractTime = lastContractTime;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
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

    public String getSupplierRatingName() {
        return supplierRatingName;
    }

    public void setSupplierRatingName(String supplierRatingName) {
        this.supplierRatingName = supplierRatingName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getSupplierLevel() {
        return supplierLevel;
    }

    public void setSupplierLevel(String supplierLevel) {
        this.supplierLevel = supplierLevel;
    }

    public BigDecimal getTradeTonnesSell() {
        return tradeTonnesSell;
    }

    public void setTradeTonnesSell(BigDecimal tradeTonnesSell) {
        this.tradeTonnesSell = tradeTonnesSell;
    }

    public BigDecimal getTradeTonnesBuy() {
        return tradeTonnesBuy;
    }

    public void setTradeTonnesBuy(BigDecimal tradeTonnesBuy) {
        this.tradeTonnesBuy = tradeTonnesBuy;
    }

    public Long getBreachDays() {
        return breachDays;
    }

    public void setBreachDays(Long breachDays) {
        this.breachDays = breachDays;
    }

    public Date getFirstContractTime() {
        return firstContractTime;
    }

    public void setFirstContractTime(Date firstContractTime) {
        this.firstContractTime = firstContractTime;
    }

    public BigDecimal getGrossProfitMargin() {
        return grossProfitMargin;
    }

    public void setGrossProfitMargin(BigDecimal grossProfitMargin) {
        this.grossProfitMargin = grossProfitMargin;
    }
}
