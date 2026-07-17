package com.spt.bas.report.client.payload;

import com.spt.tools.core.bean.PageSearchVo;

import java.util.List;

/**
 * <p>
 *  逾期信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-02-01 14:40
 */
public class OverdueCompany extends PageSearchVo {
    /**
     * 0：集团公司；1：事业部；2：业务部；3：业务员
     */
    private String matchType;

    /**
     * matchType==0时，matchUserId不用填；
     * matchType==1时，matchUserId填写事业部Id；
     * matchType==2时，matchUserId填写业务部Id；
     * matchType==3时，matchUserId填写业务员Id
     */
    private Long matchUserId;

    private String timeFrom;

    private String timeTo;

    private String companyName;

    private List<Long> matchUserIds;

    /**
     * "正常": 0,
     * "有逾期": 1,
     * "有违约": 2,
     * "全部": 3,
     * "逾期M1":4,
     * "逾期M2":5
     * }。如果是"有逾期": 1，则表示4与5的合集。
     */
    private String companyFlag;

    private String companyType;

    private String customCompanySource;

    private String address;

    public String getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(String timeFrom) {
        this.timeFrom = timeFrom;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(String timeTo) {
        this.timeTo = timeTo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyFlag() {
        return companyFlag;
    }

    public void setCompanyFlag(String companyFlag) {
        this.companyFlag = companyFlag;
    }

    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public String getCustomCompanySource() {
        return customCompanySource;
    }

    public void setCustomCompanySource(String customCompanySource) {
        this.customCompanySource = customCompanySource;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public List<Long> getMatchUserIds() {
        return matchUserIds;
    }

    public void setMatchUserIds(List<Long> matchUserIds) {
        this.matchUserIds = matchUserIds;
    }
}
