package com.yihu.ehr.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.lang.SpringContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * User-Agent检查器.
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.26 18:04
 */
@Component
public class UserAgentInterceptor extends BaseHandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userAgent = request.getHeader("User-Agent");

        if (!StringUtils.isEmpty(userAgent)) {
            if (userAgent.contains("Mozilla")) {
                return true;
            } else if (userAgent.startsWith("user") || userAgent.startsWith("client")) {
                return true;
            }
        }

        headerError(request, response, HttpStatus.NOT_FOUND, error());

        return false;
    }

    private static String error() throws JsonProcessingException {
        Map<String, String> map = new HashMap<>();
        map.put("message", "Request forbidden by administrative rules. Please make sure your request has a User-Agent header.");
        map.put("documentation_url", "https://ehr.yihu.com/api");

        return SpringContext.getService(ObjectMapper.class).writeValueAsString(map);
    }
}
