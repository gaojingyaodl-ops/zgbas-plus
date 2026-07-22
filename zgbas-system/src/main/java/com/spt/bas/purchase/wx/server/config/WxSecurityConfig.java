// Phase 4 stub — Phase 5 will overlay with complete source version
// D-06: WxSecurityConfig registers JwtAuthenticationFilter via FilterRegistrationBean
//       limiting JWT interception to /wx/*, /ewechat/*, /axq/* only.
//       Shiro session paths (/login, /index) are NOT in these patterns (per D-08).
package com.spt.bas.purchase.wx.server.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * WX 微信端安全配置
 * <p>
 * 使用 FilterRegistrationBean 注册 JwtAuthenticationFilter，限制路径范围为
 * /wx/*, /ewechat/*, /axq/*，不干扰 Shiro session 路径（/login /index 等）。
 * </p>
 *
 * @author auto-generated
 */
@Configuration
@EnableConfigurationProperties(JwtConfig.class)
public class WxSecurityConfig {

    /**
     * 注册 JwtAuthenticationFilter 为 Spring 管理 bean。
     * Spring 会注入其 @Autowired 字段（JwtUtil/CompanyUserDao/UserDetailDao）。
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    /**
     * 通过 FilterRegistrationBean 将 JwtAuthenticationFilter 注册到 Servlet 容器，
     * 路径限定为 /wx/*, /ewechat/*, /axq/*。
     * order=1 确保该 filter 在 Shiro filter chain 之前执行（Shiro 已将 /wx/** 设为 anon）。
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.addUrlPatterns("/wx/*", "/ewechat/*", "/axq/*");
        registration.setOrder(1);
        return registration;
    }
}
