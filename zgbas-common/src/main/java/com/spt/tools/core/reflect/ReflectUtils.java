/**
 * 
 */
package com.spt.tools.core.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author jian
 * 
 */
public class ReflectUtils {
	public static boolean isExistsProperty(Class<?> clazz, String propertyName) {
		boolean flag = false;
		Field field = null;
		try {

			try {
				field = clazz.getDeclaredField(propertyName);
			} catch (Exception e) {
				Class<?> superClass = clazz.getSuperclass();
				if (superClass != null) {
					field = superClass.getDeclaredField(propertyName);
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		if (field != null) {
			flag = true;
		}
		return flag;
	}

	/** 获取泛型类型 */
	public static <T> Class<T> getSuperClassGenricType(final Class<?> clazz, final int index) {

		// 返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的 Type。
		Type genType = clazz.getGenericSuperclass();

		if (!(genType instanceof ParameterizedType)) {
			return null;
		}
		// 返回表示此类型实际类型参数的 Type 对象的数组。
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (index >= params.length || index < 0) {
			return null;
		}
		if (!(params[index] instanceof Class)) {
			return null;
		}

		return (Class<T>) params[index];
	}
}
