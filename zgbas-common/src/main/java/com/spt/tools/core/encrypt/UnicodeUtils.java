package com.spt.tools.core.encrypt;

public class UnicodeUtils {

	public static String cnToUnicode(String value) {
		char[] chars = value.toCharArray();
        String returnStr = "";
        for (int i = 0; i < chars.length; i++) {
          returnStr += "\\u" + Integer.toString(chars[i], 16);
        }
        return returnStr;

	}

	/**
	 * asciicode 转为中文
	 *
	 * @param asciicode
	 *            eg:{"code":400002,"msg":"\u7b7e\u540d\u9519\u8bef"}
	 * @return eg:{"code":400002,"msg":"签名错误"}
	 */
	public static String unicode2Cn(String unicode) {
		String[] asciis = unicode.split("\\\\u");
		String nativeValue = asciis[0];
		try {
			for (int i = 1; i < asciis.length; i++) {
				String code = asciis[i];
				nativeValue += (char) Integer.parseInt(code.substring(0, 4), 16);
				if (code.length() > 4) {
					nativeValue += code.substring(4, code.length());
				}
			}
		} catch (NumberFormatException e) {
			return unicode;
		}
		return nativeValue;
		
	}
	
	public static void main(String[] args) {
		System.out.println(unicode2Cn("\\u6d4b\\u8bd5\\u5546\\u54c1"));
	}
}
