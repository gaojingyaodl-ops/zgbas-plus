package com.spt.bas.purchase.wx.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: gaojy
 * @create 2022/1/14 17:23
 * @version: 1.0
 * @description:
 */
@Data
@ConfigurationProperties(prefix = "jinxin")
public class JinXinConfig {
    private String host;

    private String livingUrl;

    private String merchNo;

    private String merchKey;

    private String merchRsaPrivateKey;

    /**
     * 通信证书配置
     */
    private String keyStorePath;

    /**
     * 通信证书密码
     */
    private String keyStorePassword;
    
    /**
     * 信任证书链配置
     */
    private String trustStorePath;

    /**
     * 信任证书密码
     */
    private String trustStorePassword;
    
    private int connectTimeout;
    private int readTimeout;

    /**
     * 临时文件路径
     */
    private String tmpFilePath;
    
}
