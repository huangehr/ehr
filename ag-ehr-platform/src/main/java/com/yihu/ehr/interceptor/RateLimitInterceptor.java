package com.yihu.ehr.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Date;

/**
 * API请求频率截取器。频率限制策略：
 * - 认证过的客户端，5000次/小时
 * - 未授权的客户端，60次/小时
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.26 15:57
 */
public class RateLimitInterceptor extends HandlerInterceptorAdapter {

    private RateLimitService rateLimitService = new RateLimitService();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        return true;
    }
}

/**
 * @author Sand
 * @version 1.0
 * @created 2016.02.26 16:02
 */
class RateLimitService {

    public static int timeToNextQuarter = 15;

    private Calendar calendar = Calendar.getInstance();

    @Scheduled(cron = "0/10 * * * * *")
    public void updateExpireTimeForCache() {
        int currentMinute = calendar.get(Calendar.MINUTE);
        int mod = currentMinute % 15;
        calendar.set(Calendar.MINUTE, currentMinute - mod + 15);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        timeToNextQuarter = (int) ((calendar.getTime().getTime() - new Date().getTime())/1000);
    }

    public long incrementLimit(String userKey) {
        return cache.incr(userKey, 1, 1, timeToNextQuarter);
    }
}