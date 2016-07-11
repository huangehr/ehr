package com.yihu.ehr.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.mvc.EndpointHandlerMapping;
import org.springframework.boot.actuate.endpoint.mvc.MvcEndpoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

@Configuration
public class InfoEndPointConfigurer extends WebMvcConfigurerAdapter  {
    /*@Autowired
    EndpointHandlerMapping endpointHandlerMapping;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        Set<MvcEndpoint> endpoints = (Set<MvcEndpoint>) endpointHandlerMapping.getEndpoints();
        MvcEndpoint infoEndPoint = null;
        for (MvcEndpoint endpoint : endpoints){
            System.out.println(endpoint.getPath());

            if (endpoint.getPath().endsWith("/info")){
                infoEndPoint = endpoint;
            }
        }

        endpoints.remove(infoEndPoint);
        registry.addInterceptor(new InfoInterceptor()).addPathPatterns("*//**");
    }

    public static class InfoInterceptor implements HandlerInterceptor{
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                throws Exception {
            if (request.getRequestURI().endsWith("/info")){
                response.sendRedirect(request.getContextPath() + "/swagger-ui.html");

                return false;
            }

            return true;
        }

        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        }
    }*/
}
