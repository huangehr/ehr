package com.yihu.ehr.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.03 20:50
 */
@Configuration
@EnableAuthorizationServer // [1]
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {
    private static final String RESOURCE_ID = "blog_resource";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override // [2]
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager);
    }

    @Override // [3]
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // @formatter:off
        clients.inMemory()
                .withClient("client-with-registered-redirect")
                .authorizedGrantTypes("authorization_code")
                .authorities("ROLE_CLIENT")
                .scopes("read", "trust")
                .resourceIds(RESOURCE_ID)
                .redirectUris("http://www.yihu.com?key=value")
                .secret("secret123")
                .and()
                .withClient("my-client-with-secret")
                .authorizedGrantTypes("client_credentials", "password")
                .authorities("ROLE_CLIENT")
                .scopes("read")
                .resourceIds(RESOURCE_ID)
                .secret("secret");
        // @formatter:on
    }

}
