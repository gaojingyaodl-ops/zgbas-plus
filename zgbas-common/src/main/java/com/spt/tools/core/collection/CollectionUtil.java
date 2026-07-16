/**
 * 
 */
package com.spt.tools.core.collection;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.lang3.StringUtils;

/**
 * @author huangjian
 * 
 */
public class CollectionUtil {

	/**
	 * 将字符串数组，转换成用逗号隔开的字符串
	 * 
	 * @param strArray
	 * @return
	 */
	public static String array2String(String[] strArray) {
		StringBuilder sbTmp = new StringBuilder();
		if (strArray != null) {
			for (String str : strArray) {
				if (str != null) {
					if (sbTmp.length() > 0) {
						sbTmp.append(",");
					}
					sbTmp.append(str.trim());
				}
			}
		}
		return sbTmp.toString();
	}

	public static String array2String(Collection<String> strArray, String split) {
		StringBuilder sbTmp = new StringBuilder();
		if (strArray != null) {
			for (String str : strArray) {
				if (str != null) {
					if (sbTmp.length() > 0) {
						sbTmp.append(split);
					}
					sbTmp.append(str.trim());
				}
			}
		}
		return sbTmp.toString();
	}

	public static String array2String(List<String> strArray) {
		StringBuilder sbTmp = new StringBuilder();
		if (strArray != null) {
			for (String str : strArray) {
				if (str != null) {
					if (sbTmp.length() > 0) {
						sbTmp.append(",");
					}
					sbTmp.append(str.trim());
				}
			}
		}
		return sbTmp.toString();
	}

	public static List<String> array2List(String[] strArray) {
		List<String> lstTmp = new ArrayList<String>();
		for (String string : strArray) {
			lstTmp.add(string);
		}
		return lstTmp;
	}

	public static Set<String> array2Set(String[] strArray) {
		Set<String> lstTmp = new HashSet<String>();
		if (strArray != null) {
			for (String string : strArray) {
				lstTmp.add(string);
			}
		}
		return lstTmp;
	}

	/**
	 * 合并数组
	 * 
	 * @param arrays
	 * @return
	 */
	public static String[] uniteArrays(String[]... arrays) {
		String[] rtnVal = null;
		if (arrays != null) {
			if (arrays.length > 0) {
				List<String> lstTmp = new ArrayList<String>();
				for (String[] strings : arrays) {
					lstTmp.addAll(array2List(strings));
				}
				String[] strAll = new String[lstTmp.size()];
				rtnVal = lstTmp.toArray(strAll);
			} else if (arrays.length == 0) {
				rtnVal = arrays[0];
			}
		}
		return rtnVal;
	}

	public static String[] uniteArrays(Collection<String[]> arrays) {
		String[] rtnVal = null;
		if (arrays != null) {
			if (arrays.size() > 0) {
				List<String> lstTmp = new ArrayList<String>();
				for (String[] strings : arrays) {
					lstTmp.addAll(array2List(strings));
				}
				String[] strAll = new String[lstTmp.size()];
				rtnVal = lstTmp.toArray(strAll);
			} else if (arrays.size() == 0) {
				rtnVal = arrays.iterator().next();
			}
		}
		return rtnVal;
	}

	public static <T> T getEntityFromList(final List<T> list, String tarProp, Object tarValue) {
		Object rtn = null;
		for (Object obj : list) {
			try {
				Object objVal = PropertyUtils.getSimpleProperty(obj, tarProp);
				if (objVal != null && tarValue != null) {
					if (objVal.equals(tarValue)) {
						rtn = obj;
						break;
					}
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		return (T) rtn;
	}

	/**
	 * 将一个list按照某个字段拆分成多个list
	 * 
	 * @param list
	 *            子元素为Map对象
	 * @param key
	 * @return
	 */
	public static HashMap splitList(List<HashMap> list, String key) {
		HashMap<String, List<HashMap>> map = new HashMap<String, List<HashMap>>();
		for (HashMap srcMap : list) {
			String value = (String) srcMap.get(key);
			List<HashMap> newList = map.get(value);
			if (newList == null) {
				newList = new ArrayList<HashMap>();
			}
			newList.add(srcMap);
			map.put(value, newList);
		}
		return map;
	}

	/**
	 * 将set转化成list
	 * 
	 * @param set
	 * @return
	 */
	public static List setCollection2List(Collection list) {
		List<Object> lstTemp = new ArrayList<Object>();
		if (list != null) {
			for (Object object : list) {
				lstTemp.add(object);
			}
		}
		return lstTemp;
	}

	/**
	 * 将set转化成list 并根据指定的字段值过滤
	 * 
	 * @param set
	 * @return
	 */
	public static List setCollection2List(Collection list, String condField, Object condValue) {
		List<Object> lstTemp = new ArrayList<Object>();
		if (list != null) {
			for (Object object : list) {
				Object value = null;
				try {
					value = PropertyUtils.getSimpleProperty(object, condField);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (condValue.equals(value)) {
					lstTemp.add(object);
				}
			}
		}
		return lstTemp;
	}

	/**
	 * 根据Map中的条件过滤实体集(Collection)
	 * 
	 * @param list
	 * @param map
	 * @return 符合map条件的数据
	 */
	public static List setCollection2List(Collection list, Map<String, ? extends Object> map) {
		List<Object> lstTemp = null;

		if (list != null) {
			lstTemp = new ArrayList<Object>();
			for (Object object : list) {
				if (map != null) {
					if (validate(object, map)) {
						lstTemp.add(object);
					}
				} else {
					lstTemp.add(object);
				}
			}
		}
		return lstTemp;
	}

	public static List<Object> hashMap2List(HashMap<String, Object> hash) {
		return map2List(hash);
	}

	public static List<Object> map2List(Map map) {
		Set set = map.keySet();
		List<Object> list = new ArrayList<Object>();
		for (Object object : set) {
			if (object instanceof String) {
				list.add(map.get(object));
			}
		}
		return list;
	}

	public static String trimArray(String oriValue, String split) {
		if (oriValue != null) {
			String[] strs = oriValue.split(split);
			StringBuffer sbRtn = new StringBuffer();
			int index = 1;
			for (String str : strs) {
				sbRtn.append(StringUtils.trim(str));
				if (index < strs.length) {
					sbRtn.append(split);
				}
				index++;
			}
			return sbRtn.toString();
		}
		return null;
	}

	private static boolean validate(Object object, Map<String, ? extends Object> map) {
		boolean flag = false;
		// for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
		Iterator iter = map.keySet().iterator();
		flag = validate(object, iter, map);
		// if (!flag) {
		// break;
		// }
		// }
		return flag;
	}

	private static boolean validate(Object object, Iterator iter, Map<String, ? extends Object> map) {
		boolean flag = true;
		if (iter.hasNext()) {
			String key = (String) iter.next();
			Object value = null;
			try {
				value = PropertyUtils.getSimpleProperty(object, key);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (value != null && value.equals(map.get(key))) {
				flag = validate(object, iter, map);
			} else if (value == null && map.get(key) == null) {
				flag = true;
			} else {
				flag = false;
			}
		}
		return flag;

	}

	public static boolean contain(final List<?> list, String tarProp, Object tarValue) {
		Object rtn = getEntityFromList(list, tarProp, tarValue);
		return rtn != null;
	}

	public static <T> List<T> getPropList(Collection<?> list, String propName) {
		List<T> lstResult = new ArrayList<T>();
		for (Object obj : list) {
			try {
				T val = (T) PropertyUtils.getSimpleProperty(obj, propName);
				lstResult.add(val);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return lstResult;
	}

	public static void trim(List list) {
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			Object object = (Object) iterator.next();
			if (object == null) {
				iterator.remove();
			}
		}
	}

	/**
	 * 对集合进行排序(根据集合里面的对象的单个属性)
	 * 
	 * @param list
	 *            需要排序的集合
	 * @param property
	 *            需要对集合里面排序的属性名
	 */
	public static void sortList(List list, String property) {
		Comparator cmp = ComparableComparator.getInstance();
		// 允许值为null
		cmp = ComparatorUtils.nullLowComparator(cmp);

		Collections.sort(list, new BeanComparator(property, cmp));
	}

	/**
	 * 对集合进行排序(根据集合里面的对象的单个属性，升降序)
	 * 
	 * @param list
	 *            需要排序的集合
	 * @param property
	 *            需要对集合里面排序的属性名
	 * @param polarity
	 *            降序true，升序false
	 */
	public static void sortList(List list, String property, boolean polarity) {
		sortList(list, property, polarity, false);
	}

	public static void sortList(List list, String property, boolean polarity, boolean nullHigh) {
		Comparator cmp = ComparableComparator.getInstance();
		// 允许值为null
		if (nullHigh) {
			cmp = ComparatorUtils.nullHighComparator(cmp);
		} else {
			cmp = ComparatorUtils.nullLowComparator(cmp);
		}
		Comparator comparator = new BeanComparator(property, cmp);
		if (!polarity) {
			comparator = new ReverseComparator(new BeanComparator(property, cmp));
		}
		Collections.sort(list, new BeanComparator(property, cmp));
	}

	public static void sortList(List list, Map<String, Boolean> map) {

		Comparator cmp = ComparableComparator.getInstance();
		// 允许值为null
		cmp = ComparatorUtils.nullLowComparator(cmp);

		ArrayList sortFields = new ArrayList();
		for (String property : map.keySet()) {
			if (map.get(property)) {
				sortFields.add(new ReverseComparator(new BeanComparator(property, cmp)));
			} else {
				sortFields.add(new BeanComparator(property, cmp));
			}
		}
		ComparatorChain multiSort = new ComparatorChain(sortFields);
		Collections.sort(list, multiSort);
	}

	/**
	 * 对集合进行排序(根据集合里面的值，升降序)
	 * 
	 * @param list
	 *            需要排序的集合
	 * @param polarity
	 *            降序true，升序false
	 */
	public static void sortList(List list, boolean polarity) {
		Comparator cmp = ComparableComparator.getInstance();
		// 允许值为null
		cmp = ComparatorUtils.nullLowComparator(cmp);

		if (polarity) {
			// 取反
			Collections.sort(list, new ReverseComparator(cmp));
		} else {
			Collections.sort(list, cmp);
		}
	}

	/**
	 * 对集合进行排序（根据集合里面的对象的多个属性）
	 * 
	 * @param list
	 *            需要排序的集合
	 * @param properties
	 *            需要对集合里面排序的对象的属性的集合， 定义成ArrayList，ArrayList里面的顺序就是排序的先后顺序
	 */
	public static void sortList(List list, List<String> properties) {

		Comparator cmp = ComparableComparator.getInstance();
		// 允许值为null
		cmp = ComparatorUtils.nullLowComparator(cmp);

		ArrayList<BeanComparator> sortFields = new ArrayList<BeanComparator>();

		for (String property : properties) {
			sortFields.add(new BeanComparator(property, cmp));
		}

		ComparatorChain multiSort = new ComparatorChain(sortFields);
		Collections.sort(list, multiSort);
	}

	/**
	 * 将set转化成list并排序
	 * 
	 * @param set
	 * @return List
	 */
	public static List setCollection2SortList(Collection coll, String property, boolean polarity) {
		List<Object> lstTemp = new ArrayList<Object>();
		if (coll != null) {
			for (Object object : coll) {
				lstTemp.add(object);
			}
		}
		Comparator cmp = ComparableComparator.getInstance();
		// 允许值为null
		cmp = ComparatorUtils.nullLowComparator(cmp);
		if (polarity) {
			Collections.sort(lstTemp, new ReverseComparator(new BeanComparator(property, cmp)));
		} else {
			Collections.sort(lstTemp, new BeanComparator(property, cmp));
		}
		return lstTemp;
	}

	/**
	 * 删除重复记录
	 * 
	 * @param coll
	 */
	public static void removeRepeat(Collection coll) {
		boolean isRepeat = false;
		Set set = new LinkedHashSet();
		for (Iterator iterator = coll.iterator(); iterator.hasNext();) {
			Object object = (Object) iterator.next();
			set.add(object);
		}
		coll.clear();
		coll.addAll(set);
	}
	
	/**
	 * 判断列表是否为NULL或者是空列表
	 * @param lst
	 * @return
	 */
	public static boolean isNullOrEmpty(Collection<?> lst)
	{
		return !(lst != null && lst.size() > 0); 
	}
}
