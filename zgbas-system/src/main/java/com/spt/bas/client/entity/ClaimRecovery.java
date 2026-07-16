package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 返回数据申请失败，打回修改，退回不予受理，办结
 */
@Entity
@Table(name = "t_picc_claimrecovery")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ClaimRecovery  extends IdEntity {

	private static final long serialVersionUID = -5298085129002624336L;

	public String sender;//发送者
	public String receiver;//接收方
	public String creationDatetime;//创建时间
	public String messageType;//传入操作类型
	public String messageStatus;//信息状态
	public String version;//版本号
	public String documentId;//交易的唯一标示
	public String usageIndicator;//标识

	public String bussinessNo;// 保单号

	public String insuredPiccCode;// 被保险人PICCCODE
	public String riskCompName;// 买方名称
	public String riskCompAddress;// 买方地址
	public String paidTerm;// 申请 期限
	public String appliAmount;//申请 金额

	public String noticeSerialNo;//限额号
	public String corpSerialNo;//企业内部限额唯一标识[即流水号]
	public String ifLc;//是否LC申请
	public String approveFlag;//审批标志 1 通过 2 失败
	public String unAcceptReason;//申请退回/不通过原因
	public String notifyTime;//最新通知时间
	public String clientNo;//客户标识，信保通编号
	public String policyNo;//保险单号
	public String quotaNo;//限额编号
	public String quotaApplyNo;//限额申请编号
	public String buyerNo;//保买方代码
	public String corpBuyerNo;//企业买方代码/企业内部买方唯一标识
	public String payMode;//支付方式
	public String payTerm;//批复信用期限
	public String quotaSum;// 批复信用限额

	public String lcNo;//信用证号
	public String bankSwift;//银行SWIFT
	public String bankEngName;//银行英文名称
	public String corpBankNo;//企业银行代码/企业内部银行唯一标识
	public String quotaState;//限额状态
	public String effectDate;//生效日期
	public String lapseDate;//失效日期
	public String auditDate;//批复日期
	public String adcondition;//特别生效条件
	public String billNote;//批复说明
	public String refuseRate;//赔付比例
	public String otherRate;//其它商业风险赔偿比例
	public String lcRate;//信用证下赔付比例
	public String poliRate;//政治风险赔付比例
	public String ifRepeat;//是否循环使用
	public String approveType;//批复类型代码
	public String idLespan;//闲置期
	public String updateFrom;//那个角色让客户修改的
	public String item1;//备用字段
	public String item2;//备用字段
	public String item3;//备用字段
	public String item4;//备用字段
	public String item5;//备用字段


	public String getBussinessNo() {
		return bussinessNo;
	}
	public void setBussinessNo(String bussinessNo) {
		this.bussinessNo = bussinessNo;
	}
	public String getInsuredPiccCode() {
		return insuredPiccCode;
	}
	public void setInsuredPiccCode(String insuredPiccCode) {
		this.insuredPiccCode = insuredPiccCode;
	}
	public String getRiskCompName() {
		return riskCompName;
	}
	public void setRiskCompName(String riskCompName) {
		this.riskCompName = riskCompName;
	}
	public String getRiskCompAddress() {
		return riskCompAddress;
	}
	public void setRiskCompAddress(String riskCompAddress) {
		this.riskCompAddress = riskCompAddress;
	}
	public String getPaidTerm() {
		return paidTerm;
	}
	public void setPaidTerm(String paidTerm) {
		this.paidTerm = paidTerm;
	}
	public String getAppliAmount() {
		return appliAmount;
	}
	public void setAppliAmount(String appliAmount) {
		this.appliAmount = appliAmount;
	}
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
		return creationDatetime;
	}
	public void setCreationDatetime(String creationDatetime) {
		this.creationDatetime = creationDatetime;
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
	public String getIfLc() {
		return ifLc;
	}
	public void setIfLc(String ifLc) {
		this.ifLc = ifLc;
	}
	public String getApproveFlag() {
		return approveFlag;
	}
	public void setApproveFlag(String approveFlag) {
		this.approveFlag = approveFlag;
	}
	public String getUnAcceptReason() {
		return unAcceptReason;
	}
	public void setUnAcceptReason(String unAcceptReason) {
		this.unAcceptReason = unAcceptReason;
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
	public String getPolicyNo() {
		return policyNo;
	}
	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}
	public String getQuotaNo() {
		return quotaNo;
	}
	public void setQuotaNo(String quotaNo) {
		this.quotaNo = quotaNo;
	}
	public String getQuotaApplyNo() {
		return quotaApplyNo;
	}
	public void setQuotaApplyNo(String quotaApplyNo) {
		this.quotaApplyNo = quotaApplyNo;
	}
	public String getBuyerNo() {
		return buyerNo;
	}
	public void setBuyerNo(String buyerNo) {
		this.buyerNo = buyerNo;
	}
	public String getCorpBuyerNo() {
		return corpBuyerNo;
	}
	public void setCorpBuyerNo(String corpBuyerNo) {
		this.corpBuyerNo = corpBuyerNo;
	}
	public String getPayMode() {
		return payMode;
	}
	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}
	public String getPayTerm() {
		return payTerm;
	}
	public void setPayTerm(String payTerm) {
		this.payTerm = payTerm;
	}
	public String getQuotaSum() {
		return quotaSum;
	}
	public void setQuotaSum(String quotaSum) {
		this.quotaSum = quotaSum;
	}
	public String getLcNo() {
		return lcNo;
	}
	public void setLcNo(String lcNo) {
		this.lcNo = lcNo;
	}
	public String getBankSwift() {
		return bankSwift;
	}
	public void setBankSwift(String bankSwift) {
		this.bankSwift = bankSwift;
	}
	public String getBankEngName() {
		return bankEngName;
	}
	public void setBankEngName(String bankEngName) {
		this.bankEngName = bankEngName;
	}
	public String getCorpBankNo() {
		return corpBankNo;
	}
	public void setCorpBankNo(String corpBankNo) {
		this.corpBankNo = corpBankNo;
	}
	public String getQuotaState() {
		return quotaState;
	}
	public void setQuotaState(String quotaState) {
		this.quotaState = quotaState;
	}
	public String getEffectDate() {
		return effectDate;
	}
	public void setEffectDate(String effectDate) {
		this.effectDate = effectDate;
	}
	public String getLapseDate() {
		return lapseDate;
	}
	public void setLapseDate(String lapseDate) {
		this.lapseDate = lapseDate;
	}
	public String getAuditDate() {
		return auditDate;
	}
	public void setAuditDate(String auditDate) {
		this.auditDate = auditDate;
	}
	public String getAdcondition() {
		return adcondition;
	}
	public void setAdcondition(String adcondition) {
		this.adcondition = adcondition;
	}
	public String getBillNote() {
		return billNote;
	}
	public void setBillNote(String billNote) {
		this.billNote = billNote;
	}
	public String getRefuseRate() {
		return refuseRate;
	}
	public void setRefuseRate(String refuseRate) {
		this.refuseRate = refuseRate;
	}
	public String getOtherRate() {
		return otherRate;
	}
	public void setOtherRate(String otherRate) {
		this.otherRate = otherRate;
	}
	public String getLcRate() {
		return lcRate;
	}
	public void setLcRate(String lcRate) {
		this.lcRate = lcRate;
	}
	public String getPoliRate() {
		return poliRate;
	}
	public void setPoliRate(String poliRate) {
		this.poliRate = poliRate;
	}
	public String getIfRepeat() {
		return ifRepeat;
	}
	public void setIfRepeat(String ifRepeat) {
		this.ifRepeat = ifRepeat;
	}
	public String getApproveType() {
		return approveType;
	}
	public void setApproveType(String approveType) {
		this.approveType = approveType;
	}
	public String getIdLespan() {
		return idLespan;
	}
	public void setIdLespan(String idLespan) {
		this.idLespan = idLespan;
	}
	public String getUpdateFrom() {
		return updateFrom;
	}
	public void setUpdateFrom(String updateFrom) {
		this.updateFrom = updateFrom;
	}
	public String getItem1() {
		return item1;
	}
	public void setItem1(String item1) {
		this.item1 = item1;
	}
	public String getItem2() {
		return item2;
	}
	public void setItem2(String item2) {
		this.item2 = item2;
	}
	public String getItem3() {
		return item3;
	}
	public void setItem3(String item3) {
		this.item3 = item3;
	}
	public String getItem4() {
		return item4;
	}
	public void setItem4(String item4) {
		this.item4 = item4;
	}
	public String getItem5() {
		return item5;
	}
	public void setItem5(String item5) {
		this.item5 = item5;
	}




}
