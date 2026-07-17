package com.spt.bas.report.client.constant;

import java.util.ArrayList;
import java.util.List;

public interface ReportConstant {
    String SERVER_NAME = "spt-bas-report";
    /**
     * 指定服务地址，默认为空字符串
     *
     * @eg http://127.0.0.1:8001
     */
    String SERVER_BEAN_NAME = "reportServerConfig";
    String SERVER_URL = "#{" + SERVER_BEAN_NAME + ".url}"; //"#{basServerConfig.url}";
    String SERVER_URL_KEY = "spt.bas.report.url";

    String DICT_TYPE_BUSINESS_ZY = "ZY";        //自营
    String DICT_TYPE_BUSINESS_SX = "SX";        //赊销
    String DICT_TYPE_BUSINESS_DL = "DL";        //代理
    String DICT_TYPE_BUSINESS_BL = "BL";        //保理
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

    String CALCULATE_CONFIG_KEY = "CALCULATE_PAEAM";
    String CALCUALTE_INSURANCE_RATE_KEY = "INSURANCE_RATE_PARAM";

    Integer SALES_WEEK_TYPE_1 = 1;          // 赊销(销售周)
    Integer SALES_WEEK_TYPE_2 = 2;          // 代采(销售周)
    Integer SALES_WEEK_TYPE_3 = 3;          // 自营(销售周)
    Integer SALES_WEEK_TYPE_4 = 4;          // 合计(销售周)

    Integer SALES_MONTH_TYPE_5 = 5;         // 赊销(销售月)
    Integer SALES_MONTH_TYPE_6 = 6;         // 代采(销售月)
    Integer SALES_MONTH_TYPE_7 = 7;         // 自营(销售月)
    Integer SALES_MONTH_TYPE_8 = 8;         // 合计(销售月)

    List<Integer> SALES_WEEK_TYPE = new ArrayList<Integer>(3){
        private static final long serialVersionUID = -2666181917283243727L;
        {
        add(SALES_WEEK_TYPE_1);
        add(SALES_WEEK_TYPE_2);
        add(SALES_WEEK_TYPE_3);
        }
    };

    List<Integer> SALES_MONTH_TYPE = new ArrayList<Integer>(3){
        private static final long serialVersionUID = 9186377519470792087L;
        {
            add(SALES_MONTH_TYPE_5);
            add(SALES_MONTH_TYPE_6);
            add(SALES_MONTH_TYPE_7);
        }
    };

}
