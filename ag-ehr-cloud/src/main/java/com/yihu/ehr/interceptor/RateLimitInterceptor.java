package com.yihu.ehr.interceptor;

import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.HttpHeader;
import com.yihu.ehr.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
public class RateLimitInterceptor extends HandlerInterceptorAdapter {
    private static final int AuthorizedRateLimit = 5000;
    private static final int UnauthorizedRateLimit = 60;

    @Autowired
    private RateLimitService rateLimitService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean authorized = isAuthorized(getAccessToken(request));

        int count = rateLimitService.incrementLimit(request.getRemoteAddr());
        int limit = authorized ? AuthorizedRateLimit : UnauthorizedRateLimit;
        boolean exceeding = authorized ? count > AuthorizedRateLimit : count > UnauthorizedRateLimit;

        response.setIntHeader(HttpHeader.RATE_LIMIT_LIMIT, limit);
        response.setHeader(HttpHeader.RATE_LIMIT_RESET, Long.toString(rateLimitService.getResetTime(request.getRemoteAddr())));
        response.setIntHeader(HttpHeader.RATE_LIMIT_REMAINING, exceeding ? 0 : limit - count);
        if (exceeding) {
            throw new ApiException(HttpStatus.FORBIDDEN,
                    ErrorCode.RateLimitExceeding,
                    "https://ehr.yihu.com/docs/api/v1/#rate-limiting",
                    request.getRemoteAddr());
        }

        return true;
    }

    private boolean isAuthorized(String token){
        if (StringUtils.isEmpty(token)) return false;

        // TODO 检查是否已授权

        return false;
    }

    /**
     * 兼容OAuth Token与Swagger api key。
     *
     * @param request
     * @return
     */
    private String getAccessToken(HttpServletRequest request){
        String token = request.getHeader(HttpHeader.AUTHORIZATION);
        if (StringUtils.isEmpty(token)){
            token = request.getParameter("access_token");
        }

        if (StringUtils.isEmpty(token)){
            token = request.getParameter("api_key");
        }

        return token;
    }
}
