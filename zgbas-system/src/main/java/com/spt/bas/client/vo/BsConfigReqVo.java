package com.spt.bas.client.vo;

import com.spt.bas.client.entity.ApplyMatch;
import com.spt.bas.client.entity.ApplyMatchDetail;

import java.math.BigDecimal;
import java.util.List;

/**
 * 业务发起控制入参Vo
 * @Author: gaojy
 * @create 2021/12/17 10:32
 * @version: 1.0
 * @description:
 */
public class BsConfigReqVo {
    private String ourCompanyName;
    private String sxCompany;
    private String contractModel;
    private String fundSource;
    private Long enterpriseId;
    private Long processId;
    private BigDecimal contractAmount = BigDecimal.ZERO;
    private ApplyMatch applyMatch;
    private List<ApplyMatchDetail> applyMatchDetailList;

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public String getSxCompany() {
        return sxCompany;
    }

    public void setSxCompany(String sxCompany) {
        this.sxCompany = sxCompany;
    }


    public String getContractModel() {
        return contractModel;
    }

    public void setContractModel(String contractModel) {
        this.contractModel = contractModel;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getFundSource() {
        return fundSource;
    }

    public void setFundSource(String fundSource) {
        this.fundSource = fundSource;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public BigDecimal getContractAmount() {
        return contractAmount;
    }

    public void setContractAmount(BigDecimal contractAmount) {
        this.contractAmount = contractAmount;
    }

    public ApplyMatch getApplyMatch() {
        return applyMatch;
    }

    public void setApplyMatch(ApplyMatch applyMatch) {
        this.applyMatch = applyMatch;
    }

    public List<ApplyMatchDetail> getApplyMatchDetailList() {
        return applyMatchDetailList;
    }

    public void setApplyMatchDetailList(List<ApplyMatchDetail> applyMatchDetailList) {
        this.applyMatchDetailList = applyMatchDetailList;
    }

    public BsConfigReqVo() {
    }

    public BsConfigReqVo(String ourCompanyName, String fundSource, Long enterpriseId, BigDecimal contractAmount) {
        this.ourCompanyName = ourCompanyName;
        this.fundSource = fundSource;
        this.enterpriseId = enterpriseId;
        this.contractAmount = contractAmount;
    }

    public BsConfigReqVo(String ourCompanyName, String contractModel, String fundSource, Long enterpriseId, Long processId, BigDecimal contractAmount) {
        this.ourCompanyName = ourCompanyName;
        this.contractModel = contractModel;
        this.fundSource = fundSource;
        this.enterpriseId = enterpriseId;
        this.processId = processId;
        this.contractAmount = contractAmount;
    }

    public BsConfigReqVo(String ourCompanyName, String contractModel, String fundSource, Long enterpriseId, Long processId, BigDecimal contractAmount, ApplyMatch applyMatch, List<ApplyMatchDetail> applyMatchDetailList) {
        this.ourCompanyName = ourCompanyName;
        this.contractModel = contractModel;
        this.fundSource = fundSource;
        this.enterpriseId = enterpriseId;
        this.processId = processId;
        this.contractAmount = contractAmount;
        this.applyMatch = applyMatch;
        this.applyMatchDetailList = applyMatchDetailList;
    }

    public BsConfigReqVo(String ourCompanyName, String sxCompany, String contractModel, String fundSource, Long enterpriseId, Long processId, BigDecimal contractAmount) {
        this.ourCompanyName = ourCompanyName;
        this.sxCompany = sxCompany;
        this.contractModel = contractModel;
        this.fundSource = fundSource;
        this.enterpriseId = enterpriseId;
        this.processId = processId;
        this.contractAmount = contractAmount;
    }

    public BsConfigReqVo(String ourCompanyName, String sxCompany, String contractModel, String fundSource, Long enterpriseId, Long processId, BigDecimal contractAmount, ApplyMatch applyMatch, List<ApplyMatchDetail> applyMatchDetailList) {
        this.ourCompanyName = ourCompanyName;
        this.sxCompany = sxCompany;
        this.contractModel = contractModel;
        this.fundSource = fundSource;
        this.enterpriseId = enterpriseId;
        this.processId = processId;
        this.contractAmount = contractAmount;
        this.applyMatch = applyMatch;
        this.applyMatchDetailList = applyMatchDetailList;
    }
}
