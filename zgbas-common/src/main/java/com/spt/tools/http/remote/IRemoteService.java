/**
 * 
 */
package com.spt.tools.http.remote;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.spt.tools.http.vo.FileUploadRequest;

/**
 * 跨服务请求工具类
 * 
 * @author huangjian
 *
 */
public interface IRemoteService {

	public <T> ResponseEntity<T> exchangeFile(String url, FileUploadRequest request, Class<T> responseType)
			throws Exception;

	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, Object args, Class<T> responseType) throws RestClientException;

	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, Object args,
			ParameterizedTypeReference<T> responseType) throws RestClientException;

	public <T> ResponseEntity<T> postForEntity(String url, Object args, Class<T> responseType)
			throws RestClientException;
	
	public <T> ResponseEntity<T> getForEntity(String url, Object args, Class<T> responseType)
			throws RestClientException;

	void setRestTemplate(RestTemplate restTemplate);

	<T> ResponseEntity<T> exchange(String url, HttpMethod method, Object args, ParameterizedTypeReference<T> responseType, Map<String, String> headerMap) throws RestClientException;

	<T> ResponseEntity<T> exchangeFile(String url, FileUploadRequest request, Class<T> responseType, Map<String, String> headerMap) throws Exception;

	<T> ResponseEntity<T> exchange(String url, HttpMethod method, Object args, Class<T> responseType, Map<String, String> headerMap) throws RestClientException;

}
