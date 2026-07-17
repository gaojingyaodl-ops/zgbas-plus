package com.spt.bas.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 估图配置参数
 *
 * @Author MoonLight
 * @Date 2024/2/20 13:57
 * @Version 1.0
 */
@Data
@ConfigurationProperties(prefix = "gutu.config")
public class GuTuConfig {

    /**
     * 估图请求URL
     */
    private String url;

    /**
     * 估图产品代号
     */
    private String name;

    /**
     * 估图产品Key
     */
    private String appKey;

    /**
     * 估图秘钥
     */
    private String secret;
}
