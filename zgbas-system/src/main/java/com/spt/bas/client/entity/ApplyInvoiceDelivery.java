package com.spt.bas.client.entity;

import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 *  发票寄送
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-20 21:17
 */
@Entity
@Table(name = "t_apply_invoice_delivery")
public class ApplyInvoiceDelivery extends IdEntity implements IPmEntity {
    private static final long serialVersionUID = 1717368173026249643L;

    /**
     * 申请状态	N-新增，A-审批中，B-驳回，D-完成
     */
    private String status;
    private String fileId;
    private Long wxUserId;
    private Long applyUserId;
    private String applyUserName;
    private String remark;

    private Long approveId;
    private Long enterpriseId;

    private String companyName;
    /**
     * 发起审批来源
     */
    private String applySource;

    private Long companyId;

    /**
     * 发票号
     */
    private String invoiceNumber;

    /**
     * 快递单号
     */
    private String expressNumber;

    /**
     * 关联合同号
     */
    private String contractNo;

    /**
     * 关联开票申请id
     */
    private Long invoiceApplyId;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系人手机号
     */
    private String contactPhone;

    /**
     * 发票寄送地址
     */
    private String contactAddress;

    /**
     * 快递公司
     */
    private String expressCompany;

    /**
     * 附件类型
     */
    private Long fileTypeId;

    /**
     * 我方抬头
     */
    private String ourCompanyName;

    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public String getFileId() {
        return fileId;
    }

    @Override
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Long getWxUserId() {
        return wxUserId;
    }

    public void setWxUserId(Long wxUserId) {
        this.wxUserId = wxUserId;
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

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    @Override
    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getApplySource() {
        return applySource;
    }

    public void setApplySource(String applySource) {
        this.applySource = applySource;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getExpressNumber() {
        return expressNumber;
    }

    public void setExpressNumber(String expressNumber) {
        this.expressNumber = expressNumber;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public Long getInvoiceApplyId() {
        return invoiceApplyId;
    }

    public void setInvoiceApplyId(Long invoiceApplyId) {
        this.invoiceApplyId = invoiceApplyId;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public String getExpressCompany() {
        return expressCompany;
    }

    public void setExpressCompany(String expressCompany) {
        this.expressCompany = expressCompany;
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
}
