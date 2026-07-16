package com.spt.bas.client.entity;


import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * PICC日志
 *
 */
@Entity
@Table(name = "t_picc_log")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PiccLog extends IdEntity {

	private static final long serialVersionUID = 2504471598906465566L;

	/**
	 * 请求类型：0-赊销；1-回款；2-查询
	 */
	private String requestType;

	/**
	 * 请求状态
	 */
	private String status = "0";

	/**
	 * 提示信息
	 */
	private String message;

	/**
	 * 请求的合同编号
	 */
	private String contractNo;

	/**
	 * 请求的合同ID
	 */
	private Long contractId;

	/**
	 * 受保险 公司
	 */
	private String companyName;

	/**
	 * 请求报文
	 */
	private String requestXml;

	/**
	 * 返回报文
	 */
	private String responseXml;

	public PiccLog() {
	}

	public PiccLog(String requestType, String status, String message, String contractNo, Long contractId, String companyName, String requestXml, String responseXml) {
		this.requestType = requestType;
		this.status = status;
		this.message = message;
		this.contractNo = contractNo;
		this.contractId = contractId;
		this.companyName = companyName;
		this.requestXml = requestXml;
		this.responseXml = responseXml;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getRequestXml() {
		return requestXml;
	}

	public void setRequestXml(String requestXml) {
		this.requestXml = requestXml;
	}

	public String getResponseXml() {
		return responseXml;
	}

	public void setResponseXml(String responseXml) {
		this.responseXml = responseXml;
	}
}
