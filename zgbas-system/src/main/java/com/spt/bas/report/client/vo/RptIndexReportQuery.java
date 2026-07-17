package com.spt.bas.report.client.vo;

import com.google.common.base.Objects;

import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/6/16 16:15
 */

public class RptIndexReportQuery {
    /**
     * 当前登录人
     */
    private Long currentUserId;

    private Long enterpriseId;
    /**
     * 部门id
     */
    private List<Long> deptIds;

    private List<Long> allDeptIds;
    // 是否是预售
    private String source;

    private Boolean userIdOrDept = false;
    /**
     * 保理合同
     */
    private List<String> businessTypeDcsx;
    /**
     * 是否是管理员
     */
    private Boolean adminFlag = false;

    private String contractType;
    /**
     * 代采/赊销
     */
    private Boolean matchCreditFlg;

    /**
     * 业绩统计名字标记；1-累计未结算提成，2-预计提成，3-上个月结算提成
     */
    private Integer nameFlag;

    /**
     * 结算状态 0-未结算，1-已结算
     */
    private Integer settleStatus;

    /**
     * 天/月
     */
    private String dayOrMonth;

    /**
     * 当前月或者是上个月，0-当前月，1-上个月
     */
    private Integer nowMonthOrLastMonth;

    /**
     * 用户ids
     */
    private List<Long> userIds;

    private String statisticsType;

    /**
     * 过去月份，0 当前月，1 上月，2 上上月 ...
     */
    private Integer lastMonthNum;

    /**
     * 时间查询 yyyy-MM-dd
     */
    private String queryDate;

    /**
     * 化工业务员ID集合
     */
    private List<Long> hgMatchUserIdList;

    public Boolean getUserIdOrDept() {
        return userIdOrDept;
    }

    public void setUserIdOrDept(Boolean userIdOrDept) {
        this.userIdOrDept = userIdOrDept;
    }

    public Boolean getMatchCreditFlg() {
        return matchCreditFlg;
    }

    public void setMatchCreditFlg(Boolean matchCreditFlg) {
        this.matchCreditFlg = matchCreditFlg;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public Integer getSettleStatus() {
        return settleStatus;
    }

    public void setSettleStatus(Integer settleStatus) {
        this.settleStatus = settleStatus;
    }

    public Integer getNowMonthOrLastMonth() {
        return nowMonthOrLastMonth;
    }

    public void setNowMonthOrLastMonth(Integer nowMonthOrLastMonth) {
        this.nowMonthOrLastMonth = nowMonthOrLastMonth;
    }

    public String getDayOrMonth() {
        return dayOrMonth;
    }

    public void setDayOrMonth(String dayOrMonth) {
        this.dayOrMonth = dayOrMonth;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public Long getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
    }

    public List<Long> getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(List<Long> deptIds) {
        this.deptIds = deptIds;
    }

    public Boolean getAdminFlag() {
        return adminFlag;
    }

    public void setAdminFlag(Boolean adminFlag) {
        this.adminFlag = adminFlag;
    }

    public Integer getNameFlag() {
        return nameFlag;
    }

    public void setNameFlag(Integer nameFlag) {
        this.nameFlag = nameFlag;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<Long> getAllDeptIds() {
        return allDeptIds;
    }

    public void setAllDeptIds(List<Long> allDeptIds) {
        this.allDeptIds = allDeptIds;
    }

    public List<String> getBusinessTypeDcsx() {
        return businessTypeDcsx;
    }

    public void setBusinessTypeDcsx(List<String> businessTypeDcsx) {
        this.businessTypeDcsx = businessTypeDcsx;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getStatisticsType() {
        return statisticsType;
    }

    public void setStatisticsType(String statisticsType) {
        this.statisticsType = statisticsType;
    }

    public Integer getLastMonthNum() {
        return lastMonthNum;
    }

    public void setLastMonthNum(Integer lastMonthNum) {
        this.lastMonthNum = lastMonthNum;
    }

    public String getQueryDate() {
        return queryDate;
    }

    public void setQueryDate(String queryDate) {
        this.queryDate = queryDate;
    }

    public List<Long> getHgMatchUserIdList() {
        return hgMatchUserIdList;
    }

    public void setHgMatchUserIdList(List<Long> hgMatchUserIdList) {
        this.hgMatchUserIdList = hgMatchUserIdList;
    }

    public RptIndexReportQuery() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RptIndexReportQuery)) {
            return false;
        }
        RptIndexReportQuery that = (RptIndexReportQuery) o;
        return Objects.equal(currentUserId, that.currentUserId) && Objects.equal(enterpriseId, that.enterpriseId) && Objects.equal(deptIds, that.deptIds) && Objects.equal(allDeptIds, that.allDeptIds) && Objects.equal(source, that.source) && Objects.equal(userIdOrDept, that.userIdOrDept) && Objects.equal(businessTypeDcsx, that.businessTypeDcsx) && Objects.equal(adminFlag, that.adminFlag) && Objects.equal(contractType, that.contractType) && Objects.equal(matchCreditFlg, that.matchCreditFlg) && Objects.equal(nameFlag, that.nameFlag) && Objects.equal(settleStatus, that.settleStatus) && Objects.equal(dayOrMonth, that.dayOrMonth) && Objects.equal(nowMonthOrLastMonth, that.nowMonthOrLastMonth) && Objects.equal(userIds, that.userIds);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(currentUserId, enterpriseId, deptIds, allDeptIds, source, userIdOrDept, businessTypeDcsx, adminFlag, contractType, matchCreditFlg, nameFlag, settleStatus, dayOrMonth, nowMonthOrLastMonth, userIds);
    }

    @Override
    public String toString() {
        return "IndexReportQuery{" +
                "currentUserId=" + currentUserId +
                ", enterpriseId=" + enterpriseId +
                ", deptIds=" + deptIds +
                ", allDeptIds=" + allDeptIds +
                ", source='" + source + '\'' +
                ", userIdOrDept=" + userIdOrDept +
                ", businessTypeDcsx=" + businessTypeDcsx +
                ", adminFlag=" + adminFlag +
                ", contractType='" + contractType + '\'' +
                ", matchCreditFlg=" + matchCreditFlg +
                ", nameFlag=" + nameFlag +
                ", settleStatus=" + settleStatus +
                ", dayOrMonth='" + dayOrMonth + '\'' +
                ", nowMonthOrLastMonth=" + nowMonthOrLastMonth +
                ", userIds=" + userIds +
                '}';
    }
}
