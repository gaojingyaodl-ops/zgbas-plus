package com.spt.bas.report.client.vo;

import com.spt.tools.core.bean.PageSearchVo;

import java.util.List;
/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/3/21 15:36
 */

public class RptUserRoiVo extends PageSearchVo {

    /**
     * 业务年月
     */
    private String baseDate;
    private String baseStartDate;
    private String baseEndDate;

    /**
     * 所属区域cd
     */
    private String branchCd;

    private String matchUserName;

    private List<Long> userList;

    /**
     * 主要拥有对应区域权限的人才能看对应权限的数据
     */
    private List<String> branchCdList;

    public List<String> getBranchCdList() {
        return branchCdList;
    }

    public void setBranchCdList(List<String> branchCdList) {
        this.branchCdList = branchCdList;
    }

    public List<Long> getUserList() {
        return userList;
    }

    public void setUserList(List<Long> userList) {
        this.userList = userList;
    }

    public String getBaseDate() {
        return baseDate;
    }

    public void setBaseDate(String baseDate) {
        this.baseDate = baseDate;
    }

    public String getBaseStartDate() {
        return baseStartDate;
    }

    public void setBaseStartDate(String baseStartDate) {
        this.baseStartDate = baseStartDate;
    }

    public String getBaseEndDate() {
        return baseEndDate;
    }

    public void setBaseEndDate(String baseEndDate) {
        this.baseEndDate = baseEndDate;
    }

    public String getBranchCd() {
        return branchCd;
    }

    public void setBranchCd(String branchCd) {
        this.branchCd = branchCd;
    }

    public String getMatchUserName() {
        return matchUserName;
    }

    public void setMatchUserName(String matchUserName) {
        this.matchUserName = matchUserName;
    }
}
