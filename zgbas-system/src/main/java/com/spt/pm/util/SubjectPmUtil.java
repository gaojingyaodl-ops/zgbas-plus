package com.spt.pm.util;

import com.spt.tools.core.number.NumberUtil;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class SubjectPmUtil {
    /**
     * 格式化标题，将标题按照顺序传进来即可
     *
     * @param arg 标题参数
     * @return 格式化后的标题
     */
    public static String formatSubject(String... arg){
        return Arrays.stream(arg).filter(StringUtils::isNotBlank).collect(Collectors.joining(RuleUtils.connector));
    }

    /**
     * 格式化金额(千分位两位小数)
     * @param val 要格式化的值
     * @param monetaryUnit 单位
     * @return
     */
    public static String formatMoney(BigDecimal val, String monetaryUnit){
        if (Objects.isNull(val)) {
            return "";
        } else if (val.compareTo(BigDecimal.ZERO) == 0) {
            return "0.00";
        } else {
            return NumberUtil.formatNumber(val, "#,###.00") + monetaryUnit;
        }
    }

    public static String formatMoney2(BigDecimal val, String monetaryUnit) {
        if (val == null) {
            return "";
        }
        return val.setScale(2, RoundingMode.HALF_UP)
                .toPlainString()
                .replaceAll("(\\d)(?=(\\d{3})+\\.)", "$1,")
                + monetaryUnit;
    }

}