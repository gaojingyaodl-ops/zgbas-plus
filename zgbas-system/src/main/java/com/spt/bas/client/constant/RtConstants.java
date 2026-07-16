package com.spt.bas.client.constant;

/**
 * 融拓对接常量
 * @Author: gaojy
 * @create 2022/4/8 18:46
 * @version: 1.0
 * @description:
 */
public interface RtConstants {
    // 融拓-获取Token
    String RT_GET_TOKEN = "/api/auth/oauth/token?grant_type=client_credentials&scope=server&client_id={0}&client_secret={1}";

    // 融拓-推送新增企业信息
    String RT_PUSH_COMPANY = "/api/sfzitoapi/sfzitoapi/coreBusiness/addBusiness";

    // 融拓-推送收货确认信息
    String RT_PUSH_CONFIRM_RECEIVE = "/api/sfzitoapi/sfzitoapi/confirmApproval";

    // 融拓-推送赊销预算信息
    String RT_PUSH_CREDIT_DETAIL = "/api/sfzitoapi/sfzitoapi/budget";

    // 融拓-推送采购付款信息
    String RT_PUSH_PAY_DETAIL = "/api/sfzitoapi/sfzitoapi/purchasePayment";

    // 融拓-推送采购入库信息
    String RT_PUSH_DELIVERY_IN_DETAIL = "/api/sfzitoapi/sfzitoapi/purchaseContract";

    // 融拓-推送采购收票信息
    String RT_PUSH_INVOICE_RECEIVE_DETAIL = "/api/sfzitoapi/sfzitoapi/purchaseInvoice";

    // 融拓-推送销售出库信息
    String RT_PUSH_DELIVERY_OUT_DETAIL = "/api/sfzitoapi/sfzitoapi/salesContract";

    // 融拓-推送销售开票信息
    String RT_PUSH_INVOICE_DETAIL = "/api/sfzitoapi/sfzitoapi/salesInvoice";

    // 融拓-推送销售收款信息
    String RT_PUSH_RECEIVE_DETAIL = "/api/sfzitoapi/sfzitoapi/salesPayment";
}
