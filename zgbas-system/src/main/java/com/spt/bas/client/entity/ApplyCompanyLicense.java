package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name ="t_apply_company_license")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyCompanyLicense extends IdEntity implements IPmEntity {
    private static final long serialVersionUID = 8833312958431089250L;

    /**
     * 申请人id
     */
    private Long applyUserId;

    /**
     * 申请人
     */
    private String applyUserName;

    /**
     * 所属区域
     */
    private String ownRegion;

    /**
     * 申请人所属部门
     */
    private Long deptId;

    /**
     * 申请时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date applyDate;


    /**
     * 状态
     */
    private String status;

    /**
     * 我方企业code
     */
    private String companyCode;

    /**
     * 我方企业名称
     */
    private String companyName;

    /**
     * 用途
     */
    private String useType;

    /**
     * 其他用途
     */
    private String otherType;

    /**
     * 使用期限
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date periodDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 附件类型
     */
    private String fileType;

    /**
     * 附件id
     */
    private String fileId;


    /**
     * 审批单ID
     */
    private Long approveId;

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getOwnRegion() {
        return ownRegion;
    }

    public void setOwnRegion(String ownRegion) {
        this.ownRegion = ownRegion;
    }

    public Long getApproveId() {
        return approveId;
    }

    @Override
    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    @Override
    public Date getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
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

    public Date getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }


    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getUseType() {
        return useType;
    }

    public void setUseType(String useType) {
        this.useType = useType;
    }

    public String getOtherType() {
        return otherType;
    }

    public void setOtherType(String otherType) {
        this.otherType = otherType;
    }

    public Date getPeriodDate() {
        return periodDate;
    }

    public void setPeriodDate(Date periodDate) {
        this.periodDate = periodDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileId() {
        return fileId;
    }

    @Override
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
