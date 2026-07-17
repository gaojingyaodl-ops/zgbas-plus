package com.spt.bas.report.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 预算统计
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-02-20 09:40
 */
public class RptMarginAmountReport extends PageSearchVo {

    /**
     * contractProfitId
     */
    private Long id;
    /**
     * 预算编号
     */
    private String approveNo;

    /**
     * 品种/牌号
     */
    private String productName;

    /**
     * 签订日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date contractTime;

    /**
     * 合同数量
     */
    private BigDecimal totalNumber;

    // ====================================================================

    /**
     * 事业部id
     */
    private Long serviceDepartmentId;

    /**
     * 事业部名称
     */
    private String serviceDepartmentName;

    /**
     * 业务部id
     */
    private Long businessDepartmentId;

    /**
     * 业务部名称
     */
    private String businessDepartmentName;

    /**
     * 业务员
     */
    private String matchUserName;

    /**
     * 业务员id
     */
    private Long matchUserId;

    // =====================================================================


    /**
     * 采购合同id
     */
    private String buyContractNo;

    /**
     * 采购供应商
     */
    private String buyCompanyName;

    /**
     * 采购单价
     */
    private BigDecimal bdealPrice;

    /**
     * 采购合同金额
     */
    private BigDecimal btotalAmount;

    /**
     * 采购合同付款金额
     */
    private BigDecimal bdealedAmount;

    /**
     * 采购合同收票金额
     */
    private BigDecimal bbilledAmount;

    /**
     * 采购-应付金额
     */
    private BigDecimal balancePayable;

    /**
     * 采购合同定金比例
     */
    private BigDecimal bPayRate;

    /**
     * 采购-合同定金金额
     */
    private BigDecimal bPayRateAmount;

    // =============================================================
    /**
     * 采购-付全款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date bPayFullTime;

    /**
     * 采购-收票日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date bBillTime;


    /**
     * 销售合同编号
     */
    private String sellContractNo;

    /**
     * 供应商(终端工厂)
     */
    private String sellCompanyName;

    /**
     * 销售单价
     */
    private BigDecimal sdealPrice;

    /**
     * 销售合同金额
     */
    private BigDecimal stotalAmount;

    /**
     * 付款金额
     */
    private BigDecimal sdealedAmount;


    /**
     * 开票金额
     */
    private BigDecimal sbilledAmount;

    /**
     * 应收余额
     */
    private BigDecimal balanceReceivable;

    /**
     * 定金比例
     */
    private BigDecimal sPayRate;

    /**
     * 定金金额
     */
    private BigDecimal sPayRateAmount;


    /**
     * 利润
     */
    private BigDecimal marginAmount;

    /**
     * 毛利
     */
    private BigDecimal companyCommissionAmount;

    /**
     * 毛利率
     */
    private BigDecimal companyCommissionRate;

    /**
     * 入库数量
     */
    private BigDecimal bWarehouseNumber;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date confirmDate;//确认收货日期

    public Date getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(Date confirmDate) {
        this.confirmDate = confirmDate;
    }

    public BigDecimal getbWarehouseNumber() {
        return bWarehouseNumber;
    }

    public void setbWarehouseNumber(BigDecimal bWarehouseNumber) {
        this.bWarehouseNumber = bWarehouseNumber;
    }

    public BigDecimal getsWarehouseNumber() {
        return sWarehouseNumber;
    }

    public void setsWarehouseNumber(BigDecimal sWarehouseNumber) {
        this.sWarehouseNumber = sWarehouseNumber;
    }

    /**
     * 出库数量
     */
    private BigDecimal sWarehouseNumber;
    /**
     * 合同状态
     */
    private String contractStatus;
    /**
     * 销售-收全款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date sPayFullTime;

    /**
     * 销售-开票日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date sBillTime;

    /**
     * 出库时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date slastDeliveryDate;

    /**
     * 入库时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date blastDeliveryDate;

    public Date getbPayFullTime() {
        return bPayFullTime;
    }

    public void setbPayFullTime(Date bPayFullTime) {
        this.bPayFullTime = bPayFullTime;
    }

    public Date getbBillTime() {
        return bBillTime;
    }

    public void setbBillTime(Date bBillTime) {
        this.bBillTime = bBillTime;
    }

    public Date getsPayFullTime() {
        return sPayFullTime;
    }

    public void setsPayFullTime(Date sPayFullTime) {
        this.sPayFullTime = sPayFullTime;
    }

    public Date getsBillTime() {
        return sBillTime;
    }

    public void setsBillTime(Date sBillTime) {
        this.sBillTime = sBillTime;
    }

    public String getApproveNo() {
        return approveNo;
    }

    public void setApproveNo(String approveNo) {
        this.approveNo = approveNo;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Date getContractTime() {
        return contractTime;
    }

    public void setContractTime(Date contractTime) {
        this.contractTime = contractTime;
    }

    public BigDecimal getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(BigDecimal totalNumber) {
        this.totalNumber = totalNumber;
    }

    public Long getServiceDepartmentId() {
        return serviceDepartmentId;
    }

    public void setServiceDepartmentId(Long serviceDepartmentId) {
        this.serviceDepartmentId = serviceDepartmentId;
    }

    public String getServiceDepartmentName() {
        return serviceDepartmentName;
    }

    public void setServiceDepartmentName(String serviceDepartmentName) {
        this.serviceDepartmentName = serviceDepartmentName;
    }

    public Long getBusinessDepartmentId() {
        return businessDepartmentId;
    }

    public void setBusinessDepartmentId(Long businessDepartmentId) {
        this.businessDepartmentId = businessDepartmentId;
    }

    public String getBusinessDepartmentName() {
        return businessDepartmentName;
    }

    public void setBusinessDepartmentName(String businessDepartmentName) {
        this.businessDepartmentName = businessDepartmentName;
    }

    public String getMatchUserName() {
        return matchUserName;
    }

    public void setMatchUserName(String matchUserName) {
        this.matchUserName = matchUserName;
    }

    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public String getBuyContractNo() {
        return buyContractNo;
    }

    public void setBuyContractNo(String buyContractNo) {
        this.buyContractNo = buyContractNo;
    }

    public String getBuyCompanyName() {
        return buyCompanyName;
    }

    public void setBuyCompanyName(String buyCompanyName) {
        this.buyCompanyName = buyCompanyName;
    }

    public BigDecimal getBdealPrice() {
        return bdealPrice;
    }

    public void setBdealPrice(BigDecimal bdealPrice) {
        this.bdealPrice = bdealPrice;
    }

    public BigDecimal getBtotalAmount() {
        return btotalAmount;
    }

    public void setBtotalAmount(BigDecimal btotalAmount) {
        this.btotalAmount = btotalAmount;
    }

    public BigDecimal getBdealedAmount() {
        return bdealedAmount;
    }

    public void setBdealedAmount(BigDecimal bdealedAmount) {
        this.bdealedAmount = bdealedAmount;
    }

    public BigDecimal getBbilledAmount() {
        return bbilledAmount;
    }

    public void setBbilledAmount(BigDecimal bbilledAmount) {
        this.bbilledAmount = bbilledAmount;
    }

    public BigDecimal getBalancePayable() {
        return balancePayable;
    }

    public void setBalancePayable(BigDecimal balancePayable) {
        this.balancePayable = balancePayable;
    }

    public BigDecimal getbPayRate() {
        return bPayRate;
    }

    public void setbPayRate(BigDecimal bPayRate) {
        this.bPayRate = bPayRate;
    }

    public BigDecimal getbPayRateAmount() {
        return bPayRateAmount;
    }

    public void setbPayRateAmount(BigDecimal bPayRateAmount) {
        this.bPayRateAmount = bPayRateAmount;
    }

    public String getSellContractNo() {
        return sellContractNo;
    }

    public void setSellContractNo(String sellContractNo) {
        this.sellContractNo = sellContractNo;
    }

    public String getSellCompanyName() {
        return sellCompanyName;
    }

    public void setSellCompanyName(String sellCompanyName) {
        this.sellCompanyName = sellCompanyName;
    }

    public BigDecimal getSdealPrice() {
        return sdealPrice;
    }

    public void setSdealPrice(BigDecimal sdealPrice) {
        this.sdealPrice = sdealPrice;
    }

    public BigDecimal getStotalAmount() {
        return stotalAmount;
    }

    public void setStotalAmount(BigDecimal stotalAmount) {
        this.stotalAmount = stotalAmount;
    }

    public BigDecimal getSdealedAmount() {
        return sdealedAmount;
    }

    public void setSdealedAmount(BigDecimal sdealedAmount) {
        this.sdealedAmount = sdealedAmount;
    }

    public BigDecimal getSbilledAmount() {
        return sbilledAmount;
    }

    public void setSbilledAmount(BigDecimal sbilledAmount) {
        this.sbilledAmount = sbilledAmount;
    }

    public BigDecimal getBalanceReceivable() {
        return balanceReceivable;
    }

    public void setBalanceReceivable(BigDecimal balanceReceivable) {
        this.balanceReceivable = balanceReceivable;
    }

    public BigDecimal getsPayRate() {
        return sPayRate;
    }

    public void setsPayRate(BigDecimal sPayRate) {
        this.sPayRate = sPayRate;
    }

    public BigDecimal getsPayRateAmount() {
        return sPayRateAmount;
    }

    public void setsPayRateAmount(BigDecimal sPayRateAmount) {
        this.sPayRateAmount = sPayRateAmount;
    }

    public BigDecimal getMarginAmount() {
        return marginAmount;
    }

    public void setMarginAmount(BigDecimal marginAmount) {
        this.marginAmount = marginAmount;
    }

    public BigDecimal getCompanyCommissionAmount() {
        return companyCommissionAmount;
    }

    public void setCompanyCommissionAmount(BigDecimal companyCommissionAmount) {
        this.companyCommissionAmount = companyCommissionAmount;
    }

    public BigDecimal getCompanyCommissionRate() {
        return companyCommissionRate;
    }

    public void setCompanyCommissionRate(BigDecimal companyCommissionRate) {
        this.companyCommissionRate = companyCommissionRate;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public Date getSlastDeliveryDate() {
        return slastDeliveryDate;
    }

    public void setSlastDeliveryDate(Date slastDeliveryDate) {
        this.slastDeliveryDate = slastDeliveryDate;
    }

    public Date getBlastDeliveryDate() {
        return blastDeliveryDate;
    }

    public void setBlastDeliveryDate(Date blastDeliveryDate) {
        this.blastDeliveryDate = blastDeliveryDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
