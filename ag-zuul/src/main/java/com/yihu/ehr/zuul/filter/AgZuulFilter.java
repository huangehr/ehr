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
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by progr1mmer on 2017/12/27.
 */
@Component
public class AgZuulFilter extends ZuulFilter {

    private static final Logger logger = LoggerFactory.getLogger(AgZuulFilter.class);
    private static final String ACCESS_TOKEN_PARAMETER = "accessToken";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private DefaultTokenServices defaultTokenServices;

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
        if (url.startsWith("/authentication/")) {
            return null;
        }
        String accessToken = this.extractToken(request);
        if (null == accessToken) {
            return this.forbidden(ctx, HttpStatus.FORBIDDEN.value(), "accessToken can not be null");
        }
        OAuth2AccessToken oAuth2AccessToken = defaultTokenServices.readAccessToken(accessToken);
        if (null == oAuth2AccessToken) {
            return this.forbidden(ctx, HttpStatus.FORBIDDEN.value(), "invalid accessToken");
        }
        if (oAuth2AccessToken.isExpired()) {
            return this.forbidden(ctx, HttpStatus.FORBIDDEN.value(), "expired accessToken");
        }
        OAuth2Authentication oAuth2Authentication = defaultTokenServices.loadAuthentication(accessToken);
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
    public JdbcTokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenService() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(tokenStore());
        //tokenServices.setClientDetailsService(clientDetails());
        //tokenServices.setTokenEnhancer(endpoints.getTokenEnhancer());
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setAccessTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30)); // 30天
        return tokenServices;
    }
}
