package com.spt.bas.client.vo;

/**
 * @Author: gaojy
 * @create 2022/2/11 14:09
 * @version: 1.0
 * @description:
 */
public class BsPayFullRuleVo {
    private Long companyId;
    private String companyName;
    private Integer addPayFullDate;

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

    public Integer getAddPayFullDate() {
        return addPayFullDate;
    }

    public void setAddPayFullDate(Integer addPayFullDate) {
        this.addPayFullDate = addPayFullDate;
    }
}
