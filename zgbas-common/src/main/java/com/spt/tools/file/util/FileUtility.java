package com.spt.tools.file.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.spt.tools.core.encrypt.Base64Utility;

/**
 * 文件操作类工具
 * 
 * @author wangyilin
 *
 */
public final class FileUtility {
	private final static Logger log = LoggerFactory.getLogger(FileUtility.class);

	private FileUtility() {
	}

	/**
	 * 读取文本文件内容
	 * 
	 * @param fileName 文件名
	 * @return 文件内容
	 */
	public static String readFile(String fileName, String encoding) {
		BufferedReader br = null;
		try {
			File file = new File(fileName);
			StringBuilder sb = new StringBuilder();
			InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
			br = new BufferedReader(read);
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line + System.getProperty("line.separator"));
			}

			br.close();
			return sb.toString();
		} catch (Exception e) {
			log.error("readFile error!", e);
			return null;
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 读取文本文件内容
	 * 
	 * @param fileName 文件名
	 * @return 文件内容
	 */
	public static String readFile(String fileName) {
		BufferedReader br = null;
		try {
			File file = new File(fileName);
			StringBuilder sb = new StringBuilder();
			InputStreamReader read = new InputStreamReader(new FileInputStream(file));
			br = new BufferedReader(read);
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line + System.getProperty("line.separator"));
			}

			br.close();
			return sb.toString();
		} catch (Exception e) {
			return null;
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 从JAR包中读取文件
	 * 
	 * @param clazz 装载类类型
	 * @param path  例如： /config/config.properties
	 * @return
	 */
	public static String readFileFromJar(Class<?> clazz, String path) {
		InputStream in = null;
		try {
			in = clazz.getResourceAsStream(path);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			StringBuffer sb = new StringBuffer();
			String s;
			while ((s = br.readLine()) != null) {
				sb.append(s + System.lineSeparator());
			}
			return sb.toString();
		} catch (Exception e) {
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public List<Base64DataVo> fileCoverBase64(HttpServletRequest request) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		List<Base64DataVo> dataList =new ArrayList<>();
		for (Iterator<String> it = multipartRequest.getFileNames(); it.hasNext();) {
			String fileName = it.next();

			List<MultipartFile> lstFiles = multipartRequest.getFiles(fileName);
			for (MultipartFile file : lstFiles) {
				Base64DataVo dataVo = new Base64DataVo();
				try {
					dataVo.setFileName(file.getOriginalFilename());
					dataVo.setBase64Data(Base64Utility.base64Encode(file.getBytes()));
					dataList.add(dataVo);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return dataList;
	}
}
