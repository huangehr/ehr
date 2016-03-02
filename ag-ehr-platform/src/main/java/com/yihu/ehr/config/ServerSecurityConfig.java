package com.yihu.ehr.config;

import com.yihu.ehr.constants.ApiVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.01 9:57
 */
@Configuration
public class ServerSecurityConfig {

    private static final String SERVER_RESOURCE_ID = "oauth2server";

    @Order(10)
    @Configuration
    protected static class UiResourceConfiguration extends WebSecurityConfigurerAdapter {
        @Autowired
        private FakeUserDetailsService userDetailsService;

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .formLogin().loginPage("/login").failureUrl("/login?error").defaultSuccessUrl("/login?logout").permitAll()
                    .and()
                    .authorizeRequests().antMatchers("/css/**", "/docs").permitAll()
                    .and()
                    .authorizeRequests().antMatchers("/swagger**", "/login/oauth/**").access("hasRole('ROLE_USER')");
        }
    }

    @Order(1)
    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.resourceId(SERVER_RESOURCE_ID);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
                    .requestMatchers().antMatchers("/api/v1.0/**")
                    .and()
                    .authorizeRequests().antMatchers("/api/v1.0/**").access("#oauth2.hasScope('read')");
        }

    }

    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
        @Autowired
        private TokenStore tokenStore;

        @Autowired
        private AuthenticationManager authenticationManager;//身份认证管理者

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

            clients.inMemory().withClient("appclinet")
                    .resourceIds(SERVER_RESOURCE_ID)
                    .authorizedGrantTypes("authorization_code")
                    .authorities("ROLE_CLIENT")
                    .scopes("read", "write", "trust", "自定义权限")//这个scope是自定义的么？？
                    .accessTokenValiditySeconds(100)
                    .secret("secret")
                    .redirectUris("redirect_uri");
        }

        @Bean
        public TokenStore tokenStore() {
            return new InMemoryTokenStore();
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.tokenStore(tokenStore)
                    .authenticationManager(authenticationManager);
                    //.pathMapping("/oauth/authorize", "/oauth2/authorize")
                    //.pathMapping("/oauth/token", "/oauth2/token");
            //以上的注释掉的是用来改变配置的
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
            oauthServer.allowFormAuthenticationForClients();
        }

        //这个bean是必不可少的，不然的话就会初始化报错的。
        @Bean
        public ApprovalStore approvalStore() throws Exception {
            TokenApprovalStore store = new TokenApprovalStore();
            store.setTokenStore(tokenStore);
            return store;
        }

    }

    /*@Autowired
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
        /*clients.inMemory()
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
    }*/
}
