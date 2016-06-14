package com.yihu.ehr.util.operator;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class NumberUtil {

    public static final BigDecimal DEFAULT_NUMBER = BigDecimal.ZERO;

    public static final String DECIMAL_FORMAT = "#,##0.00";
    public static final String INTEGER_FORMAT = "#,##0";
    public static final String DEFAULT_NUM_GROUP_STYLE_ID = "1";
    public static final String DEFAULT_DECIMAL_STYLE_ID = "2";
    public static final String[][] DECIMAL_STYLE_LIST = {
            {"1", ",", "Comma"},
            {"2", ".", "Period"}
    };
    public static final String[][] NUM_GROUP_STYLE_LIST = {
            {"1", ",", "Comma"},
            {"2", ".", "Period"},
            {"3", " ", "Space"}
    };

    protected NumberUtil() {
    }

    public static boolean isDivisible(BigDecimal dividend, BigDecimal divisor) {

        if (dividend == null || divisor == null) {
            return false;
        }

        if (compare(dividend.remainder(divisor), DEFAULT_NUMBER) == 0) {
            return true;
        }

        return false;
    }

    public static String changeToDecimalFormat(Number num) {

        return formatNumber(num, DECIMAL_FORMAT);
    }

    public static String changeToIntegerFormat(Number num) {

        return formatNumber(num, INTEGER_FORMAT);
    }

    public static String formatNumber(Number num, String format) {

        DecimalFormat noFormat = new DecimalFormat(format);

        return noFormat.format(num);
    }

    public static int compare(BigDecimal num1, BigDecimal num2) {

        if (num1 == null) {
            num1 = DEFAULT_NUMBER;
        }

        if (num2 == null) {
            num2 = DEFAULT_NUMBER;
        }

        return num1.compareTo(num2);
    }

    public static boolean gt(BigDecimal num1, BigDecimal num2) {

        if (compare(num1, num2) > 0) {
            return true;
        }

        return false;
    }

    public static boolean ge(BigDecimal num1, BigDecimal num2) {

        return !lt(num1, num2);
    }

    public static boolean lt(BigDecimal num1, BigDecimal num2) {

        if (compare(num1, num2) < 0) {
            return true;
        }

        return false;
    }

    public static boolean le(BigDecimal num1, BigDecimal num2) {

        return !gt(num1, num2);
    }

    public static boolean equals(BigDecimal num1, BigDecimal num2) {

        if (compare(num1, num2) == 0) {
            return true;
        }

        return false;
    }

    public static boolean equals(int num1, int num2) {

        if (compare(new BigDecimal(String.valueOf(num1)), new BigDecimal(String.valueOf(num2))) == 0) {
            return true;
        }

        return false;
    }

    public static boolean isPositive(BigDecimal num) {

        if (num == null) {
            return false;
        }

        if (gt(num, BigDecimal.ZERO)) {
            return true;
        }

        return false;
    }

    public static boolean isZero(BigDecimal num) {

        if (num == null) {
            return false;
        }

        if (equals(num, BigDecimal.ZERO)) {
            return true;
        }

        return false;
    }

    public static BigDecimal subtractBigDecimal(BigDecimal dec1, BigDecimal dec2) {
        return (dec1 != null ? dec1 : DEFAULT_NUMBER).subtract(dec2 != null ? dec2 : DEFAULT_NUMBER);
    }

    public static BigDecimal multiplyBigDecimal(BigDecimal dec1, BigDecimal dec2) {
        return (dec1 != null ? dec1 : DEFAULT_NUMBER).multiply(dec2 != null ? dec2 : DEFAULT_NUMBER);
    }

    public static BigDecimal divideBigDecimal(BigDecimal dec1, BigDecimal dec2) {
        if (dec2 == null || dec2.equals(DEFAULT_NUMBER)) {
            return DEFAULT_NUMBER;
        }
        return (dec1 != null ? dec1 : DEFAULT_NUMBER).divide(dec2, 2, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal divideBigDecimal(BigDecimal dec1, BigDecimal dec2, int scale) {
        if (dec2 == null || dec2.equals(DEFAULT_NUMBER)) {
            return DEFAULT_NUMBER;
        }
        return (dec1 != null ? dec1 : DEFAULT_NUMBER).divide(dec2, scale, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal floorDivideBigDecimal(BigDecimal dec1, BigDecimal dec2) {
        if (dec2 == null || dec2.equals(DEFAULT_NUMBER)) {
            return DEFAULT_NUMBER;
        }
        return (dec1 != null ? dec1 : DEFAULT_NUMBER).divide(dec2, 0, BigDecimal.ROUND_FLOOR);
    }

    public static BigDecimal ceilingDivideBigDecimal(BigDecimal dec1, BigDecimal dec2) {
        if (dec2 == null || dec2.equals(DEFAULT_NUMBER)) {
            return DEFAULT_NUMBER;
        }
        return (dec1 != null ? dec1 : DEFAULT_NUMBER).divide(dec2, 0, BigDecimal.ROUND_CEILING);
    }

    public static BigDecimal toBigDecimal(int num) {
        return toBigDecimal(Integer.toString(num));
    }

    public static Integer toInteger(long num) {
        return toInteger(Long.toString(num));
    }

    public static BigDecimal roundBigDecimal(BigDecimal dec1, int scale) {
        return (dec1 != null ? dec1 : DEFAULT_NUMBER).divide(new BigDecimal("1"), scale, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal floorBigDecimal(BigDecimal dec1, int scale) {
        return (dec1 != null ? dec1 : DEFAULT_NUMBER).divide(new BigDecimal("1"), scale, BigDecimal.ROUND_FLOOR);
    }

    public static BigDecimal ceilingBigDecimal(BigDecimal dec1, int scale) {
        return (dec1 != null ? dec1 : DEFAULT_NUMBER).divide(new BigDecimal("1"), scale, BigDecimal.ROUND_CEILING);
    }

    public static Boolean toBoolean(String str) {

        try {
            return new Boolean(str);
        } catch (Exception e) {
            return null;
        }

    }

    public static int toInt(String str) {

        Integer integer = toInteger(str);

        return (integer == null ? 0 : integer.intValue());
    }

    public static int toInt(Integer integer) {
        return (integer == null ? 0 : integer.intValue());
    }

    public static Integer toInteger(String str) {

        if (str == null) {
            return null;
        }

        if (StringUtils.isBlank(str)) {
            return 0;
        }

        try {
            return new Integer(str.trim());
        } catch (NumberFormatException nfe) {
            return null;
        }

    }

    public static Integer toInteger(int i) {

        try {
            return new Integer(i);
        } catch (NumberFormatException nfe) {
            return null;
        }

    }

    public static BigInteger toBigInteger(String str) {

        if (str == null) {
            return null;
        }

        try {
            return new BigInteger(str.trim());
        } catch (NumberFormatException nfe) {
            return null;
        }

    }

    public static BigDecimal toBigDecimal(String str) {

        if (str == null) {
            return null;
        }

        if (StringUtils.isBlank(str)) {
            return BigDecimal.ZERO;
        }

        try {
            return new BigDecimal(str.trim());
        } catch (NumberFormatException nfe) {
            return null;
        }

    }

    public static BigDecimal toBigDecimal(String str, String decPoPtn) {
        return toBigDecimal(formatDecDefault(str, decPoPtn));
    }

    public static Float toFloat(String str) {

        if (str == null) {
            return null;
        }

        try {
            return new Float(str.trim());
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    public static float totoPrimitiveFloat(String str) {
        return (str == null ? 0 : Float.parseFloat(str));
    }

    public static Long toLong(String str) {

        if (str == null) {
            return null;
        }

        try {
            return new Long(str.trim());
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    public static long totoPrimitiveLong(String str) {
        return (str == null ? 0 : Long.parseLong(str));
    }

    public static Double toDouble(String str) {

        if (str == null) {
            return null;
        }

        try {
            return new Double(str.trim());
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    public static double totoPrimitiveDouble(String str) {
        return (str == null ? 0 : Double.parseDouble(str));
    }

    public static String getDecimalStyleName(String decimalStyleId) {

        String value = null;
        for (int i = 0; i < DECIMAL_STYLE_LIST.length; i++) {
            if (DECIMAL_STYLE_LIST[i][0].equals(decimalStyleId)) {
                value = DECIMAL_STYLE_LIST[i][2];
                break;
            }
        }

        if (value == null) {
            throw new IllegalArgumentException("The value of an argument is inaccurate.");
        }

        return value;
    }

    public static String getDecPo(String decPoPtn) {
        return String.valueOf(getDecimalStyleValue(decPoPtn));
    }

    public static String getDecimalStyleValue(String decimalStyleId) {

        String value = null;
        for (int i = 0; i < DECIMAL_STYLE_LIST.length; i++) {
            if (DECIMAL_STYLE_LIST[i][0].equals(decimalStyleId)) {
                value = DECIMAL_STYLE_LIST[i][1];
                break;
            }
        }

        if (value == null) {
            throw new IllegalArgumentException("The value of an argument is inaccurate.");
        }

        return value;
    }

    public static char getDecimalStyleValueChar(String decimalStyleId) {
        return getDecimalStyleValue(decimalStyleId).charAt(0);
    }

    public static String formatIntValue(int n) {
        return formatIntValue(new Integer(n));
    }

    public static String formatIntView(int n, String numSepPtn) {
        return formatIntView(new Integer(n), numSepPtn);
    }

    public static String formatIntValue(Integer num) {

        if (num == null) {
            return null;
        }

        String format = "#####0";

        return formatNum(num, format, null, null);
    }

    public static String formatIntView(Integer num, String numGroupStyleId) {

        if (num == null) {
            return null;
        }

        String format = "###,##0";

        return formatNum(num, format, null, numGroupStyleId);
    }

    public static String formatDecValue(BigDecimal dec, String decimalStyleId) {

        return formatDecValue(dec, -1, decimalStyleId);
    }

    public static String formatDecValue(BigDecimal dec, Integer decLength, String decimalStyleId) {
        return formatDecValue(dec, decLength != null ? decLength.intValue() : -1, decimalStyleId);
    }

    public static String formatDecValue(BigDecimal dec, int decLength, String decimalStyleId) {

        if (dec == null) {
            return null;
        }

        String format = "#####0";

        if (decLength > 0) {
            // 小数桁数指定あり
            for (int i = 0; i < decLength; i++) {
                if (i == 0) {
                    format += ".";
                }
                format += "0";
            }
        } else {
            // 小数桁数指定なし
            format += ".##########";
        }

        return formatNum(dec, format, decimalStyleId, null);
    }

    public static String formatDecView(BigDecimal dec, String decimalStyleId, String numGroupStyleId) {
        return formatDecView(dec, -1, decimalStyleId, numGroupStyleId);
    }

    public static String formatDecView(BigDecimal dec, Integer decLength,
                                       String decimalStyleId, String numGroupStyleId) {
        return formatDecView(dec,
                decLength != null ? decLength.intValue() : -1,
                decimalStyleId,
                numGroupStyleId);
    }

    public static String formatDecView(BigDecimal dec, int decLength,
                                       String decimalStyleId, String numGroupStyleId) {

        if (dec == null) {
            return null;
        }

        String format = "###,##0";

        if (decLength > 0) {
            // 小数桁数指定あり
            for (int i = 0; i < decLength; i++) {
                if (i == 0) {
                    format += ".";
                }
                format += "0";
            }
        } else {
            // 小数桁数指定なし
            format += ".##########";
        }

        return formatNum(dec, format, decimalStyleId, numGroupStyleId);
    }

    protected static String formatNum(Object obj, String format, String decimalStyleId, String numGroupStyleId) {

        char decPo = decimalStyleId != null ? getDecimalStyleValueChar(decimalStyleId) : ' ';
        char numSep = numGroupStyleId != null ? getNumGroupStyleValueChar(numGroupStyleId) : ' ';

        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        df.applyPattern(format);
        String str = df.format(obj);

        if (str == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == ',') {
                sb.append(numSep);
                continue;
            }
            if (c == '.') {
                sb.append(decPo);
                if (i != str.length()) {
                    sb.append(str.substring(i + 1, str.length()));
                }
                break;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static String formatDecDefault(String str, String decPoPtn) {

        if (str == null) {
            return null;
        }

        char decPo = getDecimalStyleValueChar(decPoPtn);

        StringBuffer sb = new StringBuffer(str);

        int idx = sb.toString().indexOf(String.valueOf(decPo));

        if (idx != -1) {
            sb.replace(idx, idx + String.valueOf(decPo).length(), String.valueOf('.'));
        }
        return sb.toString();
    }

    public static String getNumGroupStyleValue(String numGroupStyleId) {

        String value = null;

        for (int i = 0; i < NUM_GROUP_STYLE_LIST.length; i++) {
            if (NUM_GROUP_STYLE_LIST[i][0].equals(numGroupStyleId)) {
                value = NUM_GROUP_STYLE_LIST[i][1];
                break;
            }
        }

        if (value == null) {
            throw new IllegalArgumentException("The value of an argument is inaccurate.");
        }

        return value;
    }

    public static char getNumGroupStyleValueChar(String numGroupStyleId) {
        return getNumGroupStyleValue(numGroupStyleId).charAt(0);
    }

    public static String getNumGroupStyleName(String numGroupStyleId) {

        String value = null;
        for (int i = 0; i < NUM_GROUP_STYLE_LIST.length; i++) {
            if (NUM_GROUP_STYLE_LIST[i][0].equals(numGroupStyleId)) {
                value = NUM_GROUP_STYLE_LIST[i][2];
                break;
            }
        }

        if (value == null) {
            throw new IllegalArgumentException("The value of an argument is inaccurate.");
        }

        return value;
    }

    public static String roundString(String str) {
        BigDecimal dec = toBigDecimal(str);
        return (dec == null ? str : roundBigDecimal(dec).toString());
    }

    public static BigDecimal roundBigDecimal(BigDecimal dec) {
        return (dec == null ? dec : dec.divide(new BigDecimal("1"), 0, BigDecimal.ROUND_HALF_UP));
    }

    public static int roundBigDecimal(int num) {
        return toInt(roundString(Integer.toString(num)));
    }

    public static String fomatNumber(double amount, String pattern) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        DecimalFormat df = (DecimalFormat) nf;
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);
        df.setDecimalSeparatorAlwaysShown(true);
        df.applyPattern(pattern);
        return df.format(amount);
    }

    public static String fomatNumber(long amount, String pattern) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        DecimalFormat df = (DecimalFormat) nf;
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);
        df.setDecimalSeparatorAlwaysShown(true);
        df.applyPattern(pattern);
        return df.format(amount);
    }

    public static BigDecimal addBigDecimal(BigDecimal dec1, BigDecimal dec2) {
        return (dec1 != null ? dec1 : new BigDecimal(0)).add(dec2 != null ? dec2 : new BigDecimal(0));
    }

    public static boolean isInteger(String target) {

        if (target == null) {
            return false;
        }

        char[] digit = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        for (int i = 0; i < target.length(); i++) {
            for (int j = 0; j < digit.length; j++) {
                if (target.charAt(i) == digit[j]) {
                    break;
                }

                if (i == 0 && target.charAt(i) == '-') {
                    break;
                }

                if (i == 0 && target.charAt(i) == '+') {
                    break;
                }

                if (j == digit.length - 1) {
                    return false;
                }

            }
        }
        return true;
    }
}