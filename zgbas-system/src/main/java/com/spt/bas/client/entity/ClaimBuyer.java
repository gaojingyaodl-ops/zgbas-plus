package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 限额批复买方信息报文
 */
@Entity
@Table(name = "t_picc_claimbuyer")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ClaimBuyer extends IdEntity {

	private static final long serialVersionUID = 6330631828677838479L;


	public String sender;//发送者
	public String receiver;//接收方
	public String CreationDatetime;//创建时间
	public String messageType;//传入操作类型
	public String messageStatus;//信息状态
	public String version;//版本号
	public String documentId;//交易的唯一标示
	public String usageIndicator;//标识

	public String noticeSerialNo;// 批复号
	public String corpSerialNo;// 流水号
	public String approveFlag;// 批复类型
	public String unacceptReason;// 退回原因
	public String notifyTime;// 操作时间
	public String clientNo;// 买方代码
	public String buyerNo;// 买方PICCCODE
	public String chnName;// 买方中文名称
	public String engName;// 买方英文名称
	public String countryCode;// 买方国家代码
	public String engAddress;// 买方注册地址
	public String chnAddress;// 买方营业地址
	public String regAddress;// 买方注册地址
	public String regNo;// 买方注册号
	public String tel;// 买方电话
	public String setDate;// 买方成立日期
	public String regYear;// 买方成立日期
	public String corpoRation;// 买方法人代表



	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getCreationDatetime() {
		return CreationDatetime;
	}

	public void setCreationDatetime(String creationDatetime) {
		CreationDatetime = creationDatetime;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getMessageStatus() {
		return messageStatus;
	}

	public void setMessageStatus(String messageStatus) {
		this.messageStatus = messageStatus;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getUsageIndicator() {
		return usageIndicator;
	}

	public void setUsageIndicator(String usageIndicator) {
		this.usageIndicator = usageIndicator;
	}

	public String getNoticeSerialNo() {
		return noticeSerialNo;
	}

	public void setNoticeSerialNo(String noticeSerialNo) {
		this.noticeSerialNo = noticeSerialNo;
	}

	public String getCorpSerialNo() {
		return corpSerialNo;
	}

	public void setCorpSerialNo(String corpSerialNo) {
		this.corpSerialNo = corpSerialNo;
	}

	public String getApproveFlag() {
		return approveFlag;
	}

	public void setApproveFlag(String approveFlag) {
		this.approveFlag = approveFlag;
	}

	public String getUnacceptReason() {
		return unacceptReason;
	}

	public void setUnacceptReason(String unacceptReason) {
		this.unacceptReason = unacceptReason;
	}

	public String getNotifyTime() {
		return notifyTime;
	}

	public void setNotifyTime(String notifyTime) {
		this.notifyTime = notifyTime;
	}

	public String getClientNo() {
		return clientNo;
	}

	public void setClientNo(String clientNo) {
		this.clientNo = clientNo;
	}

	public String getBuyerNo() {
		return buyerNo;
	}

	public void setBuyerNo(String buyerNo) {
		this.buyerNo = buyerNo;
	}

	public String getChnName() {
		return chnName;
	}

	public void setChnName(String chnName) {
		this.chnName = chnName;
	}

	public String getEngName() {
		return engName;
	}

	public void setEngName(String engName) {
		this.engName = engName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getEngAddress() {
		return engAddress;
	}

	public void setEngAddress(String engAddress) {
		this.engAddress = engAddress;
	}

	public String getChnAddress() {
		return chnAddress;
	}

	public void setChnAddress(String chnAddress) {
		this.chnAddress = chnAddress;
	}

	public String getRegAddress() {
		return regAddress;
	}

	public void setRegAddress(String regAddress) {
		this.regAddress = regAddress;
	}

	public String getRegNo() {
		return regNo;
	}

	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getSetDate() {
		return setDate;
	}

	public void setSetDate(String setDate) {
		this.setDate = setDate;
	}

	public String getRegYear() {
		return regYear;
	}

	public void setRegYear(String regYear) {
		this.regYear = regYear;
	}

	public String getCorpoRation() {
		return corpoRation;
	}

	public void setCorpoRation(String corpoRation) {
		this.corpoRation = corpoRation;
	}

}
