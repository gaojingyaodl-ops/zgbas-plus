package com.spt.bas.server.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class RuleUtil {
    /**
     * 连接符
     */
     public static final String connector="，";

    /**
     * 包裹结束符
     */
    public static final String wrapperEnd="]";

    /**
     * 金额单位
     */
    public static final String  monetaryUnit="元";

    /**
     * 日期单位
     */
    public static final String dateUnit="天";

    /**
     * 重量单位
     */
    public static final String weightUnit="吨";

    public static String companyNameSubString(String companyName){
        if (StringUtils.isNotBlank(companyName)){
            return companyName.replaceAll("(?:股份有限公司|贸易有限公司|国际贸易有限公司|科技有限公司|材料有限公司|供应链管理有限公司|有限公司)", "");
        }
        return companyName;
    }
}
