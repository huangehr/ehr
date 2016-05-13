/****************************************************************************
 * Copyright(c) Yamaha Motor Solutions CO.,Ltd. 2010 All Rights Reserved
 * <p>
 * System Name�?smart)Human Resource Management System
 * SubSystem Name�?
 * service for all substystems
 * <p>
 * File Name: DateUtil
 * <p>
 * HISTORY RECORD
 * Ver.   Date           Create User/Update     Comment
 * -------------------------------------------------------------------------
 * 1.0 �? 2010/07/12 �? tuchengye              New Making
 ***************************************************************************/
package com.yihu.ehr.util.operator;

import org.apache.commons.lang3.StringUtils;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {

    public static final String DEFAULT_DATE_YEAR_FORMAT = "yyyy";

    public static final String DEFAULT_DATE_MONTH_FORMAT = "MM";

    public static final String PRINT_DATE_YM_FORMAT = "MMM., yyyy";
    public static final String PRINT_DATE_YMD_FORMAT = "MMM. d, yyyy";

    public static final Locale PRINT_LOCALE = Locale.ENGLISH;

    public static final String DEFAULT_YEARS = "0.0";
    public static final String DEFAULT_DATE_YMD_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATE_YM_FORMAT = "yyyyMM";
    public static final String DEFAULT_DATE_MD_FORMAT = "MMdd";
    public static final String DEFAULT_CHAR_DATE_YMD_FORMAT = "yyyyMMdd";
    public static final String DEFAULT_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public final static String DEFAULT_SIMPLEDATE_FORMAT = "yyyy-MM-dd HH:mm:ss SSS";
    public final static String DEFAULT_YMDHMSDATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    public static final String DEFAULT_NOW_STRING_FORMAT = "yyyyMMddHHmmssSSS";
    public static final String DATE_MDY_FORMAT = "MMddyyyy";
    public static final String DATE_MY_FORMAT = "MMyyyy";

    public static String changeFormat(String changeDate, String beforeFormat, String afterFormat) {

        if (StringUtils.isBlank(changeDate)) {
            return changeDate;
        }

        return formatDate(parseDate(changeDate, beforeFormat), afterFormat);
    }

    public static String changeToYMDFormatForPrint(String changeDate) {

        if (StringUtils.isBlank(changeDate)) {
            return "";
        }
        DateFormat df = new SimpleDateFormat(PRINT_DATE_YMD_FORMAT, Locale.ENGLISH);

        return df.format(parseDate(changeDate, DEFAULT_CHAR_DATE_YMD_FORMAT));
    }

    public static String changeToYMFormatForPrint(String changeDate) {

        if (StringUtils.isBlank(changeDate)) {
            return "";
        }
        DateFormat df = new SimpleDateFormat(PRINT_DATE_YM_FORMAT, Locale.ENGLISH);

        return df.format(parseDate(changeDate, DEFAULT_DATE_YM_FORMAT));
    }

    public static String getFirstDate(String yearMonthStr, String yearMonthFormat, String dateFormat) throws Exception {

        DateFormat dfYearMonth = new SimpleDateFormat(yearMonthFormat);
        DateFormat dfDate = new SimpleDateFormat(dateFormat);
        java.util.Date date;

        if (yearMonthStr == null || yearMonthStr.equals("")) {
            throw new Exception(yearMonthStr + " is invalid.");
        }

        try {
            date = dfYearMonth.parse(yearMonthStr);
        } catch (ParseException e) {
            throw new Exception(yearMonthStr + " is invalid.");
        }

        return dfDate.format(date);
    }

    public static String getLastDate(String dateSource, String dateSourceFormat, String dateFormat) throws Exception {

        DateFormat dsf = new SimpleDateFormat(dateSourceFormat);
        DateFormat df = new SimpleDateFormat(dateFormat);
        java.util.Date date;

        String resultDateString;

        if (dateSource == null || dateSource.equals("")) {
            throw new Exception(dateSource + " is invalid.");
        }

        try {
            date = dsf.parse(dateSource);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            resultDateString = df.format(cal.getTime());

        } catch (ParseException e) {
            throw new Exception(dateSource + " is invalid.");
        }

        return resultDateString;
    }

    public static String getYearFromDBDate(String dateStr) {

        if (dateStr == null || dateStr.length() != DEFAULT_CHAR_DATE_YMD_FORMAT.length()) {
            return null;
        }

        return changeFormat(dateStr, DEFAULT_CHAR_DATE_YMD_FORMAT, DEFAULT_DATE_YEAR_FORMAT);
    }

    public static String getMonthFromDBDate(String dateStr) {

        if (dateStr == null || dateStr.length() != DEFAULT_CHAR_DATE_YMD_FORMAT.length()) {
            return null;
        }

        return changeFormat(dateStr, DEFAULT_CHAR_DATE_YMD_FORMAT, DEFAULT_DATE_MONTH_FORMAT);
    }

    public static String getYMFromDBDate(String dateStr) {

        if (dateStr == null || dateStr.length() != DEFAULT_CHAR_DATE_YMD_FORMAT.length()) {
            return null;
        }

        return changeFormat(dateStr, DEFAULT_CHAR_DATE_YMD_FORMAT, DEFAULT_DATE_YM_FORMAT);
    }

    public static int getDifferenceOfDays(java.util.Date dateFrom, java.util.Date dateTo) {

        return new Long((dateTo.getTime() - dateFrom.getTime()) / 1000 / 60 / 60 / 24).intValue();
    }

    public static int getDifferenceOfDays(String dateFromStr, String dateToStr, String dateFormat) {

        java.util.Date dateFrom = parseDate(dateFromStr, dateFormat);
        java.util.Date dateTo = parseDate(dateToStr, dateFormat);

        return getDifferenceOfDays(dateFrom, dateTo);
    }

    public static int getDifferenceOfDays(String dateFromStr, String dateToStr) {

        return getDifferenceOfDays(dateFromStr, dateToStr, DEFAULT_CHAR_DATE_YMD_FORMAT);
    }

    public static String formatTime(String timeStr) {

        if (timeStr == null || timeStr.length() != 6) {
            return null;
        }

        return StringUtils.substring(timeStr, 0, 2)
                + ":" + StringUtils.substring(timeStr, 2, 4)
                + ":" + StringUtils.substring(timeStr, 4);
    }

    public static String toString(Date date) {
        return toString((java.util.Date) date);
    }

    public static String toString(java.util.Date date) {
        return toString(date, DEFAULT_DATE_YMD_FORMAT);
    }

    public static String toString(Date date, String format) {
        return toString((java.util.Date) date, format);
    }

    public static String toString(java.util.Date date, String format) {

        if (date == null) {
            return null;
        }

        if (format == null) {
            throw new IllegalArgumentException("The value of an argument is inaccurate.");
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);

        return sdf.format(date);
    }

    public static String formatTimestamp(Timestamp time) {
        return formatTimestamp(time, DEFAULT_TIMESTAMP_FORMAT);
    }

    public static String formatTimestamp(Timestamp time, String format) {

        if (time == null) {
            return null;
        }

        if (format == null) {
            throw new IllegalArgumentException("The value of an argument is inaccurate.");
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(time);
    }

    public static String toString(Time time, String format) {

        if (time == null) {
            return null;
        }

        if (format == null) {
            throw new IllegalArgumentException("The value of an argument is inaccurate.");
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);

        return sdf.format(time);
    }

    public static Date formatCharDateYMD(String str) {
        return formatCharDateYMD(str, DEFAULT_DATE_YMD_FORMAT);
    }

    public static Date formatCharDateYMD(String str, String format) {

        if (str == null || str.trim().length() == 0) {
            return null;
        }

        if (format == null) {
            throw new IllegalArgumentException("The value of an argument is inaccurate.");
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);

        ParsePosition pos = new ParsePosition(0);

        java.util.Date date = sdf.parse(str, pos);

        if (date == null) {
            return null;
        }
        return new Date(date.getTime());
    }

    public static Date formatCharDateYMD(String yy, String mm, String dd) {

        if (yy == null || mm == null || dd == null || yy.trim().length() == 0 ||
                mm.trim().length() == 0 || dd.trim().length() == 0) {
            return null;
        }
        return formatCharDateYMD(yy + "-" + (mm != null && mm.length() == 1 ? "0" + mm : mm) + "-" +
                (dd != null && dd.length() == 1 ? "0" + dd : dd));
    }

    public static Timestamp toTimestamp(String str) {

        if (str == null) {
            return null;
        }

        try {
            return Timestamp.valueOf(str.trim());
        } catch (IllegalArgumentException iae) {
            return null;
        }

    }


    public static Timestamp toTimestamp(String str, String format) {

        if (str == null) {
            return null;
        }

        try {
            return new Timestamp(parseDate(str, format).getTime());
        } catch (Exception e) {
            return null;
        }

    }

    public static Time toTime(String str) {

        if (str == null) {
            return null;
        }

        try {
            return (str.length() == 5 ? Time.valueOf(str + ":00") : Time.valueOf(str));
        } catch (Exception e) {
            return null;
        }

    }

    public static String toString(Time time) {
        return toString(time, DEFAULT_TIME_FORMAT);
    }

    public static String toYM(String yy, String mm) {

        if (yy == null || mm == null) {
            return null;
        }

        if (yy.trim().length() == 0 && mm.trim().length() != 0 ||
                yy.trim().length() != 0 && mm.trim().length() == 0) {
            return null;
        }
        return yy + (mm != null && mm.length() == 1 ? "0" + mm : mm);
    }

    public static String getNowDate() {
        return getNowDate(DEFAULT_NOW_STRING_FORMAT);
    }

    public static String getNowDate(String format) {

        if (format == null) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.JAPAN);

        java.util.Date date = Calendar.getInstance().getTime();

        if (date == null) {
            return null;
        }
        return sdf.format(date);
    }

    public static Date getSysDate() {
        return new Date(Calendar.getInstance().getTime().getTime());
    }

    public static java.util.Date getSysDateYMDHMS() {

        Date dSysDateYMD = DateUtil.getSysDate();
        Timestamp ts = formatYMDToYMDHMS(dSysDateYMD.toString());

        return ts;
    }

    public static Timestamp getSysDateTime() {
        return new Timestamp(Calendar.getInstance().getTime().getTime());
    }

    public static Time getSysTime() {
        return new Time(Calendar.getInstance().getTime().getTime());
    }

    public static String toAge(String birthDay) {

        if (birthDay == null || birthDay.length() != 8) {
            return null;
        }

        int birthYear = Integer.parseInt(birthDay.substring(0, 4));
        int birthMonth = Integer.parseInt(birthDay.substring(4, 6));
        int birthDayOfMonth = Integer.parseInt(birthDay.substring(6, 8));
        return toAge(birthYear, birthMonth, birthDayOfMonth);
    }

    public static String toAge(int birthYear, int birthMonth, int birthDayOfMonth) {

        Calendar cl = Calendar.getInstance();
        int year = cl.get(Calendar.YEAR);
        int month = cl.get(Calendar.MONTH) + 1;
        int day = cl.get(Calendar.DAY_OF_MONTH);

        int sa = 0;

        if (month > birthMonth) {
            sa = 0;
        } else if (month == birthMonth && day >= birthDayOfMonth) {
            sa = 0;
        } else {
            sa = 1;
        }

        int age = year - birthYear - sa;

        return Integer.toString(age);
    }

    public static Date addDate(int add, java.util.Date d) {
        return addDate(add, new Date(d.getTime()));
    }

    public static Date addDate(int add, Date d) {

        if (d == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime((java.util.Date) d);
        cal.setTimeZone(TimeZone.getDefault());
        cal.add(Calendar.DAY_OF_MONTH, add);

        return new Date(cal.getTime().getTime());
    }

    public static String addDate(int add, String sDate) {

        if (sDate.length() < 8) {
            return null;
        }

        return formatDate(addDate(add, formatCharDateYMD(sDate, DEFAULT_CHAR_DATE_YMD_FORMAT)), DEFAULT_CHAR_DATE_YMD_FORMAT);
    }

    public static Date addMonth(int add, java.util.Date d) {
        return addMonth(add, new Date(d.getTime()));
    }

    public static Date addMonth(int add, Date d) {

        if (d == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime((java.util.Date) d);
        cal.setTimeZone(TimeZone.getDefault());
        cal.add(Calendar.MONTH, add);

        return new Date(cal.getTime().getTime());
    }

    public static String addMonth(int add, String sDate) {

        if (sDate.length() < 8) {
            return null;
        }

        return formatDate(addMonth(add, formatCharDateYMD(sDate, DEFAULT_CHAR_DATE_YMD_FORMAT)), DEFAULT_CHAR_DATE_YMD_FORMAT);
    }

    public static Date addYear(int add, java.util.Date d) {
        return addYear(add, new Date(d.getTime()));
    }

    public static Date addYear(int add, Date d) {

        if (d == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime((java.util.Date) d);
        cal.setTimeZone(TimeZone.getDefault());
        cal.add(Calendar.YEAR, add);

        return new Date(cal.getTime().getTime());

    }

    public static String addYear(int add, String sDate) {

        if (sDate.length() < 8) {
            return null;
        }

        return formatDate(addYear(add, formatCharDateYMD(sDate, DEFAULT_CHAR_DATE_YMD_FORMAT)), DEFAULT_CHAR_DATE_YMD_FORMAT);
    }

    public static String getNowDateTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.JAPAN);

        df.setTimeZone(TimeZone.getDefault());

        return df.format(new java.util.Date());
    }

    public static String getCurrentString() {
        return getCurrentString(DEFAULT_SIMPLEDATE_FORMAT);
    }

    public static String getCurrentString(String pattern) {
        SimpleDateFormat f = new SimpleDateFormat(pattern);
        return f.format(Calendar.getInstance(TimeZone.getDefault()).getTime());
    }

    public static long compareDate(String pattern, String s1, String s2) {

        SimpleDateFormat f = new SimpleDateFormat(pattern);

        try {
            return f.parse(s1).getTime() - f.parse(s2).getTime();
        } catch (Exception e) {
            return -1;
        }

    }

    public static long compareDate(java.util.Date s1, java.util.Date s2) {
        try {
            return compareDate(DEFAULT_DATE_YMD_FORMAT, toString(s1), toString(s2));
        } catch (Exception e) {
            return -1;
        }

    }

    public static long compareDateTime(java.util.Date s1, java.util.Date s2) {
        return s1.getTime() - s2.getTime();
    }

    public static java.util.Date parseDate(String value, String pattern) {
        try {
            TimeZone tz = TimeZone.getDefault();
            String dateFormat = pattern;
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            sdf.setTimeZone(tz);

            // Parse date
            java.util.Date parsed = null;

            parsed = sdf.parse(value);
            return parsed;
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatDate(java.util.Date value, String pattern) {
        TimeZone tz = TimeZone.getDefault();
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(tz);

        return sdf.format(value);
    }

    public static int getLastDay(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        return lastDay;
    }

    public static String getYMDFormat(String datePtn) {
        final String[][] DATE_FORMAT_YMD_LIST = {{"1", "yyyy/MM/dd"},
                {"2", "yyyy.MM.dd"},
                {"3", "yyyy-MM-dd"},
                {"4", "MM/dd/yyyy"},
                {"5", "MM.dd.yyyy"},
                {"6", "MM-dd-yyyy"},
                {"7", "dd/MM/yyyy"},
                {"8", "dd.MM.yyyy"},
                {"9", "dd-MM-yyyy"},
                {"A", "dd/MM yyyy"}
        };
        String format = null;

        for (int i = 0; i < DATE_FORMAT_YMD_LIST.length; i++) {
            if (DATE_FORMAT_YMD_LIST[i][0].equals(datePtn)) {
                format = DATE_FORMAT_YMD_LIST[i][1];
                break;
            }
        }

        if (format == null) {
            throw new IllegalArgumentException("The value of an argument is inaccurate.");
        }
        return format;
    }

    public static String getYMFormat(String datePtn) {
        final String[][] DATE_FORMAT_YM_LIST = {{"1", "yyyy/MM"},
                {"2", "yyyy.MM"},
                {"3", "yyyy-MM"},
                {"4", "MM/yyyy"},
                {"5", "MM.yyyy"},
                {"6", "MM-yyyy"},
                {"7", "MM/yyyy"},
                {"8", "MM.yyyy"},
                {"9", "MM-yyyy"},
                {"A", "MM yyyy"},
                {"B", "yyyyMM"}
        };
        String format = null;

        for (int i = 0; i < DATE_FORMAT_YM_LIST.length; i++) {
            if (DATE_FORMAT_YM_LIST[i][0].equals(datePtn)) {
                format = DATE_FORMAT_YM_LIST[i][1];
                break;
            }
        }

        if (format == null) {
            throw new IllegalArgumentException("The value of an argument is inaccurate.");
        }
        return format;
    }

    public static String toYMD(Date date, String datePtn) {

        if (date == null) {
            return null;
        }
        return toString(date, getYMDFormat(datePtn));
    }

    public static String formatDateYMD(String str, String datePtn) {

        if (str == null || str.trim().length() == 0) {
            return null;
        }
        return toYMD(formatCharDateYMD(str), datePtn);
    }

    public static String formatDateYMD(String yy, String mm, String dd, String datePtn) {

        if (yy == null || mm == null || dd == null || yy.trim().length() == 0
                || mm.trim().length() == 0 || dd.trim().length() == 0) {
            return null;
        }
        return formatDateYMD(yy + "-" + (mm != null && mm.length() == 1 ? "0" + mm : mm)
                + "-" + (dd != null && dd.length() == 1 ? "0" + dd : dd), datePtn);
    }

    public static String formatDateYM(Date date, String datePtn) {

        if (date == null) {
            return null;
        }
        return toString(date, getYMFormat(datePtn));
    }

    public static String formatDateYM(String str, String datePtn) {

        if (str == null || str.trim().length() == 0) {
            return null;
        }
        return formatDateYM(formatCharDateYMD(str, DEFAULT_DATE_YM_FORMAT), datePtn);
    }

    public static String formatDateYM(String yy, String mm, String datePtn) {

        if (yy == null || mm == null || yy.trim().length() == 0 || mm.trim().length() == 0) {
            return null;
        }
        return formatDateYM(yy + (mm != null && mm.length() == 1 ? "0" + mm : mm), datePtn);
    }

    public static String getTimestampFormat(String dateStyleId) {
        return getYMDFormat(dateStyleId) + " HH:mm:ss";
    }


    public static Date toDateFromTime(String time) {

        try {
            return toDateFromTime(Long.parseLong(time));
        } catch (Exception iae) {
            return null;
        }
    }

    public static Date toDateFromTime(long time) {
        return new Date(time);
    }

    public static Timestamp toTimestampFromTime(String time) {

        try {
            return toTimestampFromTime(Long.parseLong(time));
        } catch (Exception iae) {
            return null;
        }
    }

    public static Timestamp toTimestampFromTime(long time) {
        return new Timestamp(time);
    }

    public static Timestamp toTimestampFromGMT(int yy, int mm, int dd, int hh, int mi, int ss) {
        return toTimestampFromGMT(
                String.valueOf(yy),
                String.valueOf(mm),
                String.valueOf(dd),
                String.valueOf(hh),
                String.valueOf(mi),
                String.valueOf(ss));
    }

    public static Timestamp toTimestampFromGMT(String yy, String mm, String dd,
                                               String hh, String mi, String ss) {

        mm = mm != null && mm.length() == 1 ? "0" + mm : mm;
        dd = dd != null && dd.length() == 1 ? "0" + dd : dd;
        hh = hh != null && hh.length() == 1 ? "0" + hh : hh;
        mi = mi != null && mi.length() == 1 ? "0" + mi : mi;
        ss = ss != null && ss.length() == 1 ? "0" + ss : ss;

        return toTimestampFromGMT(yy + "-" + mm + "-" + dd + " " + hh + ":" + mi + ":" + ss);
    }

    public static Timestamp toTimestampFromGMT(String str) {

        if (str == null) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_TIMESTAMP_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        ParsePosition pos = new ParsePosition(0);
        java.util.Date date = sdf.parse(str, pos);
        if (date == null) {
            return null;
        }

        return new Timestamp(date.getTime());
    }

    public static Timestamp toTimestampFromGMT(Timestamp time) {
        return toTimestampFromGMT(time.toString());
    }

    public static Timestamp toTimestampFromLocal(String yy, String mm, String dd, String hh,
                                                 String mi, String ss, String differTimeSign,
                                                 String differenceTime, String summerTimeFrom,
                                                 String summerTimeTo, String summerTime) {

        mm = mm != null && mm.length() == 1 ? "0" + mm : mm;
        dd = dd != null && dd.length() == 1 ? "0" + dd : dd;
        hh = hh != null && hh.length() == 1 ? "0" + hh : hh;
        mi = mi != null && mi.length() == 1 ? "0" + mi : mi;
        ss = ss != null && ss.length() == 1 ? "0" + ss : ss;

        return toTimestampFromLocal(
                yy + "-" + mm + "-" + dd + " " + hh + ":" + mi + ":" + ss,
                differTimeSign,
                differenceTime,
                summerTimeFrom,
                summerTimeTo,
                summerTime);
    }

    public static Timestamp toTimestampFromLocal(String str, String differTimeSign,
                                                 String differenceTime, String summerTimeFrom,
                                                 String summerTimeTo, String summerTime) {

        if (str == null) {
            return null;
        }

        Timestamp time = toTimestamp(str);

        if (time == null) {
            return null;
        }

        long localTime = toGMTTimeFromLocalTime(
                time.getTime(),
                differTimeSign,
                differenceTime,
                summerTimeFrom,
                summerTimeTo,
                summerTime);

        return toTimestampFromGMT(new Timestamp(localTime));
    }

    public static Timestamp toTimestampFromLocal(Timestamp time, String differTimeSign,
                                                 String differenceTime, String summerTimeFrom,
                                                 String summerTimeTo, String summerTime) {

        return toTimestampFromLocal(
                time.toString(),
                differTimeSign,
                differenceTime,
                summerTimeFrom,
                summerTimeTo,
                summerTime);
    }

    public static long toGMTTime(long local) {

        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_TIMESTAMP_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        ParsePosition pos = new ParsePosition(0);
        java.util.Date date = sdf.parse(new Timestamp(local).toString(), pos);

        if (date == null) {
            return -1;
        }

        return date.getTime();
    }

    public static long toGMTTime(Timestamp local) {

        if (local == null) {
            return -1;
        }

        return toGMTTime(local.getTime());
    }

    public static Timestamp toGMTTimestamp(long local) {

        long time = toGMTTime(local);

        if (time == -1) {
            return null;
        }

        return new Timestamp(time);
    }

    public static Timestamp toGMTTimestamp(Timestamp local) {

        if (local == null) {
            return null;
        }

        return toGMTTimestamp(local.getTime());
    }

    public static String toYMD(String yy, String mm, String dd) {

        if (yy == null || mm == null || dd == null) {
            return null;
        }

        if (yy.trim().length() == 0 || mm.trim().length() == 0) {
            return "";
        }

        mm = mm != null && mm.length() == 1 ? "0" + mm : mm;
        if (dd != null && dd.length() == 0) {
            dd = "  ";
        }
        if (dd != null && dd.length() == 1) {
            dd = "0" + dd;
        }

        return yy + mm + dd;
    }

    public static String getYearFromYM(String ym) {

        if (ym == null || ym.length() != DEFAULT_DATE_YM_FORMAT.length()) {
            return null;
        }

        return ym.substring(0, 4);
    }

    public static String getMonthFromYM(String ym) {

        if (ym == null || ym.length() != DEFAULT_DATE_YM_FORMAT.length()) {
            return null;
        }

        return ym.substring(4, 6);
    }

    public static String getYearFromYMD(Date ymd) {

        return getYearFromYMD(toString(ymd));
    }

    public static String getMonthFromYMD(Date ymd) {

        return getMonthFromYMD(toString(ymd));
    }

    public static String getDateFromYMD(Date ymd) {

        return getDateFromYMD(toString(ymd));
    }

    public static String getYearFromYMD(String ymd) {

        if (ymd == null || ymd.length() != DEFAULT_DATE_YMD_FORMAT.length()) {
            return null;
        }

        return ymd.substring(0, 4);
    }

    public static String getMonthFromYMD(String ymd) {

        if (ymd == null || ymd.length() != DEFAULT_DATE_YMD_FORMAT.length()) {
            return null;
        }

        return ymd.substring(5, 7);
    }

    public static String getDateFromYMD(String ymd) {

        if (ymd == null || ymd.length() != DEFAULT_DATE_YMD_FORMAT.length()) {
            return null;
        }

        return ymd.substring(8, 10);
    }

    public static String getHourFromHMS(String hms) {

        if (hms == null || hms.length() != DEFAULT_TIME_FORMAT.length()) {
            return null;
        }

        return hms.substring(0, 2);
    }

    public static String getMinuteFromHMS(String hms) {

        if (hms == null || hms.length() != DEFAULT_TIME_FORMAT.length()) {
            return null;
        }

        return hms.substring(3, 5);
    }

    public static String getSecondFromHMS(String hms) {

        if (hms == null || hms.length() != DEFAULT_TIME_FORMAT.length()) {
            return null;
        }

        return hms.substring(6, 8);
    }

    public static String formatDateYMD(Date date, String dateStyleId) {

        if (date == null) {
            return null;
        }

        return toString(date, getYMDFormat(dateStyleId));
    }

    public static long getSystemTime() {
        return Calendar.getInstance().getTime().getTime();
    }

    public static Timestamp getSystemTimestamp() {
        return new Timestamp(getSystemTime());
    }

    public static long getSystemTimeGMTToday() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        Timestamp today = toTimestamp(sdf.format(Calendar.getInstance().getTime()) + " 00:00:00");

        return toGMTTime(today);
    }

    public static Timestamp getSystemTimestampGMTToday() {

        long time = getSystemTimeGMTToday();

        if (time == -1) {
            return null;
        }

        return new Timestamp(time);
    }

    public static Date getSysDateGMTToday() {

        long time = getSystemTimeGMTToday();

        if (time == -1) {
            return null;
        }

        return new Date(getSystemTimeGMTToday());
    }

    public static String formatTimestamp(Timestamp time, String differTimeSign,
                                         String differenceTime, String summerTimeFrom,
                                         String summerTimeTo, String summerTime) {

        if (time == null) {
            return null;
        }

        return toStringFormatLocalTime(
                time.getTime(),
                differTimeSign,
                differenceTime,
                summerTimeFrom,
                summerTimeTo,
                summerTime,
                DEFAULT_TIMESTAMP_FORMAT);
    }

    public static String formatTimestamp(Timestamp time, String differTimeSign,
                                         String differenceTime, String summerTimeFrom,
                                         String summerTimeTo, String summerTime, String dateStyleId) {

        if (time == null) {
            return null;
        }

        return toStringFormatLocalTime(
                time.getTime(),
                differTimeSign,
                differenceTime,
                summerTimeFrom,
                summerTimeTo,
                summerTime,
                getTimestampFormat(dateStyleId));
    }

    public static String formatTimestamp(long time, String differTimeSign, String differenceTime,
                                         String summerTimeFrom, String summerTimeTo, String summerTime) {

        return toStringFormatLocalTime(
                time,
                differTimeSign,
                differenceTime,
                summerTimeFrom,
                summerTimeTo,
                summerTime,
                DEFAULT_TIMESTAMP_FORMAT);
    }

    public static String formatTimestamp(long time, String differTimeSign, String differenceTime,
                                         String summerTimeFrom, String summerTimeTo,
                                         String summerTime, String dateStyleId) {

        return toStringFormatLocalTime(
                time,
                differTimeSign,
                differenceTime,
                summerTimeFrom,
                summerTimeTo,
                summerTime,
                getTimestampFormat(dateStyleId));
    }

    public static String formatTimestampToDate(Timestamp time, String differTimeSign,
                                               String differenceTime, String summerTimeFrom,
                                               String summerTimeTo, String summerTime) {

        if (time == null) {
            return null;
        }

        return toStringFormatLocalTime(
                time.getTime(),
                differTimeSign,
                differenceTime,
                summerTimeFrom,
                summerTimeTo,
                summerTime,
                DEFAULT_DATE_YMD_FORMAT);
    }

    public static String formatTimestampToDate(Timestamp time, String differTimeSign,
                                               String differenceTime, String summerTimeFrom,
                                               String summerTimeTo, String summerTime, String dateStyleId) {

        if (time == null) {
            return null;
        }

        return toStringFormatLocalTime(
                time.getTime(),
                differTimeSign,
                differenceTime,
                summerTimeFrom,
                summerTimeTo,
                summerTime,
                getYMDFormat(dateStyleId));
    }

    public static String formatTimestampToDate(long time, String differTimeSign,
                                               String differenceTime, String summerTimeFrom,
                                               String summerTimeTo, String summerTime) {

        return toStringFormatLocalTime(
                time,
                differTimeSign,
                differenceTime,
                summerTimeFrom,
                summerTimeTo,
                summerTime,
                DEFAULT_DATE_YMD_FORMAT);
    }

    public static String formatTimestampToDate(long time, String differTimeSign,
                                               String differenceTime, String summerTimeFrom,
                                               String summerTimeTo, String summerTime, String dateStyleId) {

        return toStringFormatLocalTime(
                time,
                differTimeSign,
                differenceTime,
                summerTimeFrom,
                summerTimeTo,
                summerTime,
                getYMDFormat(dateStyleId));
    }

    public static String toStringFormatLocalTime(long time, String differTimeSign,
                                                 String differenceTime, String summerTimeFrom,
                                                 String summerTimeTo, String summerTime, String format) {

        long localTime = time;

        long differenceTimeLong = toDifferenceTimeLong(differenceTime);

        long summerTimeLong = toSummerTimeLong(summerTime);

        if (differTimeSign != null && differTimeSign.equals("+")) {
            localTime += differenceTimeLong;
        } else {
            localTime -= differenceTimeLong;
        }

        if (isSummerTime(time, summerTimeFrom, summerTimeTo)) {
            localTime -= summerTimeLong;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        return sdf.format(new java.util.Date(localTime));
    }

    public static long toGMTTimeFromLocalTime(long localTime, String differTimeSign,
                                              String differenceTime, String summerTimeFrom,
                                              String summerTimeTo, String summerTime) {

        long time = localTime;

        long differenceTimeLong = toDifferenceTimeLong(differenceTime);

        long summerTimeLong = toSummerTimeLong(summerTime);

        if (differTimeSign != null && differTimeSign.equals("+")) {
            time -= differenceTimeLong;
        } else {
            time += differenceTimeLong;
        }

        if (isSummerTime(localTime, summerTimeFrom, summerTimeTo)) {
            time += summerTimeLong;
        }

        return time;
    }

    public static long toDifferenceTimeLong(String differenceTime) {

        long differenceTimeLong;

        try {
            long differenceTimeM = Long.parseLong(differenceTime.substring(0, 2));
            long differenceTimeS = Long.parseLong(differenceTime.substring(2, 4));

            differenceTimeLong = (differenceTimeM * 60 * 60 * 1000) + (differenceTimeS * 60 * 1000);

        } catch (Exception e) {
            differenceTimeLong = 0;
        }

        return differenceTimeLong;
    }

    public static long toSummerTimeLong(String summerTime) {

        long summerTimeLong;

        try {
            long summerTimeM = Long.parseLong(summerTime.substring(0, 2));
            long summerTimeS = Long.parseLong(summerTime.substring(2, 4));

            summerTimeLong = (summerTimeM * 60 * 60 * 1000) + (summerTimeS * 60 * 1000);

        } catch (Exception e) {
            summerTimeLong = 0;
        }

        return summerTimeLong;
    }

    public static boolean isSummerTime(long time, String summerTimeFrom, String summerTimeTo) {

        if (summerTimeFrom == null || summerTimeFrom.trim().length() != 4 ||
                summerTimeTo == null || summerTimeTo.trim().length() != 4) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_MD_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        String md = sdf.format(new java.util.Date(time));

        if (md.compareTo(summerTimeFrom) >= 0 && md.compareTo(summerTimeTo) <= 0) {
            // サマータイム
            return true;
        } else {
            return false;
        }
    }

    public static String formatGMTTimestamp(Timestamp time) {
        return toStringFormatGMTTime(time, DEFAULT_TIMESTAMP_FORMAT);
    }

    public static String formatGMTTimestamp(Timestamp time, String dateStyleId) {
        return toStringFormatGMTTime(time, getTimestampFormat(dateStyleId));
    }

    public static String formatGMTTimestamp(long time) {
        return toStringFormatGMTTime(time, DEFAULT_TIMESTAMP_FORMAT);
    }

    public static String formatGMTTimestamp(long time, String dateStyleId) {
        return toStringFormatGMTTime(time, getTimestampFormat(dateStyleId));
    }

    public static String formatGMTTimestampToDate(Timestamp time) {
        return toStringFormatGMTTime(time, DEFAULT_DATE_YMD_FORMAT);
    }

    public static String formatGMTTimestampToDate(Timestamp time, String dateStyleId) {
        return toStringFormatGMTTime(time, getYMDFormat(dateStyleId));
    }

    public static String formatGMTTimestampToDate(long time) {
        return toStringFormatGMTTime(time, DEFAULT_DATE_YMD_FORMAT);
    }

    public static String formatGMTTimestampToDate(long time, String dateStyleId) {
        return toStringFormatGMTTime(time, getYMDFormat(dateStyleId));
    }

    public static String toStringFormatGMTTime(Timestamp time, String format) {

        if (time == null) {
            return null;
        }

        return toStringFormatGMTTime(time.getTime(), format);
    }

    public static String toStringFormatGMTTime(long time, String format) {

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        return sdf.format(new java.util.Date(time));
    }

    public static String toStringFormatTime(long time, String format) {

        SimpleDateFormat sdf = new SimpleDateFormat(format);

        return sdf.format(new java.util.Date(time));
    }

    public static int getLastDay(int yy, int mm) {

        GregorianCalendar gc = new GregorianCalendar(yy, mm - 1, 1);

        return gc.getActualMaximum(GregorianCalendar.DATE);
    }

    public static String getLocalPattern(Locale locale) {
        SimpleDateFormat f = new SimpleDateFormat();
        f.setCalendar(Calendar.getInstance(locale));
        return f.toLocalizedPattern();
    }

    public static int getYears(java.util.Date date0, java.util.Date date1) {

        Calendar calendar0 = Calendar.getInstance();
        calendar0.setTime(date0);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);

        int year0 = calendar0.get(Calendar.YEAR);
        int year1 = calendar1.get(Calendar.YEAR);

        int years = year1 - year0;


        return years;
    }

    public static String getDifferenceOfYears(java.util.Date dateFrom, java.util.Date dateTo) {

        String years = DEFAULT_YEARS;

        if (dateFrom == null || dateTo == null) {

            years = DEFAULT_YEARS;
        } else {

            int days = getDifferenceOfDays(dateFrom, dateTo);
            DecimalFormat df = new DecimalFormat("#.0");

            years = df.format(days / 365.0);
        }

        return years;
    }

    public static Date formatCharDateYMDHMS(String str) {

        String format = DEFAULT_YMDHMSDATE_FORMAT;

        if (str == null || str.trim().length() == 0) {

            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        ParsePosition pos = new ParsePosition(0);
        java.util.Date date = sdf.parse(str, pos);

        if (date == null) {

            return null;
        }

        return new Date(date.getTime());
    }

    public static Timestamp formatYMDToYMDHMS(String str) {

        String format = DEFAULT_YMDHMSDATE_FORMAT;

        if (str == null || str.trim().length() == 0) {

            return null;
        }

        str += " 00:00:00";

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        ParsePosition pos = new ParsePosition(0);
        java.util.Date date = sdf.parse(str, pos);

        if (date == null) {

            return null;
        }

        Timestamp ts = DateUtil.fromatDateToTimestamp(new Date(date.getTime()));

        return ts;
    }

    public static Timestamp fromatDateToTimestamp(java.util.Date date) {

        Timestamp ts = new Timestamp(System.currentTimeMillis());

        try {

            SimpleDateFormat df = new SimpleDateFormat(DEFAULT_YMDHMSDATE_FORMAT);
            String time = df.format(date);
            ts = Timestamp.valueOf(time);
        } catch (Exception e) {

            return null;
        }

        return ts;
    }
}