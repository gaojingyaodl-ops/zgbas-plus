/**
 * 
 */
package com.spt.tools.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * object对象序列化工具类
 * 
 * @author wlddh
 *
 */

public class ObjectSerializeUtil implements Serializable {
	private static final long serialVersionUID = 667459266283284304L;
	private static final Logger log = LoggerFactory.getLogger(ObjectSerializeUtil.class);

	/**
	 * 序列化对象
	 * 
	 * @param obj
	 * @param     <T> T 必须实现Serializable接口， T 中尽量加上 serialVersionUID (private static
	 *            final long serialVersionUID)
	 * @return
	 */
	public static <T> byte[] serialize(T obj) {
		try {
			if (obj == null) {
				return null;
			}
//			if (obj instanceof String) {
//				return ((String)obj).getBytes(ChangeCharset.UTF_8);
//			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			byte[] byteArray = bos.toByteArray();
			bos.close();
			oos.close();
			return byteArray;
		} catch (IOException e) {
			log.error("ObjectSerializeUtil-IOException:{}", e);
		} finally {

		}
		return null;
	}

	/**
	 * 反序列化对象
	 * 
	 * @param byteArray
	 * @param           <T>T 必须实现Serializable接口， * T 中尽量加上 serialVersionUID (private
	 *                  static final long serialVersionUID)
	 * @return
	 */
	public static <T> T deserialize(byte[] byteArray) {
		try {
			if (byteArray == null) {
				return null;
			}
			ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
			ObjectInputStream ois = new ObjectInputStream(bis);
			T obj = (T) ois.readObject();
			ois.close();
			bis.close();
			return obj;
		} catch (Exception e) {
			log.error("ObjectSerializeUtil-IOException:{}", e);
		}

		return null;
	}

}
