package com.spt.bas.client.vo.rtVo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 融拓确认收货推送信息Vo
 *
 * @Author: gaojy
 * @create 2022/4/8 11:27
 * @version: 1.0
 * @description:
 */
public class RtConfirmReq extends RtBaseReq {

    /**
     * 该批次货物预定的收款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date actualContractPayFullTime;

    /**
     * 审批编号
     */
    private String applyNo;

    /**
     * 0-核心管理系统
     * 1-采购管家小程序
     */
    private String applySource;

    /**
     * 牌号
     */
    private String brandNumber;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 该批次的发货金额
     */
    private BigDecimal confirmReceiptAmount;

    /**
     * 确认收货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date confirmReceiptDate;

    /**
     * 该批次的发货数量
     */
    private BigDecimal confirmReceiptNumber;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 收货数量
     */
    private BigDecimal curNumber;

    /**
     * 数量
     */
    private BigDecimal dealNumber;

    /**
     * 单价
     */
    private BigDecimal dealPrice;

    /**
     * 厂商
     */
    private String factoryName;

    /**
     * 损耗金额
     */
    private BigDecimal lossAmount;

    /**
     * 实际物流费用
     */
    private BigDecimal lossAmountByActual;

    /**
     * 物流方承担损耗金额
     */
    private BigDecimal lossAmountByLogistics;

    /**
     * 我方承担损耗金额
     */
    private BigDecimal lossAmountByOur;

    /**
     * 供应商承担损耗金额
     */
    private BigDecimal lossAmountBySupplier;

    /**
     * 损耗数量
     */
    private BigDecimal lossNumber;

    /**
     * 品名
     */
    private String productName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态
     */
    private String status;

    /**
     * 用户微信ID
     */
    private String wxUserId;

    public Date getActualContractPayFullTime() {
        return actualContractPayFullTime;
    }

    public void setActualContractPayFullTime(Date actualContractPayFullTime) {
        this.actualContractPayFullTime = actualContractPayFullTime;
    }

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    public String getApplySource() {
        return applySource;
    }

    public void setApplySource(String applySource) {
        this.applySource = applySource;
    }

    public String getBrandNumber() {
        return brandNumber;
    }

    public void setBrandNumber(String brandNumber) {
        this.brandNumber = brandNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public BigDecimal getConfirmReceiptAmount() {
        return confirmReceiptAmount;
    }

    public void setConfirmReceiptAmount(BigDecimal confirmReceiptAmount) {
        this.confirmReceiptAmount = confirmReceiptAmount;
    }

    public Date getConfirmReceiptDate() {
        return confirmReceiptDate;
    }

    public void setConfirmReceiptDate(Date confirmReceiptDate) {
        this.confirmReceiptDate = confirmReceiptDate;
    }

    public BigDecimal getConfirmReceiptNumber() {
        return confirmReceiptNumber;
    }

    public void setConfirmReceiptNumber(BigDecimal confirmReceiptNumber) {
        this.confirmReceiptNumber = confirmReceiptNumber;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public BigDecimal getCurNumber() {
        return curNumber;
    }

    public void setCurNumber(BigDecimal curNumber) {
        this.curNumber = curNumber;
    }

    public BigDecimal getDealNumber() {
        return dealNumber;
    }

    public void setDealNumber(BigDecimal dealNumber) {
        this.dealNumber = dealNumber;
    }

    public BigDecimal getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(BigDecimal dealPrice) {
        this.dealPrice = dealPrice;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public BigDecimal getLossAmount() {
        return lossAmount;
    }

    public void setLossAmount(BigDecimal lossAmount) {
        this.lossAmount = lossAmount;
    }

    public BigDecimal getLossAmountByActual() {
        return lossAmountByActual;
    }

    public void setLossAmountByActual(BigDecimal lossAmountByActual) {
        this.lossAmountByActual = lossAmountByActual;
    }

    public BigDecimal getLossAmountByLogistics() {
        return lossAmountByLogistics;
    }

    public void setLossAmountByLogistics(BigDecimal lossAmountByLogistics) {
        this.lossAmountByLogistics = lossAmountByLogistics;
    }

    public BigDecimal getLossAmountByOur() {
        return lossAmountByOur;
    }

    public void setLossAmountByOur(BigDecimal lossAmountByOur) {
        this.lossAmountByOur = lossAmountByOur;
    }

    public BigDecimal getLossAmountBySupplier() {
        return lossAmountBySupplier;
    }

    public void setLossAmountBySupplier(BigDecimal lossAmountBySupplier) {
        this.lossAmountBySupplier = lossAmountBySupplier;
    }

    public BigDecimal getLossNumber() {
        return lossNumber;
    }

    public void setLossNumber(BigDecimal lossNumber) {
        this.lossNumber = lossNumber;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWxUserId() {
        return wxUserId;
    }

    public void setWxUserId(String wxUserId) {
        this.wxUserId = wxUserId;
    }
}
