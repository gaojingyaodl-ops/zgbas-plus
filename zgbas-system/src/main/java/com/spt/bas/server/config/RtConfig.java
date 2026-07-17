package com.spt.bas.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 融拓对接配置参数
 * @Author: gaojy
 * @create 2022/4/8 10:05
 * @version: 1.0
 * @description:
 */
@Data
@ConfigurationProperties(prefix = "rt.config")
public class RtConfig {
    private String url;

    private String clientId;

    private String clientSecret;

    private String sceneId;

    private String sceneKey;
}
