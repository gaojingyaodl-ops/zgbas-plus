package com.spt.tools.core.encrypt;

import java.io.UnsupportedEncodingException;

/**
 * Rc4加解密工具类
 * 
 * @author wangyilin
 *
 */
public final class Rc4Utility 
{
	private static byte[] sbox = new byte[256];;
	
	private Rc4Utility()
	{
	}
	
	/**
	 * 初始化
	 * 
	 * @param key	密钥
	 */
	public static void init(byte[] key)
	{
	    byte[] k = new byte[256];
	    byte tmp = (byte)0x00;
	    for(int i = 0; i < 256; i++) 
	    {
	    	sbox[i] = (byte)i;
	        k[i] = key[i%key.length];
	    }
	    for(int i = 0, j = 0; i < 256; i++) 
	    {
	        j = (j + b2i(sbox[i]) + b2i(k[i]))%256;
	        tmp = sbox[i];
	        sbox[i] = sbox[j];
	        sbox[j] = tmp;
	    }
	}
	
	/**
	 * 初始化
	 * 
	 * @param key	密钥
	 */
	public static void init(String key)
	{
		byte[] keyArr = key.getBytes();
		init(keyArr);
	}
	
	/**
	 * 初始化
	 * 
	 * @param key		密钥
	 * @param charset	编码
	 * @throws UnsupportedEncodingException
	 */
	public static void init(String key, String charset) throws UnsupportedEncodingException
	{
		byte[] keyArr = key.getBytes(charset);
		init(keyArr);
	}
	
	/**
	 * 解密
	 * 
	 * @param data		原始数据
	 * @param charset	编码
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String rc4Decrypt(byte[] data, String charset) throws UnsupportedEncodingException
	{
		byte[] rtn = rc4Crypt(data);
		return new String(rtn, charset);
	}
	
	/**
	 * 解密
	 * 
	 * @param data		原始数据
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String rc4Decrypt(byte[] data)
	{
		byte[] rtn = rc4Crypt(data);
		return new String(rtn);
	}
	
	/**
	 * 加密
	 * 
	 * @param data		原始数据
	 * @param charset	编码
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] rc4Encrypt(String data, String charset) throws UnsupportedEncodingException
	{
		return rc4Crypt(data.getBytes(charset));
	}
	
	/**
	 * 加密
	 * 
	 * @param data		原始数据
	 * @param charset	编码
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] rc4Encrypt(String data)
	{
		return rc4Crypt(data.getBytes());
	}
	
	/**
	 * 加解密
	 * @param data	原始数据
	 * @return
	 */
	public static byte[] rc4Crypt(byte[] data)
	{
		byte[] sbox_tmp = new byte[256];
		System.arraycopy(sbox, 0, sbox_tmp, 0, 256);
		
		int len = data.length;
		byte[] rtn = new byte[len];
		int i = 0, j = 0, t = 0;
	    int k = 0;
	    byte tmp;
	    for (k = 0; k < len; k++)
	    {
	        i = (i + 1) % 256;
	        j = (j + b2i(sbox_tmp[i])) % 256;
	        tmp = sbox_tmp[i];
	        sbox_tmp[i] = sbox_tmp[j];
	        sbox_tmp[j] = tmp;
	        t = (b2i(sbox_tmp[i]) + b2i(sbox_tmp[j])) % 256;
	        rtn[k] = (byte)(data[k]^sbox_tmp[t]);
	    }
	    return rtn;
	}

	private static int b2i(byte b)
	{
		return (int)(b&0x000000FF);
	}
	
	public static void main(String[] args) {
		try {
			Rc4Utility.init("12345678", "UTF-8");
			byte[] key = Rc4Utility.rc4Encrypt("testReport");
			String keyStr = ConvertUtility.byteArr2Str(key);
			System.out.println(keyStr);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
