/**
 * 
 */
package com.spt.tools.http.remote;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Stopwatch;
import com.spt.tools.http.vo.FileUploadRequest;

/**
 * 跨服务请求工具类
 * 
 * @author huangjian
 *
 */
public class RemoteServiceImpl implements IRemoteService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired(required = false)
	private RestTemplate restTemplate;

	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, Object args, Class<T> responseType)
			throws RestClientException {
		return exchange(url, method, args, responseType, null);
	}
	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, Object args, Class<T> responseType, Map<String, String> headerMap)
			throws RestClientException {
		logger.debug("执行远程请求");
		Stopwatch stopWatch = Stopwatch.createStarted();
		HttpHeaders requestHeaders = new HttpHeaders();
		// requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Object> httpEntity = new HttpEntity<Object>(args, requestHeaders);
		if (headerMap != null) {
			headerMap.forEach((key, value) -> {
				requestHeaders.add(key, value);
			});
		}
		ResponseEntity<T> resp = restTemplate.exchange(url, method, httpEntity, responseType);
		logger.debug("远程请求耗时：{}ms, url:{},args:{}, cost {}ms", stopWatch.elapsed(TimeUnit.MILLISECONDS), url, args);
		return resp;
	}

	public <T> ResponseEntity<T> exchangeFile(String url, FileUploadRequest request, Class<T> responseType)
			throws Exception {
		return exchangeFile(url, request, responseType, null);
	}
	@Override
	public <T> ResponseEntity<T> exchangeFile(String url, FileUploadRequest request, Class<T> responseType, Map<String, String> headerMap)
			throws Exception {
		logger.debug("执行远程请求");
		Stopwatch stopWatch = Stopwatch.createStarted();
		MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
		if (request.getResources().size() > 0) {
			ByteArrayResource resource = request.getResources().get(0);
			param.add("file", resource);
			param.add("originalFilename", resource.getFilename());
		}
		param.add("bizTableName", request.getBizFieldName());
		for (String type : request.getAllowTypes()) {
			param.add("allowTypes", type);
		}
		param.add("bizId", request.getBizId());
		param.add("filePath", request.getFilePath());
		param.add("maxPerSize", request.getMaxPerSize());
		param.add("appCode", request.getAppCode());
		param.add("serverName", request.getServerName());

		HttpHeaders requestHeaders = new HttpHeaders();
		if (headerMap != null) {
			headerMap.forEach((key, value) -> {
				requestHeaders.add(key, value);
			});
		}
		HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String, Object>>(param,requestHeaders);

		ResponseEntity<T> resp = restTemplate.exchange(url, HttpMethod.POST, httpEntity, responseType);
		logger.debug("远程请求耗时：{}ms, url:{}", stopWatch.elapsed(TimeUnit.MILLISECONDS), url);
		return resp;
	}
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, Object args,
			ParameterizedTypeReference<T> responseType) throws RestClientException{
		return exchange(url, method, args, responseType, null);
	}

	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, Object args,
			ParameterizedTypeReference<T> responseType, Map<String, String> headerMap) throws RestClientException {
		Stopwatch stopWatch = Stopwatch.createStarted();
		HttpHeaders requestHeaders = new HttpHeaders();
		if (headerMap != null) {
			headerMap.forEach((key, value) -> {
				requestHeaders.add(key, value);
			});
		}
		HttpEntity<Object> httpEntity = new HttpEntity<Object>(args, requestHeaders);
		ResponseEntity<T> resp = restTemplate.exchange(url, method, httpEntity, responseType);
		logger.debug("远程请求耗时：{}ms, url:{},args:{}, cost {}ms", stopWatch.elapsed(TimeUnit.MILLISECONDS), url, args);
		return resp;
	}

	public <T> ResponseEntity<T> postForEntity(String url, Object args, Class<T> responseType)
			throws RestClientException {
		return exchange(url, HttpMethod.POST, args, responseType);
	}

	public <T> ResponseEntity<T> getForEntity(String url, Object args, Class<T> responseType)
			throws RestClientException {
		return exchange(url, HttpMethod.GET, args, responseType);
	}

	@Override
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

}
