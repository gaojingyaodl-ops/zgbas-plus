package com.spt.framework.config;

import com.hsoft.file.sdk.remote.FileRemote;
import com.hsoft.push.sdk.remote.PushClientHttp;
import com.spt.auth.sdk.open.AuthOpenFacade;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * External HTTP SDK beans (EXT-01/02). Ports {@code basServer/config/FrameworkConfig.java:39-71}
 * verbatim in structure — three facades wired with the {@code init(secretKey, appCode, url)} pattern.
 * Keys are resolved from {@code application-{dev,prod}.yml} via the injected {@link Environment}
 * (EXT-04 / D-P2-13).
 *
 * <p><b>Security gate (T-P2-log):</b> the source logged {@code secretKey} and {@code appCode} at
 * INFO level (FrameworkConfig.java:55). That line is deleted here — no sensitive value is logged
 * at any level. The source's service-lookup null-check block (lines 57-59) is also dropped as
 * BasPiccConfig/BasClientConfig microservice residue, out of scope for the monolith.
 */
@Configuration
public class ZgbasExternalBeansConfig {

    @Autowired
    private Environment env;

    @Bean
    public FileRemote fileRemote() {
        FileRemote http = new FileRemote();
        http.init(env.getProperty("spt.app.secretKey"),
                 env.getProperty("spt.app.appCode"),
                 env.getProperty("file.server.url"));
        return http;
    }

    @Bean
    public IAuthOpenFacade authOpenFacade() {
        AuthOpenFacade http = new AuthOpenFacade();
        http.init(env.getProperty("spt.app.secretKey"),
                 env.getProperty("spt.app.appCode"),
                 env.getProperty("auth.url"));
        return http;
    }

    @Bean
    public PushClientHttp pushClientHttp() {
        PushClientHttp http = new PushClientHttp();
        http.init(env.getProperty("spt.app.secretKey"),
                 env.getProperty("spt.app.appCode"),
                 env.getProperty("push.server.url"));
        return http;
    }
}
