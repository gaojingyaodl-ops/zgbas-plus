package com.spt.tools.core.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class ParamUtil {

	/** 将请求参数字符串转换成map */
	public static Map<String, String> parser(String queryString) {

		Map<String, String> map = new HashMap<String, String>();
		if (StringUtils.isNotBlank(queryString)) {
			String[] params = queryString.split("&");
			for (String p : params) {
				String[] strs = p.split("=");
				if (strs.length == 2) {
					map.put(strs[0], strs[1]);
				}
			}
		}
		return map;
	}
}
