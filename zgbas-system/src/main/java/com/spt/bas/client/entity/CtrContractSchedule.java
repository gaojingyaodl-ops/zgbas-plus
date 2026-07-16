package com.spt.bas.client.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.tools.jpa.vo.IdEntity;

/**
 * 风控待办事项表
 */
@Entity
@Table(name = "t_ctr_contract_schedule")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CtrContractSchedule extends IdEntity{

	private static final long serialVersionUID = 3791186998432936910L;
	
	private Long contractId;			//合同ID
	private String contractNo;			//合同编号
	private String scheduleType;		//待办类型
	private String subject;				//摘要
	private Long matchUserId;			//合同业务员ID
	private String matchUserName;		//合同业务员
	private String status;				//状态	N-待处理，D-已处理
	private Long enterpriseId;			//企业账套ID
	private String disposeUserName;		//处理人
	private String remark;				//备注
	public Long getContractId() {
		return contractId;
	}
	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public Long getMatchUserId() {
		return matchUserId;
	}
	public void setMatchUserId(Long matchUserId) {
		this.matchUserId = matchUserId;
	}
	public String getMatchUserName() {
		return matchUserName;
	}
	public void setMatchUserName(String matchUserName) {
		this.matchUserName = matchUserName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getDisposeUserName() {
		return disposeUserName;
	}
	public void setDisposeUserName(String disposeUserName) {
		this.disposeUserName = disposeUserName;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getScheduleType() {
		return scheduleType;
	}
	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}
	
}
