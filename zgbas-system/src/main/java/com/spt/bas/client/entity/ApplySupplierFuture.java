package com.spt.bas.client.entity;

import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 供应商远期申请
 */
@Entity
@Table(name = "t_apply_supplier_future")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplySupplierFuture extends IdEntity implements IPmEntity {

    private Long approveId;
    private String status;
    private Long companyId;
    private String companyName;
    private Long applyUserId;
    private String applyUserName;
    private Long enterpriseId;
    private String fileId;
    private String supplierFuture; // 是否允许供应商远期合同

    private Long deptId;

    @Override
    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public Long getApproveId() {
        return approveId;
    }

    public String getStatus() {
        return status;
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

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    @Override
    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getFileId() {
        return fileId;
    }

    @Override
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getSupplierFuture() {
        return supplierFuture;
    }

    public void setSupplierFuture(String supplierFuture) {
        this.supplierFuture = supplierFuture;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }
}
