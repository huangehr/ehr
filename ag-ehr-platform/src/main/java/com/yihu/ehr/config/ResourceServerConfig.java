package com.yihu.ehr.config;

import com.yihu.ehr.constants.ApiVersion;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.01 10:03
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private static final String RESOURCE_ID = "blog_resource";

    @Override
    public void configure(HttpSecurity http) throws Exception {
        List<String> oauthMatcher = new ArrayList<>(ApiVersion.class.getFields().length);

        for (Field field : ApiVersion.class.getFields()) {
            if (field.getName().startsWith("Version")) {
                oauthMatcher.add(field.get(null) + "/**");
            }
        }

        String[] oauthMatchers = oauthMatcher.toArray(new String[oauthMatcher.size()]);
        http.requestMatchers().antMatchers(oauthMatchers).and()
                .authorizeRequests().antMatchers(oauthMatchers).access("#oauth2.hasScope('read')");
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(RESOURCE_ID);
    }
}