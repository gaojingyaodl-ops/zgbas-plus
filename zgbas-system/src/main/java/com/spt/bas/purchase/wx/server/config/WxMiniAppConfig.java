package com.spt.bas.purchase.wx.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * <p>
 *  微信小程序配置信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-17 16:57
 */
@Data
@ConfigurationProperties(prefix = "wx.miniapp")
public class WxMiniAppConfig {
    private List<Config> configs;

    @Data
    public static class Config {
        /**
         * 设置微信小程序的appid
         */
        private String appid;

        /**
         * 设置微信小程序的Secret
         */
        private String secret;

        /**
         * 设置微信小程序消息服务器配置的token
         */
        private String token;

        /**
         * 设置微信小程序消息服务器配置的EncodingAESKey
         */
        private String aesKey;

        /**
         * 消息格式，XML或者JSON
         */
        private String msgDataFormat;
    }
}
