package com.yihu.ehr.util;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ID校验器。校验身份证号，组织机构代码。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.03 14:13
 */
public class IdValidator {
    protected static final Logger logger = LoggerFactory.getLogger(IdValidator.class);

    /**
     * 验证组织机构代码。
     *
     * @param code 企业组织机构代码
     * @return
     */
    public static final boolean validateOrgCode(String code) {
        int[] ws = { 3, 7, 9, 10, 5, 8, 4, 2 };
        String str = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String regex = "^([0-9A-Z]){8}-[0-9|X]$";

        if (!code.matches(regex)) return false;

        int sum = 0;
        for (int i = 0; i < 8; i++) {
            sum += str.indexOf(String.valueOf(code.charAt(i))) * ws[i];
        }

        logger.info("sum is {}", sum);
        logger.info("sum % 11 is {}", sum % 11);

        int c9 = 11 - (sum % 11);
        String sc9 = String.valueOf(c9);
        if (11 == c9) {
            sc9 = "0";
        } else if (10 == c9) {
            sc9 = "X";
        }

        logger.info("sc9 is {}", sc9);
        return sc9.equals(String.valueOf(code.charAt(9)));
    }

    /**
     * 身份证号码验证
     * 1、号码的结构: 公民身份号码是特征组合码，由十七位数字本体码和一位校验码组成。排列顺序从左至右依次为：六位数字地址码，八位数字出生日期码，三位数字顺序码和一位数字校验码。
     * 2、地址码(前六位数）: 表示编码对象常住户口所在县(市、旗、区)的行政区划代码，按GB/T2260的规定执行。
     * 3、出生日期码（第七位至十四位）: 表示编码对象出生的年、月、日，按GB/T7408的规定执行，年、月、日代码之间不用分隔符。
     * 4、顺序码（第十五位至十七位）: 表示在同一地址码所标识的区域范围内，对同年、同月、同日出生的人编定的顺序号，顺序码的奇数分配给男性，偶数分配给女性。
     * 5、校验码（第十八位数）:
     * （1）十七位数字本体码加权求和公式 S = Sum(Ai * Wi), i = 0,  , 16 ，先对前17位数字的权求和
     *      Ai:表示第i位置上的身份证号码数字值 Wi:表示第i位置上的加权因子 Wi: 7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2
     * （2）计算模 Y = mod(S, 11) （3）通过模得到对应的校验码 Y: 0 1 2 3 4 5 6 7 8 9 10 校验码: 1 0 X 9 8 7 6 5 4 3 2
     *
     * @param id 身份证号
     * @return 有效：返回空串，否则返回错误信息
     * @throws ParseException
     */
    @SuppressWarnings("unchecked")
    public static String validateIdCardNo(String id) throws ParseException {
        if (StringUtils.isEmpty(id)) return "身份证号码长度应该为15位或18位。";

        String[] ValCodeArr = {"1", "0", "x", "9", "8", "7", "6", "5", "4", "3", "2"};
        String[] Wi = {"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2"};
        String Ai = "";

        if (id.length() != 15 && id.length() != 18) {
            return "Identity card no length must be 15 or 15.";
        }

        if (id.length() == 18) {
            Ai = id.substring(0, 17);
        } else if (id.length() == 15) {
            Ai = id.substring(0, 6) + "19" + id.substring(6, 15);
        }

        if (!NumberUtils.isNumber(Ai)) {
            return "Identity card no must be 15 digit or 17 digit except last";
        }

        String strYear = Ai.substring(6, 10);
        String strMonth = Ai.substring(10, 12);
        String strDay = Ai.substring(12, 14);
        if (!isDate(strYear + "-" + strMonth + "-" + strDay)) {
            return "Birthday section is invalid.";
        }

        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
                || (gc.getTime().getTime() - s.parse(
                strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
            return "Birthday section is out of range.";
        }

        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
            return "Invalid month.";
        }

        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            return "Invalid date.";
        }

        Hashtable h = GetAreaCode();
        if (h.get(Ai.substring(0, 2)) == null) {
            return "Region section is invalid.";
        }

        int totalmulAiWi = 0;
        for (int i = 0; i < 17; i++) {
            totalmulAiWi = totalmulAiWi
                    + Integer.parseInt(String.valueOf(Ai.charAt(i)))
                    * Integer.parseInt(Wi[i]);
        }

        int modValue = totalmulAiWi % 11;
        String strVerifyCode = ValCodeArr[modValue];
        Ai = Ai + strVerifyCode;

        if (id.length() == 18) {
            if (Ai.equals(id) == false) {
                return "Invalid identity card no.";
            }
        }

        return "";
    }

    @SuppressWarnings("unchecked")
    private static Hashtable GetAreaCode() {
        Hashtable hashtable = new Hashtable();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");

        return hashtable;
    }

    /**
     * 判断字符串是否为日期格式
     *
     * @param strDate
     * @return
     */
    public static boolean isDate(String strDate) {
        Pattern pattern = Pattern
                .compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        Matcher m = pattern.matcher(strDate);
        return m.matches();
    }
}
