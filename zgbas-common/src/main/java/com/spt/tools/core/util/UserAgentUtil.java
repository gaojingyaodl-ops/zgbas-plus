package com.spt.tools.core.util;
/**
 * 
 */

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * UserAgent判断工具类
 * 
 * @author Jian
 */
public class UserAgentUtil {

	private static String[] mobiles = { "midp", "j2me", "avant", "docomo", "novarra", "palmos", "palmsource",
			"240x320", "opwv", "chtml", "pda", "windows ce", "mmp/", "blackberry", "mib/", "symbian", "wireless",
			"nokia", "hand", "mobi", "phone", "cdm", "up.b", "audio", "sie-", "sec-", "samsung", "htc", "mot-",
			"mitsu", "sagem", "sony", "alcatel", "lg", "eric", "vx", "NEC", "philips", "mmm", "xx", "panasonic",
			"sharp", "wap", "sch", "rover", "pocket", "benq", "pt", "pg", "vox", "amoi", "bird", "compal",
			"kg", "voda", "sany", "kdd", "dbt", "sendo", "sgh", "gradi", "jb", "dddi", "moto", "iphone", "android",
			"iPod", "incognito", "webmate", "dream", "cupcake", "webos", "s8000", "bada", "googlebot-mobile",
			"windows mobile", "rv:1.2.3.4", "ucweb", "windows phone" };

	private static String[] mobiles_css3 = { "iphone", "android", "iPod", "ipad" };

	private final static String EMAIL_REGEX = "^\\w+((-\\w+)|(\\.\\w+)|(:\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$"; // 邮件"

	private final static String[] pads = { "ipad", "gt-p6200" };

	private static String[] ios = { "iphone", "iPod", "ipad" };
	private static String[] android = { "android"};
	private static String[] winPhone = { "windows mobile", "windows phone", "windows ce" };

	/**
	 * 判断是否为手机浏览器
	 * 
	 * @param userAnget
	 * @return
	 */
	public static boolean isPhone(String userAgent) {

		if ( StringUtils.isBlank(userAgent) )
			return false;

		for ( String str : mobiles ) {
			if ( userAgent.toLowerCase().indexOf(str) != -1 )
				return true;
		}

		return false;
	}

	/** 是否在微信打开 */
	public static boolean isWeixin(String userAgent) {

		if ( StringUtils.isBlank(userAgent) )
			return false;
		if ( userAgent.toLowerCase().indexOf("micromessenger") != -1 )
			return true;

		return false;
	}

	/**
	 * 判断是否为IOS手机浏览器
	 * 
	 * @param userAnget
	 * @return
	 */
	public static boolean isIos(String userAgent) {

		if ( StringUtils.isBlank(userAgent) )
			return false;

		for ( String str : ios ) {
			if ( userAgent.toLowerCase().indexOf(str) != -1 )
				return true;
		}

		return false;
	}
	/**
	 * 判断是否为android手机浏览器
	 * 
	 * @param userAnget
	 * @return
	 */
	public static boolean isAndroid(String userAgent) {

		if ( StringUtils.isBlank(userAgent) )
			return false;

		for ( String str : android ) {
			if ( userAgent.toLowerCase().indexOf(str) != -1 )
				return true;
		}

		return false;
	}
	/**
	 * 判断是否为手机浏览器
	 * 
	 * @param userAnget
	 * @return
	 */
	public static boolean isWinPhone(String userAgent) {

		if ( StringUtils.isBlank(userAgent) )
			return false;

		for ( String str : winPhone ) {
			if ( userAgent.toLowerCase().indexOf(str) != -1 )
				return true;
		}

		return false;
	}

	public static boolean isPhoneCss3(String userAgent) {

		if ( StringUtils.isBlank(userAgent) )
			return false;

		for ( String str : mobiles_css3 ) {
			if ( userAgent.toLowerCase().indexOf(str) != -1 )
				return true;
		}

		return false;
	}

	public static boolean isPad(String userAgent) {

		if ( StringUtils.isBlank(userAgent) )
			return false;

		for ( String str : pads ) {
			if ( userAgent.toLowerCase().indexOf(str) != -1 )
				return true;
		}
		return false;
	}

	public static boolean isEmail(String email) {

		return Pattern.matches(EMAIL_REGEX, email);
	}

	public static byte[] InputStream2Bytes(InputStream stream) throws IOException {

		byte[] buffs = null;
		BufferedInputStream bis = new BufferedInputStream(stream);
		byte[] buff = new byte[2048];
		int bytesRead;
		while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
			if ( bytesRead == buff.length ) {
				buffs = ArrayUtils.addAll(buffs, buff);
			} else {
				byte[] buff2 = new byte[bytesRead];
				buffs = ArrayUtils.addAll(buffs, buff2);
			}
		}
		return buffs;
	}
	/*
	 * public static boolean isPhone(String sUserAgent) { sUserAgent =
	 * sUserAgent.toLowerCase(); boolean bIsIpad = sUserAgent.contains("ipad");
	 * boolean bIsIphoneOs = sUserAgent.contains("iphone os"); boolean bIsMidp =
	 * sUserAgent.contains("midp"); boolean bIsUc7 =
	 * sUserAgent.contains("rv:1.2.3.4"); boolean bIsUc =
	 * sUserAgent.contains("ucweb"); boolean bIsAndroid =
	 * sUserAgent.contains("android"); boolean bIsCE =
	 * sUserAgent.contains("windows ce"); boolean bIsWM =
	 * sUserAgent.contains("windows mobile"); boolean bIsWp =
	 * sUserAgent.contains("windows phone"); if (bIsIpad || bIsIphoneOs ||
	 * bIsAndroid || bIsMidp || bIsUc7 || bIsUc || bIsCE || bIsWM || bIsWp) {
	 * return true; } return false; }
	 */

}
