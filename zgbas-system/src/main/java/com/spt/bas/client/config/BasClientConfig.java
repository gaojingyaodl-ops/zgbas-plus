package com.spt.bas.client.config;

import com.spt.bas.client.constant.BasConstants;
import com.spt.tools.core.bean.LocalServerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * basClient-side configuration ported verbatim from source
 * {@code basCore/basClient/.../config/BasClientConfig.java} (D-P2-07 保包名).
 *
 * <p>Produces the {@link LocalServerConfig} bean named {@code basServerConfig} — the runtime
 * anchor for the D-P4-01 方案 A Feign self-loopback. The 238 {@code @FeignClient} contracts in
 * {@code com.spt.bas.client.remote} reference {@code url = BasConstants.SERVER_URL =
 * "\#{basServerConfig.url}"} (SpEL). This bean reads {@code spt.bas.server.url} from the
 * environment (via {@link LocalServerConfig#setUrlKey(String)}), so setting
 * {@code spt.bas.server.url=http://localhost:8080} in yml makes every bas Feign proxy loop back
 * to the monolith's own port 8080.
 *
 * <p>{@code @DependsOn({"propertiesUtil"})} matches the source ordering constraint: the
 * {@link com.spt.tools.core.prop.PropertiesUtil} bean (registered by {@code ToolsCoreConfig})
 * must initialize first because {@link LocalServerConfig#getUrl()} reads through it.
 *
 * <p>Source: {@code /Users/alan/WorkSpace/IDEA/zgbas/basCore/basClient/src/main/java/com/spt/bas/client/config/BasClientConfig.java:1-19}.
 */
@Configuration
public class BasClientConfig {

	@DependsOn({"propertiesUtil"})
	@Bean(BasConstants.SERVER_BEAN_NAME)
	public LocalServerConfig localServerConfig() {
		LocalServerConfig conf = new LocalServerConfig();
		conf.setUrlKey(BasConstants.SERVER_URL_KEY);
		return conf;
	}
}
