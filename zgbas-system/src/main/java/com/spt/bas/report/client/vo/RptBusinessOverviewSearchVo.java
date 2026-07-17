package com.spt.bas.report.client.vo;

import java.util.List;

/**
 * 业务总览
 */
public class RptBusinessOverviewSearchVo {

    /**
     * 合同成交时间 开始
     */
    private String contractDateBegin;

    /**
     * 合同成交时间 结束
     */
    private String contractDateEnd;
    
    /**
     * 我方抬头
     */
    private String ourCompanyName;
    
    /**
     * 客户名称
     *  
     */
    private String companyName;
    
    /**
     * 业务类型:代采赊销标识
     */
    private Boolean matchCreditFlg;

    /**
     * 业务类型 赊销：SX 代采：DC
     */
    private String businessType;

    /**
     * 化工业务员ID集合
     */
    private List<Long> hgMatchUserIdList;

    /**
     * 业务助理权限
     */
    private Boolean businessZlPerm;
    
    /**
     * 部门IDs
     */
    private List<Long> deptIdList;


    public String getContractDateBegin() {
        return contractDateBegin;
    }

    public void setContractDateBegin(String contractDateBegin) {
        this.contractDateBegin = contractDateBegin;
    }

    public String getContractDateEnd() {
        return contractDateEnd;
    }

    public void setContractDateEnd(String contractDateEnd) {
        this.contractDateEnd = contractDateEnd;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Boolean getMatchCreditFlg() {
        return matchCreditFlg;
    }

    public void setMatchCreditFlg(Boolean matchCreditFlg) {
        this.matchCreditFlg = matchCreditFlg;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public List<Long> getHgMatchUserIdList() {
        return hgMatchUserIdList;
    }

    public void setHgMatchUserIdList(List<Long> hgMatchUserIdList) {
        this.hgMatchUserIdList = hgMatchUserIdList;
    }

    public Boolean getBusinessZlPerm() {
        return businessZlPerm;
    }

    public void setBusinessZlPerm(Boolean businessZlPerm) {
        this.businessZlPerm = businessZlPerm;
    }

    public List<Long> getDeptIdList() {
        return deptIdList;
    }

    public void setDeptIdList(List<Long> deptIdList) {
        this.deptIdList = deptIdList;
    }
}
