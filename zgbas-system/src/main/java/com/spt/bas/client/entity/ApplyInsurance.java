package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 保险资料审批
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-02-19 13:29
 */
@Entity
@Table(name = "t_apply_insurance")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@DynamicInsert
@DynamicUpdate
public class ApplyInsurance extends IdEntity implements IPmEntity {
    private Long companyId;

    /**
     * 申请保险额度
     */
    private BigDecimal applyInsuranceAmount;

    /**
     * 批复保险额度
     */
    private BigDecimal replyInsuranceAmount;

    private Long applyUserId;

    private String applyUserName;

    private Boolean enableFlg;

    private String status;

    private Long approveId;

    private Long wxUserId;

    private Long enterpriseId;

    private String companyName;

    private String applySource;

    /**
     * 发送picc状态
     * 0:未开始
     * 1:已发起 审批中
     * 2:审批通过
     * 3:未通过
     * 5:保存 未发送
     */
    private String applyStatus;

    /**
     * 申请退回/不通过原因
     */
    private String unAcceptReason;

    /**
     * 发送者
     */
    private String sender;

    /**
     * 接收方
     */
    private String receiver;

    /**
     * 创建时间
     */
    private String creationDatetime;

    /**
     * 传入操作类型
     */
    private String messageType;

    /**
     * 信息状态
     */
    private String messageStatus;

    /**
     * 版本号
     */
    private String version;

    /**
     * 交易的唯一标示
     */
    private String documentId;

    /**
     * 标识
     */
    private String usageIndicator;

    /**
     * 贵部限额 唯一标示
     */
    private String corpSerialNo;

    /**
     * 保单号
     */
    private String bussinessNo;

    /**
     * 被保险人PICCCODE
     */
    private String insuredPiccCode;

    /**
     * 买方名称
     */
    private String riskCompName;

    /**
     * 买方国家
     */
    private String countryOrArea;

    /**
     * 买方地址
     */
    private String riskCompAddress;

    /**
     * 买方联系电话
     */
    private String riskPhone;

    /**
     * 买方注册号
     */
    private String riskMark;

    /**
     * 期限
     */
    private String paidTerm;

    /**
     * 金额
     */
    private String appliAmount;

    /**
     * 商品类别
     */
    private String exportTrade;

    /**
     * 商品类别
     */
    private String exportTradeName;

    /**
     * 商品类别说明
     */
    private String exportTradeInput;

    /**
     * 与该买方以往是否存在交易 0：不存在，1：存在
     */
    private String historyBusiness;

    /**
     * 与该买方以往是否存在交易
     */
    private String historyBusinessName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 申请状态 1：已调取资信报告，2：未调取资信报告
     */
    private String isReport;

    /**
     * 申请状态 已取得资信报告 未取得资信报告
     */
    private String isReportName;

    /**
     * 报告类型 1：普通，2加急
     */
    private String reportType;

    /**
     * 报告类型  普通 加急
     */
    private String reportTypeName;
    /**
     * 申请限额是否循环使用  0：否，1：是
     */
    private String isCycleUse;
    /**
     * 买方付款表现
     */
    private String riskPerformance;

    /**
     * 买方付款表现
     */
    private String riskPerformanceName;

    /**
     * 拖欠金额
     */
    private String defaultAmount;

    /**
     * 拖欠天数
     */
    private String defaultDate;

    /**
     * 当前合同总金额
     */
    private String totalAmount;

    /**
     * 限额变更原因代码
     */
    private String changeReasonCode;

    /**
     * 限额变更原因说明
     */
    private String changeReasonRemark;

    /**
     * 买方省份
     */
    private String province;

    /**
     * 买方省份
     */
    private String provinceName;

    /**
     * 交易商品
     */
    private String bargainCommodity;

    /**
     * 预计年度赊销总额（元）
     */
    private String intendingAmount;

    /**
     * 结算方式
     */
    private String payWay;

    /**
     * 最大单次出运金额
     */
    private String maxSingleAmount;

    /**
     * 是否担保
     */
    private String isVouch;

    /**
     * 是否担保
     */
    private String isVouchName;

    /**
     * 开始信用(赊销)交易年份
     */
    private String earlyCooperateYear;

    /**
     * 上年信用(赊销)交易总金额（元）
     */
    private String sntotalTransactionAmount;

    /**
     * 今年截至目前为止的信用(赊销)交易总金额（元）
     */
    private String currentlyTotalAmount;

    /**
     * 发生年份
     */
    private String happenDate;

    /**
     * 限额号 变更限额时为必填 对应xml ID
     */
    private String limitNumber;

    /**
     * 是否首次申请
     * 是否首次申请 0为首次申请，1为打回修改后提交申请   限额申请为必填项
     */
    private String isNew = "0";

    /**
     * 起保时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date bussStartDate;

    /**
     * 终保时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date bussEndDate;

    /**
     * 申請号
     */
    private String declaraTionFormno;

    /**
     * 是否限额
     */
    private String limitFlag;

    /**
     * 买方piccCode
     */
    private String buyerNo;

    /**
     * 部门Id
     * @return
     */
    private Long deptId;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public BigDecimal getApplyInsuranceAmount() {
        return applyInsuranceAmount;
    }

    public void setApplyInsuranceAmount(BigDecimal applyInsuranceAmount) {
        this.applyInsuranceAmount = applyInsuranceAmount;
    }

    public BigDecimal getReplyInsuranceAmount() {
        return replyInsuranceAmount;
    }

    public void setReplyInsuranceAmount(BigDecimal replyInsuranceAmount) {
        this.replyInsuranceAmount = replyInsuranceAmount;
    }

    public Long getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(Long applyUserId) {
        this.applyUserId = applyUserId;
    }

    public String getApplyUserName() {
        return applyUserName;
    }

    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    public Boolean getEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(Boolean enableFlg) {
        this.enableFlg = enableFlg;
    }

    public String getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(String applyStatus) {
        this.applyStatus = applyStatus;
    }

    public String getUnAcceptReason() {
        return unAcceptReason;
    }

    public void setUnAcceptReason(String unAcceptReason) {
        this.unAcceptReason = unAcceptReason;
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

    public String getCorpSerialNo() {
        return corpSerialNo;
    }

    public void setCorpSerialNo(String corpSerialNo) {
        this.corpSerialNo = corpSerialNo;
    }

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

    public String getRiskPhone() {
        return riskPhone;
    }

    public void setRiskPhone(String riskPhone) {
        this.riskPhone = riskPhone;
    }

    public String getRiskMark() {
        return riskMark;
    }

    public void setRiskMark(String riskMark) {
        this.riskMark = riskMark;
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

    public String getExportTrade() {
        return exportTrade;
    }

    public void setExportTrade(String exportTrade) {
        this.exportTrade = exportTrade;
    }

    public String getExportTradeName() {
        return exportTradeName;
    }

    public void setExportTradeName(String exportTradeName) {
        this.exportTradeName = exportTradeName;
    }

    public String getExportTradeInput() {
        return exportTradeInput;
    }

    public void setExportTradeInput(String exportTradeInput) {
        this.exportTradeInput = exportTradeInput;
    }

    public String getHistoryBusiness() {
        return historyBusiness;
    }

    public void setHistoryBusiness(String historyBusiness) {
        this.historyBusiness = historyBusiness;
    }

    public String getHistoryBusinessName() {
        return historyBusinessName;
    }

    public void setHistoryBusinessName(String historyBusinessName) {
        this.historyBusinessName = historyBusinessName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getIsReport() {
        return isReport;
    }

    public void setIsReport(String isReport) {
        this.isReport = isReport;
    }

    public String getIsReportName() {
        return isReportName;
    }

    public void setIsReportName(String isReportName) {
        this.isReportName = isReportName;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getReportTypeName() {
        return reportTypeName;
    }

    public void setReportTypeName(String reportTypeName) {
        this.reportTypeName = reportTypeName;
    }

    public String getRiskPerformance() {
        return riskPerformance;
    }

    public void setRiskPerformance(String riskPerformance) {
        this.riskPerformance = riskPerformance;
    }

    public String getRiskPerformanceName() {
        return riskPerformanceName;
    }

    public void setRiskPerformanceName(String riskPerformanceName) {
        this.riskPerformanceName = riskPerformanceName;
    }

    public String getDefaultAmount() {
        return defaultAmount;
    }

    public void setDefaultAmount(String defaultAmount) {
        this.defaultAmount = defaultAmount;
    }

    public String getDefaultDate() {
        return defaultDate;
    }

    public void setDefaultDate(String defaultDate) {
        this.defaultDate = defaultDate;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getChangeReasonCode() {
        return changeReasonCode;
    }

    public void setChangeReasonCode(String changeReasonCode) {
        this.changeReasonCode = changeReasonCode;
    }

    public String getChangeReasonRemark() {
        return changeReasonRemark;
    }

    public void setChangeReasonRemark(String changeReasonRemark) {
        this.changeReasonRemark = changeReasonRemark;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getBargainCommodity() {
        return bargainCommodity;
    }

    public void setBargainCommodity(String bargainCommodity) {
        this.bargainCommodity = bargainCommodity;
    }

    public String getIntendingAmount() {
        return intendingAmount;
    }

    public void setIntendingAmount(String intendingAmount) {
        this.intendingAmount = intendingAmount;
    }

    public String getPayWay() {
        return payWay;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }

    public String getMaxSingleAmount() {
        return maxSingleAmount;
    }

    public void setMaxSingleAmount(String maxSingleAmount) {
        this.maxSingleAmount = maxSingleAmount;
    }

    public String getIsVouch() {
        return isVouch;
    }

    public void setIsVouch(String isVouch) {
        this.isVouch = isVouch;
    }

    public String getIsVouchName() {
        return isVouchName;
    }

    public void setIsVouchName(String isVouchName) {
        this.isVouchName = isVouchName;
    }

    public String getEarlyCooperateYear() {
        return earlyCooperateYear;
    }

    public void setEarlyCooperateYear(String earlyCooperateYear) {
        this.earlyCooperateYear = earlyCooperateYear;
    }

    public String getSntotalTransactionAmount() {
        return sntotalTransactionAmount;
    }

    public void setSntotalTransactionAmount(String sntotalTransactionAmount) {
        this.sntotalTransactionAmount = sntotalTransactionAmount;
    }

    public String getCurrentlyTotalAmount() {
        return currentlyTotalAmount;
    }

    public void setCurrentlyTotalAmount(String currentlyTotalAmount) {
        this.currentlyTotalAmount = currentlyTotalAmount;
    }

    public String getHappenDate() {
        return happenDate;
    }

    public void setHappenDate(String happenDate) {
        this.happenDate = happenDate;
    }

    public String getLimitNumber() {
        return limitNumber;
    }

    public void setLimitNumber(String limitNumber) {
        this.limitNumber = limitNumber;
    }

    public String getIsNew() {
        return isNew;
    }

    public void setIsNew(String isNew) {
        this.isNew = isNew;
    }

    public Date getBussStartDate() {
        return bussStartDate;
    }

    public void setBussStartDate(Date bussStartDate) {
        this.bussStartDate = bussStartDate;
    }

    public Date getBussEndDate() {
        return bussEndDate;
    }

    public void setBussEndDate(Date bussEndDate) {
        this.bussEndDate = bussEndDate;
    }

    public String getDeclaraTionFormno() {
        return declaraTionFormno;
    }

    public void setDeclaraTionFormno(String declaraTionFormno) {
        this.declaraTionFormno = declaraTionFormno;
    }

    public String getLimitFlag() {
        return limitFlag;
    }

    public void setLimitFlag(String limitFlag) {
        this.limitFlag = limitFlag;
    }

    public String getCountryOrArea() {
        return countryOrArea;
    }

    public void setCountryOrArea(String countryOrArea) {
        this.countryOrArea = countryOrArea;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public Long getApproveId() {
        return approveId;
    }

    @Override
    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public Long getWxUserId() {
        return wxUserId;
    }

    public void setWxUserId(Long wxUserId) {
        this.wxUserId = wxUserId;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    @Override
    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getApplySource() {
        return applySource;
    }

    public void setApplySource(String applySource) {
        this.applySource = applySource;
    }

    public String getIsCycleUse() {
        return isCycleUse;
    }

    public void setIsCycleUse(String isCycleUse) {
        this.isCycleUse = isCycleUse;
    }

    public String getBuyerNo() {
        return buyerNo;
    }

    public void setBuyerNo(String buyerNo) {
        this.buyerNo = buyerNo;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }
}
