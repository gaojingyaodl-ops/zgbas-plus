/**
 * 
 */
package com.spt.tools.core.exception;

import java.util.Date;
import java.util.regex.Pattern;

import com.spt.tools.core.date.DateOperator;

/**
 * @author jian
 *
 */
public class ValidateUtil {

	public static final String serviceTimeOut_start = "17:30";
	public static final String serviceTimeOut_end = "18:00";

	/** 服务时间验证 */
	public static boolean isOutServiceTime(String start, String end) {
		boolean flag = false;
		Date now = new Date();
		int h_start = Integer.valueOf(start.split(":")[0]);
		int m_start = Integer.valueOf(start.split(":")[1]);
		int h_end = Integer.valueOf(end.split(":")[0]);
		int m_end = Integer.valueOf(end.split(":")[1]);
		Date startTime = DateOperator.createTime(h_start, m_start);
		Date endTime = DateOperator.createTime(h_end, m_end);
		if (now.after(startTime) && now.before(endTime)) {
			flag = true;
		}
		return flag;
	}
	
	public static boolean isMobile(String str) {
		boolean flag = Pattern.matches("1(3|4|5|6|7|8|9)\\d{9}", str);
		return flag;
	}
}
