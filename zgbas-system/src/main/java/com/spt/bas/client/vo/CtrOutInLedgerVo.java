package com.spt.bas.client.vo;

import com.spt.tools.core.bean.PageSearchVo;

public class CtrOutInLedgerVo extends PageSearchVo {

    /**
     * 合同编号
     */
    private String contractNo;

    /** 所属区域CD */
    private String branchCd;

    /** 所属区域名称 */
    private String branchName;

    /**
     * 对方企业ID
     */
    private Long companyId;

    /**
     * 对方企业名称
     */
    private String companyName;

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
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

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
