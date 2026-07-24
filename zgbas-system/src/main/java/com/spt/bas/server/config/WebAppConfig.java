/**
 * 
 */
package com.spt.bas.server.config;

import java.util.List;

import javax.servlet.MultipartConfigElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.unit.DataSize;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.http.interceptor.PageInterceptor;

/**
 * @author wlddh
 *
 */
@Configuration
public class WebAppConfig extends WebMvcConfigurerAdapter {

	@Autowired
	private PageInterceptor pageInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		 registry.addInterceptor(pageInterceptor);  
	}
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addRedirectViewController("/", "/index");
		super.addViewControllers(registry);
	}

	// Phase 5 Plan 05-03 (D-P5-01): CORS verbatim from WX WebAppConfig — the ONLY
	// unique contribution of the WX WebAppConfig. Everything else (multipartConfigElement,
	// PageInterceptor, message converters, '/'->'/index') already exists here, so the WX
	// WebAppConfig is NOT migrated wholesale — duplicating multipartConfigElement would
	// throw BeanDefinitionStoreException.
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
//				.allowedOrigins("*")
				.allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
				.allowCredentials(true)
				.allowedOriginPatterns("*")
				.maxAge(3600)
				.allowedHeaders("*");
	}
	/**
	 * 装饰器
	 * @return
	 * 2016年8月27日下午12:37:20
	 *//*
	@Bean
	public FilterRegistrationBean siteMeshFilter(){
		FilterRegistrationBean fitler = new FilterRegistrationBean();
		WebSiteMeshFilter siteMeshFilter = new WebSiteMeshFilter();
		fitler.setFilter(siteMeshFilter);
		return fitler;
	}*/
	
	
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		MappingJackson2HttpMessageConverter converter =new MappingJackson2HttpMessageConverter();
		ObjectMapper objectMapper =JsonUtil.getObjectMapper();
//		SerializationFeature.FAIL_ON_EMPTY_BEANS
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		converter.setObjectMapper(objectMapper);
		converters.add(converter);
		converters.add(new FormHttpMessageConverter());
		converters.add(new ByteArrayHttpMessageConverter());
		converters.add(new StringHttpMessageConverter());
		super.configureMessageConverters(converters);
	}
	
	
	@Bean
	public FilterRegistrationBean characterEncodingFilter() {
		FilterRegistrationBean filter=new FilterRegistrationBean();	
		CharacterEncodingFilter characterEncodingFilter =new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		filter.setFilter(characterEncodingFilter);
		return filter;
	}
	
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 设置文件大小限制 ,超出设置页面会抛出异常信息，
        // 这样在文件上传的地方就需要进行异常信息的处理了;
        factory.setMaxFileSize(DataSize.ofMegabytes(10L)); // KB,MB
        /// 设置总上传数据总大小
        factory.setMaxRequestSize(DataSize.ofMegabytes(100L));
        // Sets the directory location where files will be stored.
        // factory.setLocation("路径地址");
        return factory.createMultipartConfig();
    }
}
