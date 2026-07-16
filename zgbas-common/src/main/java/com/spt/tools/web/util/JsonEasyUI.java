/**
 * 
 */
package com.spt.tools.web.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;

import com.spt.tools.core.json.JsonUtil;

/**
 * @author huangjian
 * 
 */
public class JsonEasyUI {

	// ------EasyUI专用方法和常量-----//

	public static String JSON_NAME = "_easy_grid";

	public static String INSERT_RECORDS_KEY = "_inserted";

	public static String UPDATE_RECORDS_KEY = "_updated";

	public static String DELETE_RECORDS_KEY = "_deleted";

	public static <T> List<T> getInsertRecords(Class<T> clazz, String suffix, HttpServletRequest request) {
		return getJsonObject(clazz, INSERT_RECORDS_KEY + suffix, request);
	}

	public static <T> List<T> getUpdatedRecords(Class<T> clazz, String suffix, HttpServletRequest request) {
		return getJsonObject(clazz, UPDATE_RECORDS_KEY + suffix, request);
	}

	public static <T> List<T> getDeletedRecords(Class<T> clazz, String suffix, HttpServletRequest request) {
		return getJsonObject(clazz, DELETE_RECORDS_KEY + suffix, request);
	}

	public static <T> List<T> getInsertRecords(Class<T> clazz, HttpServletRequest request) {
		return getJsonObject(clazz, INSERT_RECORDS_KEY, request);
	}

	public static <T> List<T> getUpdatedRecords(Class<T> clazz, HttpServletRequest request) {
		return getJsonObject(clazz, UPDATE_RECORDS_KEY, request);
	}

	public static <T> List<T> getDeletedRecords(Class<T> clazz, HttpServletRequest request) {
		return getJsonObject(clazz, DELETE_RECORDS_KEY, request);
	}

	private static <T> List<T> getJsonObject(Class<T> clazz, String key, HttpServletRequest request) {
		String json = request.getParameter(JSON_NAME);
		return JsonUtil.json2List(clazz, json, key);
	}

	/**
	 * 根据List自动生成jquery_easyui_grid所需的json数据
	 * 
	 * @param list
	 *            列表数据
	 * @param dateformat
	 *            日期格式化
	 * @param excludes
	 *            不需要生成json的字段名列表(如果包含one-2-many或者many-2-one的字段一定要包含在excludes里)
	 */
	private static String list2Json(final List<?> list, String dateformat, String[] excludes, Set<String> codeFields) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("total", list.size());
		map.put("rows", list);
		return JsonUtil.obj2Json(map, dateformat, excludes, codeFields);
	}

	public static String list2Json(final List<?> list) {
		return list2Json(list, null, new String[0], null);
	}

	public static String list2Json(final List<?> list, String[] excludes) {
		return list2Json(list, null, excludes, null);
	}

	public static String list2Json(final List<?> list, String[] excludes, Set<String> codeFields) {
		return list2Json(list, null, excludes, codeFields);
	}

	/**
	 * 根据Page自动生成jquery_easyui_grid所需的json数据，包含分页信息
	 * 
	 * @param page
	 *            翻页信息
	 * @param dateformat
	 *            日期格式化
	 * @param excludes
	 *            不需要生成json的字段名列表(如果包含one-2-many或者many-2-one的字段一定要包含在excludes里)
	 */
	private static void renderJson(HttpServletResponse response, final Page<?> page, String dateformat,
			String[] excludes, Map<String, Object> footer) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("total", page.getTotalElements());
		map.put("rows", page.getContent());
		if (footer != null) {
			map.put("footer", new Map[] { footer });
		}
		renderDataJson(response, map, dateformat, excludes, null);
	}

	public static void renderJson(HttpServletResponse response, final Page<?> page) {
		renderJson(response, page, null, new String[0], null);
	}

	public static void renderJson(HttpServletResponse response, final Page<?> page, Map<String, Object> footer) {
		renderJson(response, page, null, new String[0], footer);
	}

	public static void renderJson(HttpServletResponse response, final Page<?> page, String[] excludes) {
		renderJson(response, page, null, excludes, null);
	}

	public static void renderJson(HttpServletResponse response, final Page<?> page, String[] excludes,
			Map<String, Object> footer) {
		renderJson(response, page, null, excludes, footer);
	}

	/**
	 * 根据List自动生成jquery_easyui_grid所需的json数据
	 * 
	 * @param list
	 *            列表数据
	 * @param dateformat
	 *            日期格式化
	 * @param excludes
	 *            不需要生成json的字段名列表(如果包含one-2-many或者many-2-one的字段一定要包含在excludes里)
	 */
	private static void renderListJson(HttpServletResponse response, final List<?> list, String dateformat,
			String[] excludes, Map<String, Object> footer) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("total", list.size());
		map.put("rows", list);
		map.put("footer", new Map[] { footer });
		renderDataJson(response, map, dateformat, excludes, null);
	}

	public static void renderListJson(HttpServletResponse response, final List<?> list) {
		renderListJson(response, list, null, new String[0], null);
	}

	public static void renderListJson(HttpServletResponse response, final List<?> list, String[] excludes) {
		renderListJson(response, list, null, excludes, null);
	}

	public static void renderListJson(HttpServletResponse response, final List<?> list, String[] excludes,
			Map<String, Object> footer) {
		renderListJson(response, list, null, excludes, footer);
	}
	public static void renderDataJson(HttpServletResponse response, final Object obj, String dateformat,
			String[] excludes, Set<String> codeFields) {
		String jsonString = JsonUtil.obj2Json(obj);
		RenderUtil.renderText(jsonString, response);
	}
}
