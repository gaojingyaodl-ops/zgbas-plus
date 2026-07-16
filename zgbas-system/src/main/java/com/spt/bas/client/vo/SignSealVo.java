package com.spt.bas.client.vo;

/**
 * @Author: gaojy
 * @create 2022/3/3 15:37
 * @version: 1.0
 * @description:
 */
public class SignSealVo {
    /**
     * 企业ID
     */
    private Long companyId;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 公章ID
     */
    private String officialSealId;

    /**
     * 合同章ID
     */
    private String chapterContractSealId;

    /**
     * 物流章ID
     */
    private String logisticsSealId;

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

    public String getOfficialSealId() {
        return officialSealId;
    }

    public void setOfficialSealId(String officialSealId) {
        this.officialSealId = officialSealId;
    }

    public String getChapterContractSealId() {
        return chapterContractSealId;
    }

    public void setChapterContractSealId(String chapterContractSealId) {
        this.chapterContractSealId = chapterContractSealId;
    }

    public String getLogisticsSealId() {
        return logisticsSealId;
    }

    public void setLogisticsSealId(String logisticsSealId) {
        this.logisticsSealId = logisticsSealId;
    }

    public SignSealVo() {
    }
}
