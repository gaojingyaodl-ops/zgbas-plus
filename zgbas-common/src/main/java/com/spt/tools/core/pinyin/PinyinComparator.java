package com.spt.tools.core.pinyin;

import java.util.Comparator;

/**
 * 拼音比较器
 */
public class PinyinComparator implements Comparator<Object> {
	public int compare(Object o1, Object o2) {
		String str1 = PinyinUtil.getPinyin((String) o1);
		String str2 = PinyinUtil.getPinyin((String) o2);
		return str1.compareTo(str2);
	}
}
