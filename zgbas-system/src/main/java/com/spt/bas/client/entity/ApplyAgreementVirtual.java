package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 协议采购申请单
 * @Author MoonLight
 * @Date 2024/8/19 11:50
 * @Version 1.0
 */
@Entity
@Table(name = "t_apply_agreement_virtual")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyAgreementVirtual extends IdEntity implements IPmEntity {
    private static final long serialVersionUID = 6328327838993347933L;

    /**
     * 审批单ID
     */
    private Long approveId;

    /**
     * 采购编号
     */
    private String stockVirtualNo;

    /**
     * 品名CD
     */
    private String productCd;

    /**
     * 品名名称
     */
    private String productName;

    /**
     * 牌号
     */
    private String brandNumber;

    /**
     * 厂商
     */
    private String factoryName;

    /**
     * 数量
     */
    private BigDecimal dealNumber;

    /**
     * 包装规格
     */
    private String wrapSpecs;

    /**
     * 质量标准
     */
    private String qualityStandard;

    /**
     * 业务员ID
     */
    private Long matchUserId;

    /**
     * 业务员姓名
     */
    private String matchUserName;

    /**
     * 状态
     */
    private String status;

    /**
     * 企业ID
     */
    private Long companyId;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 我方企业名称
     */
    private String ourCompanyName;

    /**
     * 结算方式
     */
    private String deliveryMode;

    /**
     * 交货方式
     */
    private String deliveryType;

    /**
     * 支付方式
     */
    private String payType;

    /**
     * 定金金额
     */
    private BigDecimal payBondAmount;

    /**
     * 定金比例
     */
    private BigDecimal payRate;

    /**
     * 付全款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payFullTime;

    /**
     * 付定金日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date payBondTime;

    /**
     * 交货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date deliveryDate;

    /**
     * 交货日期补充
     */
    private String arrivalTimeExt;

    /**
     * 区域表id（省市区）
     */
    private String bsAreaId;

    /**
     * 交货地点（省市区）
     */
    private String deliveryAddr;

    /**
     * 详细地址
     */
    private String contactAddr;

    /**
     * 仓储费
     */
    private BigDecimal warehouseCost;

    /**
     * 运输费
     */
    private BigDecimal transportCost;

    /**
     * 装卸费
     */
    private BigDecimal stevedorage;

    /**
     * 含税单价
     */
    private BigDecimal dealPrice;

    /**
     * 最低销售价
     * 销售指导价
     */
    private BigDecimal minSellPrice;

    /**
     * 不含税单价
     */
    private BigDecimal dealAmountNotax;

    /**
     * 总价
     */
    private BigDecimal totalAmount;

    /**
     * 补充条款
     */
    private String extraTerm;

    /**
     * 合同模板ID
     */
    private Long templateId;

    /**
     * 合同附件ID
     */
    private String contentTemplateId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 企业账套ID
     */
    private Long enterpriseId;

    /**
     * 销售业务员ID
     */
    private Long sellMatchUserId;

    /**
     * 销售业务员名称
     */
    private String sellMatchUserName;

    public Long getApproveId() {
        return approveId;
    }

    @Override
    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public String getStockVirtualNo() {
        return stockVirtualNo;
    }

    public void setStockVirtualNo(String stockVirtualNo) {
        this.stockVirtualNo = stockVirtualNo;
    }

    public String getProductCd() {
        return productCd;
    }

    public void setProductCd(String productCd) {
        this.productCd = productCd;
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

    public BigDecimal getDealNumber() {
        return dealNumber;
    }

    public void setDealNumber(BigDecimal dealNumber) {
        this.dealNumber = dealNumber;
    }

    public String getWrapSpecs() {
        return wrapSpecs;
    }

    public void setWrapSpecs(String wrapSpecs) {
        this.wrapSpecs = wrapSpecs;
    }

    public String getQualityStandard() {
        return qualityStandard;
    }

    public void setQualityStandard(String qualityStandard) {
        this.qualityStandard = qualityStandard;
    }

    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public String getMatchUserName() {
        return matchUserName;
    }

    public void setMatchUserName(String matchUserName) {
        this.matchUserName = matchUserName;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getOurCompanyName() {
        return ourCompanyName;
    }

    public void setOurCompanyName(String ourCompanyName) {
        this.ourCompanyName = ourCompanyName;
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(String deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public BigDecimal getPayBondAmount() {
        return payBondAmount;
    }

    public void setPayBondAmount(BigDecimal payBondAmount) {
        this.payBondAmount = payBondAmount;
    }

    public BigDecimal getPayRate() {
        return payRate;
    }

    public void setPayRate(BigDecimal payRate) {
        this.payRate = payRate;
    }

    public Date getPayFullTime() {
        return payFullTime;
    }

    public void setPayFullTime(Date payFullTime) {
        this.payFullTime = payFullTime;
    }

    public Date getPayBondTime() {
        return payBondTime;
    }

    public void setPayBondTime(Date payBondTime) {
        this.payBondTime = payBondTime;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getArrivalTimeExt() {
        return arrivalTimeExt;
    }

    public void setArrivalTimeExt(String arrivalTimeExt) {
        this.arrivalTimeExt = arrivalTimeExt;
    }

    public String getBsAreaId() {
        return bsAreaId;
    }

    public void setBsAreaId(String bsAreaId) {
        this.bsAreaId = bsAreaId;
    }

    public String getDeliveryAddr() {
        return deliveryAddr;
    }

    public void setDeliveryAddr(String deliveryAddr) {
        this.deliveryAddr = deliveryAddr;
    }

    public String getContactAddr() {
        return contactAddr;
    }

    public void setContactAddr(String contactAddr) {
        this.contactAddr = contactAddr;
    }

    public BigDecimal getWarehouseCost() {
        return warehouseCost;
    }

    public void setWarehouseCost(BigDecimal warehouseCost) {
        this.warehouseCost = warehouseCost;
    }

    public BigDecimal getTransportCost() {
        return transportCost;
    }

    public void setTransportCost(BigDecimal transportCost) {
        this.transportCost = transportCost;
    }

    public BigDecimal getStevedorage() {
        return stevedorage;
    }

    public void setStevedorage(BigDecimal stevedorage) {
        this.stevedorage = stevedorage;
    }

    public BigDecimal getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(BigDecimal dealPrice) {
        this.dealPrice = dealPrice;
    }

    public BigDecimal getMinSellPrice() {
        return minSellPrice;
    }

    public void setMinSellPrice(BigDecimal minSellPrice) {
        this.minSellPrice = minSellPrice;
    }

    public BigDecimal getDealAmountNotax() {
        return dealAmountNotax;
    }

    public void setDealAmountNotax(BigDecimal dealAmountNotax) {
        this.dealAmountNotax = dealAmountNotax;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getExtraTerm() {
        return extraTerm;
    }

    public void setExtraTerm(String extraTerm) {
        this.extraTerm = extraTerm;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getContentTemplateId() {
        return contentTemplateId;
    }

    public void setContentTemplateId(String contentTemplateId) {
        this.contentTemplateId = contentTemplateId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    @Override
    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Long getSellMatchUserId() {
        return sellMatchUserId;
    }

    public void setSellMatchUserId(Long sellMatchUserId) {
        this.sellMatchUserId = sellMatchUserId;
    }

    public String getSellMatchUserName() {
        return sellMatchUserName;
    }

    public void setSellMatchUserName(String sellMatchUserName) {
        this.sellMatchUserName = sellMatchUserName;
    }

    public ApplyAgreementVirtual() {
    }
}
