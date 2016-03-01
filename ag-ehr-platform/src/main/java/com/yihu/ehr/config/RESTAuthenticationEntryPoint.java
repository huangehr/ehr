package com.yihu.ehr.config;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.02.26 9:57
 */
@Component
public class RESTAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        if (authException instanceof InsufficientAuthenticationException) {
            response.getWriter().write("{\"message\": \"Required Authorization\"}");
        } else {
            response.getWriter().write("{\"message\": " + authException.getMessage() + "}");
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}