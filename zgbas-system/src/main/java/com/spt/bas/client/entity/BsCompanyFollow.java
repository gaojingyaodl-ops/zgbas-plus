package com.spt.bas.client.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.tools.jpa.vo.IdEntity;
/**
 * 企业跟进记录
 */
@Entity
@Table(name = "t_bs_company_follow")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsCompanyFollow extends IdEntity{

	private static final long serialVersionUID = 7032445130854547582L;
	private Long companyId;			//企业id
	private String followType;		//方式（S-短信，P-电话，Q-QQ，W-微信）
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
	public String getFollowType() {
		return followType;
	}
	public void setFollowType(String followType) {
		this.followType = followType;
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
