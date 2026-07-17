package com.spt.bas.report.client.vo;

import com.spt.tools.core.bean.PageSearchVo;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: wm
 * @Date: Created in 2022-06-21 09:50
 */
public class RptCtrContractFinanceSearch extends PageSearchVo {
    private String contractNo; //合同编号
    private String businessType;//业务类型
    private String statisticalType;// 统计类型
    private String companyName;//企业名称

    private String ourCompanyName;//我方抬头
    private List<String> ourCompanyNames;//我方抬头

    private String contractStatus;
    private String contractType;
    private String contractTimeBegin;
    private String contractTimeEnd;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 化工业务员ID集合
     */
    private List<Long> hgMatchUserIdList;

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getStatisticalType() {
        return statisticalType;
    }

    public void setStatisticalType(String statisticalType) {
        this.statisticalType = statisticalType;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public List<String> getOurCompanyNames() {
        return ourCompanyNames;
    }

    public void setOurCompanyNames(List<String> ourCompanyNames) {
        this.ourCompanyNames = ourCompanyNames;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
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

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public List<Long> getHgMatchUserIdList() {
        return hgMatchUserIdList;
    }

    public void setHgMatchUserIdList(List<Long> hgMatchUserIdList) {
        this.hgMatchUserIdList = hgMatchUserIdList;
    }
}
