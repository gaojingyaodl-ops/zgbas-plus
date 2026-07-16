package com.spt.bas.client.entity;


import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;

/**
 * PICC推送报文接口，赊销申请
 * @author dengyanhua
 *
 */
@Entity
@Table(name = "t_picc_push_data")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PiccPushData extends IdEntity {

	private static final long serialVersionUID = 8450577886386845143L;
	/**
	 * 请求类别
	 */
	private String reuqestType;
	/**
	 * 请求报文体
	 */
	private String requestXml;
	/**
	 * 请求的url
	 */
	private String requestUrl;
	/**
	 * 请求状态
	 */
	private String status = "0";

	/**
	 * 次数
	 */
	private Long dataCount;
	/**
	 * 请求的合同编号
	 */
	private String contractNo;
	/**
	 * 请求的合同ID
	 */
	private Long contractId;
	/**
	 * 回调信息
	 */
	private String message;
	/**
	 * 订单号
	 */
	private String responseNo;

	/**
	 * 发送状态 1 人保发送成功 2人保 发送失败 3 回款正常 4 赊销申请批复成功
	 */
	private String sendCode;

	/**
	 * 申請号
	 */
	private String declaraTionFormno;

	/**
	 * 赊销金额
	 */
	private BigDecimal transAmount = BigDecimal.ZERO;

	/**
	 * 受保险 公司
	 */
	private String companyName;

	/**
	 *  可用金额
	 */
	private BigDecimal piccAvailableAmount = BigDecimal.ZERO;

	/**
	 * 人保授信总额
	 */
	private BigDecimal piccLineCreditAmount = BigDecimal.ZERO;

	/**
	 *	状态 1:成功 0:退回
	 */
	private String approveFlag;

	/**
	 * 申请类型
	 * 0：赊销
	 * 1：回款
	 */
	private String approveType;

	/**
	 * 是否限额 0：限额 1：非限额
	 */
	private String limitFlag;

	private String serialNumber;

	private String riskPiccCode;

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	@Transient
	public BigDecimal getPiccLineCreditAmount() {
		return piccLineCreditAmount;
	}

	public void setPiccLineCreditAmount(BigDecimal piccLineCreditAmount) {
		this.piccLineCreditAmount = piccLineCreditAmount;
	}
	@Transient
	public BigDecimal getPiccAvailableAmount() {
		return piccAvailableAmount;
	}

	public void setPiccAvailableAmount(BigDecimal piccAvailableAmount) {
		this.piccAvailableAmount = piccAvailableAmount;
	}

	public BigDecimal getTransAmount() {
		return transAmount;
	}

	public void setTransAmount(BigDecimal transAmount) {
		this.transAmount = transAmount;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}


	public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	public String getDeclaraTionFormno() {
		return declaraTionFormno;
	}

	public void setDeclaraTionFormno(String declaraTionFormno) {
		this.declaraTionFormno = declaraTionFormno;
	}

	public String getReuqestType() {
		return reuqestType;
	}

	public void setReuqestType(String reuqestType) {
		this.reuqestType = reuqestType;
	}

	public String getRequestXml() {
		return requestXml;
	}

	public void setRequestXml(String requestXml) {
		this.requestXml = requestXml;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getDataCount() {
		return dataCount;
	}

	public void setDataCount(Long dataCount) {
		this.dataCount = dataCount;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getResponseNo() {
		return responseNo;
	}

	public void setResponseNo(String responseNo) {
		this.responseNo = responseNo;
	}

	public String getApproveFlag() {
		return approveFlag;
	}

	public void setApproveFlag(String approveFlag) {
		this.approveFlag = approveFlag;
	}

	public String getApproveType() {
		return approveType;
	}

	public void setApproveType(String approveType) {
		this.approveType = approveType;
	}

	public String getLimitFlag() {
		return limitFlag;
	}

	public void setLimitFlag(String limitFlag) {
		this.limitFlag = limitFlag;
	}
}
