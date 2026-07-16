package com.spt.bas.client.entity;
/**
 * 与代采预算的销售合同对应的服务合同
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "t_ctr_service_contract")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CtrServiceContract extends IdEntity {
    private static final long serialVersionUID = -6098160193341654841L;
    private Long approveId;                 // 审批ID
    private Long ctrContractId;             // 关联的销售合同ID
    private String serviceContractNo;       // 服务合同编号
    private Long enterpriseId;              // 企业账套ID
    private Long matchUserId;               // 业务员ID
    private String matchUserName;           // 业务员名称
    private Long deptId;                    // 部门ID
    private BigDecimal totalAmount;         // 合同总价（服务费）
    private BigDecimal rate = BigDecimal.ZERO; //销售合同服务费的费率
    private BigDecimal interestAmount = BigDecimal.ZERO;        //罚金
    private BigDecimal receiveInterestAmount = BigDecimal.ZERO; //已收罚息
    private BigDecimal interestRate = BigDecimal.ZERO;          //逾期罚金的费率
    private String fileId;                  // 合同附件ID
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date contractStartTime;         //合同开始日期（赊销开始时间）
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date contractEndTime;           // 合同结束日期（赊销结束时间）
    private int creditCycle;                // 赊销天数
    private String rateType;                // 服务合同类型（服务费先收B，或服务费后收A）
    private String payType;                 // 付款方式
    private Long companyId;                 // 对方企业ID
    private String companyName;             // 对方企业名称
    private String ourCompanyName;          // 我方企业名称
    private String remark;                  // 备注
    private String contractStatus;          // 合同状态，执行中、逾期、违约
    private Boolean orverdurFlg;            // 合同逾期标识
    private String status;                  // 数据状态
    private String contractTemplate;        // 合同模板，U表示上传的模板，其他字母表示商品通模板
    private String ctrTemplateFileId;       // 合同模板附件ID
    private Long bsTemplateContractId;      // 服务合同模板ID

    /**
     * 服务费已开票金额
     */
    private BigDecimal billedAmount = BigDecimal.ZERO;
    /**
     * 已收服务费金额
     */
    private BigDecimal dealedAmount = BigDecimal.ZERO;

    public Long getCtrContractId() {
        return ctrContractId;
    }

    public void setCtrContractId(Long ctrContractId) {
        this.ctrContractId = ctrContractId;
    }

    public String getServiceContractNo() {
        return serviceContractNo;
    }

    public void setServiceContractNo(String serviceContractNo) {
        this.serviceContractNo = serviceContractNo;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public String getMatchUserName() {
        return matchUserName;
    }

    public void setMatchUserName(String matchUserName) {
        this.matchUserName = matchUserName;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getInterestAmount() {
        return interestAmount;
    }

    public void setInterestAmount(BigDecimal interestAmount) {
        this.interestAmount = interestAmount;
    }

    public BigDecimal getReceiveInterestAmount() {
        return receiveInterestAmount;
    }

    public void setReceiveInterestAmount(BigDecimal receiveInterestAmount) {
        this.receiveInterestAmount = receiveInterestAmount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Date getContractStartTime() {
        return contractStartTime;
    }

    public void setContractStartTime(Date contractStartTime) {
        this.contractStartTime = contractStartTime;
    }

    public Date getContractEndTime() {
        return contractEndTime;
    }

    public void setContractEndTime(Date contractEndTime) {
        this.contractEndTime = contractEndTime;
    }

    public int getCreditCycle() {
        return creditCycle;
    }

    public void setCreditCycle(int creditCycle) {
        this.creditCycle = creditCycle;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
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

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public Boolean getOrverdurFlg() {
        return orverdurFlg;
    }

    public void setOrverdurFlg(Boolean orverdurFlg) {
        this.orverdurFlg = orverdurFlg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContractTemplate() {
        return contractTemplate;
    }

    public void setContractTemplate(String contractTemplate) {
        this.contractTemplate = contractTemplate;
    }

    public String getCtrTemplateFileId() {
        return ctrTemplateFileId;
    }

    public void setCtrTemplateFileId(String ctrTemplateFileId) {
        this.ctrTemplateFileId = ctrTemplateFileId;
    }

    public Long getBsTemplateContractId() {
        return bsTemplateContractId;
    }

    public void setBsTemplateContractId(Long bsTemplateContractId) {
        this.bsTemplateContractId = bsTemplateContractId;
    }

    public Long getApproveId() {
        return approveId;
    }

    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public BigDecimal getBilledAmount() {
        return billedAmount;
    }

    public void setBilledAmount(BigDecimal billedAmount) {
        this.billedAmount = billedAmount;
    }

    public BigDecimal getDealedAmount() {
        return dealedAmount;
    }

    public void setDealedAmount(BigDecimal dealedAmount) {
        this.dealedAmount = dealedAmount;
    }
}
