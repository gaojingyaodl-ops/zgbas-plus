package com.spt.bas.web.config;

import com.spt.tools.shiro.util.ShiroUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "shiro.prop")
public class ShiroPropConfig {
    private String appCd;
    private String mockPassword;
    private boolean sessionEnable = true;

    public void setAppCd(String appCd) {
        this.appCd = appCd;
    }

    public String getMockPassword() {
        return mockPassword;
    }

    public void setMockPassword(String mockPassword) {
        this.mockPassword = mockPassword;
    }

    public String getAppCd() {
        return appCd;
    }

    public boolean isSessionEnable() {
        return sessionEnable;
    }

    public void setSessionEnable(boolean sessionEnable) {
        this.sessionEnable = sessionEnable;
    }
}
