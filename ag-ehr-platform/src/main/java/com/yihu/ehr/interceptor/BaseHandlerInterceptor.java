package com.yihu.ehr.interceptor;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Sand Wen on 2016.2.27.
 */
public class BaseHandlerInterceptor extends HandlerInterceptorAdapter {
    public void headerError(HttpServletRequest request,
                            HttpServletResponse response,
                            HttpStatus httpStatus,
                            String body) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(httpStatus.value());
        response.getWriter().print(body);
    }
}
