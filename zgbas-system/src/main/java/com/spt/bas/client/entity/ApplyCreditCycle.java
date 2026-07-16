package com.spt.bas.client.entity;

import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-01-12 11:32
 */
@Entity
@Table(name = "t_apply_credit_cycle")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyCreditCycle extends IdEntity implements IPmEntity {

    private static final long serialVersionUID = 7482390152883071922L;

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

    private Long companyId;

    private String applySource;

    private Long creditDays;



    public String getStatus() {
        return status;
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

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getApplySource() {
        return applySource;
    }

    public void setApplySource(String applySource) {
        this.applySource = applySource;
    }

    public Long getCreditDays() {
        return creditDays;
    }

    public void setCreditDays(Long creditDays) {
        this.creditDays = creditDays;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }
}
