package com.yihu.ehr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@SpringBootApplication
public class OAuth2SvrApp {

    public static void main(String[] args) {
        SpringApplication.run(OAuth2SvrApp.class, args);
    }

    private static final String RESOURCE_ID = "blog_resource";

    @Configuration
    @EnableAuthorizationServer // [1]
    protected static class OAuth2Config extends AuthorizationServerConfigurerAdapter {

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
                    .redirectUris("http://anywhere?key=value")
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

    @Configuration
    @EnableResourceServer // [2]
    protected static class ResourceServer extends ResourceServerConfigurerAdapter {

        @Override // [3]
        public void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http
                    // Just for laughs, apply OAuth protection to only 2 resources
                    .requestMatchers().antMatchers("/", "/admin/beans").and()
                    .authorizeRequests()
                    .anyRequest().access("#oauth2.hasScope('read')"); //[4]
            // @formatter:on
        }

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.resourceId(RESOURCE_ID);
        }
    }
}
