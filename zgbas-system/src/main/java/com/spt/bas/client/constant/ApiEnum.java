package com.spt.bas.client.constant;

/**
 * 第三方接口枚举类
 *
 * @Author: gaojy
 * @create 2022/2/21 9:36
 * @version: 1.0
 * @description:
 */
public enum ApiEnum {
    ZY_LOGIN("zy_login_password", "获取访问凭证"),
    ZY_ORDER_ADD("zy_order_add", "下单"),
    ZY_CONFIRM_BID("zy_order_confirmBid", "确认中标"),
    ZY_ORDER_CANCEL("zy_order_cancel", "取消订单"),
    ZY_VEHICLE_LOCATION("zy_vehicle_location", "查询车辆定位"),
    ZY_VEHICLE_TRACK("zy_vehicle_track", "查询车辆历史轨迹"),
    ZY_ORDER_GET_OPERATE_LIST("zy_order_getOperateList", "查询订单操作记录"),
    WFQ_NOTE_ANALYSIS_BASIC("wfq_noteAnalysisBasic", "票据分析BASIC接口"),
    WFQ_GET_ORIGINAL_FIELD_RESULT("wfq_getOriginalFieldResult", "票据分析接口-V2.1"),
    WFQ_TAX_ANALYSIS_BASIC("wfq_taxAnalysisBasic", "税据分析BASIC接口"),
    CFCA_GENERATE_SHORT_URL("cfca_generateShortUrl", "生成短链接ShortUrl-3911"),
    CFCA_SEARCH_SIGN_SEAL("cfca_searchSignSeal", "查询印章-3014"),
    CFCA_OPEN_PERSON_ACCOUNT("cfca_openPersonAccount", "个人开户-3001"),
    CFCA_OPEN_ENTERPRISE_ACCOUNT("cfca_openEnterpriseAccount", "企业开户-3002"),
    CFCA_ADD_SIGN_SEAL("cfca_addSignSeal", "增加印章-3011"),
    CFCA_UPDATE_SIGN_SEAL("cfca_updateSignSeal", "修改印章-3012"),
    CFCA_DELETE_SIGN_SEAL("cfca_deleteSignSeal", "删除印章-3013"),
    CFCA_ADD_ENTERPRISE_TRANSACTOR("cfca_addEnterpriseTransactor", "添加企业经办人-3021"),
    CFCA_UPDATE_ENTERPRISE_TRANSACTOR("cfca_updateEnterpriseTransactor", "修改企业经办人-3022"),
    CFCA_DELETE_ENTERPRISE_TRANSACTOR("cfca_deleteEnterpriseTransactor", "删除企业经办人-3023"),
    CFCA_QUERY_ENTERPRISE_TRANSACTOR("cfca_queryEnterpriseTransactor", "查询企业经办人-3024"),
    CFCA_CREATE_CONTRACT("cfca_createContract", "创建合同-3201"),
    CFCA_GET_CONTRACT_SIGNATURE_ATTR("cfca_getContractSignatureAttr", "获得合同签名原文-3204"),
    CFCA_SIGN_CONTRACT_SIGNATURE_ATTR("cfca_signContractSignatureAttr", "合同原文签署-3205"),
    CFCA_SIGN_CONTRACT_SIGNATURE("cfca_signContractSignature", "签署合同-3206"),
    CFCA_QUERY_CONTRACT("cfca_queryContract", "查询合同-3210"),
    CFCA_DELETE_CONTRACT("cfca_deleteContract", "删除合同-3221"),
    CFCA_DOWNLOAD_CONTRACT("cfca_downloadContract", "下载合同"),
    CFCA_SEND_VERIFICATION_CODE("cfca_sendVerificationCode", "发送验证码-3101"),
    CFCA_CONFIRM_VERIFICATION_CODE("cfca_confirmVerificationCode", "确认验证码-3102"),
    CFCA_CERTIFICATE_BINDING("cfca_certificateBinding", "证书绑定-3301"),
    CFCA_CERTIFICATE_UNBINDING("cfca_certificateUnbinding", "证书解绑 3302"),
    JINXIN_AUTHENTICATION("jinxin_authentication","活体认证人脸识别"),
    CFCA_UPLOAD_CONTRACT_SIGNED("cfca_uploadContractSigned", "上传合同签署 3203"),
    CFCA_GENERATE_SHORT_URL_3912("cfca_generateShortUrl3912", "生成短链接ShortUrl-3912"),
    CFCA_UPDATE_CONTRACT_INFO("cfca_updateContactInfo", "修改联系方式-3003"),
    CFCA_QUERY_CONTRACT_INFO("cfca_QueryContactInfo", "查询联系方式-3004"),
    RT_GET_TOKEN("rt_getToken","获取Token"),
    RT_PUSH_COMPANY("rt_pushCompany","推送新增企业信息"),
    RT_PUSH_CONFIRM_RECEIVE("rt_pushConfirmReceive","推送收货确认信息"),
    RT_PUSH_CREDIT_DETAIL("rt_pushCreditDetail","推送赊销预算信息"),
    RT_PUSH_PAY_DETAIL("rt_pushPayDetail","推送采购付款信息"),
    RT_PUSH_DELIVERY_IN_DETAIL("rt_pushDeliveryInDetail","推送采购入库信息"),
    RT_PUSH_INVOICE_RECEIVE_DETAIL("rt_pushInvoiceReceiveDetail","推送采购收票信息"),
    RT_PUSH_DELIVERY_OUT_DETAIL("rt_pushDeliveryOutDetail","推送销售出库信息"),
    RT_PUSH_INVOICE_DETAIL("rt_pushInvoiceDetail","推送销售开票信息"),
    RT_PUSH_RECEIVE_DETAIL("rt_pushReceiveDetail","推送销售收款信息");



    private final String apiCode;
    private final String apiName;

    ApiEnum(String apiCode, String apiName) {
        this.apiCode = apiCode;
        this.apiName = apiName;
    }

    public String getApiCode() {
        return apiCode;
    }

    public String getApiName() {
        return apiName;
    }
}
