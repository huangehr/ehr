package com.yihu.ehr.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.01 9:57
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {

    private static final String RESOURCE_ID = "blog_resource";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.pathMapping("/oauth/authorize", "/login/oauth/authorize")
                .pathMapping("/oauth/confirm_access", "/login/oauth/confirm_access")
                .pathMapping("/oauth/token", "/login/oauth/token")
                .pathMapping("/oauth/check_token", "/login/oauth/check_token")
                .pathMapping("/oauth/token_key", "/login/oauth/token_key")
                .pathMapping("/oauth/error ", "/login/oauth/error ")
                .authenticationManager(authenticationManager);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
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
    }
}
