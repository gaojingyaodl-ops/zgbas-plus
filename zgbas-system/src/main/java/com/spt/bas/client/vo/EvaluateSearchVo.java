package com.spt.bas.client.vo;

import com.spt.tools.core.bean.PageSearchVo;

import java.util.List;

public class EvaluateSearchVo extends PageSearchVo {
    private Long userId;
    private Long deptId;
    private String evaluateMonth;
    /**
     * 类型
     * A: 查看所有的考核人员
     * B: 查看本中心考核人员
     */
    private String evaluateTypes="C";
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

    public String getEvaluateTypes() {
        return evaluateTypes;
    }

    public void setEvaluateTypes(String evaluateTypes) {
        this.evaluateTypes = evaluateTypes;
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
