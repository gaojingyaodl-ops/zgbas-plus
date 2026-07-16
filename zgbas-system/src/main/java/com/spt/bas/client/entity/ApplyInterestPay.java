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
 * 申请单-付款申请单
 */
@Entity
@Table(name = "t_apply_interest_pay")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyInterestPay extends IdEntity implements IPmEntity {
    private static final long serialVersionUID = -1;

    /**
     * 关联审批单ID
     */
    private Long approveId;

    /**
     * 审批状态
     */
    private String status;

    /**
     * 申请人ID
     */
    private Long applyUserId;

    /**
     * 申请人姓名
     */
    private String applyUserName;

    /**
     * 中游合同ID逗号连接
     */
    private String contractIds;

    /**
     * 收款方
     */
    private String receiveCompanyName;

    /**
     * 收款方开户行
     */
    private String receiveBankName;

    /**
     * 收款方开户行账号
     */
    private String receiveBankAccount;

    /**
     * 我方抬头
     */
    private String ourCompanyName;

    /**
     * 付款金额
     */
    private BigDecimal payAmount;

    /**
     * 付款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payDate;

    /**
     * 费用类型
     */
    private String costMode;

    /**
     * 企业账套ID
     */
    private Long enterpriseId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 附件ID
     */
    private String fileId;

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

    public String getContractIds() {
        return contractIds;
    }

    public void setContractIds(String contractIds) {
        this.contractIds = contractIds;
    }

    public String getReceiveCompanyName() {
        return receiveCompanyName;
    }

    public void setReceiveCompanyName(String receiveCompanyName) {
        this.receiveCompanyName = receiveCompanyName;
    }

    public String getReceiveBankName() {
        return receiveBankName;
    }

    public void setReceiveBankName(String receiveBankName) {
        this.receiveBankName = receiveBankName;
    }

    public String getReceiveBankAccount() {
        return receiveBankAccount;
    }

    public void setReceiveBankAccount(String receiveBankAccount) {
        this.receiveBankAccount = receiveBankAccount;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public String getCostMode() {
        return costMode;
    }

    public void setCostMode(String costMode) {
        this.costMode = costMode;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    @Override
    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getFileId() {
        return fileId;
    }

    @Override
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Long getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(Long applyUserId) {
        this.applyUserId = applyUserId;
    }

    public String getApplyUserName() {
        return applyUserName;
    }

    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }
}
