package com.spt.bas.purchase.wx.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Phase 5 Plan 05-03 (D-P5-15): WX-only carrier config.
 *
 * <p>Replaces the legacy {@code FrameworkConfig}, which is NOT migrated verbatim.
 * FrameworkConfig's six {@code @Bean}s are all dropped because the monolith
 * already provides them or they would conflict:
 * <ul>
 *   <li>{@code threadPoolTaskExecutor} — monolith ScheduleConfig provides scheduling executors</li>
 *   <li>{@code dataSourceConfig} — monolith owns the datasource</li>
 *   <li>{@code pushClientHttp} — monolith keeps external push SDK (constraint #7)</li>
 *   <li>{@code authOpenFacade} / {@code fileRemote} — monolith keeps external auth/file SDK (constraint #7)</li>
 *   <li>{@code customConfig} — superseded by FilterRegistrationBean wiring</li>
 * </ul>
 *
 * <p>This class contributes only the WX-specific {@link EweChatConfig} bean, bound
 * to prefix {@code ewechat.config}.
 */
@Configuration
public class WxCarrierConfig {

    @Bean
    @ConfigurationProperties(prefix = "ewechat.config")
    public EweChatConfig eweChatConfig() {
        return new EweChatConfig();
    }
}
