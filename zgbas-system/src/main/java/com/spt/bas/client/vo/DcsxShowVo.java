package com.spt.bas.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.entity.CtrContract;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

public class DcsxShowVo extends ApplyCtrDCSX {


    private static final long serialVersionUID = 1L;
    private Boolean canStartBuy = false;
    private String ReplyStatus;//回复状态
    private String contractTimeStr;//合同时间
    private BigDecimal orverdurAmount;//逾期金额
    private String deptName;//事业部
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date lastPayDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date lastDeliveryDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date lastBillDate;
    private String piccPushFlgStr;
    private String piccReceiveFlgStr;

    private Boolean existServiceText = false;
    private Boolean existServiceTextFileId = false;
    private String serviceFileUrl;

    private Boolean existContractText;
    private Boolean existContractTextFileId = false;
    private String contractFileUrl;

    private Integer contractDifTime;
    private Long pairId;
    private BigDecimal dealAmount= BigDecimal.ZERO;
    //-----------------------------------------导出毛利台账

    /**
     * 备注
     */
    private String remark;
    /**
     * 序号
     */
    private BigDecimal number;


    /**
     * 申请日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    protected Date createdDateml;

    /**
     * 系统合同编号（代采赊销合同编号）
     */
    private String contractNo;

    /**
     * 商品信息
     */
    private String productName;

//------------------上游信息
    /**
     * 数量
     */
    private BigDecimal totalNumbers;

    /**
     * 上游企业名称
     */
    private String companyNames;

    /**
     * 上游含税单价
     */
    private BigDecimal dealPrices;

    /**
     * 上游总金额
     */
    private BigDecimal totalAmounts;

    /**
     * 上游规定还款日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payFullTime;

//------------------中游信息
    /**
     * 中游含税单价
     */
    private BigDecimal dealPricez;

    /**
     * 中游总金额
     */
    private BigDecimal totalAmountz;

    /**
     * 中游规定还款日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payFullTimez;

    //------------------下游信息
    /**
     * 下游企业名称
     */
    private String ourCompanyName;

    /**
     * 下游含税单价
     */
    private BigDecimal dealPricex;

    /**
     * 下游总金额
     */
    private BigDecimal totalAmountx;

    /**
     * 下游规定还款日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payFullTimex;

    /**
     * 确认收货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date confirmDate;

    /**
     * 毛利
     */
    private BigDecimal grossProfit;

    /**
     * 开票开关
     */
    private  Boolean  kpFlag;

    /**
     * 付款开关
     */
    private  Boolean  fkFlag;

    /**
     * 收款开关
     */
    private  Boolean  skFlag;

    /**
     * 收票开关
     */
    private  Boolean  spFlag;

    /**
     * 是否显示确认收货按钮
     */
    private  Boolean  showConfirmReceiptBotton;

    /**
     * 补充协议附件url
     */
    private String protocolFileUrl;
    
    

    public BigDecimal getDealAmount() {
        return dealAmount;
    }

    public void setDealAmount(BigDecimal dealAmount) {
        this.dealAmount = dealAmount;
    }

    public Boolean getKpFlag() {
        return kpFlag;
    }

    public void setKpFlag(Boolean kpFlag) {
        this.kpFlag = kpFlag;
    }

    public Boolean getFkFlag() {
        return fkFlag;
    }

    public void setFkFlag(Boolean fkFlag) {
        this.fkFlag = fkFlag;
    }

    public Boolean getSkFlag() {
        return skFlag;
    }

    public void setSkFlag(Boolean skFlag) {
        this.skFlag = skFlag;
    }

    public Boolean getSpFlag() {
        return spFlag;
    }

    public void setSpFlag(Boolean spFlag) {
        this.spFlag = spFlag;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }


    public BigDecimal getNumber() {
        return number;
    }

    public void setNumber(BigDecimal number) {
        this.number = number;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Date getCreatedDateml() {
        return createdDateml;
    }

    public void setCreatedDateml(Date createdDateml) {
        this.createdDateml = createdDateml;
    }

    @Override
    public String getContractNo() {
        return contractNo;
    }

    @Override
    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getTotalNumbers() {
        return totalNumbers;
    }

    public void setTotalNumbers(BigDecimal totalNumbers) {
        this.totalNumbers = totalNumbers;
    }

    public String getCompanyNames() {
        return companyNames;
    }

    public void setCompanyNames(String companyNames) {
        this.companyNames = companyNames;
    }

    public BigDecimal getDealPrices() {
        return dealPrices;
    }

    public void setDealPrices(BigDecimal dealPrices) {
        this.dealPrices = dealPrices;
    }

    public BigDecimal getTotalAmounts() {
        return totalAmounts;
    }

    public void setTotalAmounts(BigDecimal totalAmounts) {
        this.totalAmounts = totalAmounts;
    }

    @Override
    public Date getPayFullTime() {
        return payFullTime;
    }

    @Override
    public void setPayFullTime(Date payFullTime) {
        this.payFullTime = payFullTime;
    }

    public BigDecimal getDealPricez() {
        return dealPricez;
    }

    public void setDealPricez(BigDecimal dealPricez) {
        this.dealPricez = dealPricez;
    }

    public BigDecimal getTotalAmountz() {
        return totalAmountz;
    }

    public void setTotalAmountz(BigDecimal totalAmountz) {
        this.totalAmountz = totalAmountz;
    }

    public Date getPayFullTimez() {
        return payFullTimez;
    }

    public void setPayFullTimez(Date payFullTimez) {
        this.payFullTimez = payFullTimez;
    }

    @Override
    public String getOurCompanyName() {
        return ourCompanyName;
    }

    @Override
    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public BigDecimal getDealPricex() {
        return dealPricex;
    }

    public void setDealPricex(BigDecimal dealPricex) {
        this.dealPricex = dealPricex;
    }

    public BigDecimal getTotalAmountx() {
        return totalAmountx;
    }

    public void setTotalAmountx(BigDecimal totalAmountx) {
        this.totalAmountx = totalAmountx;
    }

    public Date getPayFullTimex() {
        return payFullTimex;
    }

    public void setPayFullTimex(Date payFullTimex) {
        this.payFullTimex = payFullTimex;
    }

    @Override
    public Date getConfirmDate() {
        return confirmDate;
    }

    @Override
    public void setConfirmDate(Date confirmDate) {
        this.confirmDate = confirmDate;
    }

    public BigDecimal getGrossProfit() {
        return grossProfit;
    }

    public void setGrossProfit(BigDecimal grossProfit) {
        this.grossProfit = grossProfit;
    }


    public Boolean getCanStartBuy() {
        return canStartBuy;
    }

    public void setCanStartBuy(Boolean canStartBuy) {
        this.canStartBuy = canStartBuy;
    }

    public String getReplyStatus() {
        return ReplyStatus;
    }

    public void setReplyStatus(String replyStatus) {
        ReplyStatus = replyStatus;
    }

    public String getContractTimeStr() {
        return contractTimeStr;
    }

    public void setContractTimeStr(String contractTimeStr) {
        this.contractTimeStr = contractTimeStr;
    }

    public Boolean getExistContractText() {
        return existContractText;
    }

    public void setExistContractText(Boolean existContractText) {
        this.existContractText = existContractText;
    }

    public BigDecimal getOrverdurAmount() {
        return orverdurAmount;
    }

    public void setOrverdurAmount(BigDecimal orverdurAmount) {
        this.orverdurAmount = orverdurAmount;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Date getLastPayDate() {
        return lastPayDate;
    }

    public void setLastPayDate(Date lastPayDate) {
        this.lastPayDate = lastPayDate;
    }

    public Date getLastDeliveryDate() {
        return lastDeliveryDate;
    }

    public void setLastDeliveryDate(Date lastDeliveryDate) {
        this.lastDeliveryDate = lastDeliveryDate;
    }

    public Date getLastBillDate() {
        return lastBillDate;
    }

    public void setLastBillDate(Date lastBillDate) {
        this.lastBillDate = lastBillDate;
    }

    public Integer getContractDifTime() {
        return contractDifTime;
    }

    public void setContractDifTime(Integer contractDifTime) {
        this.contractDifTime = contractDifTime;
    }

    public String getPiccPushFlgStr() {
        return piccPushFlgStr;
    }

    public void setPiccPushFlgStr(String piccPushFlgStr) {
        this.piccPushFlgStr = piccPushFlgStr;
    }

    public String getPiccReceiveFlgStr() {
        return piccReceiveFlgStr;
    }

    public void setPiccReceiveFlgStr(String piccReceiveFlgStr) {
        this.piccReceiveFlgStr = piccReceiveFlgStr;
    }

    public Long getPairId() {
        return pairId;
    }

    public void setPairId(Long pairId) {
        this.pairId = pairId;
    }

    public Boolean getExistServiceText() {
        return existServiceText;
    }

    public void setExistServiceText(Boolean existServiceText) {
        this.existServiceText = existServiceText;
    }

    public Boolean getExistServiceTextFileId() {
        return existServiceTextFileId;
    }

    public void setExistServiceTextFileId(Boolean existServiceTextFileId) {
        this.existServiceTextFileId = existServiceTextFileId;
    }

    public String getServiceFileUrl() {
        return serviceFileUrl;
    }

    public void setServiceFileUrl(String serviceFileUrl) {
        this.serviceFileUrl = serviceFileUrl;
    }

    public Boolean getExistContractTextFileId() {
        return existContractTextFileId;
    }

    public void setExistContractTextFileId(Boolean existContractTextFileId) {
        this.existContractTextFileId = existContractTextFileId;
    }

    public String getContractFileUrl() {
        return contractFileUrl;
    }

    public void setContractFileUrl(String contractFileUrl) {
        this.contractFileUrl = contractFileUrl;
    }

    public Boolean getShowConfirmReceiptBotton() {
        return showConfirmReceiptBotton;
    }

    public void setShowConfirmReceiptBotton(Boolean showConfirmReceiptBotton) {
        this.showConfirmReceiptBotton = showConfirmReceiptBotton;
    }

    public String getProtocolFileUrl() {
        return protocolFileUrl;
    }

    public void setProtocolFileUrl(String protocolFileUrl) {
        this.protocolFileUrl = protocolFileUrl;
    }
}
