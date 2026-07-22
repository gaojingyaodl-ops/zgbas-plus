// Phase 4 stub — Phase 5 will overlay with complete source version
package com.spt.bas.purchase.wx.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * jwt配置
 * @Author shengong
 * @Date 2020-07-24 22:29
 * @Description TODO
 */
@Data
@ConfigurationProperties(prefix = "jwt.config")
public class JwtConfig {
    /**
     * jwt 加密 key，默认值：sgcoding.
     */
    private String key;

    /**
     * jwt 过期时间，默认值：600000 {@code 10 分钟}.
     */
    private Long ttl;

    /**
     * 开启 记住我 之后 jwt 过期时间，默认值 604800000 {@code 7 天}
     */
    private Long remember;
}
