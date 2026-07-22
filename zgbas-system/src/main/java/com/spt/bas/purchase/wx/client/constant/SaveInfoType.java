package com.spt.bas.purchase.wx.client.constant;

/**
 * 保存信息类型
 */
public enum SaveInfoType {

    QUOTA_TEST("1","额度测试保存信息"),

    BASE_INFO("2","上传证件保存信息"),

    ENTRUST("3","委托授权保存信息"),

    SUPPLY_INFO("4","补充信息"),

    COMPANY_ALLOWED("5","企业资料确认"),

    CFCA("6", "CFCA审批"),

    CREDIT_REFERENCE("7", "企业征信信息"),

    WAREHOUSE_INFO("8", "仓库地址信息"),

    INVOICE_INFO("9", "发票信息"),

    INSURANCE_INFO("10","保险信息"),

    VIP_INFO("11","vip信息"),

    VIP_TE_INFO("12","vip提额信息"),

    VIP_COLLECT_MONEY_INFO("13","vip提额收款审批信息"),

    CFCA_INFO("14","cfca开户"),


    ;

    private String type;

    private String typeName;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    SaveInfoType(String type, String typeName) {
        this.type = type;
        this.typeName = typeName;
    }
}
