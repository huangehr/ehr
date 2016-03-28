package com.yihu.ehr.Filter;

import com.yihu.ehr.constants.SessionAttributeKeys;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/3/26
 */
@Component("loginFilter")
public class SessionOutTimeFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String path = httpServletRequest.getRequestURI();
        if (path.indexOf("/login") != -1
                || path.indexOf(httpServletRequest.getContextPath() + "/static-dev") != -1
                || path.indexOf(httpServletRequest.getContextPath() + "/develop") != -1
                || path.indexOf(httpServletRequest.getContextPath() + "/rest") != -1
                || path.indexOf("swagger") != -1
                || path.indexOf(httpServletRequest.getContextPath() + "/v2/api-docs") != -1
                || path.indexOf(httpServletRequest.getContextPath() + "/browser") != -1
                || path.indexOf(httpServletRequest.getContextPath() + "/mobile") != -1) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        if (httpServletRequest.getSession(false) == null) {
            httpServletResponse.getWriter().print("<script>top.location.href='"
                    + httpServletRequest.getContextPath() + "/login'</script>");
            return;
        }

        Object obj = httpServletRequest.getSession().getAttribute(SessionAttributeKeys.CurrentUser);
        if (obj == null) {
            httpServletResponse.getWriter().print("<script>top.location.href='"
                    + httpServletRequest.getContextPath() + "/login'</script>");
            return;
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}
