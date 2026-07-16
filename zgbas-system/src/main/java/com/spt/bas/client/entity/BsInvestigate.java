package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 *  企业实地调研信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-01-12 16:56
 */
@Entity
@Table(name = "t_bs_investigate")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@DynamicInsert
@DynamicUpdate
public class BsInvestigate extends IdEntity {

    private static final long serialVersionUID = 7270300786317634517L;

    private Long companyId;

    /**
     * 所在领域
     */
    private String companyField;
    /**
     * 所在行业
     */
    private String productionField;
    /**
     * 产业链环节，可以多选
     */
    private String companyLink;
    /**
     * 供求环境
     */
    private String supplyDemand;
    /**
     * 企业收入
     */
    private String companyIncome;
    /**
     * 行业地址
     */
    private String industryStatus;
    /**
     * 行业规模
     */
    private String industryScale;
    /**
     * 市场占有率
     */
    private String marketShare;
    /**
     * 口碑评价
     */
    private String wordMouth;
    /**
     * 景气度评价
     */
    private String goodEvaluation;
    /**
     * 使用原料
     */
    private String rawMaterial;
    /**
     * 企业产品
     */
    private String product;
    /**
     * 固定资产，可以多选
     */
    private String fixedAssets;
    /**
     * 流动资产，可以多选
     */
    private String currentAssets;

    /**
     * 股东背景
     */
    private String shareholder;

    /**
     * 法人/控制人学历
     */
    private String legalRepresentEduRecord;

    /**
     * 经营年限
     */
    private String operatingYears;

    public String getCompanyField() {
        return companyField;
    }

    public void setCompanyField(String companyField) {
        this.companyField = companyField;
    }

    public String getProductionField() {
        return productionField;
    }

    public void setProductionField(String productionField) {
        this.productionField = productionField;
    }

    public String getCompanyLink() {
        return companyLink;
    }

    public void setCompanyLink(String companyLink) {
        this.companyLink = companyLink;
    }

    public String getSupplyDemand() {
        return supplyDemand;
    }

    public void setSupplyDemand(String supplyDemand) {
        this.supplyDemand = supplyDemand;
    }

    public String getCompanyIncome() {
        return companyIncome;
    }

    public void setCompanyIncome(String companyIncome) {
        this.companyIncome = companyIncome;
    }

    public String getIndustryStatus() {
        return industryStatus;
    }

    public void setIndustryStatus(String industryStatus) {
        this.industryStatus = industryStatus;
    }

    public String getIndustryScale() {
        return industryScale;
    }

    public void setIndustryScale(String industryScale) {
        this.industryScale = industryScale;
    }

    public String getMarketShare() {
        return marketShare;
    }

    public void setMarketShare(String marketShare) {
        this.marketShare = marketShare;
    }

    public String getWordMouth() {
        return wordMouth;
    }

    public void setWordMouth(String wordMouth) {
        this.wordMouth = wordMouth;
    }

    public String getGoodEvaluation() {
        return goodEvaluation;
    }

    public void setGoodEvaluation(String goodEvaluation) {
        this.goodEvaluation = goodEvaluation;
    }

    public String getRawMaterial() {
        return rawMaterial;
    }

    public void setRawMaterial(String rawMaterial) {
        this.rawMaterial = rawMaterial;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getFixedAssets() {
        return fixedAssets;
    }

    public void setFixedAssets(String fixedAssets) {
        this.fixedAssets = fixedAssets;
    }

    public String getCurrentAssets() {
        return currentAssets;
    }

    public void setCurrentAssets(String currentAssets) {
        this.currentAssets = currentAssets;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getShareholder() {
        return shareholder;
    }

    public void setShareholder(String shareholder) {
        this.shareholder = shareholder;
    }

    public String getLegalRepresentEduRecord() {
        return legalRepresentEduRecord;
    }

    public void setLegalRepresentEduRecord(String legalRepresentEduRecord) {
        this.legalRepresentEduRecord = legalRepresentEduRecord;
    }

    public String getOperatingYears() {
        return operatingYears;
    }

    public void setOperatingYears(String operatingYears) {
        this.operatingYears = operatingYears;
    }
}
