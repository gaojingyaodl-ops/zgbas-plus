package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 中银补充材料
 */
@Entity
@Table(name = "t_zhong_yin_supplement_info")
public class ZhongYinSupplementInfo extends IdEntity {
    private static final long serialVersionUID = -6368704844205079314L;

    /**
     * 企业ID
     */
    private Long companyId;

    /**
     * 申请赊销额度
     */
    private String applyCreditAmount;

    /**
     * 账期
     */
    private String creditDays;

    /**
     * 平均毛利润
     */
    private String averageGrossProfit;

    /**
     * 常用牌号
     */
    private String commonlyBrandNumber;

    /**
     * 常用牌号月使用量（吨）
     */
    private String commonlyBrandNumberMonthlyUsage;

    /**
     * 地理位置
     */
    private String geographyPosition;

    /**
     * 销售模式
     */
    private String sellMode;

    /**
     * 当年销售额
     */
    private String currentYearSellAmount;

    /**
     * 上年销售额
     */
    private String lastYearSellAmount;

    /**
     * 有无历史合作
     */
    private String historyCooperation;

    /**
     * 每月用电量
     */
    private String monthElectricNum;

    /**
     * 每月电费
     */
    private String monthElectricCost;

    /**
     * 有无合同买卖纠纷
     */
    private String contractSalesDisputes;

    /**
     * 土地证及房产车辆等资产证明附件
     */
    private String assetCertificateFileId;

    /**
     * 机器设备发票或所有权证明附件
     */
    private String deviceCertificateFileId;

    /**
     * 公司纳税申报表（近3-6个月）附件
     */
    private String companyTaxReturnFileId;

    /**
     * 发票（近3-6个月）附件
     */
    private String invoiceFileId;

    /**
     * 银行流水（近3-6个月）附件
     */
    private String bankStatementFileId;

    /**
     * 判决书或结案证明材料附件
     */
    private String judgmentFileId;

    /**
     * 批复结果
     */
    private String approveResult;

    /**
     * 申请人保日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date applyZhongYinDate;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getApplyCreditAmount() {
        return applyCreditAmount;
    }

    public void setApplyCreditAmount(String applyCreditAmount) {
        this.applyCreditAmount = applyCreditAmount;
    }

    public String getCreditDays() {
        return creditDays;
    }

    public void setCreditDays(String creditDays) {
        this.creditDays = creditDays;
    }

    public String getAverageGrossProfit() {
        return averageGrossProfit;
    }

    public void setAverageGrossProfit(String averageGrossProfit) {
        this.averageGrossProfit = averageGrossProfit;
    }

    public String getCommonlyBrandNumber() {
        return commonlyBrandNumber;
    }

    public void setCommonlyBrandNumber(String commonlyBrandNumber) {
        this.commonlyBrandNumber = commonlyBrandNumber;
    }

    public String getCommonlyBrandNumberMonthlyUsage() {
        return commonlyBrandNumberMonthlyUsage;
    }

    public void setCommonlyBrandNumberMonthlyUsage(String commonlyBrandNumberMonthlyUsage) {
        this.commonlyBrandNumberMonthlyUsage = commonlyBrandNumberMonthlyUsage;
    }

    public String getGeographyPosition() {
        return geographyPosition;
    }

    public void setGeographyPosition(String geographyPosition) {
        this.geographyPosition = geographyPosition;
    }

    public String getSellMode() {
        return sellMode;
    }

    public void setSellMode(String sellMode) {
        this.sellMode = sellMode;
    }

    public String getCurrentYearSellAmount() {
        return currentYearSellAmount;
    }

    public void setCurrentYearSellAmount(String currentYearSellAmount) {
        this.currentYearSellAmount = currentYearSellAmount;
    }

    public String getLastYearSellAmount() {
        return lastYearSellAmount;
    }

    public void setLastYearSellAmount(String lastYearSellAmount) {
        this.lastYearSellAmount = lastYearSellAmount;
    }

    public String getHistoryCooperation() {
        return historyCooperation;
    }

    public void setHistoryCooperation(String historyCooperation) {
        this.historyCooperation = historyCooperation;
    }

    public String getMonthElectricNum() {
        return monthElectricNum;
    }

    public void setMonthElectricNum(String monthElectricNum) {
        this.monthElectricNum = monthElectricNum;
    }

    public String getMonthElectricCost() {
        return monthElectricCost;
    }

    public void setMonthElectricCost(String monthElectricCost) {
        this.monthElectricCost = monthElectricCost;
    }

    public String getContractSalesDisputes() {
        return contractSalesDisputes;
    }

    public void setContractSalesDisputes(String contractSalesDisputes) {
        this.contractSalesDisputes = contractSalesDisputes;
    }

    public String getAssetCertificateFileId() {
        return assetCertificateFileId;
    }

    public void setAssetCertificateFileId(String assetCertificateFileId) {
        this.assetCertificateFileId = assetCertificateFileId;
    }

    public String getDeviceCertificateFileId() {
        return deviceCertificateFileId;
    }

    public void setDeviceCertificateFileId(String deviceCertificateFileId) {
        this.deviceCertificateFileId = deviceCertificateFileId;
    }

    public String getCompanyTaxReturnFileId() {
        return companyTaxReturnFileId;
    }

    public void setCompanyTaxReturnFileId(String companyTaxReturnFileId) {
        this.companyTaxReturnFileId = companyTaxReturnFileId;
    }

    public String getInvoiceFileId() {
        return invoiceFileId;
    }

    public void setInvoiceFileId(String invoiceFileId) {
        this.invoiceFileId = invoiceFileId;
    }

    public String getBankStatementFileId() {
        return bankStatementFileId;
    }

    public void setBankStatementFileId(String bankStatementFileId) {
        this.bankStatementFileId = bankStatementFileId;
    }

    public String getJudgmentFileId() {
        return judgmentFileId;
    }

    public void setJudgmentFileId(String judgmentFileId) {
        this.judgmentFileId = judgmentFileId;
    }

    public String getApproveResult() {
        return approveResult;
    }

    public void setApproveResult(String approveResult) {
        this.approveResult = approveResult;
    }

    public Date getApplyZhongYinDate() {
        return applyZhongYinDate;
    }

    public void setApplyZhongYinDate(Date applyZhongYinDate) {
        this.applyZhongYinDate = applyZhongYinDate;
    }
}
