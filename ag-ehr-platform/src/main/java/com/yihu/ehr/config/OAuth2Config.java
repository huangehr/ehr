package com.yihu.ehr.config;

import com.yihu.ehr.service.oauth2.*;
import com.yihu.ehr.web.EhrAuthorizationEndpoint;
import io.swagger.annotations.Authorization;
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
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpointHandlerMapping;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.03 20:50
 */
//@Configuration
public class OAuth2Config{
    private static final String RESOURCE_ID = "ehr";

    //@Configuration
    //@EnableAuthorizationServer
    public static class OAuth2ServerConfig extends AuthorizationServerConfigurerAdapter {

        @Autowired
        private AuthenticationManager authenticationManager;

        EhrTokenServices tokenServices = new EhrTokenServices();
        EhrClientDetailsService clientDetailsService = new EhrClientDetailsService();
        EhrAuthorizationCodeService authorizationCodeService = new EhrAuthorizationCodeService();
        OAuth2RequestFactory requestFactory = new DefaultOAuth2RequestFactory(clientDetailsService);

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            /*tokenServices.setTokenStore(tokenStore());
            tokenServices.setSupportRefreshToken(true);
            tokenServices.setReuseRefreshToken(true);
            tokenServices.setClientDetailsService(clientDetailsService);
            tokenServices.setTokenEnhancer(tokenEnhancer());
            addUserDetailsService(tokenServices, this.userDetailsService);*/

            endpoints.authenticationManager(authenticationManager);
            endpoints.authorizationCodeServices(authorizationCodeService);
            endpoints.tokenServices(tokenServices);
            endpoints.tokenGranter(new EhrTokenGranter(tokenServices, authorizationCodeService, clientDetailsService, requestFactory));
            endpoints.setClientDetailsService(clientDetailsService);
            endpoints.exceptionTranslator(new EhrOAuth2ExceptionTranslator());
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.withClientDetails(clientDetailsService);
        }
    }

    //@Configuration
    //@EnableResourceServer
    public static class ResourceServer extends ResourceServerConfigurerAdapter {
        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/api/v1.0/**").authorizeRequests().anyRequest().access("#oauth2.hasScope('read')");
        }

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.resourceId(RESOURCE_ID);
        }
    }
}