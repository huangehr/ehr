package com.yihu.ehr.zuul;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by progr1mmer on 2017/12/27.
 */
@Component
public class AgZuulFilter extends ZuulFilter {

    private static Logger LOG = LoggerFactory.getLogger(AgZuulFilter.class);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        LOG.debug(String.format("%s >>> %s", request.getMethod(), request.getRequestURL().toString()));
        //String accessToken = request.getParameter("accessToken");
        /**
        if(accessToken == null) {
            LOG.warn("token is empty");
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            try {
                ctx.getResponse().getWriter().write("token is empty");
            }catch (Exception e){}

            return null;
        }
        LOG.info("ok");
        return null;
         */
        return null;
    }
}
