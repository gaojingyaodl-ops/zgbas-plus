package com.spt.bas.client.constant;

/**
 * 客户授信额度流水类型
 *
 * @Author: gaojy
 * @create 2022/11/24 16:15
 * @version: 1.0
 * @description:
 */
public enum CreditFlowEnum {
    AA("AA", "预算申请单发起，更新审批占用额度"),
    AC("AC", "预算申请单追回/驳回，更新审批占用额度"),
    AD("AD", "预算审批单通过，更新已使用额度"),
    UR("UR", "收款审批通过，更新已使用额度"),
    UC("UC", "收款审批作废，更新已使用额度"),
    CC("CC", "审批单/合同作废，更新已使用额度"),
    CJ("CJ", "合同金额调整，更新已使用额度");

    private String flowCode;
    private String flowName;

    CreditFlowEnum(String flowCode, String flowName) {
        this.flowCode = flowCode;
        this.flowName = flowName;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public String getFlowName() {
        return flowName;
    }
}
