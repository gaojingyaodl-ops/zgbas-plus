package com.spt.bas.report.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

public class RptCompany extends IdEntity {

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
    /** 信用等级 W-白名单 G-灰名单 B-黑名单 */
    private String creditRating;
    private String creditRatingName;
    /** 客户分类 等级为 A类客户 B类客户 C类客户 D类客户 */
    private String companyGrade;
    /** 人保批复额度 */
    private BigDecimal piccCreditAmount;
    /**
     * 大地额度
     */
    private BigDecimal daDiCreditAmount;
    /** 中银额度 */
    private BigDecimal zhongYinCreditAmount;
    /** 账期 */
    private Long creditDays;
    /** 逾期天数 - 合同总的逾期天数 */
    private Long breachDays;
    /** 是否访厂 */
    private Boolean accessReportFlg;
    private String accessReportFlgStr;
    /** 线上化标识-是否开通小程序 */
    private Boolean onLineFlg = false;
    private String onLineFlgStr;
    /** 是否诉讼 */
    private Boolean legalFlg;
    private String legalFlgStr;
    /** 实控人担保 0-未签署、1-已签署 是否连带  */
    private Boolean actualGuaranteeFlg;
    private String actualGuaranteeFlgStr;
    /** 创建时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date createdDate;
    
    /** 常用牌号 */
    private String commonBrandNumber;
    /** 交易单数 */
    private Integer tradeCount;
    /** 交易吨数 */
    private BigDecimal tradeTonnes;
    private BigDecimal tradeTonnesSell;
    private BigDecimal tradeTonnesBuy;

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

    /** 加价 */
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

    /**
     * 开户人
     */
    private Long ownerOfAccountId;

    /**
     * 开户人名称
     */
    private String ownerOfAccountName;


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

    public String getCreditRating() {
        return creditRating;
    }

    public void setCreditRating(String creditRating) {
        this.creditRating = creditRating;
    }

    public String getCompanyGrade() {
        return companyGrade;
    }

    public void setCompanyGrade(String companyGrade) {
        this.companyGrade = companyGrade;
    }

    public BigDecimal getPiccCreditAmount() {
        return piccCreditAmount;
    }

    public void setPiccCreditAmount(BigDecimal piccCreditAmount) {
        this.piccCreditAmount = piccCreditAmount;
    }

    public BigDecimal getDaDiCreditAmount() {
        return daDiCreditAmount;
    }

    public void setDaDiCreditAmount(BigDecimal daDiCreditAmount) {
        this.daDiCreditAmount = daDiCreditAmount;
    }

    public Long getCreditDays() {
        return creditDays;
    }

    public void setCreditDays(Long creditDays) {
        this.creditDays = creditDays;
    }

    public Long getBreachDays() {
        return breachDays;
    }

    public void setBreachDays(Long breachDays) {
        this.breachDays = breachDays;
    }

    public Boolean getAccessReportFlg() {
        return accessReportFlg;
    }

    public void setAccessReportFlg(Boolean accessReportFlg) {
        this.accessReportFlg = accessReportFlg;
    }

    public Boolean getOnLineFlg() {
        return onLineFlg;
    }

    public void setOnLineFlg(Boolean onLineFlg) {
        this.onLineFlg = onLineFlg;
    }

    public Boolean getLegalFlg() {
        return legalFlg;
    }

    public void setLegalFlg(Boolean legalFlg) {
        this.legalFlg = legalFlg;
    }

    public Boolean getActualGuaranteeFlg() {
        return actualGuaranteeFlg;
    }

    public void setActualGuaranteeFlg(Boolean actualGuaranteeFlg) {
        this.actualGuaranteeFlg = actualGuaranteeFlg;
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

    public BigDecimal getGrossProfitMargin() {
        return grossProfitMargin;
    }

    public void setGrossProfitMargin(BigDecimal grossProfitMargin) {
        this.grossProfitMargin = grossProfitMargin;
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

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
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

    public Long getOwnerOfAccountId() {
        return ownerOfAccountId;
    }

    public void setOwnerOfAccountId(Long ownerOfAccountId) {
        this.ownerOfAccountId = ownerOfAccountId;
    }

    public String getOwnerOfAccountName() {
        return ownerOfAccountName;
    }

    public void setOwnerOfAccountName(String ownerOfAccountName) {
        this.ownerOfAccountName = ownerOfAccountName;
    }

    public String getCreditRatingName() {
        return creditRatingName;
    }

    public void setCreditRatingName(String creditRatingName) {
        this.creditRatingName = creditRatingName;
    }

    public String getAccessReportFlgStr() {
        return accessReportFlgStr;
    }

    public void setAccessReportFlgStr(String accessReportFlgStr) {
        this.accessReportFlgStr = accessReportFlgStr;
    }

    public String getOnLineFlgStr() {
        return onLineFlgStr;
    }

    public void setOnLineFlgStr(String onLineFlgStr) {
        this.onLineFlgStr = onLineFlgStr;
    }

    public String getLegalFlgStr() {
        return legalFlgStr;
    }

    public void setLegalFlgStr(String legalFlgStr) {
        this.legalFlgStr = legalFlgStr;
    }

    public String getActualGuaranteeFlgStr() {
        return actualGuaranteeFlgStr;
    }

    public void setActualGuaranteeFlgStr(String actualGuaranteeFlgStr) {
        this.actualGuaranteeFlgStr = actualGuaranteeFlgStr;
    }

    public BigDecimal getTradeTonnes() {
        return tradeTonnes;
    }

    public void setTradeTonnes(BigDecimal tradeTonnes) {
        this.tradeTonnes = tradeTonnes;
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

    public Date getFirstContractTime() {
        return firstContractTime;
    }

    public void setFirstContractTime(Date firstContractTime) {
        this.firstContractTime = firstContractTime;
    }

    public BigDecimal getZhongYinCreditAmount() {
        return zhongYinCreditAmount;
    }

    public void setZhongYinCreditAmount(BigDecimal zhongYinCreditAmount) {
        this.zhongYinCreditAmount = zhongYinCreditAmount;
    }
}
