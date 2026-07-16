package com.spt.bas.client.vo;

import com.spt.bas.client.entity.CtrProduct;

import java.math.BigDecimal;


public class CtrProductVo extends CtrProduct {

    private static final long serialVersionUID = 9090977436708221427L;

    /**
     * 厂商ID
     */
    private Long factoryId;
    /**
     * 厂商名称
     */
    private String factoryName;

    private String wrapSpecsStr;

    private BigDecimal maxCurNumber;   //最大可出库数量
    /**
     * 不含税单价 含税单价/（1+13%）
     */
    private BigDecimal dealPriceNoTax;
    /**
     * 不含税总价 含税总价/（1+13%）
     */
    private BigDecimal totalPriceNoTax;
    
    private Long buyCompanyId;            //获取对应采购方企业ID

    private  String wrapSpecs;

    private  String payMode;

    private   String  signAddress;

    private   String  additionalAgreement;

    private  String logisticsQuotation;

    private  String zyflag;

    public String getZyflag() {
        return zyflag;
    }

    public void setZyflag(String zyflag) {
        this.zyflag = zyflag;
    }

    public String getLogisticsQuotation() {
        return logisticsQuotation;
    }

    public void setLogisticsQuotation(String logisticsQuotation) {
        this.logisticsQuotation = logisticsQuotation;
    }

    public String getSignAddress() {
        return signAddress;
    }

    public void setSignAddress(String signAddress) {
        this.signAddress = signAddress;
    }

    public String getAdditionalAgreement() {
        return additionalAgreement;
    }

    @Override
    public String getWrapSpecs() {
        return wrapSpecs;
    }

    @Override
    public void setWrapSpecs(String wrapSpecs) {
        this.wrapSpecs = wrapSpecs;
    }

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public String getWrapSpecsStr() {
        return wrapSpecsStr;
    }

    public void setWrapSpecsStr(String wrapSpecsStr) {
        this.wrapSpecsStr = wrapSpecsStr;
    }

    public BigDecimal getMaxCurNumber() {
        return maxCurNumber;
    }

    public void setMaxCurNumber(BigDecimal maxCurNumber) {
        this.maxCurNumber = maxCurNumber;
    }

    public Long getBuyCompanyId() {
        return buyCompanyId;
    }

    public void setBuyCompanyId(Long buyCompanyId) {
        this.buyCompanyId = buyCompanyId;
    }

    @Override
    public Long getFactoryId() {
        return factoryId;
    }

    @Override
    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }

    @Override
    public String getFactoryName() {
        return factoryName;
    }

    @Override
    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public BigDecimal getDealPriceNoTax() {
        return dealPriceNoTax;
    }

    public void setDealPriceNoTax(BigDecimal dealPriceNoTax) {
        this.dealPriceNoTax = dealPriceNoTax;
    }

    public BigDecimal getTotalPriceNoTax() {
        return totalPriceNoTax;
    }

    public void setTotalPriceNoTax(BigDecimal totalPriceNoTax) {
        this.totalPriceNoTax = totalPriceNoTax;
    }
}
