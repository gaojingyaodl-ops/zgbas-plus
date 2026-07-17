package com.spt.bas.report.client.vo;

import com.spt.tools.core.bean.PageSearchVo;

import java.util.List;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/5/27 10:00
 */

public class RptEvaluateUserSearchVo extends PageSearchVo {
    private Long userId;
    private Long deptId;
    private String userName;
    private String evaluateStatus;
    private String evaluateMonth;
    /**
     * 考核人所属编码
     */
    private String branchCd;
    /**
     * 部门 id
     */
    private List<Long> deptIdList;

    /**
     * 中心负责人ID
     */
    private Long deptLeaderId;

    /**
     * 当前登录人 id
     */
    private Long currentUserId;

    public String getBranchCd() {
        return branchCd;
    }

    public void setBranchCd(String branchCd) {
        this.branchCd = branchCd;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEvaluateStatus() {
        return evaluateStatus;
    }

    public void setEvaluateStatus(String evaluateStatus) {
        this.evaluateStatus = evaluateStatus;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getEvaluateMonth() {
        return evaluateMonth;
    }

    public void setEvaluateMonth(String evaluateMonth) {
        this.evaluateMonth = evaluateMonth;
    }

    public List<Long> getDeptIdList() {
        return deptIdList;
    }

    public void setDeptIdList(List<Long> deptIdList) {
        this.deptIdList = deptIdList;
    }

    public Long getDeptLeaderId() {
        return deptLeaderId;
    }

    public void setDeptLeaderId(Long deptLeaderId) {
        this.deptLeaderId = deptLeaderId;
    }

    public Long getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
    }
}
