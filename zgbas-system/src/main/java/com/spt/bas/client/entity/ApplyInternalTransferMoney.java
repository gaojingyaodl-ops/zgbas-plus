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
 * qzh
 * 2021/10/19
 * 内部资金拆借
 */
@Entity
@Table(name = "t_apply_internal_transfer_money")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyInternalTransferMoney extends IdEntity implements IPmEntity {

    /**
     * 申请人
     */
    private String applyUserName;
    /**
     * 状态
     */
    private String status;
    /**
     * 申请日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date applyDate;
    /**
     * 借款方
     */
    private String borrower;
    /**
     * 贷款方
     */
    private String lender;
    /**
     * 还款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date repaymentDate;
    /**
     * 金额
     */
    private BigDecimal repaymentAmount;

    /**
     * 利息
     */
    private BigDecimal interest;
    /**
     * 用途
     */
    private String applyuse;
    /**
     * 备注
     */
    private String remark;
    /**
     * 审批编号
     */
    private Long approveId;
    /**
     * 附件id
     */
    private String fileId;


    public String getFileId() {
        return fileId;
    }

    @Override
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getApplyUserName() {
        return applyUserName;
    }

    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    public Date getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }

    public String getBorrower() {
        return borrower;
    }

    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    public String getLender() {
        return lender;
    }

    public void setLender(String lender) {
        this.lender = lender;
    }

    public Date getRepaymentDate() {
        return repaymentDate;
    }

    public void setRepaymentDate(Date repaymentDate) {
        this.repaymentDate = repaymentDate;
    }

    public BigDecimal getRepaymentAmount() {
        return repaymentAmount;
    }

    public void setRepaymentAmount(BigDecimal repaymentAmount) {
        this.repaymentAmount = repaymentAmount;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public String getApplyuse() {
        return applyuse;
    }

    public void setApplyuse(String applyuse) {
        this.applyuse = applyuse;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getApproveId() {
        return approveId;
    }


    @Override
    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
