package com.spt.tools.http.feign;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spt.tools.core.json.JsonUtil;

import feign.Request;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;

@Configuration
public class FeignConfig {

	@Bean
	public Encoder encoder() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		ObjectMapper objectMapper = JsonUtil.getObjectMapper();
		converter.setObjectMapper(objectMapper);
		List<HttpMessageConverter<?>> list = new ArrayList<>();
		list.add(converter);
		final HttpMessageConverters converters = new HttpMessageConverters(list);
		ObjectFactory<HttpMessageConverters> messageConverters = new ObjectFactory<HttpMessageConverters>() {

			@Override
			public HttpMessageConverters getObject() throws BeansException {
				return converters;
			}
		};

		SpringEncoder encoder = new SpringEncoder(messageConverters);
		return encoder;
	}

	@Bean
	public Decoder decode() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		ObjectMapper objectMapper = JsonUtil.getObjectMapper();
		converter.setObjectMapper(objectMapper);
		List<HttpMessageConverter<?>> list = new ArrayList<>();
		list.add(converter);
		final HttpMessageConverters converters = new HttpMessageConverters(list);
		ObjectFactory<HttpMessageConverters> messageConverters = new ObjectFactory<HttpMessageConverters>() {

			@Override
			public HttpMessageConverters getObject() throws BeansException {
				return converters;
			}
		};
		SpringDecoder springDecoder = new SpringDecoder(messageConverters);
		ResponseEntityDecoder encoder = new ResponseEntityDecoder(springDecoder);
		return encoder;
	}

	@Bean
	public ErrorDecoder errorDecoder(){
		return new CommErrorDecoder();
	}

	
	/** 配置超时时间 */
	@Bean
	Request.Options feignOptions() {
		return new Request.Options(10 * 1000, 60 * 1000);
	}
}
