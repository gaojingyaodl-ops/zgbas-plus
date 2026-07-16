package com.spt.tools.core.number;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * 数字格式化类
 * 
 * @author huangjian
 * */
public class NumberUtil {
	public static final String PATTERN_NUM = "^[-]{0,1}[0-9]+(\\.[0-9]{0,6}){0,1}$";

	public static String formartMonth(int month) {
		// DecimalFormat formattor = new DecimalFormat("00");
		return formatNumber(month, "00");
	}

	public static String formartDay(int day) {
		return formatNumber(day, "00");
	}

	public static String formatNumber(Object value) {
		return formatNumber(value, "#,###.##", false);
	}
	/**数量格式化*/
	public static String formatDealNum(Object value) {
		return formatNumber(value, "#.###", false);
	}
	/**价格格式化*/
	public static String formatDealPrice(Object value) {
		return formatNumber(value, "#,###.##", false);
	}

	public static String formatNumber(Object value, String formatter) {
		return formatNumber(value, formatter, false);
	}

	private static boolean isEmpty(Object o) {
		return o != null ? o.toString().trim().length() == 0 : true;
	}

	/**
	 * @param value
	 * @param formatter
	 *            格式化字符串 如:0.00
	 * @param negative2Null
	 *            负数返回空串
	 * @return
	 */
	public static String formatNumber(Object value, String formatter, boolean negative2Null) {
		String rtValue = "";
		if (!isEmpty(value)) {
			DecimalFormat formattor = new DecimalFormat(formatter);
			if (isFloatNumber(value)) {
				rtValue = formattor.format(value);
			} else if (isNumber(value.toString())) {
				BigDecimal bdValue = new BigDecimal(value.toString());
				rtValue = formattor.format(bdValue);
			} else {
				// throw new NumberFormatException();
				rtValue = value.toString();
			}
		}
		if (negative2Null) {
			rtValue = isZero(rtValue) ? "" : rtValue.trim();
		}
		return rtValue;
	}

	public static boolean isNumber(String str) {
		return Pattern.matches(PATTERN_NUM, str);
	}

	/**
	 * 2位小数
	 * 
	 * @param value
	 * @return
	 */
	public static String formatDecimal2(Object value) {
		return formatNumber(value, "0.00", false);
	}

	/**
	 * 2位小数
	 * 
	 * @param value
	 * @return 如果小于0返回"",否则返回二位小数
	 */
	public static String formatNegativeNull(Object value) {
		return formatNumber(value, "0.00", true);
	}

	private static boolean isFloatNumber(Object value) {
		return value instanceof BigDecimal || value instanceof Float || value instanceof Double
				|| value instanceof Integer;
	}

	/**
	 * 
	 * 
	 * @param value
	 * @return 如果等于"0"返回true,或者返回false
	 */
	private static boolean isZero(String value) {
		if ((!isEmpty(value) && Float.valueOf(value) == 0) || isEmpty(value))
			return true;
		else
			return false;
	}

	public static Double formatDouble(String str) {

		if (StringUtils.isBlank(str))
			return new Double(0);
		if ("NaN.undefined".equals(str.trim()))
			return new Double(0);

		String regEx = "[^\\d\\.-]";
		// Pattern p=Pattern.compile(regEx);
		String val = str == null ? "" : str.replaceAll(regEx, "");
		return Double.valueOf(StringUtils.isEmpty(val) ? "0" : val);
	}

}
