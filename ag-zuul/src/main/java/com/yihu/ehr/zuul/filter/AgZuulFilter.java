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
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Set;

/**
 * Created by progr1mmer on 2017/12/27
 */
@Component
public class AgZuulFilter extends ZuulFilter {

    private static final Logger logger = LoggerFactory.getLogger(AgZuulFilter.class);
    private static final String ACCESS_TOKEN_PARAMETER = "token";

    @Autowired
    private ObjectMapper objectMapper;
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
        //内部微服务有不需要认证的地址请在URL上追加/open/来进行过滤，如/api/v1.0/open/**，不要在此继续追加！！！
        if (url.contains("/authentication/")
                || url.contains("/file/")
                || url.contains("/open/")
                || url.contains("/jkzl/")
                || url.contains("/fzGateway/")
                || url.contains("/usersOfApp")
                || url.contains("/users/h5/handshake")
                || url.contains("/appVersion/getAppVersion")
                || url.contains("/messageTemplate/messageOrderPush")
                || url.contains("/account/")) {
            return true;
        }
        return this.authenticate(ctx, request, url);
    }

    private Object authenticate(RequestContext ctx, HttpServletRequest request, String path) {
        String accessToken = this.extractToken(request);
        if (null == accessToken) {
            return this.forbidden(ctx, HttpStatus.FORBIDDEN.value(), "token can not be null");
        }
        OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(accessToken);
        if (null == oAuth2AccessToken) {
            return this.forbidden(ctx, HttpStatus.FORBIDDEN.value(), "invalid token");
        }
        if (oAuth2AccessToken.isExpired()) {
            return this.forbidden(ctx, HttpStatus.PAYMENT_REQUIRED.value(), "expired token"); //返回402 登陆过期
        }
        //将token的认证信息附加到请求中，转发给下游微服务
        /*OAuth2Authentication auth = tokenStore.readAuthentication(accessToken);
        ctx.addZuulRequestHeader("x-auth-name", auth.getName());*/
        //以下代码取消注释可开启Oauth2应用资源授权验证
        /*Set<String> resourceIds = auth.getOAuth2Request().getResourceIds();
        for (String resourceId : resourceIds) {
            if (resourceId.equals("*")) {
                return true;
            }
            if (!resourceId.startsWith("/")) {
                resourceId = "/" + resourceId;
            }
            path = path.toLowerCase();
            if (path.startsWith(resourceId)
                    && (path.length() == resourceId.length() || path.charAt(resourceId.length()) == '/')) {
                return true;
            }
        }
        return this.forbidden(ctx, HttpStatus.FORBIDDEN.value(), "invalid token does not contain request resource " + path);*/
        return true;
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
        return false;
    }

    @Bean
    @Primary
    public RedisTokenStore redisTokenStore(JedisConnectionFactory jedisConnectionFactory) {
        return new RedisTokenStore(jedisConnectionFactory);
    }

}
