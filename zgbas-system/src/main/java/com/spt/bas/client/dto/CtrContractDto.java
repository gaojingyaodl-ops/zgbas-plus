package com.spt.bas.client.dto;

import com.spt.tools.core.bean.PageSearchVo;

import java.util.List;

public class CtrContractDto extends PageSearchVo {

    private Long companyId;

    private List<String> contractNoList;

    private Long userId;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public List<String> getContractNoList() {
        return contractNoList;
    }

    public void setContractNoList(List<String> contractNoList) {
        this.contractNoList = contractNoList;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
