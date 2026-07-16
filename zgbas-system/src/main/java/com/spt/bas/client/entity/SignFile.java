package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "t_sign_file")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SignFile extends IdEntity {
    private static final long serialVersionUID = 985262208661963792L;
    /**
     * 类型
     * 1-短链接签署
     * 2-自动签署
     */
    private String signType = "1";

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件附件ID
     */
    private String fileId;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 签署日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date signDate;

    /**
     * 状态
     */
    private String signStatus;

    /**
     * 签署公司
     */
    private String companyNames;

    /**
     * 安心签合同号
     */
    private String cfcaContractNo;
    private String file;
    private Boolean enableFlg = true;

    /**
     * 关联盖章申请ID
     */
    private Long sealUsageApproveId;

    /**
     * 关联盖章申请编号
     */
    private String sealUsageApproveNo;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getSignDate() {
        return signDate;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    public String getSignStatus() {
        return signStatus;
    }

    public void setSignStatus(String signStatus) {
        this.signStatus = signStatus;
    }

    public String getCompanyNames() {
        return companyNames;
    }

    public void setCompanyNames(String companyNames) {
        this.companyNames = companyNames;
    }

    public String getCfcaContractNo() {
        return cfcaContractNo;
    }

    public void setCfcaContractNo(String cfcaContractNo) {
        this.cfcaContractNo = cfcaContractNo;
    }

    public Boolean getEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(Boolean enableFlg) {
        this.enableFlg = enableFlg;
    }

    public Long getSealUsageApproveId() {
        return sealUsageApproveId;
    }

    public void setSealUsageApproveId(Long sealUsageApproveId) {
        this.sealUsageApproveId = sealUsageApproveId;
    }

    public String getSealUsageApproveNo() {
        return sealUsageApproveNo;
    }

    public void setSealUsageApproveNo(String sealUsageApproveNo) {
        this.sealUsageApproveNo = sealUsageApproveNo;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }
}
