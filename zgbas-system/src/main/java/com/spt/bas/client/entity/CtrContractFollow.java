package com.spt.bas.client.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.spt.tools.jpa.vo.IdEntity;

/**
 * 合同跟踪记录表  (逾期通知回复)
 *  
 */
@Entity
@Table(name = "t_ctr_contract_follow")
public class CtrContractFollow extends IdEntity{
	
	private static final long serialVersionUID = 1L;
	private Long enterpriseId;		//企业账套ID
	private Long ctrContractId;		//合同ID
	private String respUserId;		//责任人ID
	private Long createUserId;		//创建人ID
	private String createUserName;	//创建人姓名
	private String notifyContent;	//通知内容
	private String contractStatus;	//合同状态
	private String status;			//状态  N-未回复，D-已回复
	private String replyContent;	//回复内容
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public Long getCtrContractId() {
		return ctrContractId;
	}
	public void setCtrContractId(Long ctrContractId) {
		this.ctrContractId = ctrContractId;
	}
	public String getRespUserId() {
		return respUserId;
	}
	public void setRespUserId(String respUserId) {
		this.respUserId = respUserId;
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
	public String getNotifyContent() {
		return notifyContent;
	}
	public void setNotifyContent(String notifyContent) {
		this.notifyContent = notifyContent;
	}
	public String getContractStatus() {
		return contractStatus;
	}
	public void setContractStatus(String contractStatus) {
		this.contractStatus = contractStatus;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getReplyContent() {
		return replyContent;
	}
	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}
	

}
