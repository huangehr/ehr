//package com.yihu.ehr.interceptor;
//
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.Calendar;
//import java.util.Date;
//
///**
// * Created by Sand Wen on 2016.2.27.
// */
////@Component
//public class RateLimitService {
//
//    public static int timeToNextQuarter = 15;
//
//    private Calendar calendar = Calendar.getInstance();
//
//    @Scheduled(cron = "0/10 * * * * *")
//    public void updateExpireTimeForCache() {
//        int currentMinute = calendar.get(Calendar.MINUTE);
//        int mod = currentMinute % 15;
//        calendar.set(Calendar.MINUTE, currentMinute - mod + 15);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
//
//        timeToNextQuarter = (int) ((calendar.getTime().getTime() - new Date().getTime())/1000);
//    }
//
//    static int test = 0;
//    public int incrementLimit(String ip) {
//        return ++test;
//    }
//
//    public long getResetTime(String ip){
//        return System.currentTimeMillis()/1000 + 60;
//    }
//
//    public void reset(String ip){
//
//    }
//}
