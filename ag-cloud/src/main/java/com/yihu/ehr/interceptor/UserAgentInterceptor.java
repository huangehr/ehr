//package com.yihu.ehr.interceptor;
//
//import com.yihu.ehr.constants.ErrorCode;
//import com.yihu.ehr.exception.ApiException;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
///**
// * User-Agent检查器.
// *
// * @author Sand
// * @version 1.0
// * @created 2016.02.26 18:04
// */
////@Component
//public class UserAgentInterceptor extends HandlerInterceptorAdapter {
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String userAgent = request.getHeader("User-Agent");
//
//        if (!StringUtils.isEmpty(userAgent)) {
//            if (userAgent.contains("Mozilla")) {
//                return true;
//            } else if (userAgent.startsWith("user ") || userAgent.startsWith("client ")) {
//                return true;
//            }
//        }
//
//        throw new ApiException(HttpStatus.FORBIDDEN,
//                ErrorCode.MissingUserAgent,
//                "https://ehr.yihu.com/docs",
//                request.getRemoteAddr());
//    }
//}
