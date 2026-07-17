package com.spt.bas.report.client.vo;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

public class RptIndexStatisticsReqVo {
    
    /**
     * 统计类型
     */
    private String statisticsType;
    
    /**
     * 开始时间
     */
    private Date startDate;

    /**
     * 结束时间
     */
    private Date endDate;

    /**
     * 我方名称
     * @return
     */
    private String ourCompanyName;

    /**
     * 我方列表
     */
    private List<String> ourCompanyNameList;

    /**
     * 背靠背赊销标识
     */
    private Boolean matchCreditFlg;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 月份
     */
    private String month;
    
    /**
     * 化工业务员ID集合
     */
    private List<Long> hgMatchUserIdList;
    

    public String getStatisticsType() {
        return statisticsType;
    }

    public void setStatisticsType(String statisticsType) {
        this.statisticsType = statisticsType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public List<String> getOurCompanyNameList() {
        return ourCompanyNameList;
    }

    public void setOurCompanyNameList(List<String> ourCompanyNameList) {
        this.ourCompanyNameList = ourCompanyNameList;
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

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public List<Long> getHgMatchUserIdList() {
        return hgMatchUserIdList;
    }

    public void setHgMatchUserIdList(List<Long> hgMatchUserIdList) {
        this.hgMatchUserIdList = hgMatchUserIdList;
    }

    public RptIndexStatisticsReqVo() {
    }

    public RptIndexStatisticsReqVo(String key) {
        String ourCompanyName = "";
        String businessType = "";
        if(StringUtils.isNotBlank(key)){
            ourCompanyName = key.substring(0,key.indexOf(","));
            businessType = key.substring(key.indexOf(",")+1,key.length());
        }

        this.ourCompanyName = ourCompanyName;
        this.businessType = businessType;
    }
}
