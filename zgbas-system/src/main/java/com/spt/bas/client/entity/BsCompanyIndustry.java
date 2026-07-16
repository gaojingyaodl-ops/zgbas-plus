package com.spt.bas.client.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.tools.jpa.vo.IdEntity;

/**
 * 客户行业分类表
 *
 */
@Entity
@Table(name = "t_bs_company_industry")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsCompanyIndustry extends IdEntity{

	private static final long serialVersionUID = 4075780814285807029L;
	
	private String parentIndustryId;
	private String industryCode;
	private String industryName;
	private Integer grand;
	public String getParentIndustryId() {
		return parentIndustryId;
	}
	public void setParentIndustryId(String parentIndustryId) {
		this.parentIndustryId = parentIndustryId;
	}
	public String getIndustryCode() {
		return industryCode;
	}
	public void setIndustryCode(String industryCode) {
		this.industryCode = industryCode;
	}
	public String getIndustryName() {
		return industryName;
	}
	public void setIndustryName(String industryName) {
		this.industryName = industryName;
	}
	public Integer getGrand() {
		return grand;
	}
	public void setGrand(Integer grand) {
		this.grand = grand;
	}
	
}
