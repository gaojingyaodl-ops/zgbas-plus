package com.spt.bas.report.client.entity;

import com.spt.tools.core.bean.PageSearchVo;

public class RptEvaluateTotalSearch extends PageSearchVo {
    private String evaluateMonth; //	varchar(20)	考核年月
    private String deptName; //	varchar(20)	考核部门
    private String deptId; //	varchar(20)	考核部门

    public String getEvaluateMonth() {
        return evaluateMonth;
    }

    public void setEvaluateMonth(String evaluateMonth) {
        this.evaluateMonth = evaluateMonth;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }
}
