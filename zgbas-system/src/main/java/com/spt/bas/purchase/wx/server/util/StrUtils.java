package com.spt.bas.purchase.wx.server.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2024/1/12 10:32
 */

public class StrUtils {

    public static String PLACEHOLDER = "-";

    /**
     * 如果为空则返回占位符
     *
     * @param str 字符串
     * @return 结果
     */
    public static String toPlaceholder(String str) {
        return StringUtils.isBlank(str) ? PLACEHOLDER : str;
    }
}
