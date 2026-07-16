/*
 * Created on 2005-6-20
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.spt.tools.core.date;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.validator.GenericValidator;

/**
 * 日期转化类
 * 
 * @author huangj 2009-12-16
 */
class DateParser {

	private static final Calendar calendar = Calendar.getInstance();
	public static String FORMAT_STR_DATETAMP = "yyyyMMdd";
	public static String FORMAT_STR = "yyyy-MM-dd";

	public static String FORMAT_STR_WITH_TIME = "yyyy-MM-dd HH:mm";

	public static String FORMAT_STR_WITH_TIME_S = "yyyy-MM-dd HH:mm:ss";
	// 时间戳
	public static String FORMAT_STR_WITH_TIMESTAMP = "yyyyMMddHHmmss";
	public static String FORMAT_STR_WITH_ChineseCHINESE_S = "yyyy年MM月dd日  HH:mm:ss";
	public static Locale defaultLocale = Locale.getDefault();
	
	private static String[] formats = new String[] { "yyyy-MM-dd","yyyyMMdd", "yyyy-MM-d", "yyyy-M-dd", "yy-MM-dd", "yyyy/MM/dd",
			"yyyy/MM/d", "yyyy/M/dd", "yyyy/M/d", "yyyy\\MM\\dd", "yyyy\\MM\\d", "yyyy\\M\\dd", "yyyy\\M\\d","dd-MM-yy" };

	/**
	 * 将字符串转化为日期，允许字符串以-或\或/来分隔年月日
	 */
	public static Date parse(String dateString) throws RuntimeException {
		if (dateString == null)
			return null;
		Date date = null;
		try {
			date = DateUtils.parseDate(dateString, formats);
		} catch (ParseException e) {
			try {
				date = DateUtils.parseDate(dateString, FORMAT_STR_WITH_TIME,FORMAT_STR_WITH_TIME_S,FORMAT_STR_WITH_TIMESTAMP,FORMAT_STR_WITH_ChineseCHINESE_S);
			} catch (Exception ex) {
				throw new RuntimeException("error.dateFormatError");
			}
		}
		return date;
	}

	/**
	 * 将字符串转化为日期，日期格式只允许-来分隔年月日
	 * 
	 * @param dateStr    要被转化的日期（或和时间）
	 * @param isMustTime 是否包含时间
	 * @return 被转化后的日期，如果无法转化则返回null
	 */
	public static Date parse(String dateStr, boolean isMustTime) {
		try {
			if (isMustTime) {
				return DateUtils.parseDate(dateStr, FORMAT_STR_WITH_TIME);
			} else {
				return DateUtils.parseDate(dateStr, FORMAT_STR);
			}
		} catch (Exception e) {
			return null;
		}
	}

	public static Date localDateTime2Date(LocalDateTime localDateTime) {
		ZoneId zone = ZoneId.systemDefault();
		Date date = Date.from(localDateTime.atZone(zone).toInstant());
		return date;
	}

	public static Date localDate2Date(LocalDate localDate) {
		ZoneId zone = ZoneId.systemDefault();
		Date date = Date.from(localDate.atStartOfDay().atZone(zone).toInstant());
		return date;
	}

	public static LocalDateTime date2LocalDateTime(Date date) {
		Instant instant = date.toInstant();
		ZoneId zone = ZoneId.systemDefault();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
		return localDateTime;
	}

	public static LocalDate date2LocalDate(Date date) {
		Instant instant = date.toInstant();
		ZoneId zone = ZoneId.systemDefault();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
		LocalDate localDate = localDateTime.toLocalDate();
		return localDate;
	}

	/**
	 * 按照指定的格式将字符串转化为日期，如果无法转化则会抛出异常
	 */
	public static Date parse(String dateString, String formatString) {
		if (StringUtils.isBlank(dateString))
			return null;
		try {
			return DateUtils.parseDate(dateString, formatString);
		} catch (ParseException ex1) {
			try {
				return parse(dateString);
			} catch (Exception ex2) {
				throw new RuntimeException("error.dateFormatError");
			}
		}
	}

	/**
	 * 判断一个字符串是否能转换成日期,空串也会返回 true (空串解析成日期时返回 null)
	 */
	public static boolean isValidateDateString(String dateString) {
		if (StringUtils.isEmpty(dateString)) {
			return false;
		}
		boolean isDate = false;
		for (String formt : formats) {
			isDate = GenericValidator.isDate(dateString, formt, true);
			if (isDate) {
				break;
			}
		}
		if (!isDate) {
			isDate = GenericValidator.isDate(dateString, FORMAT_STR_WITH_TIME, true)
					|| GenericValidator.isDate(dateString, FORMAT_STR_WITH_TIMESTAMP, true)
					|| GenericValidator.isDate(dateString, FORMAT_STR_WITH_ChineseCHINESE_S, true)
					|| GenericValidator.isDate(dateString, FORMAT_STR_WITH_TIME_S, true);
		}

		return isDate;
	}

	/**
	 * 将一个日期转化为字符串
	 * 
	 * @param date 要被转化的日期，不包含时间
	 */
	public static String formatDate(Date date) {
		return formatDate(date, false);
	}

	/**
	 * 将一个日期转化为字符串，包含时间
	 */
	public static String formatDate(Date date, boolean withTime) {
		if (withTime)
			return formatDate(date, FORMAT_STR_WITH_TIME);
		else
			return formatDate(date, FORMAT_STR);
	}

	/**
	 * 按照指定的格式将日期（允许包含时间）转化为字符串
	 */
	public static String formatDate(Date date, String formatString) {
		return DateFormatUtils.format(date, formatString);
//		return formatDate(date, DateTimeFormatter.ofPattern(formatString));
	}

	/**
	 * 得到小于当前时间一个月的时间
	 */
	public static Date getDateLeNow() {
		Calendar calendarIn = Calendar.getInstance();
		calendarIn.setTime(new Date());
		calendarIn.roll(Calendar.MONTH, -1);
		return calendarIn.getTime();
	}

	/**
	 * 得到当前月的时间
	 */
	public static Date getDateNow() {
		Calendar calendarIn = Calendar.getInstance();
		calendarIn.setTime(new Date());
		calendarIn.roll(Calendar.MONTH, 0);
		return calendarIn.getTime();
	}

	public static Date addDays(Date date, int days) {
		if (date == null)
			return null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_YEAR, days);
		return calendar.getTime();
	}
	
	/**
	 * 把一个日期值加上相应的小时数
	 * @return
	 */
	public static Date addHours(Date date, int hours) {
		if (date == null)
			return null;
		calendar.setTime(date);
		calendar.add(Calendar.HOUR_OF_DAY, hours);
		return calendar.getTime();
	}

	/**
	 * 计算从开始的日期到结束的日期之间有几天
	 * 
	 * @param startDate 开始的日期
	 * @param endDate   结束的日期
	 * @return 两个日期之间的天数
	 */
	public static Long getDays(Date startDate, Date endDate) {
		if (startDate == null || endDate == null)
			return null;
		Long num = (endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000);
		// 如果两个日期包含时间则可能出现误差,所以需要修正
		Date temp = new Date(startDate.getTime() + 24 * 60 * 60 * 1000 * num);
		if (DateUtils.isSameDay(temp, endDate)) {
			if (endDate.compareTo(temp) > 0) {
				num++;
			}
			if (endDate.compareTo(temp) < 0) {
				num--;
			}
		}
		return num;
	}

	/**
	 * 计算从开始的时间到结束的时间之间有几个小时
	 * 
	 * @param startDate 开始的时间
	 * @param endDate   结束的时间
	 * @return 两个时间之间的小时数
	 */
	public static Long getHours(Date startDate, Date endDate) {
		Long hours = new Long(0);
		if (startDate != null && endDate != null) {
			hours = ((endDate.getTime() - startDate.getTime()) / 1000 / 60 / 60);
		}
		return hours;
	}

	/**
	 * 计算从开始的时间到结束的时间之间有几分钟
	 * 
	 * @param startDate 开始的时间
	 * @param endDate   结束的时间
	 * @return 两个时间之间的分钟数
	 */
	public static Long getMinutes(Date startDate, Date endDate) {
		Long minute = new Long(0);
		if (endDate != null && startDate != null) {
			minute = ((startDate.getTime() - endDate.getTime()) / 1000 / 60);
		}
		return minute;
	}

	/**
	 * 两个时间相差距离多少天多少小时多少分多少秒
	 * 
	 * @param str1 时间参数 1 格式：1990-01-01 12:00:00
	 * @param str2 时间参数 2 格式：2009-01-01 12:00:00
	 * @return String 返回值为：xx天xx小时xx分xx秒
	 */
	public static String getDistanceTime(Date starDate, Date endDate) {
		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		long time1 = starDate.getTime();
		long time2 = endDate.getTime();
		long diff = time2 - time1;
		day = diff / (24 * 60 * 60 * 1000);
		hour = (diff / (60 * 60 * 1000) - day * 24);
		min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
		sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		return (day > 0 ? (day + "天 ") : "") + hour + "时 " + min + "分 " + sec + "秒";
	}

	/**
	 * 两个时间相差距离多少天多少小时多少分
	 * 
	 * @param str1 时间参数 1 格式：1990-01-01 12:00:00
	 * @param str2 时间参数 2 格式：2009-01-01 12:00:00
	 * @return String 返回值为：xx天xx小时xx分xx秒
	 */
	public static String getDistanceTimeHM(Date starDate, Date endDate) {
		long day = 0;
		long hour = 0;
		long min = 0;
//		long sec = 0;
		long time1 = starDate.getTime();
		long time2 = endDate.getTime();
		long diff = time2 - time1;
		day = diff / (24 * 60 * 60 * 1000);
		hour = (diff / (60 * 60 * 1000) - day * 24);
		min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
//		sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		return (day > 0 ? (day + "天 ") : "") + hour + "时 " + min + "分 ";
	}

	/**
	 * 将年月日和时间合并成带具体时间的方法<br>
	 * 为了将Ts的时间和日期分开存储 转换成一个时间对象存储
	 * 
	 * @param date     具体的日期，格式为：yyyy-MM-dd
	 * @param longTime 具体的时间，格式为：958或2109
	 * @return
	 */
	public static Date getDateTime(Date date, Long longTime) {
		Calendar calendar = Calendar.getInstance();
		if (date == null)
			return null;
		calendar.setTime(date);
		int longHour = new Long(longTime / 100).intValue();
		int longMinute = new Long(longTime % 100).intValue();
		calendar.set(Calendar.HOUR_OF_DAY, longHour);
		calendar.set(Calendar.MINUTE, longMinute);
		return calendar.getTime();
	}

	/**
	 * 给时间对象加月日
	 * 
	 * @param date  要被加上月日的时间
	 * @param strMd 格式为MM-dd
	 * @return
	 */
	public static Date getDateReplaceMd(Date date, String strMd) {
		Calendar calendar = Calendar.getInstance();
		if (date == null || strMd == null)
			return null;
		calendar.setTime(date);

		Calendar calendarMd = Calendar.getInstance();
		Date dateMd = parse(strMd, "MM-dd");
		calendarMd.setTime(dateMd);
		int intMonth = calendarMd.get(Calendar.MONTH);
		int intDay = calendarMd.get(Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.MONTH, intMonth);
		calendar.set(Calendar.DAY_OF_MONTH, intDay);
		return calendar.getTime();
	}

	public static void setUsLocale() {
		FORMAT_STR = "dd-MMM-yy";
		FORMAT_STR_WITH_TIME = "dd-MMM-yy HH:mm";
		defaultLocale = Locale.US;
//		defaultDateFormaterWithTime = DateTimeFormatter.ofPattern(FORMAT_STR_WITH_TIME, defaultLocale);
//		defaultDateFormatter = DateTimeFormatter.ofPattern(FORMAT_STR, defaultLocale);
	}

}
