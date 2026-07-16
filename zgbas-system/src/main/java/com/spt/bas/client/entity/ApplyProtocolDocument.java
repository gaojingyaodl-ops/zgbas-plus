package com.spt.bas.client.entity;

import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 协议文件申请单
 */
@Entity
@Table(name = "t_apply_protocol_document")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyProtocolDocument extends IdEntity implements IPmEntity {

    private static final long serialVersionUID = -2023334534523068234L;

    /**
     * 协议类型
     */
    private String docType;

    /**
     * 审批ID
     */
    private Long approveId;

    /**
     * 审批单号
     */
    private String approveNo;

    /**
     * 审批状态
     */
    private String status;

    /**
     * 内容
     */
    private String content;


    /**
     * 附件
     */
    private String fileId;

    /**
     * 备注
     */
    private String remark;

    private String cfcaContractNo;

    private String signCompanyName;

    /**
     * 企业账套ID
     */
    private Long enterpriseId;

    /**
     * 审批完成是否自动签署文件
     */
    private Boolean autoSignFlag = true;

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public Long getApproveId() {
        return approveId;
    }

    @Override
    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public String getApproveNo() {
        return approveNo;
    }

    public void setApproveNo(String approveNo) {
        this.approveNo = approveNo;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileId() {
        return fileId;
    }

    @Override
    public void setFileId(String fileId) {
        this.fileId = fileId;
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

    @Override
    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getCfcaContractNo() {
        return cfcaContractNo;
    }

    public void setCfcaContractNo(String cfcaContractNo) {
        this.cfcaContractNo = cfcaContractNo;
    }

    public String getSignCompanyName() {
        return signCompanyName;
    }

    public void setSignCompanyName(String signCompanyName) {
        this.signCompanyName = signCompanyName;
    }

    public Boolean getAutoSignFlag() {
        return autoSignFlag;
    }
    public void setAutoSignFlag(Boolean autoSignFlag) {
        this.autoSignFlag = autoSignFlag;
    }
}
