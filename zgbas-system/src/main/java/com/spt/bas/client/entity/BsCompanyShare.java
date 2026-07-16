package com.spt.bas.client.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.tools.jpa.vo.IdEntity;

/**
 * 企业共享表
 */
@Entity
@Table(name = "t_bs_company_share")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsCompanyShare extends IdEntity {

	private static final long serialVersionUID = 7032445130854547582L;
	private Long companyId; // 企业id
	private String remark; // 备注
	private Long sharedUserId; // 被共享人id
	private String sharedUserName; // 被共享人姓名
	private Long createUserId; // 创建人id
	private String createUserName; // 创建人姓名
	private Long enterpriseId;
	
	private String companyName;

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getSharedUserId() {
		return sharedUserId;
	}

	public void setSharedUserId(Long sharedUserId) {
		this.sharedUserId = sharedUserId;
	}

	public String getSharedUserName() {
		return sharedUserName;
	}

	public void setSharedUserName(String sharedUserName) {
		this.sharedUserName = sharedUserName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

}
