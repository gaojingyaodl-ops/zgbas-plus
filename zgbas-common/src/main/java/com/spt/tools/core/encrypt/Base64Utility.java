package com.spt.tools.core.encrypt;

/**
 * Base64 算法工具类
 * 
 * @author wangyilin
 *
 */
public final class Base64Utility 
{
	private static char[] char_table = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N',
		'O','P','Q','R','S','T','U','V','W','X','Y','Z',
		'a','b','c','d','e','f','g','h','i','j','k','l','m','n',
		'o','p','q','r','s','t','u','v','w','x','y','z',
		'0','1','2','3','4','5','6','7','8','9','+','/'};
	
	private Base64Utility()
	{
	}
	
	/**
	 * Base64编码
	 * @param data	原始数据
	 * @return		编码后字符串
	 */
	public static String base64Encode(byte[] data)
	{
		return base64Encode(data, 0, data.length);
	}
	
	/**
	 * Base64编码
	 * @param data		原始数据
	 * @param offset	起始偏移
	 * @param length	数据长度
	 * @return			编码后字符串
	 */
	public static String base64Encode(byte[] data, int offset, int length)
	{
		StringBuffer sb = new StringBuffer();
		for(int i=1; i<=(length/3);i++)
		{
			sb.append(encodeByte1(data[offset]));
			sb.append(encodeByte1AndByte2(data[offset],data[offset+1]));
			sb.append(encodeByte2AndByte3(data[offset+1],data[offset+2]));
			sb.append(encodeByte3(data[offset+2]));
			offset+=3;
		}
		
		switch (length % 3)	//判断对3的余数
		{
	    case 1:		//剩余1字节
	    	sb.append(encodeByte1(data[offset]));
	    	sb.append(encodeByte1AndByte2(data[offset],(byte)0x00));
	    	sb.append("==");
			break;
		case 2:    //剩余2字节
			sb.append(encodeByte1(data[offset]));
	    	sb.append(encodeByte1AndByte2(data[offset],data[offset+1]));
	    	sb.append(encodeByte2AndByte3(data[offset+1],(byte)0x00));
	    	sb.append("=");
			break;
		default:
			break;
		}
		
		return sb.toString();
	}
	
	/**
	 * Base64解码
	 * @param str		编码后字符串
	 * @param buffer	解码后数据输出缓存
	 * @return			明文数据长度
	 */
	public static int base64Decode(String str, byte[] buffer)
	{
		return base64Decode(str, buffer, 0);
	}
	
	/**
	 * Base64解码
	 * @param str		编码后字符串
	 * @param buffer	解码后数据输出缓存
	 * @param offset	输出偏移
	 * @return			明文数据长度
	 */
	public static int base64Decode(String str, byte[] buffer, int offset)
	{
		if (str == null || str.length() == 0 || (str.length()%4 !=0))
		{
			return 0;
		}
		else
		{
			int clearDataLen;
			if (str.endsWith("=="))
			{
				clearDataLen = (str.length()/4)*3 - 2;
			}
			else if (str.endsWith("="))
			{
				clearDataLen = (str.length()/4)*3 - 1;
			}
			else 
			{
				clearDataLen = (str.length()/4)*3;
			}

			int pos = offset;
			char[] charArr = str.toCharArray();
			for (int i=0;i<charArr.length;i+=4)
			{
				byte b = decodeByte1(charArr[i],charArr[i+1]);
				buffer[pos++] = b;
				if (pos == clearDataLen) break;
				b = decodeByte2(charArr[i+1],charArr[i+2]);
				buffer[pos++] = b;
				if (pos == clearDataLen) break;
				b = decodeByte3(charArr[i+2],charArr[i+3]);
				buffer[pos++] = b;
				if (pos == clearDataLen) break;
			}
			
			return clearDataLen;
		}
	}
	
	/**
	 * Base64解码
	 * @param str	编码后字符串
	 * @return		解码后数据
	 */
	public static byte[] base64Decode(String str)
	{
		if (str == null || str.length() == 0 || (str.length()%4 !=0))
		{
			return new byte[]{};
		}
		else
		{
			int clearDataLen;
			if (str.endsWith("=="))
			{
				clearDataLen = (str.length()/4)*3 - 2;
			}
			else if (str.endsWith("="))
			{
				clearDataLen = (str.length()/4)*3 - 1;
			}
			else 
			{
				clearDataLen = (str.length()/4)*3;
			}
			byte[] data = new byte[clearDataLen];
			int pos = 0;
			char[] charArr = str.toCharArray();
			for (int i=0;i<charArr.length;i+=4)
			{
				byte b = decodeByte1(charArr[i],charArr[i+1]);
				data[pos++] = b;
				if (pos == clearDataLen) break;
				b = decodeByte2(charArr[i+1],charArr[i+2]);
				data[pos++] = b;
				if (pos == clearDataLen) break;
				b = decodeByte3(charArr[i+2],charArr[i+3]);
				data[pos++] = b;
				if (pos == clearDataLen) break;
			}
			return data;
		}
	}
	
	private static char encodeByte1(byte b1)
	{
		byte tmp = (byte)(b1&(byte)0xFC);
		int index = (int)((tmp>>2)&(byte)(0x3F));
		return char_table[index];
	}
	
	private static char encodeByte1AndByte2(byte b1, byte b2)
	{
		byte tmp1 = (byte)((b1&(byte)0x03)<<4);
		tmp1 &= (byte)0x30;
		byte tmp2 = (byte)((b2&(byte)0xF0)>>4);
		tmp2 &= (byte)0x0F;
		int index = (int)(tmp1|tmp2);
		return char_table[index];
	}
	
	private static char encodeByte2AndByte3(byte b2, byte b3)
	{
		byte tmp1 = (byte)((b2&(byte)0x0F)<<2);
		tmp1 &= (byte)0x3C;
		byte tmp2 = (byte)((b3&(byte)0xC0)>>6);
		tmp2 &= (byte)0x03;
		int index = (int)(tmp1|tmp2);
		return char_table[index];
	}
	
	private static char encodeByte3(byte b3)
	{
		byte tmp1 = (byte)(b3&(byte)0x3F);
		int index = (int)tmp1;
		return char_table[index];
	}
	
	private static byte decodeByte1(char c1, char c2)
	{
		byte tmp1 = (byte)((byte)(charToByte(c1)<<2)&(byte)(0xFC));
		byte tmp2 = (byte)((byte)(charToByte(c2)>>4)&(byte)0x03);
		return (byte)(tmp1|tmp2);
	}
	
	private static byte decodeByte2(char c2, char c3)
	{
		byte tmp1 = (byte)((byte)(charToByte(c2)<<4)&(byte)(0xF0));
		byte tmp2 = (byte)((byte)(charToByte(c3)>>2)&(byte)0x0F);
		return (byte)(tmp1|tmp2);
	}
	
	private static byte decodeByte3(char c3, char c4)
	{
		byte tmp1 = (byte)((byte)(charToByte(c3)<<6)&(byte)(0xC0));
		byte tmp2 = (byte)((byte)(charToByte(c4))&(byte)0x3F);
		return (byte)(tmp1|tmp2);
	}
	
	private static byte charToByte(char c)
	{
		for (int i = 0; i < 64; i++)
		{
			if (char_table[i] == c)
			{
				return (byte)i;
			}
		}
		if (c == '=')
		{
			return (byte)0x00;
		}
		return (byte)0xFF;
	}
}
