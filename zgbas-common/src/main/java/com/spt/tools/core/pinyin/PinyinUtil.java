package com.spt.tools.core.pinyin;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinyinUtil {

	/** 获取完整拼音 */
	public static String getPinyin(String str) {
		return getStringPinYin(str, false);
	}

	/** 获取拼音首字母（大写） */
	public static String getPinyinFirst(String str) {
		return getStringPinYin(str, true);
	}

	// 转换单个字符
	private static String getCharacterPinYin(char c, boolean onlyFirst, HanyuPinyinOutputFormat format) {
		String[] pinyin = null;
		try {
			pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}

		// 如果c不是汉字，toHanyuPinyinStringArray会返回null
		if (pinyin == null || pinyin.length == 0)
			return null;

		if (onlyFirst) {
			return pinyin[0].substring(0, 1).toUpperCase();
		}
		// 只取一个发音，如果是多音字，仅取第一个发音
		return pinyin[0];

	}

	/** 将字符串中的中文转化为拼音,其他字符不变 */
	private static String getStringPinYin(String str, boolean onlyFirst) {
		if (str == null) {
			return null;
		}
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_V);
		StringBuilder sb = new StringBuilder();
		String tempPinyin = null;
		for (int i = 0; i < str.length(); ++i) {
			tempPinyin = getCharacterPinYin(str.charAt(i), onlyFirst, format);
			if (tempPinyin == null) {
				// 如果str.charAt(i)非汉字，则保持原样
				sb.append(str.charAt(i));
			} else {
				sb.append(tempPinyin);
			}
		}
		return sb.toString();
	}

}
