package com.spt.tools.core.encrypt;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

/**
 * 编码格式转换工具类
 * 
 * @author wangyilin
 *
 */
public final class ConvertUtility
{
	private ConvertUtility()
	{
	}
	
	public static String i2HexStr(int v, int len)
	{
		String hexStr = Integer.toHexString(v);
		
		if (hexStr.length() % 2 != 0)
		{
			hexStr = "0" + hexStr;
		}
		
		if (len*2 <= hexStr.length())
		{
			return hexStr.toUpperCase().substring(hexStr.length() - len*2);
		}
		else
		{
			char[] chr = new char[(len*2 - hexStr.length())];
			Arrays.fill(chr, '0');
			return String.valueOf(chr) + hexStr.toUpperCase();
		}
	}
	
	public static int hexStr2i(String v)
	{
		return Integer.valueOf(v, 16);
	}
	
	public static byte[] str2ByteArr(String v)
	{
		byte[] arr = new byte[v.length()/2];

        int j = 0;
        for (int i = 0; i < v.length() - 1; i+=2, j++)
        {
            byte b = (byte)hexStr2i(v.substring(i, i + 2));
            arr[j] = b;
        }
        return arr;
	}
	
	public static String byteArr2Str(byte[] v)
	{
		StringBuffer buffer = new StringBuffer();
		for (byte b : v)
		{
			buffer.append(i2HexStr((int)b,1));
		}
		return buffer.toString();
	}
	
	public static String byteArr2Str(byte[] v, int offset, int len)
    {
		StringBuffer rtn = new StringBuffer();
        for (int i = offset; i < offset + len; i++)
        {
            rtn.append(i2HexStr((int)v[i],1));
        }
        return rtn.toString();
    }
	
	public static boolean isValidNum(String v)
	{
		return v.matches("[0-9]+(.[0-9]+)?");
	}
	
    // 正序
    public static void intToByteArray(int i, byte[] outBuffer, int offset)
    {
        outBuffer[offset] = (byte) ((i >> 24) & 0xFF);
        outBuffer[offset + 1] = (byte) ((i >> 16) & 0xFF);
        outBuffer[offset + 2] = (byte) ((i >> 8) & 0xFF);
        outBuffer[offset + 3] = (byte) (i & 0xFF);
    }

    // 逆序
    public static void intToByteArray2(int i, byte[] outBuffer, int offset)
    {
        outBuffer[offset + 3] = (byte) ((i >> 24) & 0xFF);
        outBuffer[offset + 2] = (byte) ((i >> 16) & 0xFF);
        outBuffer[offset + 1] = (byte) ((i >> 8) & 0xFF);
        outBuffer[offset] = (byte) (i & 0xFF);
    }
    
    // 正序
    public static void shortToByteArray(short s, byte[] outBuffer, int offset)
    {
    	outBuffer[offset]=(byte)(0xFF&s>>8);
    	outBuffer[offset + 1]=(byte)(0xFF&s);
    }
    
    // 逆序
    public static void shortToByteArray2(short s, byte[] outBuffer, int offset)
    {
    	outBuffer[offset + 1]=(byte)(0xFF&s>>8);
    	outBuffer[offset]=(byte)(0xFF&s);
    }
    
    // 正序
    public static short makeShort(byte[] b, int offset)
    {
    	return (short)((b[offset]<<8 & 0xFF00) | (b[offset + 1] & 0x00FF));
    }
    
    // 逆序
    public static short makeShort2(byte[] b, int offset)
    {
    	return (short)((b[offset + 1]<<8 & 0xFF00) | (b[offset] & 0x00FF));
    }
    
    // 正序
    public static int byteArrayToInt(byte[] b, int offset)
    {
        int value = 0;
        for (int i = 0; i < 4; i++)
        {
            int shift = (4 - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }

    // 逆序
    public static int byteArrayToInt2(byte[] b, int offset)
    {
        int value = 0;
        for (int i = 3; i >= 0; i--)
        {
            int shift = i * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }
	
	public static Date strToDate(String dateStr, String format)
	{
		DateFormat sdf = new SimpleDateFormat(format);   
		try 
		{   
			Date date = sdf.parse(dateStr);   
			return date;  
		} 
		catch (Exception e) 
		{
			return null;
		}
	}
	
	/**
	 * 按照yyyy-MM-dd格式转换
	 * 
	 * @param dateStr
	 * @return
	 */
	public static Date strToDate(String dateStr)
	{
		return strToDate(dateStr, "yyyy-MM-dd HH:mm:ss");
	}
	
	public static String dateToStr(Date date, String format)
	{
		DateFormat sdf = new SimpleDateFormat(format);   
		return sdf.format(date);
	}
	
	/**
	 * 转换成yyyy-MM-dd格式
	 * 
	 * @param date
	 * @return
	 */
	public static String dateToStr(Date date)
	{
		return dateToStr(date, "yyyy-MM-dd");
	}
	
	public static Timestamp dateToTimeStamp(Date date)
	{
		return new java.sql.Timestamp(date.getTime());
	}
	
	public static String exceptionToStr(Exception e)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(e.getClass().getName() + " message: " + e.getMessage() + System.lineSeparator());
		for (StackTraceElement el : e.getStackTrace())
		{
			sb.append(el.toString() + System.lineSeparator());
		}
		return sb.toString();
	}
	
	public static short makeShort(byte b1, byte b2)
	{
		return (short)(((short)b1 << 8) + ((short)b2 & 0xFF));
	}
	
	public static byte[] shortToBytes(short s)
	{
		byte[] rtn = new byte[2];
		rtn[0]=(byte)(0xFF&s>>8);
		rtn[1]=(byte)(0xFF&s);
		return rtn;
	}
	
	/**
	 * Int32 -> 127.0.0.1
	 * @param value
	 * @return
	 */
	public static String intToIp(int value)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(String.valueOf((int)((value >> 24) & ((int)0x000000FF))));
		sb.append(".");
		sb.append(String.valueOf((int)((value >> 16) & ((int)0x000000FF))));
		sb.append(".");
		sb.append(String.valueOf((int)((value >> 8) & ((int)0x000000FF))));
		sb.append(".");
		sb.append(String.valueOf((int)((value) & ((int)0x000000FF))));
		return sb.toString();
	}
	
	/**
	 * 127.0.0.1 -> Int32
	 * @param ip
	 * @return
	 */
	public static int ipToInt(String ip)
	{
		if (StringUtils.isNotBlank(ip))
		{
			return 0;
		}
		String[] ipArr = ip.split("[.]");
		if (ipArr.length != 4)
		{
			return 0;
		}
		int rtn = 0;
		for (int i = 0; i < 4; i++)
		{
			int v = Integer.valueOf(ipArr[i]);
			rtn |= (v << 8*(3-i));
		}
		return rtn;
	}
	
	public static String o2s(Object obj)
	{
		if (obj == null)
		{
			return "";
		}
		else
		{
			return String.valueOf(obj);
		}
	}
	
	/**
	 * 将unsigned int值的大小数端互换
	 * @param v
	 * @return
	 */
	public static long swapUint(long v)
	{
		return (long)((((v<<24 & 0x00000000FF000000L) | (v<<8 & 0x0000000000FF0000L)
				| (v>>8 & 0x000000000000FF00L) | (v>>24 & 0x00000000000000FFL))) & 0x00000000FFFFFFFFL);
	}
	
	public static final String decodeURL(String s)
	{
		StringBuffer sb = new StringBuffer();
		char[] chrArr = new char[s.length()];
		s.getChars(0, s.length(), chrArr, 0);
		String hex;
		char chr;
		int asc;
		for (int i = 0; i < chrArr.length; i++)
		{
			chr = chrArr[i];
			if (chr != '%')
			{
				sb.append(chr);
			}
			else
			{
				hex = new String(chrArr, i+1, 2);
				asc = hexStr2i(hex);
				sb.append((char)asc);
				i+=2;
			}
		}
		return sb.toString();
	}
	
	/**
	 * UTF8转GBK编码
	 * @param s
	 * @return
	 */
	public static final String utf8toGbk(String s)
	{
		try
		{
			String utf8 = new String(s.getBytes("UTF-8"),"UTF-8");  
			return new String(utf8.getBytes("GBK"),"GBK");
		}
		catch (Exception e)
		{
			return s;
		}
	}
}
