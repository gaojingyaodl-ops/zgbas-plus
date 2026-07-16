package com.spt.bas.client.entity;

import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-21 09:10
 */
@Entity
@Table(name = "t_apply_cfca")
public class ApplyCfca extends IdEntity implements IPmEntity {

    /**
     * 申请状态		N-新增，A-审批中，B-驳回，D-完成
     */
    private String status;

    private String fileId;

    private Long applyUserId;

    private String applyUserName;

    private Long wxUserId;

    private Long approveId;

    private Long enterpriseId;

    private String companyName;

    private Integer ukeyNumber;

    private Long companyId;

    private String applySource;

    private Long deptId;

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

    public Long getWxUserId() {
        return wxUserId;
    }

    public void setWxUserId(Long wxUserId) {
        this.wxUserId = wxUserId;
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

    public Integer getUkeyNumber() {
        return ukeyNumber;
    }

    public void setUkeyNumber(Integer ukeyNumber) {
        this.ukeyNumber = ukeyNumber;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }
}
