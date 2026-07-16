package com.spt.bas.client.vo;

import java.math.BigDecimal;
import java.util.Date;

public class CtrContractOphisRequest {
	private Long id;
	private String applyType;
	private String remark;
	private Long ctrContractId;
	private boolean isCancel;
	private Long createUserId; // 创建用户ID
	private String createUserName;// 创建用户名
	private BigDecimal dealNumber; //收货数量
	private Date deliveryInTime; //收货时间
	private String fileId;
	private Date happenDate;//发生日期
	private String contractStatus;//合同状态
	private Long approveId;// 审批ID
	private String processName; // 最近审批人姓名
	/**
	 *  企业账套Id
	 */
	private Long enterpriseId;
	/**
	 * 合同分类，CTR-普通，DCSX-代采赊销合同
	 */
	private String contractGroup;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getContractStatus() {
		return contractStatus;
	}

	public void setContractStatus(String contractStatus) {
		this.contractStatus = contractStatus;
	}

	public String getApplyType() {
		return applyType;
	}

	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getCtrContractId() {
		return ctrContractId;
	}

	public void setCtrContractId(Long ctrContractId) {
		this.ctrContractId = ctrContractId;
	}

	public boolean isCancel() {
		return isCancel;
	}

	public void setCancel(boolean isCancel) {
		this.isCancel = isCancel;
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

	public BigDecimal getDealNumber() {
		return dealNumber;
	}

	public void setDealNumber(BigDecimal dealNumber) {
		this.dealNumber = dealNumber;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public Long getApproveId() {
		return approveId;
	}

	public void setApproveId(Long approveId) {
		this.approveId = approveId;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public Date getDeliveryInTime() {
		return deliveryInTime;
	}

	public void setDeliveryInTime(Date deliveryInTime) {
		this.deliveryInTime = deliveryInTime;
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
