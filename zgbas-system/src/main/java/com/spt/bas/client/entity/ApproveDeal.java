package com.spt.bas.client.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.tools.jpa.vo.IdEntity;

/**
 * 待办事项表
 *
 */
@Entity
@Table(name = "t_approve_deal")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApproveDeal extends IdEntity{

	private static final long serialVersionUID = 1L;
	private	Long	enterpriseId;		//	企业账套ID
	private	String	relaUserId;			//	责任人ID
	private	String	dealType;			//	类型
	private	Long	relationId;			//	关联ID
	private	String	relationTable;		//	关联表
	private	String	subject;			//	摘要
	private	String	remark;				//	备注
	private	long	createdUserId;		//	创建人ID
	private String processCode;			// 流程代码
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getRelaUserId() {
		return relaUserId;
	}
	public void setRelaUserId(String relaUserId) {
		this.relaUserId = relaUserId;
	}
	public String getDealType() {
		return dealType;
	}
	public void setDealType(String dealType) {
		this.dealType = dealType;
	}
	public Long getRelationId() {
		return relationId;
	}
	public void setRelationId(Long relationId) {
		this.relationId = relationId;
	}
	public String getRelationTable() {
		return relationTable;
	}
	public void setRelationTable(String relationTable) {
		this.relationTable = relationTable;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public long getCreatedUserId() {
		return createdUserId;
	}
	public void setCreatedUserId(long createdUserId) {
		this.createdUserId = createdUserId;
	}
	public String getProcessCode() {
		return processCode;
	}
	public void setProcessCode(String processCode) {
		this.processCode = processCode;
	}



}
