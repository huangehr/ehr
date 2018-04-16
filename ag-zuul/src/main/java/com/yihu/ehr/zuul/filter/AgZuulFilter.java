package com.yihu.ehr.zuul.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.yihu.ehr.util.rest.Envelop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by progr1mmer on 2017/12/27
 */
@Component
public class AgZuulFilter extends ZuulFilter {

    private static final Logger logger = LoggerFactory.getLogger(AgZuulFilter.class);
    private static final String ACCESS_TOKEN_PARAMETER = "token";

    @Autowired
    private ObjectMapper objectMapper;
    //@Autowired
    //private DataSource dataSource;
    @Autowired
    private TokenStore tokenStore;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String url = request.getRequestURI();
        if (url.startsWith("/authentication/")
                || url.contains("/usersOfApp")
                || url.contains("/open/")
                || url.startsWith("/jkzl/")
                || url.contains("/users/h5/handshake")
                || url.contains("/appVersion/getAppVersion")
                || url.contains("/messageTemplate/messageOrderPush")) {
            return null;
        }
        String accessToken = this.extractToken(request);
        if (null == accessToken) {
            return this.forbidden(ctx, HttpStatus.FORBIDDEN.value(), "token can not be null");
        }
        OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(accessToken);
        if (null == oAuth2AccessToken) {
            return this.forbidden(ctx, HttpStatus.FORBIDDEN.value(), "invalid token");
        }
        if (oAuth2AccessToken.isExpired()) {
            return this.forbidden(ctx, HttpStatus.UNAUTHORIZED.value(), "expired token"); //返回401 需要重新获取 token
        }
        //OAuth2Authentication oAuth2Authentication = tokenStore.readAuthentication(accessToken);
        return null;
    }

    private String extractToken(HttpServletRequest request) {
        String accessToken = request.getHeader(ACCESS_TOKEN_PARAMETER);
        if (null == accessToken) {
            accessToken = request.getParameter(ACCESS_TOKEN_PARAMETER);
        }
        return accessToken;
    }

    private Object forbidden(RequestContext requestContext, int status, String errorMsg) {
        requestContext.setSendZuulResponse(false);
        Envelop envelop = new Envelop();
        envelop.setErrorCode(status);
        envelop.setErrorMsg(errorMsg);
        try {
            requestContext.setResponseStatusCode(status);
            requestContext.getResponse().getWriter().write(objectMapper.writeValueAsString(envelop));
        } catch (IOException e) {
            requestContext.setResponseStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            logger.error(e.getMessage());
        }
        return null;
    }

    @Bean
    @Primary
    public RedisTokenStore redisTokenStore(JedisConnectionFactory jedisConnectionFactory) {
        return new RedisTokenStore(jedisConnectionFactory);
    }

    /*@Bean
    @Primary
    public JdbcTokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }*/
}
