package com.spt.bas.report.client.vo;

import com.spt.tools.core.bean.PageSearchVo;

import java.util.List;

public class RptFundReceivableStatisticsVo extends PageSearchVo {
    // 区域
    private Long deptId;
    // 合同签订开始日期
    private String contractTimeBegin;
    // 合同签订结束日期
    private String contractTimeEnd;
    // 资金方名称集合
    private List<String> companyNameList;

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getContractTimeBegin() {
        return contractTimeBegin;
    }

    public void setContractTimeBegin(String contractTimeBegin) {
        this.contractTimeBegin = contractTimeBegin;
    }

    public String getContractTimeEnd() {
        return contractTimeEnd;
    }

    public void setContractTimeEnd(String contractTimeEnd) {
        this.contractTimeEnd = contractTimeEnd;
    }

    public List<String> getCompanyNameList() {
        return companyNameList;
    }

    public void setCompanyNameList(List<String> companyNameList) {
        this.companyNameList = companyNameList;
    }
}
