package com.spt.tools.core.encrypt;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * 基于JDK的Des算法（含Base64转换）
 * 
 * @author wangyilin
 *
 */
public final class DesAndConvertUtility 
{
	private static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";
	private static final byte[] IV = {(byte)0xF1, (byte)0x24, (byte)0x79, (byte)0xC0, (byte)0xC8, (byte)0x98, (byte)0x21, (byte)0xD6};
	
	private DesAndConvertUtility()
	{
	}
	
	/**
	 * 加密并且转换为base64字符串
	 * 
	 * @param key	密钥，例如1122334455667788 代表：0x1122334455667788
	 * @param src	明文
	 * @return		密文
	 * @throws UnsupportedEncodingException
	 */
	public static String encryptBase64(String key, String src) throws UnsupportedEncodingException
	{
		byte[] keybyte = ConvertUtility.str2ByteArr(key);
		//byte[] keybyte = key.getBytes("UTF8");
		
		byte[] srcbyte = src.getBytes("UTF8");
		//System.err.println(ConvertUtility.byteArr2Str(srcbyte));
		byte[] raw = encryptRaw(keybyte, srcbyte);
		//System.err.println(ConvertUtility.byteArr2Str(raw));
		return Base64Utility.base64Encode(raw); 
	}
	
	/**
	 * 解密Base64字符串并且解密
	 * 
	 * @param key	密钥，例如1122334455667788 代表：0x1122334455667788
	 * @param src	密文
	 * @return		明文
	 * @throws UnsupportedEncodingException
	 */
	public static String decryptBase64(String key, String src) throws UnsupportedEncodingException
	{
		byte[] keybyte = ConvertUtility.str2ByteArr(key);
		byte[] srcbyte = Base64Utility.base64Decode(src);
		byte[] raw = decryptRaw(keybyte, srcbyte);
		return new String(raw, "UTF8");
	}
	
    public static byte[] encryptRaw(byte[] keybyte, byte[] srcbyte) 
    {
       try 
       {
    	   DESKeySpec dks = new DESKeySpec(keybyte); 
    	   SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES"); 
    	   Key secretKey = keyFactory.generateSecret(dks); 
    	   Cipher cipher = Cipher.getInstance(ALGORITHM_DES); 
    	   IvParameterSpec iv = new IvParameterSpec(IV); 
    	   AlgorithmParameterSpec paramSpec = iv; 
    	   cipher.init(Cipher.ENCRYPT_MODE, secretKey,paramSpec); 
    	   byte[] bytes = cipher.doFinal(srcbyte); 
    	   return bytes;   
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (java.lang.Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    public static byte[] decryptRaw(byte[] keybyte, byte[] srcbyte) 
    {    
    	try 
    	{
    		DESKeySpec dks = new DESKeySpec(keybyte); 
    		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES"); 
    		Key secretKey = keyFactory.generateSecret(dks); 
    		Cipher cipher = Cipher.getInstance(ALGORITHM_DES); 
    		IvParameterSpec iv = new IvParameterSpec(IV); 
    		AlgorithmParameterSpec paramSpec = iv; 
    		cipher.init(Cipher.DECRYPT_MODE, secretKey,paramSpec); 
    		return cipher.doFinal(srcbyte); 
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (java.lang.Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }
}
