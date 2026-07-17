package com.spt.bas.report.client.vo;

import java.util.List;

public class RptGrossProfitMarginSearchVo {
    
    private List<String> eighteenMonthList;
    private List<Long> deptIdList;

    public List<String> getEighteenMonthList() {
        return eighteenMonthList;
    }

    public void setEighteenMonthList(List<String> eighteenMonthList) {
        this.eighteenMonthList = eighteenMonthList;
    }

    public List<Long> getDeptIdList() {
        return deptIdList;
    }

    public void setDeptIdList(List<Long> deptIdList) {
        this.deptIdList = deptIdList;
    }
}
