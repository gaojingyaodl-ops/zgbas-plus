package com.spt.bas.client.entity;

import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 公司准入申请表
 *
 */
@Entity
@Table(name = "t_bs_company_allowed")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsCompanyAllowed extends IdEntity implements IPmEntity {

    private static final long serialVersionUID = -5730809980670093151L;
    private Long companyId;             //企业ID
    private String companyName;         //公司名称
    private Long approveId;				//审批ID
    private String allowed;             //是否准入，Y、准入，N、禁止，NEW、新增
    private String creditRating;        //信用等级 W-白名单 G-灰名单 B-黑名单
    private String companyCategory;     //企业性质
    private Long enterpriseId;			//企业账套ID
    private String remark;				//备注
    private Long applyUserId;			//申请人ID
    private String applyUserName;		//申请人名称
    private Long wxUserId;            // 小程序用户id
    private String applySource;              // 审批发起来源

    private Long deptId;         //部门Id

    @Override
    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    @Override
    public void setStatus(String status) {

    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getAllowed() {
        return allowed;
    }

    public void setAllowed(String allowed) {
        this.allowed = allowed;
    }

    public String getCreditRating() {
        return creditRating;
    }

    public void setCreditRating(String creditRating) {
        this.creditRating = creditRating;
    }

    public String getCompanyCategory() {
        return companyCategory;
    }

    public void setCompanyCategory(String companyCategory) {
        this.companyCategory = companyCategory;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getWxUserId() {
        return wxUserId;
    }

    public void setWxUserId(Long wxUserId) {
        this.wxUserId = wxUserId;
    }

    public String getApplySource() {
        return applySource;
    }

    public void setApplySource(String applySource) {
        this.applySource = applySource;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }
}