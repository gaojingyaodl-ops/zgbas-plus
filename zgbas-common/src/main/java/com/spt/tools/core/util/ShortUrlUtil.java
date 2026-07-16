package com.spt.tools.core.util;

import java.security.MessageDigest;

/**
 * 短链接生成工具类
 * 
 * @author wlddh
 *
 */
public class ShortUrlUtil {

	public static void main(String[] args) {
		System.out.println(shortUrl("http://www.totrade.cn/tc/index.a"));
	}

	public static String shortUrl(String url) {
		String[] chars = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
				"q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
				"B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
				"W", "X", "Y", "Z" };
		// 对传入网址进行 MD5 加密
		String md5str = getMD5Str(url);
//		System.out.println(md5str);
		String hex = md5str;
		String sTempSubString = hex.substring(8, 16);
		long lHexLong = 0x3FFFFFF3 & Long.parseLong(sTempSubString, 16);
		String outChars = "";
		for (int j = 0; j < 6; j++) {
			long index = 0x0000003C & lHexLong;
			outChars += chars[(int) index];
			// 每次循环按位右移 5 位
			lHexLong = lHexLong >> 5;
		}
		return outChars;
	}

	private static String getMD5Str(String str) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
		} catch (Exception e) {
			System.out.println("catch!");
		}
		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return md5StrBuff.toString();
	}
}
