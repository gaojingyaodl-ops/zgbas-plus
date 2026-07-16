package com.spt.bas.client.vo;

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
 * 收票申请Vo
 */
public class ApplyInvoiceReceivedVo extends IdEntity implements IPmEntity {

    private static final long serialVersionUID = -3040919775351047884L;
    /**
     * 合同id
     */
    private Long contractId;
    /**
     * 业务编号
     */
    private String businessNo;
    /**
     * 合同编号
     */
    private String contractNo;
    /**
     * 公司id
     */
    private Long companyId;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 已付金额
     */
    private BigDecimal payedAmount;
    /**
     * 合同总价
     */
    private BigDecimal totalAmount;
    /**
     * 发票金额
     */
    private BigDecimal invoiceAmount;
    /**
     * 已收
     */
    private BigDecimal billedAmount;
    /**
     * 进项发票号码
     */
    private String inInvoiceNo;
    /**
     * 进项发票日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date inInvoiceDate;
    /**
     * 进项记账凭证号
     */
    private String inBillNo;
    /**
     * 发票抬头
     */
    private String invoiceCompanyName;
    /**
     * 发票抬头 同 invoiceCompanyName
     */
    private String ourCompanyName;
    private String status;
    private String remark;
    private Long approveId;
    /**
     * 申请单号
     */
    private String applyNo;
    private Long enterpriseId;
    /**
     * 附件id
     */
    private String fileId;
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
     * @return
     */
    private Long deptId;

    /**
     * 业务员
     */
    private  String  matchUserName;

    public BigDecimal getBilledAmount() {
        return billedAmount;
    }

    public void setBilledAmount(BigDecimal billedAmount) {
        this.billedAmount = billedAmount;
    }

    public String getMatchUserName() {
        return matchUserName;
    }

    public void setMatchUserName(String matchUserName) {
        this.matchUserName = matchUserName;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
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

    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getInInvoiceNo() {
        return inInvoiceNo;
    }

    public void setInInvoiceNo(String inInvoiceNo) {
        this.inInvoiceNo = inInvoiceNo;
    }

    public Date getInInvoiceDate() {
        return inInvoiceDate;
    }

    public void setInInvoiceDate(Date inInvoiceDate) {
        this.inInvoiceDate = inInvoiceDate;
    }

    public String getInBillNo() {
        return inBillNo;
    }

    public void setInBillNo(String inBillNo) {
        this.inBillNo = inBillNo;
    }

    public String getInvoiceCompanyName() {
        return invoiceCompanyName;
    }

    public void setInvoiceCompanyName(String invoiceCompanyName) {
        this.invoiceCompanyName = invoiceCompanyName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public BigDecimal getPayedAmount() {
        return payedAmount;
    }

    public void setPayedAmount(BigDecimal payedAmount) {
        this.payedAmount = payedAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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

    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }


    public Long getFileTypeId() {
        return fileTypeId;
    }

    public void setFileTypeId(Long fileTypeId) {
        this.fileTypeId = fileTypeId;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
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
}
