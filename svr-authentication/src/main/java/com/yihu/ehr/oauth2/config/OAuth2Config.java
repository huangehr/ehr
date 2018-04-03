package com.yihu.ehr.oauth2.config;

import com.yihu.ehr.oauth2.oauth2.*;
import com.yihu.ehr.oauth2.oauth2.jdbc.*;
import com.yihu.ehr.oauth2.oauth2.redis.EhrRedisTokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import javax.sql.DataSource;
import java.io.Serializable;

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
        private AuthorizationCodeServices inMemoryAuthorizationCodeServices;
        @Autowired
        private AuthorizationServerTokenServices ehrTokenServices;
        @Autowired
        private ClientDetailsService ehrJdbcClientDetailsService;
        @Autowired
        private TokenGranter ehrTokenGranter;
        /*@Autowired
        private TokenStore ehrJdbcTokenStore;*/
        @Autowired
        private TokenStore ehrRedisTokenStore;

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.authenticationManager(authenticationManager)
                    .authorizationCodeServices(inMemoryAuthorizationCodeServices)
                    .tokenServices(ehrTokenServices)
                    .tokenStore(ehrRedisTokenStore)
                    //.exceptionTranslator(new EhrOAuth2ExceptionTranslator())
                    .tokenGranter(ehrTokenGranter)
                    .setClientDetailsService(ehrJdbcClientDetailsService);
        }


        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.withClientDetails(ehrJdbcClientDetailsService);
        }

        /*@Override
        public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
            security.passwordEncoder(new StandardPasswordEncoder());
        }*/
    }

    @Bean
    @Primary
    EhrTokenGranter ehrTokenGranter(
            AuthenticationManager authenticationManager,
            EhrTokenServices tokenServices,
            AuthorizationCodeServices authorizationCodeService,
            ClientDetailsService clientDetailsService,
            EhrJdbcUserSecurityService ehrJDBCUserSecurityService,
            EhrUserDetailsService ehrUserDetailsService) {
        EhrTokenGranter tokenGranter = new EhrTokenGranter(
                authenticationManager,
                tokenServices,
                authorizationCodeService,
                clientDetailsService,
                new DefaultOAuth2RequestFactory(clientDetailsService),
                ehrJDBCUserSecurityService,
                ehrUserDetailsService);
        return tokenGranter;
    }

    @Bean
    @Primary
    RedisTemplate<String, Serializable> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, Serializable> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Serializable.class));
        redisTemplate.setHashValueSerializer(new GenericToStringSerializer<>(Long.class));
        return redisTemplate;
    }

    /**
     * jdbc client服务类
     *
     * @return
     */
    @Bean
    @Primary
    EhrJdbcClientDetailsService ehrJdbcClientDetailsService() {
        return new EhrJdbcClientDetailsService(dataSource);
    }


    /**
     * ehr jdbc store code
     *
     * @return
    @Bean
    EhrJDBCAuthorizationCodeService ehrJDBCAuthorizationCodeService() {
        return new EhrJDBCAuthorizationCodeService(dataSource);
    }
     */

    /**
     * memory store code
     * @return
     */
    @Bean
    @Primary
    InMemoryAuthorizationCodeServices inMemoryAuthorizationCodeServices() {
        return new InMemoryAuthorizationCodeServices();
    }

    /**
     * jdbc store access_token
     * @return
     */
    /*@Bean
    EhrJdbcTokenStore ehrJdbcTokenStore() {
        EhrJdbcTokenStore tokenStoreService = new EhrJdbcTokenStore(dataSource);
        return tokenStoreService;
    }*/

    /**
     * redis store access_token
     * @return
     */
    @Bean
    @Primary
    EhrRedisTokenStore ehrRedisTokenStore(JedisConnectionFactory jedisConnectionFactory) {
        return  new EhrRedisTokenStore(jedisConnectionFactory);
    }

    @Bean
    @Primary
    EhrTokenServices ehrTokenServices(
            ClientDetailsService ehrJdbcClientDetailsService,
            TokenStore ehrRedisTokenStore,
            JdbcTemplate jdbcTemplate) {
        EhrTokenServices tokenServices = new EhrTokenServices();
        tokenServices.setReuseRefreshToken(true);
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setTokenStore(ehrRedisTokenStore);
        tokenServices.setJdbcTemplate(jdbcTemplate);
        tokenServices.setClientDetailsService(ehrJdbcClientDetailsService);
        return tokenServices;
    }

    @Bean
    @Primary
    EhrJdbcAccessUrlService ehrJDBCAccessUrlService(DataSource dataSource) {
        return new EhrJdbcAccessUrlService(dataSource);
    }

    @Bean
    @Primary
    EhrJdbcUserSecurityService ehrJDBCUserSecurityService(DataSource dataSource) {
        return new EhrJdbcUserSecurityService(dataSource);
    }

    //@Configuration
    // @EnableResourceServer
    public static class ResourceServer extends ResourceServerConfigurerAdapter {

        @Autowired
        private EhrTokenServices tokenServices;

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
}