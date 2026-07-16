package com.spt.bas.client.entity;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;


/**
 * 中游合同企业信息
 */
@Entity
@Table(name = "t_bs_company_dcsx")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsCompanyDcsx extends IdEntity {

    /**
     * 企业代码
     */
    private String companyCd;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 企业简称
     */
    private String companyAbbr;

    /**
     * 中间链合同计算比率
     */
    private BigDecimal chainRate;

    /**
     * 中间链合同计算账期
     */
    private Long chainDays;

    /**
     * 中间链加价比例
     */
    private BigDecimal premiumRate;

    /**
     * 中间链加价金额
     */
    private BigDecimal premiumAmount;

    /**
     * 法人代表
     */
    private String companyPerson;

    /**
     * 联系人
     */
    private String companyContact;

    /**
     * 电话
     */
    private String companyPhone;

    /**
     * 传真
     */
    private String companyFax;

    /**
     * 税号
     */
    private String companyTaxNo;

    /**
     * 开户行
     */
    private String companyBankName;

    /**
     * 账号
     */
    private String companyCardId;

    /**
     * 签订地点
     */
    private String signingAddr;

    /**
     * 是否为代采方
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean dcsxFlg = true;

    /**
     * 是否有效
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean enableFlg = false;

    /**
     * 是否开通电子签
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean signFlg = false;

    /**
     * 是否自动签署
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean autoSignFlg = false;

    /**
     * 单价计算类型
     * 1-固定收益 * 周期  【单价 = 采购单价 * (1 + 0.0005 * 5天)】
     * 2-固定收益 * 周期(税后)  【单价 = 采购单价 * (1 + 0.0004 * 5天) * 1.13】
     * 3-固定年化 * 账期  【单价 = 采购单价 * (1 + 0.1/365 * 账期)】
     * 4-固定加单价 【单价 = 采购单价 + 中间链加价金额(premiumAmount)】
     */
    private String calculateType;

    /**
     * 年化收益
     */
    private BigDecimal annualizedRevenue;

    /**
     * 付款方式
     */
    private  String chainPayType;

    //排序序号
    private Long dispOrderNo;
    
    private String address;

    /**
     * 是否参与审批
     */
    private  Boolean approverFlag;

//    /**
//     * 资金方余额
//     */
//    private BigDecimal fundAmount = BigDecimal.ZERO;

    /**
     * 资金方余额 - 青光
     */
    private BigDecimal fundAmountQg = BigDecimal.ZERO;

    /**
     * 资金方余额 - 网塑
     */
    private BigDecimal fundAmountWs = BigDecimal.ZERO;

    /**
     * 是否开通余额
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean fundFlg = false;

    /**
     * 是否我司
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean ourCompanyFlag = false;

    /**
     * 保费余额
     * @return
     */
    private BigDecimal insuranceAmount;

    public Boolean getOurCompanyFlag() {
        return Objects.nonNull(ourCompanyFlag) && ourCompanyFlag;
    }

    public void setOurCompanyFlag(Boolean ourCompanyFlag) {
        this.ourCompanyFlag = ourCompanyFlag;
    }

    public Boolean getApproverFlag() {
        return approverFlag;
    }

    public void setApproverFlag(Boolean approverFlag) {
        this.approverFlag = approverFlag;
    }

    public Long getDispOrderNo() {
        return dispOrderNo;
    }

    public void setDispOrderNo(Long dispOrderNo) {
        this.dispOrderNo = dispOrderNo;
    }

    public String getChainPayType() {
        return chainPayType;
    }

    public void setChainPayType(String chainPayType) {
        this.chainPayType = chainPayType;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getCompanyFax() {
        return companyFax;
    }

    public void setCompanyFax(String companyFax) {
        this.companyFax = companyFax;
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

    public String getCompanyCardId() {
        return companyCardId;
    }

    public void setCompanyCardId(String companyCardId) {
        this.companyCardId = companyCardId;
    }

    public String getSigningAddr() {
        return signingAddr;
    }

    public void setSigningAddr(String signingAddr) {
        this.signingAddr = signingAddr;
    }

    public BigDecimal getChainRate() {
        return chainRate;
    }

    public void setChainRate(BigDecimal chainRate) {
        this.chainRate = chainRate;
    }

    public Long getChainDays() {
        return chainDays;
    }

    public void setChainDays(Long chainDays) {
        this.chainDays = chainDays;
    }

    public String getCompanyCd() {
        return companyCd;
    }

    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    public Boolean getDcsxFlg() {
        return dcsxFlg;
    }

    public void setDcsxFlg(Boolean dcsxFlg) {
        this.dcsxFlg = dcsxFlg;
    }

    public BigDecimal getPremiumRate() {
        return premiumRate;
    }

    public void setPremiumRate(BigDecimal premiumRate) {
        this.premiumRate = premiumRate;
    }

    public Boolean getEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(Boolean enableFlg) {
        this.enableFlg = enableFlg;
    }

    public String getCalculateType() {
        return calculateType;
    }

    public void setCalculateType(String calculateType) {
        this.calculateType = calculateType;
    }

    public BigDecimal getAnnualizedRevenue() {
        return Objects.isNull(annualizedRevenue) ? BigDecimal.ZERO : annualizedRevenue;
    }

    public void setAnnualizedRevenue(BigDecimal annualizedRevenue) {
        this.annualizedRevenue = annualizedRevenue;
    }

    public BigDecimal getPremiumAmount() {
        return Objects.isNull(premiumAmount) ? BigDecimal.ZERO : premiumAmount;
    }

    public void setPremiumAmount(BigDecimal premiumAmount) {
        this.premiumAmount = premiumAmount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompanyAbbr() {
        return companyAbbr;
    }

    public void setCompanyAbbr(String companyAbbr) {
        this.companyAbbr = companyAbbr;
    }

    public Boolean getSignFlg() {
        return signFlg;
    }

    public void setSignFlg(Boolean signFlg) {
        this.signFlg = signFlg;
    }

    public Boolean getAutoSignFlg() {
        return autoSignFlg;
    }

    public void setAutoSignFlg(Boolean autoSignFlg) {
        this.autoSignFlg = autoSignFlg;
    }

//    public BigDecimal getFundAmount() {
//        return fundAmount;
//    }
//
//    public void setFundAmount(BigDecimal fundAmount) {
//        this.fundAmount = fundAmount;
//    }

    public BigDecimal getFundAmountQg() {
        return Objects.nonNull(fundAmountQg) ? fundAmountQg : BigDecimal.ZERO;
    }

    public void setFundAmountQg(BigDecimal fundAmountQg) {
        this.fundAmountQg = fundAmountQg;
    }

    public BigDecimal getFundAmountWs() {
        return Objects.nonNull(fundAmountWs) ? fundAmountWs : BigDecimal.ZERO;
    }

    public void setFundAmountWs(BigDecimal fundAmountWs) {
        this.fundAmountWs = fundAmountWs;
    }

    public Boolean getFundFlg() {
        return Objects.nonNull(fundFlg) ? fundFlg : Boolean.FALSE;
    }

    public void setFundFlg(Boolean fundFlg) {
        this.fundFlg = fundFlg;
    }

    public BigDecimal getInsuranceAmount() {
        return insuranceAmount;
    }

    public void setInsuranceAmount(BigDecimal insuranceAmount) {
        this.insuranceAmount = insuranceAmount;
    }
}
