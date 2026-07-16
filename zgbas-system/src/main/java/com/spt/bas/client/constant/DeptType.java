package com.spt.bas.client.constant;

/**
 * 机构类型
 * @author shengong
 */
public enum DeptType {

    DEPT("dept", "部门"),

    GROUP("group", "集团"),

    CENTER("center", "中心"),

    COMPANY("company", "公司"),

    TEAM("team", "小组"),

    ;
    private String type;

    private String desc;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    DeptType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
