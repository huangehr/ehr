package com.yihu.ehr.interceptor;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User-Agent check.
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.26 18:04
 */
public class UserAgentInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String userAgent = request.getHeader("User-Agent");
        if(StringUtils.isEmpty(userAgent)){
            return false;
        } else if(userAgent.contains("user") || userAgent.contains("client_name")){
            String[] tokens = userAgent.split(";");

            // TODO: add user or app client id validity
        } else if (!userAgent.contains("Mozilla")){
            return false;
        }

        return true;
    }
}
