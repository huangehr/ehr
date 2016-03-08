package com.yihu.ehr.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.08.26 8:49
 */
public class DateFormatter {
    static final String simpleDateTimePattern = "yyyy-MM-dd HH:mm:ss";
    static final String utcDateTimePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    static final String slashDateTimePattern = "yyyy/MM/dd HH:mm:ss";
    static final String simpleDatePattern = "yyyy-MM-dd";
    static final String simpleDateTimeShortPattern = "yyyy/MM/dd HH:mm";

    private static ThreadLocal<SimpleDateFormat> simpleDateTimeShortFormat = new ThreadLocal<SimpleDateFormat>(){
        @Override
        protected synchronized SimpleDateFormat initialValue(){
            return new SimpleDateFormat(simpleDateTimeShortPattern);
        }
    };

    private static ThreadLocal<SimpleDateFormat> simpleDateTimeFormat = new ThreadLocal<SimpleDateFormat>(){
        @Override
        protected synchronized SimpleDateFormat initialValue(){
            return new SimpleDateFormat(simpleDateTimePattern);
        }
    };

    private static ThreadLocal<SimpleDateFormat> utcDateTimeFormat = new ThreadLocal<SimpleDateFormat>(){
        @Override
        protected synchronized SimpleDateFormat initialValue(){
            return new SimpleDateFormat(utcDateTimePattern);
        }
    };

    private static ThreadLocal<SimpleDateFormat> slashDateTimeFormat = new ThreadLocal<SimpleDateFormat>(){
        @Override
        protected synchronized SimpleDateFormat initialValue(){
            return new SimpleDateFormat(slashDateTimePattern);
        }
    };

    private static ThreadLocal<SimpleDateFormat> simpleDateFormat = new ThreadLocal<SimpleDateFormat>(){
        @Override
        protected synchronized SimpleDateFormat initialValue(){
            return new SimpleDateFormat(simpleDatePattern);
        }
    };

    public static String simpleDateTimeFormat(final Date date){
        String time = simpleDateTimeFormat.get().format(date);
        return time;
    }

    public static Date simpleDateTimeParse(final String date) throws ParseException {
        Date parsedDate = simpleDateTimeFormat.get().parse(date);
        return parsedDate;
    }

    public static String utcDateTimeFormat(final Date date){
        String time = utcDateTimeFormat.get().format(date);
        return time;
    }

    public static Date utcDateTimeParse(final String date) throws ParseException {
        Date parsedDate = utcDateTimeFormat.get().parse(date);
        return parsedDate;
    }

    public static String slashDateTimeFormat(final Date date){
        String result = slashDateTimeFormat.get().format(date);
        return result;
    }

    public static Date slashDateTimeParse(final String date) throws ParseException {
        Date parsedDate = slashDateTimeFormat.get().parse(date);
        return parsedDate;
    }

    public static String simpleDateFormat(final Date date){
        String result = simpleDateFormat.get().format(date);
        return result;
    }

    public static Date simpleDateParse(final String date) throws ParseException {
        Date parsedDate = simpleDateFormat.get().parse(date);
        return parsedDate;
    }

    public static String simpleDateTimeShortFormat(final Date date){
        String result = simpleDateTimeShortFormat.get().format(date);
        return result;
    }

    public static Date simpleDateTimeShortParse(final String date) throws ParseException {
        Date parsedDate = simpleDateTimeShortFormat.get().parse(date);
        return parsedDate;
    }
}
