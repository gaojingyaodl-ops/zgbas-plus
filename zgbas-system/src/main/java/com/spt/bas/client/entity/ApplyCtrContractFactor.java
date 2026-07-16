package com.spt.bas.client.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 保理合同扩展表
 * qzh
 */
@Entity
@Table(name = "t_ctr_contract_factor")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyCtrContractFactor extends IdEntity implements IPmEntity {
    /**
     * 我方抬头
     */
    private String ourCompanyName;

    /**
     * 保理公司
     */
    private String factorCompany;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 保理状态
     */
    private String factorStatus;

    /**
     * 合同金额
     */
    private BigDecimal contractAmount;

    /**
     * 保证金金额
     */
    private BigDecimal bondAmount;

    /**
     * 银行放款金额
     */
    private BigDecimal loanAmount;

    /**
     * 已还款金额
     */
    private BigDecimal backAmount;

    /**
     * 债权凭证附件ID
     */
    private Long rightsFileId;

    /**
     * 收货证明附件ID
     */
    private Long goodsFileId;

    /**
     * 下家发票附件ID
     */
    private Long invoiceFileId;

    /**
     * 实际放款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date loanDate;

    /**
     * 实际还款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date backDate;

    /**
     * 计划还款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date planBackDate;

    /**
     * 是否可发起保证金付款
     */
    private Boolean factorPayFlg = true;

    /**
     * 是否有效
     */
    private String enableFlg;

    private Long approveId;

    private String status;

    /**
     * 用于定时任务防止重复发起正在生僻中流程
     */
    private String repaymentApplyStatus;


    public String getRepaymentApplyStatus() {
        return repaymentApplyStatus;
    }

    public void setRepaymentApplyStatus(String repaymentApplyStatus) {
        this.repaymentApplyStatus = repaymentApplyStatus;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public String getFactorCompany() {
        return factorCompany;
    }

    public void setFactorCompany(String factorCompany) {
        this.factorCompany = factorCompany;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getFactorStatus() {
        return factorStatus;
    }

    public void setFactorStatus(String factorStatus) {
        this.factorStatus = factorStatus;
    }

    public BigDecimal getContractAmount() {
        return contractAmount;
    }

    public void setContractAmount(BigDecimal contractAmount) {
        this.contractAmount = contractAmount;
    }

    public BigDecimal getBondAmount() {
        return bondAmount;
    }

    public void setBondAmount(BigDecimal bondAmount) {
        this.bondAmount = bondAmount;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public BigDecimal getBackAmount() {
        return backAmount;
    }

    public void setBackAmount(BigDecimal backAmount) {
        this.backAmount = backAmount;
    }

    public Long getRightsFileId() {
        return rightsFileId;
    }

    public void setRightsFileId(Long rightsFileId) {
        this.rightsFileId = rightsFileId;
    }

    public Long getGoodsFileId() {
        return goodsFileId;
    }

    public void setGoodsFileId(Long goodsFileId) {
        this.goodsFileId = goodsFileId;
    }

    public Long getInvoiceFileId() {
        return invoiceFileId;
    }

    public void setInvoiceFileId(Long invoiceFileId) {
        this.invoiceFileId = invoiceFileId;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public Date getBackDate() {
        return backDate;
    }

    public void setBackDate(Date backDate) {
        this.backDate = backDate;
    }

    public Date getPlanBackDate() {
        return planBackDate;
    }

    public void setPlanBackDate(Date planBackDate) {
        this.planBackDate = planBackDate;
    }

    public String getEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(String enableFlg) {
        this.enableFlg = enableFlg;
    }

    public Long getApproveId() {
        return approveId;
    }

    @Override
    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getFactorPayFlg() {
        return factorPayFlg;
    }

    public void setFactorPayFlg(Boolean factorPayFlg) {
        this.factorPayFlg = factorPayFlg;
    }
}
