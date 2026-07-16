package com.spt.bas.client.vo;

public class LiabilityText {
    /**
     * 业务员
     */
    private String matchUserName;
    /**
     * 创建时间
     */
    private String createdDate;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 合同编号
     */
    private String contractNo;
    /**
     * 货名（品名/牌号/厂商）
     */
    private String productsName;
    /**
     * 金额（C类：50%,D类：100%）
     */
    private String companyPrice;

    public String getMatchUserName() {
        return matchUserName;
    }

    public void setMatchUserName(String matchUserName) {
        this.matchUserName = matchUserName;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
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

    public String getProductsName() {
        return productsName;
    }

    public void setProductsName(String productsName) {
        this.productsName = productsName;
    }

    public String getCompanyPrice() {
        return companyPrice;
    }

    public void setCompanyPrice(String companyPrice) {
        this.companyPrice = companyPrice;
    }
}
