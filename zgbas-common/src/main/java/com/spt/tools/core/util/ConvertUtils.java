package com.spt.tools.core.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Date;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spt.tools.core.date.DateOperator;

/**
 * 
 * @author lbqi
 *
 */
public class ConvertUtils {

	private static Logger log = LoggerFactory.getLogger(ConvertUtils.class);

	public static String convert(Object obj) {

		return (String) convert(obj, String.class);
	}

	public static <T> T convert(Object obj, Class<T> targetType) {

		Object rtnVal = null;
		if (targetType == long.class || targetType == Long.class) {
			rtnVal = toLong(obj);
		} else if (targetType == int.class || targetType == Integer.class) {
			rtnVal = toInt(obj);
		} else if (targetType == String.class) {
			rtnVal = toString(obj);
		} else if (targetType == Date.class) {
			rtnVal = toDate(obj);
		} else if (targetType == BigDecimal.class) {
			rtnVal = toBigDecimal(obj);
		} else if (targetType == Boolean.class) {
			if (obj != null) {
				if (obj instanceof Boolean) {
					rtnVal = (Boolean) obj;
				} else {
					rtnVal = BooleanUtils.toBooleanObject(toInt(obj));
				}
			} else {
				// rtnVal=false;
			}
		}

		return (T) rtnVal;
	}

	private static Date toDate(Object obj) {

		Date rtnVal = null;
		if (obj != null) {
			if (obj instanceof Date) {
				rtnVal = (Date) obj;
			} else if (obj instanceof String) {
				rtnVal = DateOperator.parse((String) obj);
			}
		}
		return rtnVal;
	}

	private static String toString(Object obj) {

		String rtnVal = null;
		if (obj != null) {
			if (obj instanceof String) {
				rtnVal = (String) obj;
			} else {
				// log.info(obj.getClass().getSimpleName());
				rtnVal = obj.toString();
			}
		}
		return rtnVal;
	}

	private static BigDecimal toBigDecimal(Object obj) {

		BigDecimal rtnVal = null;
		if (obj != null) {
			if (obj instanceof BigInteger) {
				BigInteger bigInteger = (BigInteger) obj;
				rtnVal = BigDecimal.valueOf(bigInteger.longValue());
			} else if (obj instanceof Integer) {
				Integer integer = (Integer) obj;
				rtnVal = BigDecimal.valueOf(integer.longValue());
			} else if (obj instanceof BigDecimal) {
				rtnVal = (BigDecimal) obj;
			} else if (obj instanceof Double) {
				rtnVal = new BigDecimal(obj.toString());
			} else if (obj instanceof Long) {
				rtnVal = BigDecimal.valueOf((Long) obj);
			} else if (obj instanceof String) {
				rtnVal = new BigDecimal(obj.toString());
			}
		}
		return rtnVal;
	}

	private static Long toLong(Object obj) {

		Long rtnVal = null;
		if (obj != null) {
			if (obj instanceof BigInteger) {
				BigInteger bigInteger = (BigInteger) obj;
				rtnVal = bigInteger.longValue();
			} else if (obj instanceof Integer) {
				Integer integer = (Integer) obj;
				rtnVal = integer.longValue();
			} else if (obj instanceof BigDecimal) {
				BigDecimal bigDecimal = (BigDecimal) obj;
				rtnVal = bigDecimal.longValue();
			} else if (obj instanceof Long) {
				rtnVal = (Long) obj;
			} else if (obj instanceof Double) {
				rtnVal = Double.valueOf(obj.toString()).longValue();
			} else if (obj instanceof String) {
				rtnVal = Long.valueOf(obj.toString());
			}
		}
		return rtnVal;
	}
	 /**
     *分转化为人民币(小数点后2位）
     * @param amount 额
     * @return
     */
    public static BigDecimal toCNY(BigDecimal amount){
    	if(null == amount){
    		return new BigDecimal("0");
    	}
    	return amount.divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP);
    }
    /**
     *格式化金额，千分位(小数点后2位）
     * @param amount 额
     * @return
     */
    public static String formatCNY(BigDecimal amount){
    	 DecimalFormat d1 =new DecimalFormat("#,##0.####;(#)");
    	if(null == amount){
    		return "0";
    	}
    	return d1.format(amount);
    }
	private static Integer toInt(Object obj) {

		Integer rtnVal = null;
		if (obj != null) {
			if (obj instanceof BigInteger) {
				BigInteger bigInteger = (BigInteger) obj;
				rtnVal = bigInteger.intValue();
			} else if (obj instanceof BigDecimal) {
				BigDecimal bigDecimal = (BigDecimal) obj;
				rtnVal = bigDecimal.intValue();
			} else if (obj instanceof Integer) {
				rtnVal = (Integer) obj;
			} else if (obj instanceof Byte) {
				Byte b = (Byte) obj;
				rtnVal = b.intValue();
			} else if (obj instanceof Long) {
				Long l = (Long) obj;
				rtnVal = l.intValue();
			}
		}
		return rtnVal;
	}
	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String convertToHideStr(String str) {
		if(str==null || str.equals(""))
	    {
	        throw new IllegalArgumentException("格式错误.str:"+str);
	    }
	    int start = (str.length()/2)-(str.length()/4);
	    int end = (str.length()/2)+(str.length()/4);
	    StringBuffer sb = new StringBuffer(str.subSequence(0, start));
	    for(int i=start;i<end;i++)
	    {
	        sb.append("*");
	    }
	    sb.append(str.substring(end));
	    return sb.toString();
	}

	public static String convertToHideStr(String str, String lc) {
		if (lc.equalsIgnoreCase("BR")) {
			String newstr = str.substring(str.length() - 8, str.length() - 4);
			String result = str.replaceAll(newstr, "****");
			return result.substring(1, str.length());
		}
		String newstr = str.substring(str.length() - 8, str.length() - 4);
		String result = str.replaceAll(newstr, "****");
		return result;
	}

}
