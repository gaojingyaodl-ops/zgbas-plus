package com.spt.bas.report.client.vo;

import java.math.BigDecimal;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/4/11 16:45
 */

public class RptBaseCostAndContractVo {

    /**
     * 业务员
     */
    private String matchUserName;

    /**
     * 业务员id
     */
    private Long matchUserId;

    /**
     * 区域cd
     */
    private String branchCd;

    /**
     * 区域名称
     */
    private String branchName;

    /**
     * 销售总价
     */
    private BigDecimal sellTotalAmount;

    /**
     * 采购总价
     */
    private BigDecimal buyTotalAmount;

    /**
     * 总运费
     */
    private BigDecimal transportAmount;

    /**
     * 仓储费
     */
    private BigDecimal warehouseAmount;

    /**
     *
     */
    private BigDecimal steveDorageAmount;


    /**
     * 数量
     */
    private BigDecimal dealNumber;

    /**
     * 业务类型名称
     */
    private String businessName;

    private String businessType;


    private String businessTypeDcsx;


    private Boolean matchCreditFlg;

    /**
     * 年/月
     */
    private String baseDate;

    /**
     * 销售合同号
     */
    private String sellContractNo;

    /**
     *
     */
    private String buyContractNo;

    public BigDecimal getTransportAmount() {
        return transportAmount;
    }

    public void setTransportAmount(BigDecimal transportAmount) {
        this.transportAmount = transportAmount;
    }

    public BigDecimal getWarehouseAmount() {
        return warehouseAmount;
    }

    public void setWarehouseAmount(BigDecimal warehouseAmount) {
        this.warehouseAmount = warehouseAmount;
    }

    public BigDecimal getSteveDorageAmount() {
        return steveDorageAmount;
    }

    public void setSteveDorageAmount(BigDecimal steveDorageAmount) {
        this.steveDorageAmount = steveDorageAmount;
    }

    public String getSellContractNo() {
        return sellContractNo;
    }

    public void setSellContractNo(String sellContractNo) {
        this.sellContractNo = sellContractNo;
    }

    public String getBuyContractNo() {
        return buyContractNo;
    }

    public void setBuyContractNo(String buyContractNo) {
        this.buyContractNo = buyContractNo;
    }

    public String getBaseDate() {
        return baseDate;
    }

    public void setBaseDate(String baseDate) {
        this.baseDate = baseDate;
    }

    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public String getBusinessTypeDcsx() {
        return businessTypeDcsx;
    }

    public void setBusinessTypeDcsx(String businessTypeDcsx) {
        this.businessTypeDcsx = businessTypeDcsx;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getMatchUserName() {
        return matchUserName;
    }

    public void setMatchUserName(String matchUserName) {
        this.matchUserName = matchUserName;
    }

    public String getBranchCd() {
        return branchCd;
    }

    public void setBranchCd(String branchCd) {
        this.branchCd = branchCd;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public BigDecimal getSellTotalAmount() {
        return sellTotalAmount;
    }

    public void setSellTotalAmount(BigDecimal sellTotalAmount) {
        this.sellTotalAmount = sellTotalAmount;
    }

    public BigDecimal getBuyTotalAmount() {
        return buyTotalAmount;
    }

    public void setBuyTotalAmount(BigDecimal buyTotalAmount) {
        this.buyTotalAmount = buyTotalAmount;
    }

    public BigDecimal getDealNumber() {
        return dealNumber;
    }

    public void setDealNumber(BigDecimal dealNumber) {
        this.dealNumber = dealNumber;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Boolean getMatchCreditFlg() {
        return matchCreditFlg;
    }

    public void setMatchCreditFlg(Boolean matchCreditFlg) {
        this.matchCreditFlg = matchCreditFlg;
    }
}
