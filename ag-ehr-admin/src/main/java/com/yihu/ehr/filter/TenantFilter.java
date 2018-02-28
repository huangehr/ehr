package com.yihu.ehr.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class TenantFilter implements Filter {

    private ServletContext servletContext;

    public TenantFilter(){
        super();
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        servletContext = filterConfig.getServletContext();
    }

    public void doFilter( ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {

        //设置请求头,调用app,标准的微服务需要该请求头

        WritableHttpServletRequest httpReq = new WritableHttpServletRequest((HttpServletRequest)req);
        httpReq.addHeader("Tenant","ehr");

        filterChain.doFilter(httpReq, res);

    }

    public void destroy(){
    }
}