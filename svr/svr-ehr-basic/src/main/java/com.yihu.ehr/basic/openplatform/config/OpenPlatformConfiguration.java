package com.yihu.ehr.basic.openplatform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:yhy23456@163.com">huiyang.yu</a>
 * @since 2018.03.21
 */
@Configuration
@ConfigurationProperties(prefix = "open.Platform")
public class OpenPlatformConfiguration {

    private String appId;

    private String secret;

    private String serverUrl;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
}
