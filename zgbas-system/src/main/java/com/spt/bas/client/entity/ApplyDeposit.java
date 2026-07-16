package com.spt.bas.client.entity;

import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * <p>
 *  申请单-入金验证
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-02 14:16
 */
@Entity
@Table(name = "t_apply_deposit")
public class ApplyDeposit extends IdEntity implements IPmEntity {
    private static final long serialVersionUID = -399893882567918970L;
    private BigDecimal targetAmount;
    private BigDecimal actualAmount;
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
     *部门Id
     */
    private Long deptId;

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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    @Override
    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }
    public Long getApproveId() {
        return approveId;
    }

    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    @Basic
    @Column(name = "target_amount")
    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    @Basic
    @Column(name = "actual_amount")
    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    @Basic
    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Basic
    @Column(name = "file_id")
    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    @Basic
    @Column(name = "wx_user_id")
    public Long getWxUserId() {
        return wxUserId;
    }

    public void setWxUserId(Long wxUserId) {
        this.wxUserId = wxUserId;
    }

    @Basic
    @Column(name = "apply_user_id")
    public Long getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(Long applyUserId) {
        this.applyUserId = applyUserId;
    }

    @Basic
    @Column(name = "apply_user_name")
    public String getApplyUserName() {
        return applyUserName;
    }

    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    @Basic
    @Column(name = "remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplyDeposit that = (ApplyDeposit) o;
        return Objects.equals(targetAmount, that.targetAmount) &&
                Objects.equals(actualAmount, that.actualAmount) &&
                Objects.equals(status, that.status) &&
                Objects.equals(fileId, that.fileId) &&
                Objects.equals(wxUserId, that.wxUserId) &&
                Objects.equals(applyUserId, that.applyUserId) &&
                Objects.equals(applyUserName, that.applyUserName) &&
                Objects.equals(remark, that.remark);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetAmount, actualAmount, status, fileId, wxUserId, applyUserId, applyUserName, remark);
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }
}
