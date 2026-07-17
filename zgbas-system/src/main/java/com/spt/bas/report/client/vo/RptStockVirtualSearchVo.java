package com.spt.bas.report.client.vo;

import com.spt.tools.core.bean.PageSearchVo;

import java.math.BigDecimal;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/5/11 11:16
 */


public class RptStockVirtualSearchVo extends PageSearchVo {

    /**
     * 类型
     */
    private String contractTypeSearch;

    /**
     * 企业名称
     */
    private String companyNameSearch;

    /**
     * 品名
     */
    private String productNameSearch;

    /**
     * 牌号
     */
    private String brandNumberSearch;

    /**
     * 厂商
     */
    private String factoryNameSearch;

    /**
     * 数量(小)
     */
    private BigDecimal minDealNumberSearch;

    /**
     * 数量(小)
     */
    private BigDecimal maxDealNumberSearch;

    /**
     * 包装规格
     */
    private String wrapSpecsSearch;

    /**
     * 质量标准
     */
    private String qualityStandardSearch;

    public String getContractTypeSearch() {
        return contractTypeSearch;
    }

    public void setContractTypeSearch(String contractTypeSearch) {
        this.contractTypeSearch = contractTypeSearch;
    }

    public String getCompanyNameSearch() {
        return companyNameSearch;
    }

    public void setCompanyNameSearch(String companyNameSearch) {
        this.companyNameSearch = companyNameSearch;
    }

    public String getProductNameSearch() {
        return productNameSearch;
    }

    public void setProductNameSearch(String productNameSearch) {
        this.productNameSearch = productNameSearch;
    }

    public String getBrandNumberSearch() {
        return brandNumberSearch;
    }

    public void setBrandNumberSearch(String brandNumberSearch) {
        this.brandNumberSearch = brandNumberSearch;
    }

    public String getFactoryNameSearch() {
        return factoryNameSearch;
    }

    public void setFactoryNameSearch(String factoryNameSearch) {
        this.factoryNameSearch = factoryNameSearch;
    }

    public BigDecimal getMinDealNumberSearch() {
        return minDealNumberSearch;
    }

    public void setMinDealNumberSearch(BigDecimal minDealNumberSearch) {
        this.minDealNumberSearch = minDealNumberSearch;
    }

    public BigDecimal getMaxDealNumberSearch() {
        return maxDealNumberSearch;
    }

    public void setMaxDealNumberSearch(BigDecimal maxDealNumberSearch) {
        this.maxDealNumberSearch = maxDealNumberSearch;
    }

    public String getWrapSpecsSearch() {
        return wrapSpecsSearch;
    }

    public void setWrapSpecsSearch(String wrapSpecsSearch) {
        this.wrapSpecsSearch = wrapSpecsSearch;
    }

    public String getQualityStandardSearch() {
        return qualityStandardSearch;
    }

    public void setQualityStandardSearch(String qualityStandardSearch) {
        this.qualityStandardSearch = qualityStandardSearch;
    }
}
