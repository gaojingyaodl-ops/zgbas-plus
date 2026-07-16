package com.spt.tools.http.vo;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/** 文件上传通用请求 */
public class FileUploadRequest implements Serializable {
	private static final long serialVersionUID = 2810963300805154671L;
	public static final String[] FILE_SUFFIX_IMAGE = { ".jpg", ".gif", ".png", ".bmp" };
	private String appCode;// 应用代码
	private String serverName;// 服务名称,根据域名判断存储在本地还是存储在阿里云
	private String filePath;// 文件存储想对路径，例如：sup/user/
	private Long bizId = 0l;// 实体记录ID
	private String bizTableName;
	private String bizFieldName;// 上传附件所对应的字段，如果为null，表示针对记录的附件，其他情况，增加指定字段的附件
	private long maxPerSize = 1024 * 1024 * 10;// 每次上传单个文件最大值,10M
	private String[] allowTypes = FILE_SUFFIX_IMAGE;
	private List<ByteArrayResource> resources=new ArrayList<>();

	public void addResource(final MultipartFile file) throws IOException {
		ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
			@Override
			public String getFilename() throws IllegalStateException {
				String filename= file.getOriginalFilename();
//				try {
//					return new String(filename.getBytes("utf-8"), "iso8859-1");
//				} catch (UnsupportedEncodingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				return filename;
			}

		};
		resources.add(resource);
	}

	public void parseFiles(HttpServletRequest request) {

		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

		for (Iterator<String> it = multipartRequest.getFileNames(); it.hasNext();) {
			String fileName = it.next();

			List<MultipartFile> lstFiles = multipartRequest.getFiles(fileName);
			for (MultipartFile file : lstFiles) {
				try {
					addResource(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Long getBizId() {
		return bizId;
	}

	public void setBizId(Long bizId) {
		this.bizId = bizId;
	}

	public String getBizTableName() {
		return bizTableName;
	}

	public void setBizTableName(String bizTableName) {
		this.bizTableName = bizTableName;
	}

	public String getBizFieldName() {
		return bizFieldName;
	}

	public void setBizFieldName(String bizFieldName) {
		this.bizFieldName = bizFieldName;
	}

	public long getMaxPerSize() {
		return maxPerSize;
	}

	public void setMaxPerSize(long maxPerSize) {
		this.maxPerSize = maxPerSize;
	}

	public String[] getAllowTypes() {
		return allowTypes;
	}

	public void setAllowTypes(String[] allowTypes) {
		this.allowTypes = allowTypes;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public List<ByteArrayResource> getResources() {
		return resources;
	}

	public void setResources(List<ByteArrayResource> resources) {
		this.resources = resources;
	}

}
