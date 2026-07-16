package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 企业业务扩展表
 */
@Entity
@Table(name = "t_company_business_expansion")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CompanyBusinessExpansion extends IdEntity {

    private static final long serialVersionUID = 6801422997931243896L;

    /**
     * 企业ID
     */
    private Long companyId;
    /** 逾期天数 - 合同总的逾期天数 */
    private Long breachDays;
    /** 常用牌号 */
    private String commonBrandNumber;
    /** 交易单数 */
    private Integer tradeCount;
    /** 交易吨数 */
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
    private Date lastContractTime;
    
    /** 首次成交时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date firstContractTime;

    /** 业务区域 企业领用人所属部门 */
    private Long deptId;

    /** 业务员ID  企业领用人 */
    private Long matchUserId;

    /** 业务员名称 */
    private String matchUserName;

    /** 是否诉讼 */
    private Boolean legalFlg;


    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getBreachDays() {
        return breachDays;
    }

    public void setBreachDays(Long breachDays) {
        this.breachDays = breachDays;
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

    public Date getFirstContractTime() {
        return firstContractTime;
    }

    public void setFirstContractTime(Date firstContractTime) {
        this.firstContractTime = firstContractTime;
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

    public Boolean getLegalFlg() {
        return legalFlg;
    }

    public void setLegalFlg(Boolean legalFlg) {
        this.legalFlg = legalFlg;
    }
}
