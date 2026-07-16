package com.spt.bas.client.vo;

public class CtrContractFileDownloadVo {

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 我方抬头
     */
    private String ourCompanyName;

    /**
     * 合同编号
     */
    private String contractNo;

    private String buyContentFileId;
    private String sellContentFileId;
    private String fileId;
    private String dcsxContractFileId;
    private String contractType;
    private String businessType;

    private String requestUrl;
    private Long enterpriseId;
    private Long userId;

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

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getBuyContentFileId() {
        return buyContentFileId;
    }

    public void setBuyContentFileId(String buyContentFileId) {
        this.buyContentFileId = buyContentFileId;
    }

    public String getSellContentFileId() {
        return sellContentFileId;
    }

    public void setSellContentFileId(String sellContentFileId) {
        this.sellContentFileId = sellContentFileId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getDcsxContractFileId() {
        return dcsxContractFileId;
    }

    public void setDcsxContractFileId(String dcsxContractFileId) {
        this.dcsxContractFileId = dcsxContractFileId;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
