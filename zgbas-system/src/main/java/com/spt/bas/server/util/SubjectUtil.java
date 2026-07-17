package com.spt.bas.server.util;

import com.spt.bas.client.constant.BasConstants;
import com.spt.tools.core.number.NumberUtil;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class SubjectUtil {
    /**
     * 格式化标题，将标题按照顺序传进来即可
     *
     * @param arg 标题参数
     * @return 格式化后的标题
     */
    public static String formatSubject(String... arg){
        String collect = Arrays.stream(arg).filter(StringUtils::isNotBlank).collect(Collectors.joining(RuleUtil.connector));
        // 去除]后面的，分隔符
        return collect.replaceAll(RuleUtil.wrapperEnd + RuleUtil.connector , RuleUtil.wrapperEnd);
    }

    public static String getBusinessName(String businessType, Boolean matchCreditFlg) {
        String businessName = "";
        if (StringUtils.equals(BasConstants.BUSINESS_TYPE_KC_CG, businessType)) {
            businessName = "库存采购";
        } else if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, businessType)) {
            businessName = "代采托盘";
        } else if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, businessType) && Boolean.TRUE.equals(matchCreditFlg)) {
            businessName = "代采赊销";
        } else if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, businessType) && Boolean.FALSE.equals(matchCreditFlg)) {
            businessName = "代采";
        } else if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_XS, businessType) || StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_CG, businessType)) {
            businessName = "自营";
        }
        return businessName;
    }

    /**
     * 格式化金额(千分位两位小说)
     * @param val 要格式化的值
     * @param monetaryUnit 单位
     * @return
     */
    public static String formatMoney(BigDecimal val, String monetaryUnit) {
        if (Objects.isNull(val)) {
            return "";
        } else if (val.compareTo(BigDecimal.ZERO) == 0) {
            return "0.00";
        } else {
            return NumberUtil.formatNumber(val, "#,###.00") + monetaryUnit;
        }
    }
}
