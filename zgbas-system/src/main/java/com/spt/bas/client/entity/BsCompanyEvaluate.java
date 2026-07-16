package com.spt.bas.client.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.tools.jpa.vo.IdEntity;
/**
 * 企业评价记录
 */
@Entity
@Table(name = "t_bs_company_evaluate")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsCompanyEvaluate extends IdEntity{

	private static final long serialVersionUID = -5564288745680361819L;
	private Long companyId;			//企业id
	private Long grade;				//评价分数（1-10分）
	private String content;			//内容
	private Long createUserId;		//创建人id
	private String createUserName;	//创建人姓名
	private Long enterpriseId;
	
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Long getGrade() {
		return grade;
	}
	public void setGrade(Long grade) {
		this.grade = grade;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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

}
