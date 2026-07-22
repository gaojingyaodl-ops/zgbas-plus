package com.spt.bas.purchase.wx.client.constant;

public interface PurchaseWxConstant {
    String SERVER_NAME = "purchase-wx-server";
    /**
     * 指定服务地址，默认为空字符串
     *
     * @eg http://127.0.0.1:8001
     */
    String SERVER_BEAN_NAME = "purchaseWxServerConfig";
    String SERVER_URL = "#{" + SERVER_BEAN_NAME + ".url}"; //"#{basServerConfig.url}";
    String SERVER_URL_KEY = "spt.bas.purchaseWx.url";

    String DICT_TYPE_BUSINESS_ZY = "ZY";        //自营
    String DICT_TYPE_BUSINESS_SX = "SX";        //赊销
    String DICT_TYPE_BUSINESS_DL = "DL";        //代理
    String DICT_TYPE_BUSINESS_SY = "SY";        //质押
    String BUSINESS_TYPE_ZY_CG = "ZY-CG";        //自营采购
    String BUSINESS_TYPE_ZY_XS = "ZY-XS";        //自营销售
    String BUSINESS_TYPE_ZY_BB = "ZY-BB";        //背靠背
    String BUSINESS_TYPE_ZY_JK = "ZY-JK";        //自营进口
    String BUSINESS_TYPE_SX_SX = "SX-SX";        //赊销
    String BUSINESS_TYPE_SX_HK = "SX-HK";        //货到付款
    String BUSINESS_TYPE_DL_KZ = "DL-KZ";        //代理开证
    String BUSINESS_TYPE_DL_DC = "DL-DC";        //国企代采
    String BUSINESS_TYPE_SY_CG = "SY-CG";        //质押采购
    String BUSINESS_TYPE_SY_XS = "SY-XS";        //质押销售

    String OUR_COMPANY_NAME_ZJWS = "浙江网塑电子商务股份有限公司";
    String OUR_COMPANY_NAME_NBWS = "网塑（宁波）化工有限公司";
    String OUR_COMPANY_NAME_SHWS = "网塑（上海）化工有限公司";

    /**
     * 开票类型 开货款发票
     */
    String APPLY_INVOICE_TYPE_AMOUNT = "0";

    /**
     * 开票类型 开服务费发票
     */
    String APPLY_INVOICE_TYPE_SERVICE_AMOUNT = "1";

}
