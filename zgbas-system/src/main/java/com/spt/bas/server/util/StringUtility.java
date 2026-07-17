package com.spt.bas.server.util;

import java.lang.reflect.Field;
public class StringUtility 
{
	private final static char[] zeroArr = new char[]{'0','0','0','0','0','0','0','0','0','0'};
	private final static int KEYVALUELEN = 10;
	
	private StringUtility()
	{
	}
	
	public static boolean isNullOrEmpty(String str)
	{
		if (str == null) return true;
		if (str.isEmpty()) return true;
		return false;
	}
	public static boolean isNotBlank(String str)
	{
		return !isNullOrEmpty(str);
	}
	public static String o2s(Object o)
	{
		if (o == null)
		{
			return null;
		}
		else
		{
			if (o instanceof String)
			{
				return (String)o;
			}
			else
			{
				return o.toString();
			}
		}
	}
	
	public static String alignRight(String data, int len) 
	{
		StringBuilder sb = new StringBuilder(data);
		while(sb.length()<len) 
		{
			sb.insert(0, ' ');
		}
		return sb.toString();
	}

	public static String alignLeft(String data, int len) 
	{
		StringBuilder sb = new StringBuilder(data);
		while(sb.length()<len) 
		{
			sb.append(' ');
		}
		return sb.toString();
	}
	
	/**
	 * 对应一个int数字，生成长度为10的固定长字符串
	 * @param value
	 * @return
	 */
	public static String generateFixLengthNumString(int value)
	{
		String key = String.valueOf(value);
		String s = new String(zeroArr, 0, KEYVALUELEN - key.length());
		return s + key;
	}
	
	
	/**
	 * 替换指定对象 String类型为NULL的属性成空字符串 ""
	 * @param obj
	 */
	public static void replaceAllNull(Object obj)
	{
		if(obj == null) return;
		
		Class<?> cls = obj.getClass();
		if(cls == null)	return;
		
		Field[] flds = cls.getDeclaredFields();
		if(flds == null) return;
		
		Field field;
		for ( int i = 0; i < flds.length; i++ )
		{
			field = flds[i];
			field.setAccessible(true);
			
			//String类型的属性
			if (field.getGenericType().toString().equals("class java.lang.String")) 
			{ 
				try 
				{
					if(field.get(obj) == null)
					{
						field.set(obj, "");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
