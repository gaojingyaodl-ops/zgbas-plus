package com.spt.bas.purchase.wx.server.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * BigDecimal 工具类
 *
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/12/20 11:11
 */

public class NumUtils {
    public static BigDecimal delNull(BigDecimal value) {
        return Objects.isNull(value) ? BigDecimal.ZERO : value;
    }

    /**
     * 加法
     *
     * @param value1 加数
     * @param value2 加数
     * @return 和
     */
    public static BigDecimal calAdd(BigDecimal value1, BigDecimal value2) {
        return delNull(value1).add(delNull(value2));
    }

    /**
     * 减法
     *
     * @param value1 减数
     * @param value2 被减数
     * @return 差
     */
    public static BigDecimal calSub(BigDecimal value1, BigDecimal value2) {
        return delNull(value1).subtract(delNull(value2));
    }

    /**
     * 乘法
     *
     * @param value1 乘数
     * @param value2 被乘数
     * @return 积
     */
    public static BigDecimal calMul(BigDecimal value1, BigDecimal value2) {
        return delNull(value1).multiply(delNull(value2));
    }

    /**
     * 除法
     *
     * @param value1 除数
     * @param value2 被除数
     * @return 商
     */
    public static BigDecimal calDiv(BigDecimal value1, BigDecimal value2) {
        return delNull(value1).divide(delNull(value2), 2, RoundingMode.HALF_UP);
    }

    /**
     * A < B
     *
     * @param value1 值A
     * @param value2 值B
     * @return 比较结果
     */
    public static Boolean compareALessB(BigDecimal value1, BigDecimal value2) {
        return delNull(value1).compareTo(delNull(value2)) < 0;
    }

    /**
     * A <= B
     *
     * @param value1 值A
     * @param value2 值B
     * @return 比较结果
     */
    public static Boolean compareALessBAndEqual(BigDecimal value1, BigDecimal value2) {
        return !compareAGreaterB(value1, value2);
    }

    /**
     * A = B
     *
     * @param value1 值A
     * @param value2 值B
     * @return 比较结果
     */
    public static Boolean compareAEqualB(BigDecimal value1, BigDecimal value2) {
        return delNull(value1).compareTo(delNull(value2)) == 0;
    }

    public static Boolean compareANotEqualB(BigDecimal value1, BigDecimal value2) {
        return !compareAEqualB(value1, value2);
    }

    /**
     * A > B
     *
     * @param value1 值A
     * @param value2 值B
     * @return 结果
     */
    public static Boolean compareAGreaterB(BigDecimal value1, BigDecimal value2) {
        return delNull(value1).compareTo(delNull(value2)) > 0;
    }

    /**
     * A >= B
     *
     * @param value1 值A
     * @param value2 值B
     * @return 比较结果
     */
    public static Boolean compareAGreaterBAndEqual(BigDecimal value1, BigDecimal value2) {
        return !compareALessB(value1, value2);
    }

    /**
     * 将Bigdecimal 转化为String 格式
     *
     * @param value 值
     * @return 结果
     */
    public static String toStr(BigDecimal value) {
        return toStr(value, 2);
    }

    /**
     * 将Bigdecimal 转化为String 格式
     *
     * @param value 值
     * @param scale 保留小数位数
     * @return 结果
     */
    public static String toStr(BigDecimal value, int scale) {
        if (Objects.isNull(value)) {
            return "0";
        }
        //DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
        return value.setScale(scale, RoundingMode.HALF_UP).toString();
    }
}
