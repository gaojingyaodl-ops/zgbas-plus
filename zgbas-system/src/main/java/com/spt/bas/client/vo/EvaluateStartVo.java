package com.spt.bas.client.vo;

import java.util.List;

public class EvaluateStartVo {
    private List<Long> deptIds;
    private String evaluateMonth;
//    private List<SysUser> sysUsers;


    public List<Long> getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(List<Long> deptIds) {
        this.deptIds = deptIds;
    }

    public String getEvaluateMonth() {
        return evaluateMonth;
    }

    public void setEvaluateMonth(String evaluateMonth) {
        this.evaluateMonth = evaluateMonth;
    }
}
