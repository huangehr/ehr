package com.yihu.ehr.config;

import com.yihu.ehr.service.oauth2.*;
import com.yihu.ehr.web.EhrAuthorizationEndpoint;
import io.swagger.annotations.Authorization;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.*;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpointHandlerMapping;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;

import java.util.Arrays;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.03 20:50
 */
@Configuration
public class OAuth2Config{
    private static final String RESOURCE_ID = "ehr";

    @Configuration
    @EnableAuthorizationServer
    public static class OAuth2ServerConfig extends AuthorizationServerConfigurerAdapter {

        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private EhrAuthorizationCodeService authorizationCodeService;

        @Autowired
        private EhrTokenServices tokenServices;

        @Autowired
        EhrClientDetailsService clientDetailsService;

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            EhrTokenGranter tokenGranter = new EhrTokenGranter(tokenServices,
                    authorizationCodeService,
                    clientDetailsService,
                    new DefaultOAuth2RequestFactory(clientDetailsService));

            String scopes[] = "user,user.demographic_id,user.health_profiles,organization".split(",");
            /*tokenGranter.grant("authorization_code", new TokenRequest(null, "client-with-registered-redirect",
                    Arrays.asList(scopes),
                    "authorization_code"));*/

            endpoints.authenticationManager(authenticationManager);
            endpoints.authorizationCodeServices(authorizationCodeService);
            endpoints.tokenServices(tokenServices);
            endpoints.setClientDetailsService(clientDetailsService);
            endpoints.exceptionTranslator(new EhrOAuth2ExceptionTranslator());
            endpoints.tokenGranter(tokenGranter);
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.withClientDetails(clientDetailsService);
        }
    }

    @Configuration
    @EnableResourceServer
    public static class ResourceServer extends ResourceServerConfigurerAdapter {

        @Autowired
        private EhrTokenServices tokenServices;

        @Autowired
        EhrClientDetailsService clientDetailsService;

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/api/v1.0/**").authorizeRequests().anyRequest().access("#oauth2.hasScope('read')");
        }

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.resourceId(RESOURCE_ID);
            resources.tokenServices(tokenServices);
            resources.tokenExtractor(new EhrTokenExtractor());
            resources.authenticationEntryPoint(new EhrOAuth2AuthenticationEntryPoint());
        }
    }

    @Bean
    EhrClientDetailsService ehrClientDetailsService(){
        return new EhrClientDetailsService();
    }

    @Bean
    EhrAuthorizationCodeService authorizationCodeService(){
        return new EhrAuthorizationCodeService();
    }

    @Bean
    EhrTokenStoreService tokenStoreService(){
        EhrTokenStoreService tokenStoreService = new EhrTokenStoreService();

        return tokenStoreService;
    }

    @Bean
    EhrTokenServices ehrTokenServices(EhrClientDetailsService clientDetailsService, EhrTokenStoreService tokenStoreService){
        EhrTokenServices tokenServices = new EhrTokenServices();

        tokenServices.setReuseRefreshToken(true);
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setTokenStore(tokenStoreService);
        tokenServices.setClientDetailsService(clientDetailsService);

        return tokenServices;
    }
}