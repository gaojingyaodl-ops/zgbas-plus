package com.spt.tools.http.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URLUtil {

	private static Logger log = LoggerFactory.getLogger(URLUtil.class);

	public static String getEncodeURL(Map<String, String> params, String prex, String enc)
			throws UnsupportedEncodingException {

		StringBuffer sb = new StringBuffer();
		Set<String> keys = params.keySet();
		for ( String key : keys ) {
			String value = params.get(key);
			sb.append(prex + key + "=" + URLEncoder.encode(value, enc));
			prex = "&";
		}
		log.info("=========== URLUtil getEncodeURL ================>>>>>>>>>>>>" + sb.toString());
		return sb.toString();
	}

	public static String getUrlByMap(Map<String, String> params, String prex) {
		StringBuffer sb = new StringBuffer();
		Set<String> keys = params.keySet();
		for ( String key : keys ) {
			String value = params.get(key);
			sb.append(prex + key + "=" + value);
			prex = "&";
		}
		log.info("=========== URLUtil getUrlByMap ================>>>>>>>>>>>>" + sb.toString());
		return sb.toString();
	}
	
	public static String getQueryStr(HttpServletRequest request, String prex, String enc)
			throws UnsupportedEncodingException {

		Enumeration<String> en = request.getParameterNames();
		String str = "";
		while (en.hasMoreElements()) {
			String paramName = (String) en.nextElement();
			String paramValue = request.getParameter(paramName);
			str += (prex + paramName + "=" + ((enc != null && !"".equals(enc)) ? URLEncoder.encode(paramValue, enc)
					: paramValue));
			prex = "&";
		}
		// log.info("=========== URLUtil getQueryStr  ================>>>>>>>>>>>>"+str);
		return str;
	}

	public static Map<String, Object> converMapObject(Map<String, String[]> map) {

		Set<Map.Entry<String, String[]>> set = map.entrySet();
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		for ( Iterator<Map.Entry<String, String[]>> it = set.iterator(); it.hasNext(); ) {
			Map.Entry<String, String[]> entry = it.next();
			if ( "m".equals(entry.getKey()) )
				continue;
			paramsMap.put(entry.getKey(), entry.getValue()[0]);
		}
		return paramsMap;
	}

	public static Map<String, String> converMap(Map<String, String[]> map) {

		Set<Map.Entry<String, String[]>> set = map.entrySet();
		Map<String, String> paramsMap = new HashMap<String, String>();
		for ( Iterator<Map.Entry<String, String[]>> it = set.iterator(); it.hasNext(); ) {
			Map.Entry<String, String[]> entry = it.next();
			if ( "m".equals(entry.getKey()) )
				continue;
			paramsMap.put(entry.getKey(), entry.getValue()[0]);
		}
		return paramsMap;
	}

	public static Map<String, String> getParametersMap(HttpServletRequest req) {

		Enumeration<String> en = req.getParameterNames();
		Map<String, String> parameters = new HashMap<String, String>();
		while (en.hasMoreElements()) {
			String paramName = en.nextElement();
			String paramValue = req.getParameter(paramName);
			parameters.put(paramName, paramValue);
		}
		return parameters;
	}
	
	/***
	 * 加载域名信息
	 */
	public static String getDomainName(HttpServletRequest request) {
		String scheme = request.getScheme();
		String serverName = request.getServerName();
		int port = request.getServerPort();

		StringBuilder sb = new StringBuilder();
		sb.append(scheme);
		sb.append("://");
		sb.append(serverName);
		boolean hasPort = false;
		if (scheme.equals("http") && port != 80 || scheme.equals("https") && port != 443) {
			hasPort = true;
		}
		if (port > 0 && hasPort) {
			sb.append(":");
			sb.append(port);
		}
		return sb.toString();
	}


}
