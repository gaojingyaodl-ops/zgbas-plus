/**
 * File: Md5Encrypt.java
 * Description: Md5加密数据
 * Copyright 2010 GamaxPay. All rights reserved
 *  
 */
package com.spt.tools.core.encrypt;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * 用Md5进行数据加密 
 * @author Jacky Zhou
 */
public class Md5Encrypt {

	private static Logger logger = LoggerFactory.getLogger(Md5Encrypt.class.getName());

	private static Md5Encrypt md5 = new Md5Encrypt();
	
	private Md5Encrypt(){		
	}

	public static Md5Encrypt getInstance(){
		return md5;
	}
	
	/**
	 * 对所有商户的参数进行加密
	 * @param map 所有将加密的参数
	 * @param privateKey 需要对参数加密的私钥
	 * @return    加密以后得到的字符串对象
	 */
	public static String sign(Map<String, String> params, String privateValue){	
		return sign(params, null , privateValue);
		
	}
	public static String sign(Map<String, String> params,String privateKey, String privateValue){	
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		StringBuffer content = new StringBuffer();
		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			String value = "";
//			try {
//				value = URLDecoder.decode((String) params.get(key), "UTF-8");
//			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			value = (String) params.get(key);
			if (key == null || key.equalsIgnoreCase("sign")
					|| key.equalsIgnoreCase("sign_type")){
				continue;
			}
			content.append(key + "=" + value + "&");
		}
		String linkedContent = content.toString().substring(0, content.lastIndexOf("&"));
		String signcontent;
		if (privateKey!=null) {
			signcontent = linkedContent +"&"+ privateKey+"="+privateValue;		
		}else {
			signcontent = linkedContent + privateValue;		
		}	
		return encrypt(signcontent);
		
	}	
	
	
	/**
	 * 对传入的字符串数据进行MD5加密
	 * @param source	字符串数据
	 * @return   加密以后的数据
	 */
	public static String encrypt(String source) {
		MessageDigest md = null;		
		byte[] bt = null;
		try {
			bt = source.getBytes("UTF-8");
			md = MessageDigest.getInstance("MD5");
			md.update(bt);
			return bytesToHexString(md.digest()); 
		} catch (NoSuchAlgorithmException e) {
			logger.error("非法摘要算法", e);
			throw new RuntimeException(e);	
		}catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	
	/**
	 * 把字节数组转换成16进制字符串
	 * @param bArray 传入的二进制数组
	 * @return 16进制的字符串
	 */
	public static String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

}
