package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

public class RptIndexStatisticsVo {

    /**
     * 统计类型
     */
    private String statisticsType;
    
    /**
     * 统计类型
     */
    private String statisticsTypeName;
    
    /**
     * 总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 总逾期罚息金额
     */
    private BigDecimal totalBreachAmount;
    
    /**
     * 总数量
     */
    private BigDecimal totalNumber;
    
    /**
     * 总单数
     */
    private Integer totalCount;
    
    /**
     * 到期日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payFullTime;

    /**
     * 我方名称
     * @return
     */
    private String ourCompanyName;

    /**
     * 序号
     */
    private Integer dispOrderNo;

    /**
     * 背靠背赊销标识
     */
    private Boolean matchCreditFlg;
    
    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 年
     */
    private String year;
    
    /**
     * 月份
     */
    private String month;

    /**
     * 周
     */
    private String week;

    /**
     * 开始日期 str
     * @return
     */
    private String startDate;

    public String getStatisticsType() {
        return statisticsType;
    }

    public void setStatisticsType(String statisticsType) {
        this.statisticsType = statisticsType;
    }

    public String getStatisticsTypeName() {
        return statisticsTypeName;
    }

    public void setStatisticsTypeName(String statisticsTypeName) {
        this.statisticsTypeName = statisticsTypeName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getTotalBreachAmount() {
        return totalBreachAmount;
    }

    public void setTotalBreachAmount(BigDecimal totalBreachAmount) {
        this.totalBreachAmount = totalBreachAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(BigDecimal totalNumber) {
        this.totalNumber = totalNumber;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Date getPayFullTime() {
        return payFullTime;
    }

    public void setPayFullTime(Date payFullTime) {
        this.payFullTime = payFullTime;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public Integer getDispOrderNo() {
        return dispOrderNo;
    }

    public void setDispOrderNo(Integer dispOrderNo) {
        this.dispOrderNo = dispOrderNo;
    }

    public Boolean getMatchCreditFlg() {
        return matchCreditFlg;
    }

    public void setMatchCreditFlg(Boolean matchCreditFlg) {
        this.matchCreditFlg = matchCreditFlg;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
