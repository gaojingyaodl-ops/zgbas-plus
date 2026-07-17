package com.spt.bas.server.enums;

/**
 * @Author MoonLight
 * @Date 2024/7/15 14:48
 * @Version 1.0
 */
public enum FundFlowEnum {
    Recharge("AD", "充值"),
    RechargeCancel("AC","充值作废"),
    Payment("PD", "代采赊销付款"),
    PaymentCancel("PC", "代采赊销作废"),
    VirtualPayment("VD", "采购付款"),
    VirtualPaymentCancel("VC", "库存采购作废"),
    ReceiveRefund("RD", "代采赊销收退款"),
    ReceiveRefundCancel("RC", "代采赊销收退款作废"),
    VirtualRefund("VR", "采购退款"),
    VirtualRefundCancel("VL", "库存采购退款作废"),
    ;

    private final String flowType;
    private final String flowName;

    public String getFlowType() {
        return flowType;
    }

    public String getFlowName() {
        return flowName;
    }

    FundFlowEnum(String flowType, String flowName){
        this.flowType = flowType;
        this.flowName = flowName;
    }
}
