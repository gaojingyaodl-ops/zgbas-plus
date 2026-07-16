package com.spt.bas.client.constant;

/**
 * 量身定制配置字段
 */
public enum CustomSetting {

    CUSTOM_SETTING("customSetting", "量身定制自定义配置字段", "量身定制自定义配置字段"),
    /**
     * 公司类型
     */
    COMPANY_TYPE("companyType","公司类型","公司类型"),
    CUSTOM_COMPANY_SOURCE("customCompanySource", "行业类型", "行业类型"),
    CUSTOM_REPAYMENT_PERIOD("customRepaymentPeriod", "还款周期", "账期"),

    CUSTOM_MY_ROLE("customMyRole","我的角色","我的角色");



    private String code;

    private String name;

    private String remark;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    CustomSetting(String code, String name, String remark) {
        this.code = code;
        this.name = name;
        this.remark = remark;
    }
}
