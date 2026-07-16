package com.spt.bas.client.vo;

import java.math.BigDecimal;

public class DcContractText {

    private static final long serialVersionUID = 5815316857357570261L;

    private String contractNo;
    private String contractTimeStr;
    private String productName;
    private String brandNumber;
    private String factoryName;
    private String wrapSpecs;
    private String dealNumber;
    private String dealPrice;
    private BigDecimal dealPriceNoTax;
    private String totalPrice;
    private BigDecimal totalPriceNum;
    private BigDecimal totalPriceNoTax;
    private String cnMoney;
    private String deliAddr;
    private String deliveryDateStr;
    private String deliveryType;
    private String transAmountRemark;
    private String payFullTimeStr;
    private Integer creditDays;

    /**
     * 质量标准
     */
    private String qualityStandardStr;
    
    
    /**
     * 付款方式
     */
    private String payMode;
    
    private String contractModel;
    private String payRemaindTime;
    private String extraTerm;
    private BigDecimal buyBondAmount;
    private String buyPayBondDate;
    private String buyPayFullDate;
    private String sellPayFullDate;
    

    private BigDecimal bondAmount;
    private BigDecimal payRateAmount;
    
    
    /**
     * 签订地点
     */
    private String signingAddr;

    /**
     * 法人
     */
    private String ourCompanyPerson;

    /**
     * 联系人
     */
    private String ourCompanyContact;

    /**
     * 传真
     */
    private String ourCompanyFax;

    /**
     * 电话
     */
    private String ourCompanyPhone;

    /**
     * 税号
     */
    private String ourCompanyTaxNo;

    /**
     * 开户行
     */
    private String ourCompanyBankName;

    /**
     * 行号
     */
    private String ourCompanyBankNo;

    /**
     * 企业名称
     */
    private String ourCompanyName;

    /**
     * 企业地址
     */
    private String ourAddress;

    /**
     * 代采购方名称
     */
    private String companyName;

    /**
     * 代采购方法人
     */
    private String companyPerson;

    /**
     * 代采购方联系人
     */
    private String companyContact;

    /**
     * 代采购方传真
     */
    private String companyFax;

    /**
     * 代采购方电话
     */
    private String companyPhone;

    /**
     * 代采购方税号
     */
    private String companyTaxNo;

    /**
     * 代采购方开户行
     */
    private String companyBankName;

    /**
     * 代采购方行号
     */
    private String companyBankNo;

    /**
     * 代采购方地址
     */
    private String address;

    private Long enterpriseId;

    //内容条款/ 定金 全款
    private   String  clause ;

    public String getClause() {
        return clause;
    }

    public void setClause(String clause) {
        this.clause = clause;
    }

    public String getCompanyPerson() {
        return companyPerson;
    }

    public void setCompanyPerson(String companyPerson) {
        this.companyPerson = companyPerson;
    }

    public String getCompanyContact() {
        return companyContact;
    }

    public void setCompanyContact(String companyContact) {
        this.companyContact = companyContact;
    }

    public String getCompanyFax() {
        return companyFax;
    }

    public void setCompanyFax(String companyFax) {
        this.companyFax = companyFax;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getCompanyTaxNo() {
        return companyTaxNo;
    }

    public void setCompanyTaxNo(String companyTaxNo) {
        this.companyTaxNo = companyTaxNo;
    }

    public String getCompanyBankName() {
        return companyBankName;
    }

    public void setCompanyBankName(String companyBankName) {
        this.companyBankName = companyBankName;
    }

    public String getCompanyBankNo() {
        return companyBankNo;
    }

    public void setCompanyBankNo(String companyBankNo) {
        this.companyBankNo = companyBankNo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getContractTimeStr() {
        return contractTimeStr;
    }

    public void setContractTimeStr(String contractTimeStr) {
        this.contractTimeStr = contractTimeStr;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBrandNumber() {
        return brandNumber;
    }

    public void setBrandNumber(String brandNumber) {
        this.brandNumber = brandNumber;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getDealNumber() {
        return dealNumber;
    }

    public void setDealNumber(String dealNumber) {
        this.dealNumber = dealNumber;
    }

    public String getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(String dealPrice) {
        this.dealPrice = dealPrice;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCnMoney() {
        return cnMoney;
    }

    public void setCnMoney(String cnMoney) {
        this.cnMoney = cnMoney;
    }

    public String getDeliAddr() {
        return deliAddr;
    }

    public void setDeliAddr(String deliAddr) {
        this.deliAddr = deliAddr;
    }

    public String getDeliveryDateStr() {
        return deliveryDateStr;
    }

    public void setDeliveryDateStr(String deliveryDateStr) {
        this.deliveryDateStr = deliveryDateStr;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getTransAmountRemark() {
        return transAmountRemark;
    }

    public void setTransAmountRemark(String transAmountRemark) {
        this.transAmountRemark = transAmountRemark;
    }

    public String getPayFullTimeStr() {
        return payFullTimeStr;
    }

    public void setPayFullTimeStr(String payFullTimeStr) {
        this.payFullTimeStr = payFullTimeStr;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public String getSigningAddr() {
        return signingAddr;
    }

    public void setSigningAddr(String signingAddr) {
        this.signingAddr = signingAddr;
    }

    public String getOurCompanyPerson() {
        return ourCompanyPerson;
    }

    public void setOurCompanyPerson(String ourCompanyPerson) {
        this.ourCompanyPerson = ourCompanyPerson;
    }

    public String getOurCompanyContact() {
        return ourCompanyContact;
    }

    public void setOurCompanyContact(String ourCompanyContact) {
        this.ourCompanyContact = ourCompanyContact;
    }

    public String getOurCompanyFax() {
        return ourCompanyFax;
    }

    public void setOurCompanyFax(String ourCompanyFax) {
        this.ourCompanyFax = ourCompanyFax;
    }

    public String getOurCompanyPhone() {
        return ourCompanyPhone;
    }

    public void setOurCompanyPhone(String ourCompanyPhone) {
        this.ourCompanyPhone = ourCompanyPhone;
    }

    public String getOurCompanyTaxNo() {
        return ourCompanyTaxNo;
    }

    public void setOurCompanyTaxNo(String ourCompanyTaxNo) {
        this.ourCompanyTaxNo = ourCompanyTaxNo;
    }

    public String getOurCompanyBankName() {
        return ourCompanyBankName;
    }

    public void setOurCompanyBankName(String ourCompanyBankName) {
        this.ourCompanyBankName = ourCompanyBankName;
    }

    public String getOurCompanyBankNo() {
        return ourCompanyBankNo;
    }

    public void setOurCompanyBankNo(String ourCompanyBankNo) {
        this.ourCompanyBankNo = ourCompanyBankNo;
    }

    public String getOurAddress() {
        return ourAddress;
    }

    public void setOurAddress(String ourAddress) {
        this.ourAddress = ourAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getWrapSpecs() {
        return wrapSpecs;
    }

    public void setWrapSpecs(String wrapSpecs) {
        this.wrapSpecs = wrapSpecs;
    }

    public String getQualityStandardStr() {
        return qualityStandardStr;
    }

    public void setQualityStandardStr(String qualityStandardStr) {
        this.qualityStandardStr = qualityStandardStr;
    }

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public String getContractModel() {
        return contractModel;
    }

    public void setContractModel(String contractModel) {
        this.contractModel = contractModel;
    }

    public String getPayRemaindTime() {
        return payRemaindTime;
    }

    public void setPayRemaindTime(String payRemaindTime) {
        this.payRemaindTime = payRemaindTime;
    }

    public String getExtraTerm() {
        return extraTerm;
    }

    public void setExtraTerm(String extraTerm) {
        this.extraTerm = extraTerm;
    }

    public BigDecimal getBondAmount() {
        return bondAmount;
    }

    public void setBondAmount(BigDecimal bondAmount) {
        this.bondAmount = bondAmount;
    }

    public BigDecimal getPayRateAmount() {
        return payRateAmount;
    }

    public void setPayRateAmount(BigDecimal payRateAmount) {
        this.payRateAmount = payRateAmount;
    }

    public String getBuyPayFullDate() {
        return buyPayFullDate;
    }

    public void setBuyPayFullDate(String buyPayFullDate) {
        this.buyPayFullDate = buyPayFullDate;
    }

    public String getSellPayFullDate() {
        return sellPayFullDate;
    }

    public void setSellPayFullDate(String sellPayFullDate) {
        this.sellPayFullDate = sellPayFullDate;
    }

    public String getBuyPayBondDate() {
        return buyPayBondDate;
    }

    public void setBuyPayBondDate(String buyPayBondDate) {
        this.buyPayBondDate = buyPayBondDate;
    }

    public BigDecimal getBuyBondAmount() {
        return buyBondAmount;
    }

    public void setBuyBondAmount(BigDecimal buyBondAmount) {
        this.buyBondAmount = buyBondAmount;
    }

    public BigDecimal getTotalPriceNum() {
        return totalPriceNum;
    }

    public void setTotalPriceNum(BigDecimal totalPriceNum) {
        this.totalPriceNum = totalPriceNum;
    }

    public Integer getCreditDays() {
        return creditDays;
    }

    public void setCreditDays(Integer creditDays) {
        this.creditDays = creditDays;
    }
}
