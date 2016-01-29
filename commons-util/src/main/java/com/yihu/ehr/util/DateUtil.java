package com.yihu.ehr.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {

    /** 
     * 日期比较,判断endDate - fromDate 是否超过expiresIn，是则返回true，否则返回false.
     *
     *  @param fromDate
     *  @param endDate
     *  @param expiresIn 有效期间
     *
     * @return true -- 过期  false -- 未过期
     */
    public static Boolean isExpire(Date fromDate, Date endDate, Integer expiresIn){

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        long diff = (endDate.getTime() - fromDate.getTime())/1000;

        if(diff > expiresIn){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * 日期增减,根据传入的参数，进行日期的增减.
     *
     *  @param date   需要变更的日期
     *  @param type   变更的类型 y -代码变更年份， m - 代表变更月份， d - 代表变更日期
     *  @param cos    变更的数量  正数值 增加， 负数值 减少
     *
     * @return Date
     */
    public static Date add(Date date, String type, int cos) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        if(type.equals("y"))
        {
            calendar.add(Calendar.YEAR,cos);

            return calendar.getTime();
        }
        else if(type.equals("m"))
        {
            calendar.add(Calendar.MONTH,cos);

            return calendar.getTime();
        }
        else if(type.equals("d"))
        {
            calendar.add(Calendar.DATE,cos);

            return calendar.getTime();
        }
        else
        {
           return null;
        }
    }

    /**
     * 将getTime转化回DATE类型.
     *
     * @param cos 需要变更的日期
     * @return Date
     */
    public static String add(long cos) throws ParseException {

        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date begin = dfs.parse("1970-01-01 00:00:00:00");

        Date end = new Date(cos * 1000
                + begin.getTime());
        String endStr = dfs.format(end);

        return endStr;

    }


}  
