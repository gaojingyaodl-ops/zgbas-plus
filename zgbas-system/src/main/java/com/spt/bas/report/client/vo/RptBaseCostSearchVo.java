package com.spt.bas.report.client.vo;


/**
 * 业务成本统计表
 */
public class RptBaseCostSearchVo {

    /** 业务员ID */
    private Long matchUserId;

    /** 业务员 */
    private String matchUserName;

    /** 所属区域CD */
    private String branchCd;

    /** 所属区域名称 */
    private String branchName;

    /** 业务成本年月 */
    private String baseDate;


    /** 业务成开始年月 */
    private String baseDateStart;

    /** 业务成本结束年月 */
    private String baseDateEnd;

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

    public String getBranchCd() {
        return branchCd;
    }

    public void setBranchCd(String branchCd) {
        this.branchCd = branchCd;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBaseDate() {
        return baseDate;
    }

    public void setBaseDate(String baseDate) {
        this.baseDate = baseDate;
    }

    public String getBaseDateStart() {
        return baseDateStart;
    }

    public void setBaseDateStart(String baseDateStart) {
        this.baseDateStart = baseDateStart;
    }

    public String getBaseDateEnd() {
        return baseDateEnd;
    }

    public void setBaseDateEnd(String baseDateEnd) {
        this.baseDateEnd = baseDateEnd;
    }
}
