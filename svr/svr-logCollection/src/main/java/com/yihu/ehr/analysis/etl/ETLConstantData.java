package com.yihu.ehr.analysis.etl;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by lyr-pc on 2017/2/20.
 */
public class ETLConstantData {

    private static Properties cities = new Properties();

    static {
        try {
            cities.load(ETLConstantData.class.getResourceAsStream("/city.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /***************************体征指标正常范围*******************************/
    // 血糖餐前最小值
    public static final double HEALTH_STANDARD_ST_MIN_BEFORE = 4;
    // 血糖餐前最大值
    public static final double HEALTH_STANDARD_ST_MAX_BEFORE = 7;
    // 血糖餐后最小值
    public static final double HEALTH_STANDARD_ST_MIN_AFTER = 4;
    // 血糖餐后最大值
    public static final double HEALTH_STANDARD_ST_MAX_AFTER = 11.1;

    // 舒张压最小值
    public static final int HEALTH_STANDARD_SZY_MIN = 60;
    // 舒张压最大值
    public static final int HEALTH_STANDARD_SZY_MAX = 90;
    // 收缩压最小值
    public static final int HEALTH_STANDARD_SSY_MIN = 90;
    // 收缩压最大值
    public static final int HEALTH_STANDARD_SSY_MAX = 140;

    /**
     * 性别
     *
     * @return
     */
    public static String sexName(int sex) {
        switch (sex) {
            case 1:
                return "男";
            case 2:
                return "女";
            default:
                return "其他";
        }
    }

    /**
     * 年龄段
     *
     * @param age
     * @return
     */
    public static String ageInterval(int age) {
        if (age < 7) {
            return "儿童";
        } else if (age >= 7 && age <= 14) {
            return "少年";
        } else if (age >= 15 && age <= 35) {
            return "青年";
        } else if (age >= 36 && age <= 60) {
            return "中年";
        } else {
            return "老年";
        }
    }

    /**
     * 获取城市名称
     *
     * @param city
     * @return
     */
    public static String cityName(String city) {
        return cities.get(city).toString();
    }

    /**
     * 体质指数转换
     *
     * @param bmi
     * @return
     */
    public static String bmiName(double bmi) {
        if (bmi < 18.5) {
            return "偏瘦";
        } else if (bmi >= 18.5 && bmi < 24) {
            return "体重正常";
        } else if (bmi >= 24 && bmi < 27) {
            return "超重";
        } else if (bmi >= 27 && bmi < 30) {
            return "偏胖";
        } else if (bmi >= 30 && bmi < 35) {
            return "肥胖";
        } else if (bmi >= 35 && bmi < 40) {
            return "重度肥胖";
        } else {
            return "极重度肥胖";
        }
    }

    /**
     * 心律情况
     *
     * @param rate
     * @return
     */
    public static String heartRate(double rate) {
        if (rate >= 40 && rate <= 160) {
            return "心率正常";
        } else {
            return "心率不正常";
        }
    }

    /**
     * 空腹血糖判断
     *
     * @param value
     * @return
     */
    public static int xueTangBefore(double value) {
        if (value < ETLConstantData.HEALTH_STANDARD_ST_MIN_BEFORE) {
            return -1;
        }
        if (value > ETLConstantData.HEALTH_STANDARD_ST_MAX_BEFORE) {
            return 1;
        }
        return 0;
    }

    /**
     * 餐后血糖判断
     *
     * @param value
     * @return
     */
    public static int xueTangAfter(double value) {
        if (value < ETLConstantData.HEALTH_STANDARD_ST_MIN_AFTER) {
            return -1;
        }
        if (value > ETLConstantData.HEALTH_STANDARD_ST_MAX_AFTER) {
            return 1;
        }
        return 0;
    }

    /**
     * 收缩压比较
     *
     * @param value
     * @return
     */
    public static int ssy(double value) {
        if (value < ETLConstantData.HEALTH_STANDARD_SSY_MIN) {
            return -1;
        }
        if (value > ETLConstantData.HEALTH_STANDARD_SSY_MAX) {
            return 1;
        }
        return 0;
    }

    /**
     * 舒张压比较
     *
     * @param value
     * @returnz
     */
    public static int szy(double value) {
        if (value < ETLConstantData.HEALTH_STANDARD_SZY_MIN) {
            return -1;
        }
        if (value > ETLConstantData.HEALTH_STANDARD_SZY_MAX) {
            return 1;
        }
        return 0;
    }
}
