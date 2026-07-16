package com.spt.tools.jpa.persistence;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.engine.jdbc.ClobProxy;

import com.spt.tools.core.date.DateOperator;

public class SearchFilter {

	public enum Operator {
		EQ, LIKE, LIKES, LIKEE, GT, LT, GTE, LTE, IN, NIN, NEQ, NN, ISNULL, BT
	}

	/**
	 * 属性数据类型.
	 */
	public enum PropertyType {
		A(Object.class), S(String.class), I(Integer.class), L(Long.class), N(Double.class), M(BigDecimal.class), D(
				Date.class), B(Boolean.class), C(Clob.class), s(Short.class), E(Enum.class);

		private Class<?> clazz;

		PropertyType(Class<?> clazz) {
			this.clazz = clazz;
		}

		public Class<?> getValue() {
			return clazz;
		}
	}

	public String fieldName;
	public Object value;
	public Operator operator;
	private List<SearchFilter> lstOr=new ArrayList<>();

	public SearchFilter(String fieldName, Operator operator, Object value) {
		this.fieldName = fieldName;
		this.value = value;
		this.operator = operator;
	}

	/**
	 * searchParams中key的格式为OPERATOR_FIELDNAME
	 */
	public static Map<String, SearchFilter> parse(Map<String, Object> searchParams) {
		Map<String, SearchFilter> filters = new HashMap<>();
		if (searchParams != null && searchParams.size() > 0)
			for (Entry<String, Object> entry : searchParams.entrySet()) {
				// 过滤掉空值
				String key = entry.getKey();
				Object value = entry.getValue();
				SearchFilter filter = createFilter(key, value);
				if (filter != null) {
					filters.put(key, filter);
				}
			}

		return filters;
	}

	/**
	 * @param key
	 *            的格式为OPERATOR_FIELDNAME
	 *            EQL_companyId_OR_EQL_parentId
	 */
	public static SearchFilter createFilter(String key, Object value) {
		if ((value instanceof String && StringUtils.isBlank((String) value)) || value == null) {
			return null;
		}
		// 拆分operator与filedAttribute
		SearchFilter filter =null;
		for (String k:key.split("_OR_")){
			SearchFilter temp = createSingleFilter(k, value);
			if (filter != null) {
				filter.lstOr.add(temp);
			} else {
				filter = temp;
			}
		}
		
		return filter;
	}
	
	public static SearchFilter createSingleFilter(String key, Object value) {
		if ((value instanceof String && StringUtils.isBlank((String) value)) || value == null) {
			return null;
		}
		// 拆分operator与filedAttribute
		
		String[] names = StringUtils.split(key, "_");
		if (names.length != 2) {
			throw new IllegalArgumentException(key + " is not a valid search filter name");
		}
		String filedName = names[1];

		Class<?> propertyType;

		String matchTypeStr = StringUtils.substringBefore(key, "_");
		String matchTypeCode = StringUtils.substring(matchTypeStr, 0, matchTypeStr.length() - 1);
		String propertyTypeCode = StringUtils.substring(matchTypeStr, matchTypeStr.length() - 1, matchTypeStr.length());
		try {
			propertyType = Enum.valueOf(PropertyType.class, propertyTypeCode).getValue();
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("filter名称" + key + "没有按规则编写,无法得到属性值类型.", e);
		}

		Operator operator = Operator.valueOf(matchTypeCode);

		value = SearchFilter.convertValues(operator, value, propertyType);
		// 创建searchFilter
		SearchFilter filter = new SearchFilter(filedName, operator, value);
		return filter;
	}

	public static Object convertValues(Operator operator, Object value, Class<?> propertyType) {

		// 按entity property中的类型将字符串转化为实际类型.
		if (propertyType == Date.class && operator.equals(Operator.LT)) {
			value = convertValue(value, propertyType);
			Date dateValue = (Date) value;
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dateValue);
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			value = calendar.getTime();
		}
		if (operator.equals(Operator.IN)) {

			if (value instanceof String) {
				String vals = (String) value;
				List<String> list = new ArrayList<String>();
				for (String str : vals.split(",")) {
					if (StringUtils.isNotBlank(str)) {
						list.add(StringUtils.trim(str));
					}
				}
				value = list;
			}else if (value instanceof String[]){
				String[] vals = (String[]) value;
				List<String> list = new ArrayList<String>();
				for (String str : vals) {
					if (StringUtils.isNotBlank(str)) {
						list.add(StringUtils.trim(str));
					}
				}
				value = list;
			}
		} else if (operator.equals(Operator.NIN)) {

			if (value instanceof String) {
				String vals = (String) value;
				List<String> list = new ArrayList<String>();
				for (String str : vals.split(",")) {
					if (StringUtils.isNotBlank(str)) {
						list.add(StringUtils.trim(str));
					}
				}
				value = list;
			}else if (value instanceof String[]){
				String[] vals = (String[]) value;
				List<String> list = new ArrayList<String>();
				for (String str : vals) {
					if (StringUtils.isNotBlank(str)) {
						list.add(StringUtils.trim(str));
					}
				}
				value = list;
			}
		} else if (operator.equals(Operator.BT)) {
//			value = value;
		} else if (propertyType == Clob.class) {
			if (operator.equals(Operator.LIKE))
				value = "%" + value + "%";
			value = ClobProxy.generateProxy((String) value);
		} else if (propertyType == Enum.class) {
			// TODO 枚举类型转换
		} else if (propertyType == Date.class) {
			//日期
			Date date  = (Date)convertValue(value, propertyType);
			value = date;
			if (operator.equals(Operator.LTE)) {
				boolean hasTime = DateOperator.hasTime(date);
				if (!hasTime) {
					value = DateOperator.lastSeDate(date);
				}
			}
		}else {
			value = convertValue(value, propertyType);
		}

		return value;
	}

	public static Object convertValue(Object value, Class<?> toType) {
		try {
			if (value instanceof String) {
				value = StringUtils.trim((String)value);
			}
			DateConverter dc = new DateConverter();
			dc.setUseLocaleFormat(true);
			dc.setPatterns(new String[] { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss" });
			ConvertUtils.register(dc, Date.class);
			return ConvertUtils.convert(value, toType);
		} catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}

	/**
	 * 将反射时的checked exception转换为unchecked exception.
	 */
	public static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
		if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
				|| e instanceof NoSuchMethodException)
			return new IllegalArgumentException("Reflection Exception.", e);
		else if (e instanceof InvocationTargetException)
			return new RuntimeException("Reflection Exception.", ((InvocationTargetException) e).getTargetException());
		else if (e instanceof RuntimeException) {
			return (RuntimeException) e;
		}
		return new RuntimeException("Unexpected Checked Exception.", e);
	}

	public List<SearchFilter> getLstOr() {
		return lstOr;
	}
}
