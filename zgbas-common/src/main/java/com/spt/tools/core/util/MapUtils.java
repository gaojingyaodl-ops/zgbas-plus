package com.spt.tools.core.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spt.tools.core.json.JsonUtil;

public class MapUtils {
	private static Logger logger = LoggerFactory.getLogger(MapUtils.class);
	
	/**
	 * bean 转换成 map
	 * 
	 * @param bean
	 * @param exludes
	 *            排除的属性
	 * @return
	 */
	public static Map<String, String> tansBean2Map(Object bean) {
		if (bean == null) {
			return null;
		}
		Set<String> exludeSet = new HashSet<>();
		// if (exludes != null && exludes.length > 0){
		// exludeSet.addAll(Sets.newHashSet(exludes));
		// }
		exludeSet.add("class");
		Map<String, String> map = new HashMap<>();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();

				if (!exludeSet.contains(key)) {// 过滤class属性
					Method getter = property.getReadMethod();
					Object value = getter.invoke(bean);
					String strValue = null;
					if (value ==null) {
						strValue = "";
					}else {
						if (value instanceof String) {
							strValue = String.valueOf(value);
						}else {
							strValue = JsonUtil.obj2Json(value);
						}
					}
					if (StringUtils.isNotBlank(strValue) && (value instanceof Integer || !"0".equals(strValue))) {
						map.put(key, strValue);
					}

				}
			}
		} catch (Exception e) {
			logger.error("transform bean to map error", e);
		}

		return map;
	}

	public static <T> T tansMap2Bean(Map<String, String> map, Class<T> clazz) {
		try {
			T bean = clazz.newInstance();
			// BeanUtils.populate(bean, map);

			BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();
				String value = map.get(key);
				Method setter = property.getWriteMethod();
				Object valTrans = ConvertUtils.convert(value, property.getPropertyType());
				if (setter!=null && value!=null) {
					setter.invoke(bean, valTrans);
				}
			}
			return bean;
		} catch (Exception e) {
			logger.error("transform bean to map error", e);
		}

		return null;
	}
}
