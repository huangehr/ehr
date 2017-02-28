package com.yihu.ehr.oauth2.config;

import com.yihu.ehr.oauth2.oauth2.*;
import com.yihu.ehr.oauth2.oauth2.jdbc.EhrJDBCAuthorizationCodeService;
import com.yihu.ehr.oauth2.oauth2.jdbc.EhrJDBCClientDetailsService;
import com.yihu.ehr.oauth2.oauth2.jdbc.EhrJDBCTokenStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import javax.sql.DataSource;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.03 20:50
 */
@Configuration
public class OAuth2Config {
    private static final String RESOURCE_ID = "ehr";

    @Autowired
    private DataSource dataSource;

    @Configuration
    @EnableAuthorizationServer
    public static class OAuth2ServerConfig extends AuthorizationServerConfigurerAdapter {

        @Autowired
        private AuthenticationManager authenticationManager;
        @Autowired
        private AuthorizationCodeServices authorizationCodeService;
        @Autowired
        private EhrTokenServices tokenServices;
        @Autowired
        private EhrJDBCClientDetailsService clientDetailsService;
        @Autowired
        private EhrJDBCTokenStoreService ehrJDBCTokenStoreService;

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            EhrTokenGranter tokenGranter = new EhrTokenGranter(tokenServices,
                    authorizationCodeService,
                    clientDetailsService,
                    new DefaultOAuth2RequestFactory(clientDetailsService));

            endpoints.authenticationManager(authenticationManager)
                    .authorizationCodeServices(authorizationCodeService)
                    .tokenServices(tokenServices)
                    .tokenStore(ehrJDBCTokenStoreService)
                    .exceptionTranslator(new EhrOAuth2ExceptionTranslator())
                    .tokenGranter(tokenGranter)
                    .setClientDetailsService(clientDetailsService);
        }


        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients
                    .withClientDetails(clientDetailsService);
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
            security.passwordEncoder(new StandardPasswordEncoder());
        }
    }

    //@Configuration
   // @EnableResourceServer
    public static class ResourceServer extends ResourceServerConfigurerAdapter {

        @Autowired
        private EhrTokenServices tokenServices;

        @Autowired
        private EhrJDBCClientDetailsService clientDetailsService;

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
                    .requestMatcher(
                            new OrRequestMatcher(
                                    new AntPathRequestMatcher("/api/v1.0/**")
                            ))
                    .authorizeRequests()
                    .antMatchers("/api/v1.0/**").access("#oauth2.hasScope('read')");
        }

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources
                    .resourceId(RESOURCE_ID)
                    .tokenServices(tokenServices)
                    .tokenExtractor(new EhrTokenExtractor())
                    .authenticationEntryPoint(new EhrOAuth2AuthenticationEntryPoint());
        }
    }

    /**
     * jdbc 客户端服务类
     *
     * @return
     */
    @Bean
    EhrJDBCClientDetailsService ehrJDBCClientDetailsService() {
        return new EhrJDBCClientDetailsService(dataSource);
    }

    /**
     * jdbc code的服务类
     *
     * @return
     */
    @Bean
    EhrJDBCAuthorizationCodeService ehrJDBCAuthorizationCodeService() {
        return new EhrJDBCAuthorizationCodeService(dataSource);
    }

    @Bean
    EhrJDBCTokenStoreService tokenStoreService() {
        EhrJDBCTokenStoreService tokenStoreService = new EhrJDBCTokenStoreService(dataSource);

        return tokenStoreService;
    }

    @Bean
    EhrTokenServices ehrTokenServices(EhrJDBCClientDetailsService clientDetailsService, EhrJDBCTokenStoreService tokenStoreService) {
        EhrTokenServices tokenServices = new EhrTokenServices();

        tokenServices.setReuseRefreshToken(true);
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setTokenStore(tokenStoreService);
        tokenServices.setClientDetailsService(clientDetailsService);

        return tokenServices;
    }
}