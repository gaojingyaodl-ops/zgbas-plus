package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
@Table(name = "t_apply_pay")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyPay extends IdEntity implements IPmEntity {

    private static final long serialVersionUID = -4371299419388739800L;

    /**
     * 业务编号
     */
    private String businessNo;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 付款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payDate;

    /**
     * 合同总价
     */
    private BigDecimal totalAmount;

    /**
     * 已付金额
     */
    private BigDecimal payedAmount;

    /**
     * 未付款金额
     */
    private BigDecimal unpayedAmount;

    /**
     * 付款类型
     * A-全款
     * B-定金
     * P-追加保证金
     * R-尾款
     * T-退保
     * Z-逐笔
     * C-部分
     */
    private String payType;

    /**
     * 付款金额
     */
    private BigDecimal payAmount;

    /**
     * 收款公司ID
     */
    private Long companyId;

    /**
     * 收款公司名称
     */
    private String companyName;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 银行账号
     */
    private String bankAccount;

    /**
     * 付款时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payTime;

    /**
     * 支付方式
     */
    private String payMode;

    /**
     * 票证期限
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date ticketDueTime;

    /**
     * 票据到期时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date dueTime;

    /**
     * 状态
     * N-新增
     * A-审批中
     * B-驳回
     * D-完成
     * C-撤回
     */
    private String status;

    /**
     * 审批ID
     */
    private Long approveId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 企业账套ID
     */
    private Long enterpriseId;

    /**
     * 申请编号
     */
    private String applyNo;

    /**
     * 附件ID
     */
    private String fileId;

    /**
     * 我方抬头
     */
    private String ourCompanyName;

    /**
     * 定金金额
     */
    private BigDecimal bondAmount;

    /**
     * 是否需要风控审批
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean riskApproveFlg;

    /**
     * 附件类型
     */
    private Long fileTypeId;

    /**
     * 业务类型
     * DCSX:代采赊销
     */
    private String businessType;

    /**
     * 部门Id
     */
    private Long deptId;

    /**
     * 保证金金额
     */
    private BigDecimal factorAmount = BigDecimal.ZERO;

    /**
     * 是否按批次批量拆分申请
     */
    private Boolean batchPayApplyFlg = false;

    /**
     * 是否全双签
     */
    private Boolean allSealFlg = false;

    /**
     * 超额标识
     */
    private Boolean overageFlg = false;

    /**
     * 超额提示
     */
    private String overageMessage;

    /**
     * 所属区域
     */
    private String owningRegion;

    public BigDecimal getFactorAmount() {
        return factorAmount;
    }

    public void setFactorAmount(BigDecimal factorAmount) {
        this.factorAmount = factorAmount;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getBusinessNo() {
        return businessNo;
    }

    public void setBusinessNo(String businessNo) {
        this.businessNo = businessNo;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPayedAmount() {
        return payedAmount;
    }

    public void setPayedAmount(BigDecimal payedAmount) {
        this.payedAmount = payedAmount;
    }

    public BigDecimal getUnpayedAmount() {
        return unpayedAmount;
    }

    public void setUnpayedAmount(BigDecimal unpayedAmount) {
        this.unpayedAmount = unpayedAmount;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
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

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public Date getTicketDueTime() {
        return ticketDueTime;
    }

    public void setTicketDueTime(Date ticketDueTime) {
        this.ticketDueTime = ticketDueTime;
    }

    public Date getDueTime() {
        return dueTime;
    }

    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getApproveId() {
        return approveId;
    }

    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String approveNo) {
        this.applyNo = approveNo;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public BigDecimal getBondAmount() {
        return bondAmount;
    }

    public void setBondAmount(BigDecimal bondAmount) {
        this.bondAmount = bondAmount;
    }

    public Boolean getRiskApproveFlg() {
        return riskApproveFlg;
    }

    public void setRiskApproveFlg(Boolean riskApproveFlg) {
        this.riskApproveFlg = riskApproveFlg;
    }

    public Long getFileTypeId() {
        return fileTypeId;
    }

    public void setFileTypeId(Long fileTypeId) {
        this.fileTypeId = fileTypeId;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Boolean getBatchPayApplyFlg() {
        return batchPayApplyFlg;
    }

    public void setBatchPayApplyFlg(Boolean batchPayApplyFlg) {
        this.batchPayApplyFlg = batchPayApplyFlg;
    }

    public Boolean getAllSealFlg() {
        return allSealFlg;
    }

    public void setAllSealFlg(Boolean allSealFlg) {
        this.allSealFlg = allSealFlg;
    }

    public Boolean getOverageFlg() {
        return overageFlg;
    }

    public void setOverageFlg(Boolean overageFlg) {
        this.overageFlg = overageFlg;
    }

    public String getOverageMessage() {
        return overageMessage;
    }

    public void setOverageMessage(String overageMessage) {
        this.overageMessage = overageMessage;
    }

    public String getOwningRegion() {
        return owningRegion;
    }

    public void setOwningRegion(String owningRegion) {
        this.owningRegion = owningRegion;
    }
}
