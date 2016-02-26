package com.yihu.ehr.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.config.ApiHttpHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * API请求频率截取器。频率限制策略：
 * - 认证过的客户端，5000次/小时
 * - 未授权的客户端，60次/小时
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.26 15:57
 */
@Component
public class RateLimitInterceptor extends BaseHandlerInterceptor {
    private static final int AuthorizedRateLimit = 5000;
    private static final int UnauthorizedRateLimit = 60;

    @Autowired
    private RateLimitService rateLimitService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean authorized = isAuthorized(getAccessToken(request));

        int count = rateLimitService.incrementLimit(request.getRemoteAddr());
        int limit = authorized ? AuthorizedRateLimit : UnauthorizedRateLimit;
        boolean overhead = authorized ? count > AuthorizedRateLimit : count > UnauthorizedRateLimit;

        response.setIntHeader(ApiHttpHeader.RATE_LIMIT_LIMIT, limit);
        response.setHeader(ApiHttpHeader.RATE_LIMIT_RESET, Long.toString(rateLimitService.getResetTime(request.getRemoteAddr())));
        response.setIntHeader(ApiHttpHeader.RATE_LIMIT_REMAINING, overhead ? 0 : limit - count);
        if (overhead) {
            headerError(request, response, HttpStatus.NOT_FOUND, error(request.getRemoteAddr()));

            return false;
        }

        return true;
    }

    private boolean isAuthorized(String token){
        if (StringUtils.isEmpty(token)) return false;

        return false;
    }

    private String getAccessToken(HttpServletRequest request){
        String token = request.getHeader(ApiHttpHeader.AUTHORIZATION);
        if (StringUtils.isEmpty(token)){
            token = request.getParameter("api_key");
        }

        return token;
    }

    private static String error(String ip) throws JsonProcessingException {
        Map<String, String> map = new HashMap<>();
        map.put("message", ip + "的API请求超过限制(好消息：认证提限优惠大酬宾，活动参见链接)");
        map.put("documentation_url", "https://ehr.yihu.com/docs/api/v1/#rate-limiting");

        return new ObjectMapper().writeValueAsString(map);
    }
}
