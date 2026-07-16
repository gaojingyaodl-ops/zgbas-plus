package com.spt.bas.client.vo;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ContractStatusResponseVo {
	private String contractNo;			//合同编号
	private String approveNo;			//审批编号
	private String status = "1";		//审批结果 1：成功  0： 拒绝
	private String type;				//审批类型 收款-E;出库-O;开票-N;入库-I;作废-C
	private String fileId;				//附件ID
	private String urgeBuyFileId;		//确认收货附件ID
	private String billFileId;			//发票附件ID
	private Long enterpriseId;			//企业账套ID
	private Boolean onLineFlg;
	private Boolean creditFlg;
	private String appCode;
	private String message;
	private String sendCode;			//1 投保成功  2投保失败  3 回款正常 4 回款失败
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date piccHappenDate;		//出货日期
	private BigDecimal piccAvailableAmount;//人保可用额度
	private String contractStatus;		//N-新增，S-已签约，F2-已收款，G2-已发货，V2-已开票，C-已作废
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getApproveNo() {
		return approveNo;
	}
	public void setApproveNo(String approveNo) {
		this.approveNo = approveNo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getUrgeBuyFileId() {
		return urgeBuyFileId;
	}
	public void setUrgeBuyFileId(String urgeBuyFileId) {
		this.urgeBuyFileId = urgeBuyFileId;
	}
	public String getBillFileId() {
		return billFileId;
	}
	public void setBillFileId(String billFileId) {
		this.billFileId = billFileId;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public Boolean getOnLineFlg() {
		return onLineFlg;
	}
	public void setOnLineFlg(Boolean onLineFlg) {
		this.onLineFlg = onLineFlg;
	}
	public Boolean getCreditFlg() {
		return creditFlg;
	}
	public void setCreditFlg(Boolean creditFlg) {
		this.creditFlg = creditFlg;
	}
	public String getAppCode() {
		return appCode;
	}
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSendCode() {
		return sendCode;
	}
	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}
	public Date getPiccHappenDate() {
		return piccHappenDate;
	}
	public void setPiccHappenDate(Date piccHappenDate) {
		this.piccHappenDate = piccHappenDate;
	}
	public BigDecimal getPiccAvailableAmount() {
		return piccAvailableAmount;
	}
	public void setPiccAvailableAmount(BigDecimal piccAvailableAmount) {
		this.piccAvailableAmount = piccAvailableAmount;
	}
	public String getContractStatus() {
		return contractStatus;
	}
	public void setContractStatus(String contractStatus) {
		this.contractStatus = contractStatus;
	}
	
}
