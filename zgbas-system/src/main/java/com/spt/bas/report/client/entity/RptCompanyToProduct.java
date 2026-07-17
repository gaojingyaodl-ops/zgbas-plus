package com.spt.bas.report.client.entity;

import com.spt.tools.jpa.vo.IdEntity;

public class RptCompanyToProduct extends IdEntity {
    /** 品名 */
    private String productName;
    /** 牌号 */
    private String brandNumber;
    /** 厂商 */
    private String factoryName;
    /** 企业ID */
    private Long companyId;
    /** 条数 */
    private Integer totalCount;

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

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
}
