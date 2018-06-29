package com.yihu.quota.etl;

import org.joda.time.DateTime;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by chenweida on 2017/6/1.
 */
public class Contant {
    //抽取数据的开始时间
    public static String startTime = "${start_time}";
    //抽取数据的结束时间
    public static String endTime = "${end_time}";


    //数据库类型
    public static class db_type {
        public static String oracle = "oracle";
        public static String mysql = "mysql";
    }

    /**
     * DateModel的map的key常量
     */
    public static class extract {
        public static final String computeKey1 = "oneKey";
        public static final String computeKey2 = "senondKey";
    }

    /**
     * 运算常量
     */
    public static class compute {
        public static final String add = "1"; //累加
        public static final String division = "2"; //相除
        public static final int perCount = 20000;//单位统计量
    }

    /**
     * 主维度常量
     */
    public static class main_dimension {
        public static final String time_day = "1";//时间维度 日
        public static final String time_week = "2";//时间维度 周
        public static final String time_month = "3";//时间维度  月
        public static final String time_year = "4";//时间维度 年
        public static final String area_province = "5";//行政区划 省
        public static final String area_city = "6";//行政区划 市
        public static final String area_town = "7";//行政区划 区县
        public static final String area_org = "8";//行政区划 机构
        public static final String area_team = "9";//行政区划  团队
    }

    public static class quota {
        public static final String dataLevel_all = "1"; // 全量
        public static final String dataLevel_increase = "2"; // 增量

        public static final String aggregation_count = "count";
        public static final String aggregation_sum = "sum";
        public static final String aggregation_list = "list";
        public static final String aggregation_distinct = "distinct"; // 去重

    }

    /**
     * areaLevel 具体的值
     */
    public static class main_dimension_areaLevel {
        public static final String area_province = "1";//行政区划 省
        public static final String area_city = "2";//行政区划 市
        public static final String area_town = "3";//行政区划 区县
        public static final String area_org = "4";//行政区划 机构
        public static final String area_team = "5";//行政区划  团队

        public static String getAreaLevelByMainDimension(String key) {
            switch (key) {
                case main_dimension.area_province: {
                    return area_province;
                }
                case main_dimension.area_city: {
                    return area_city;
                }
                case main_dimension.area_town: {
                    return area_town;
                }
                case main_dimension.area_org: {
                    return area_org;
                }
                case main_dimension.area_team: {
                    return area_team;
                }
            }
            return "";
        }
    }


    /**
     * 主维度 时间维度
     */
    public static class main_dimension_timeLevel {
        public static final String year = "1";
        public static final String month = "2";
        public static final String week = "3";
        public static final String day = "4";

        public static String getStartTime(String key) {
            LocalDate today = LocalDate.now();
            if (StringUtils.isEmpty(key)) {
                key = day;
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00'Z'");
            switch (key) {
                case year: {
                    //去年度第一天
                    return simpleDateFormat.format(getCurrYearFirst(-1));
                }
                case month: {
                    //上个月第一天
                    return getYesterMonthDay(simpleDateFormat, -1);
                }
                case week: {
                    //上周周第一天
                    return getStartDayOfWeek(simpleDateFormat, -1);
                }
                case day: {
                    //昨天
                    return getYesterday();
                }
            }
            return getYesterday();
        }

        /**
         * 获取这个月第一天
         *
         * @param today
         * @return
         */
        private static String getCurrentMonthDay(LocalDate today) {
            LocalDate firstday = LocalDate.of(today.getYear(), today.getMonth(), 1);
            return firstday.format(DateTimeFormatter.ISO_DATE);
        }

        /**
         * 获取上个月第一天
         *
         * @return
         */
        private static String getYesterMonthDay(SimpleDateFormat simpleDateFormat, Integer n) {
            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH);
            calendar.set(Calendar.MONTH, month + n);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            Date strDateTo = calendar.getTime();
            return simpleDateFormat.format(strDateTo);
        }

        /**
         * 获取本周第一天
         *
         * @param simpleDateFormat
         * @param n                为推迟的周数，1本周，-1向前推迟一周，2下周，依次类推
         * @return
         */
        private static String getStartDayOfWeek(SimpleDateFormat simpleDateFormat, Integer n) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, n * 7);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            Date date = cal.getTime();
            return simpleDateFormat.format(date);
        }

        /**
         * 获取去年的第一天
         *
         * @return
         */
        private static Date getCurrYearFirst(Integer n) {
            Calendar currCal = Calendar.getInstance();
            int currentYear = currCal.get(Calendar.YEAR) + n;
            return getYearFirst(currentYear);
        }

        /**
         * 获取某年第一天日期
         *
         * @param year 年份
         * @return Date
         */
        private static Date getYearFirst(int year) {
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(Calendar.YEAR, year);
            Date currYearFirst = calendar.getTime();
            return currYearFirst;
        }

        /**
         * 获取昨天
         *
         * @return
         */
        private static String getYesterday() {
            return new org.joda.time.LocalDate(new DateTime().minusDays(1)).toString("yyyy-MM-dd'T'00:00:00'Z'");
        }
    }


    public static class save_status {
        public static final String success = "1";
        public static final String fail = "0";
        public static final String executing = "2";
    }


    public static class save {
        public static final String es = "1";
        public static final String mysql = "2";
    }

    public static class convert{
        public static String level_age_1="1";
        public static String level_age_2="2";
        public static String level_age_3="3";
        public static String level_age_4="4";
        public static String level_age_5="5";
        public static String level_age_1_name="0~6";
        public static String level_age_2_name="7~17";
        public static String level_age_3_name="18~40";
        public static String level_age_4_name="41~65";
        public static String level_age_5_name=">65";
    }

    public static class quartz_cron {
        //每年 1月1号 0点 0点0秒触发
        public static final String everyYearFirstDay = "0 0 0 1 1 ? *";
        //每个月1号 0点 0点0秒触发
        public static final String everyMonthFirstDay = "0 0 0 1 * ?";
        //每周一 0点 0点0秒触发
        public static final String everyWeekFirstDay = "0 0 0 ? * MON";
        //每天0点0分 0秒触发
        public static final String everyDay = "0 0 0 * * ?";
    }

    public static class orgHealthTypeCode {
        public static final String hospital = "hospital"; //医院卫生机构
        public static final String basicMedical = "basic_medical"; //基层医疗卫生机构
        public static final String pro_public = "pro_public";//专业公共卫生机构
        public static final String other = "other";//其他医疗卫生机构

        public static final String hospital_Id = "13"; //医院卫生机构
        public static final String basicMedical_Id = "57"; //基层医疗卫生机构
        public static final String pro_public_Id = "94";//专业公共卫生机构
        public static final String other_Id = "134";//其他医疗卫生机构
    }


}
