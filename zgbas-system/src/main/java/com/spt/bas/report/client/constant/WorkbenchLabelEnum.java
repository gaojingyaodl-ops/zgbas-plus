package com.spt.bas.report.client.constant;


/**
 * 业务经理工作台类型枚举类
 *
 * @Author lsj
 * @Date 2024/12/12 15:22
 * @Version 1.0
 */
public enum WorkbenchLabelEnum {
    NCK("NCK", "待出库"),
    NSK("NSK", "待收款"),
    NKP("NKP", "待开票"),
    NSP("NSP", "待收票"),

    N("N", "即将到期"),
    B("B", "宽限期"),
    D("D", "催告期"),
    S("S", "逾期"),
    P("P", "诉讼"),
    
    DC("DC", "代采预算"),
    SX("SX", "赊销预算"),
    DCSX("DCSX", "代采赊销预算"),

    XZ("XZ", "本月新增"),
    BX("BX", "待保险批复"),
    HY("HY", "活跃"),
    CM("CM", "沉默"),
    
    ;

    private final String labelCode;

    private final String labelName;

    public String getLabelCode() {
        return labelCode;
    }

    public String getLabelName() {
        return labelName;
    }
    
    WorkbenchLabelEnum(String labelCode, String labelName) {
        this.labelCode = labelCode;
        this.labelName = labelName;
    }

}
