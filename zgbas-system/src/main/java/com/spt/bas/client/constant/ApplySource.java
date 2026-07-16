package com.spt.bas.client.constant;

/**
 * 审批申请来源
 * @author shengong
 */
public enum ApplySource {
    WEBSITE("0", "核心管理系统"),

    PURCHASE("1","采购管家小程序"),

    RISK("2","risk风控系统"),
    
    CMS("3","cms浙塑网站")
    ;

    ApplySource(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private String code;
    private String name;

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
}
