package com.spt.bas.client.vo;

import java.io.Serializable;
import java.util.List;

public class CompanyCreditExportVo implements Serializable {
    private BsCompanySearchVo bsCompanySearchVo;
    private List<CompanyCreditInfo0Vo> companyCreditInfo0VoList;

    public BsCompanySearchVo getBsCompanySearchVo() {
        return bsCompanySearchVo;
    }

    public void setBsCompanySearchVo(BsCompanySearchVo bsCompanySearchVo) {
        this.bsCompanySearchVo = bsCompanySearchVo;
    }

    public List<CompanyCreditInfo0Vo> getCompanyCreditInfo0VoList() {
        return companyCreditInfo0VoList;
    }

    public void setCompanyCreditInfo0VoList(List<CompanyCreditInfo0Vo> companyCreditInfo0VoList) {
        this.companyCreditInfo0VoList = companyCreditInfo0VoList;
    }
}
