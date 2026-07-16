package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "t_ctr_contract_ophis")
public class CtrContractOphis extends IdEntity{
	
	/**
	 * 合同-状态历史表
	 */
	private static final long serialVersionUID = -414150608183832769L;
	
	private Long enterpriseId; //企业账套Id
	private Long ctrContractId;//合同id
	private Long createUserId; //创建用户ID
	private String createUserName;//创建用户名
	private String contractStatus;//合同状态
	private String remark;//备注
	private String processName; // 最近审批人姓名
	private Long approveId;// 审批ID
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date happenDate;//发生日期
	/**
	 * 合同分类，CTR-普通，DCSX-代采赊销合同
	 */
	private String contractGroup;
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
	public String getContractStatus() {
		return contractStatus;
	}
	public void setContractStatus(String contractStatus) {
		this.contractStatus = contractStatus;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	public Long getApproveId() {
		return approveId;
	}
	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}
	public Date getHappenDate() {
		return happenDate;
	}
	public void setHappenDate(Date happenDate) {
		this.happenDate = happenDate;
	}

	public String getContractGroup() {
		return contractGroup;
	}

	public void setContractGroup(String contractGroup) {
		this.contractGroup = contractGroup;
	}
}
