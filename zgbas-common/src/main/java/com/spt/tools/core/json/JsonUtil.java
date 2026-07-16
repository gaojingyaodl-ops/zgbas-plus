package com.spt.tools.core.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.common.collect.Maps;
import com.spt.tools.core.date.DateOperator;

/**
 * json转换工具类
 * 
 * @author Jian
 */
public class JsonUtil {
	private final static Logger log = LoggerFactory.getLogger(JsonUtil.class);

	public static ObjectMapper getObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
//		SimpleBeanPropertyFilter propertyFilter = SimpleBeanPropertyFilter.serializeAllExcept(CollectionUtil
//				.array2Set(new String[0]));
//		SimpleFilterProvider filters = new SimpleFilterProvider().addFilter("excludeFilter", propertyFilter);
//		mapper.setFilters(filters);

		SimpleModule testModule = new SimpleModule("BooleanModule", Version.unknownVersion());
		ToStringSerializer stringSerializer = new ToStringSerializer();
		// testModule.addSerializer(boolean.class, stringSerializer);
		testModule.addSerializer(String.class, stringSerializer);
		mapper.registerModule(testModule);

		CustomerSerializerFactory factory = new CustomerSerializerFactory(null);
		mapper.setSerializerFactory(factory);
//		
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.disable(SerializationFeature.FAIL_ON_SELF_REFERENCES);
		mapper.disable(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS);
		// 去掉null值
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
		return mapper;
	}

	public static String obj2Json(final Object obj) {
		return obj2Json(obj, null, null, null);
	}

	public static String obj2Json(final Object obj, String[] excludes) {
		return obj2Json(obj, null, excludes, null);
	}

	public static String obj2Json(final Object obj, String[] excludes, Set<String> codeFields) {
		return obj2Json(obj, null, excludes, codeFields);
	}

	public static String obj2Json(final Object obj, String dateformat, String[] excludes, Set<String> codeFields) {
		String jsonString = "";
		if (obj == null) {
			return jsonString;
		}
		ObjectMapper mapper = getObjectMapper();
		SimpleDateFormat format;
		try {
			format = new SimpleDateFormat(dateformat);
		} catch (Exception ex) {
			format = new SimpleDateFormat(DateOperator.FORMAT_STR_WITH_TIME);
		}
		mapper.setDateFormat(format);
		try {
			jsonString = mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			log.error("obj2Json", e);
		}
		// JsonConfig config = getJsonConfig(excludes, dateformat, codeFields);
		return jsonString;
	}

	/**
	 * 把jsonString转成对象
	 * 
	 * @param clazz
	 * @param jsonString
	 * @param root
	 * @return
	 */
	public static <T> List<T> json2List(Class<T> clazz, String jsonString, String root) {
		if (StringUtils.isBlank(jsonString)) {
			return null;
		}
		ObjectMapper mapper = getObjectMapper();
		List<T> list = new ArrayList<T>();
		try {
//			JavaType javaType = getCollectionType(mapper, ArrayList.class, clazz);
//			list =  mapper.readValue(jsonString, javaType); 
			if (StringUtils.isNotBlank(root)) {
				JSONObject jsonObject = new JSONObject(jsonString);
				JSONArray array = jsonObject.getJSONArray(root);
				for (int i = 0; i < array.length(); i++) {
					Object tmp = array.get(i);
					T obj = mapper.readValue(tmp.toString(), clazz);
					list.add(obj);
				}
			} else {
				MappingIterator<T> it = mapper.readerFor(clazz).readValues(jsonString);
				list = it.readAll();
			}
		} catch (Exception e) {
			log.error("json2List", e);
		}
		return list;
	}

	public static <T> List<T> json2List(Class<T> clazz, String jsonString) {
		return json2List(clazz, jsonString, null);
	}

	/**
	 * 获取泛型的Collection Type
	 * 
	 * @param collectionClass 泛型的Collection
	 * @param elementClasses  元素类
	 * @return JavaType Java类型
	 * @since 1.0
	 */
	public static JavaType getCollectionType(ObjectMapper mapper, Class<?> collectionClass,
			Class<?>... elementClasses) {
		return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
	}

	/**
	 * 把jsonString转成对象
	 * 
	 * @param            <T>
	 * @param clazz
	 * @param jsonString
	 * @return
	 */
	public static <T> T json2Object(Class<T> clazz, String jsonString) {
		if (StringUtils.isBlank(jsonString)) {
			return null;
		}
		ObjectMapper mapper = getObjectMapper();
		try {
			return mapper.readValue(jsonString, clazz);
		} catch (Exception e) {
			log.error("json2Object", e);
		}
		return null;
	}

	public static Map<String, Object> json2Map(String jsonString) {
		if (StringUtils.isBlank(jsonString)) {
			return Maps.newHashMap();
		}
		ObjectMapper mapper = getObjectMapper();
		try {
			TypeReference<Map<String, Object>> clazz = new TypeReference<Map<String, Object>>() {
			};
			return mapper.readValue(jsonString, clazz);
		} catch (Exception e) {
			log.error("json2Map", e);
		}
		return null;
	}

	public static <T> T json2Object(TypeReference<T> clazz, String jsonString) {
		if (StringUtils.isBlank(jsonString)) {
			return null;
		}
		ObjectMapper mapper = getObjectMapper();
		try {
			return mapper.readValue(jsonString, clazz);
		} catch (Exception e) {
			log.error("json2Object", e);
		}
		return null;
	}
}
