package com.yihu.ehr.redis.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.exception.ApiException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 缓存服务共通类
 *
 * @author 张进军
 * @date 2017/12/1 14:00
 */
public class CacheCommonBiz {

    /**
     * 制造缓存Key前缀 = 缓存分类编码 + :
     *
     * @param categoryCode 缓存分类编码
     * @return 缓存Key前缀
     */
    public static String makeKeyPrefix(String categoryCode) {
        return categoryCode + ":";
    }

    /**
     * 生成Redis缓存的唯一key
     * 最终生成的key = 前缀 + 填充值的Key规则
     *
     * @param keyRule       Key规则表达式
     * @param keyRuleParams Key规则命名参数的值
     * @param categoryCode  缓存分类编码
     * @return 缓存的 key
     * @throws Exception
     */
    public static String generateKey(String keyRule, String keyRuleParams, String categoryCode) throws Exception {
        String errorMsg = "";

        ObjectMapper objectMapper = new ObjectMapper();
        List<String> paramNames = parseParamNames(keyRule);
        String key = keyRule;
        if (paramNames.size() != 0) {
            if (StringUtils.isEmpty(keyRuleParams)) {
                errorMsg = "Key规则表达式中有命名参数，则Key规则命名参数的值必传。";
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, errorMsg);
            }

            Map<String, Object> paramValues = objectMapper.readValue(keyRuleParams, Map.class);
            for (int i = 0, length = paramNames.size(); i < length; i++) {
                String paramName = paramNames.get(i);
                Object paramValue = paramValues.get(paramName);
                if (paramValue == null) {
                    errorMsg = "命名参数 " + paramName + " 缺少值，或参数名错误。";
                    throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, errorMsg);
                } else {
                    key = key.replace("{" + paramName + "}", paramValue.toString());
                }
            }
        }
        key = makeKeyPrefix(categoryCode) + key;
        return key;
    }

    /**
     * 校验缓存Key规则表达式
     * <p>
     * 例：规则 xxx{a}xx{b}{c}
     * 说明：
     * 1.a、b、c 代表三个命名参数，参数用"{}"包含起来。是否需要定义参数，根据业务而定。
     * 2.x 代表任意字符，可有可无，根据业务自行定义。
     *
     * @param keyRule
     * @return
     */
    public static boolean validateKeyRule(String keyRule) throws ApiException {
        String errorMsg = "";

        if (!keyRule.contains("{") && !keyRule.contains("}")) {
            return true;
        }

        int leftBraceNum = countLeftBrace(keyRule);
        int rightBraceNum = countRightBrace(keyRule);

        int preIndex = 0;
        while (preIndex != -1) {
            int start = keyRule.indexOf("{", preIndex);
            int end = keyRule.indexOf("}", preIndex);

            // 大括号只用于包含参数名
            int innerStart = keyRule.indexOf("{", start + 1);
            if (leftBraceNum != rightBraceNum || (innerStart != -1 && innerStart < end)) {
                errorMsg = "大括号\"{\"、\"}\"只能用于包裹参数名，参数名中或其他地方不能使用。";
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, errorMsg);
            }
            // 参数名不能为空
            if (start == end - 1) {
                errorMsg = "大括号中参数名不能为空。";
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, errorMsg);
            }

            preIndex = keyRule.indexOf("{", end + 1);
        }

        return true;
    }

    /**
     * 解析Key规则中的占位参数名
     *
     * @param keyRule Key规则表达式
     * @return 占位参数名集合
     */
    public static List<String> parseParamNames(String keyRule) {
        List<String> paramNames = new ArrayList<>();

        if (!keyRule.contains("{") && !keyRule.contains("}")) {
            return paramNames;
        }

        int preIndex = 0;
        while (preIndex != -1) {
            int start = keyRule.indexOf("{", preIndex);
            int end = keyRule.indexOf("}", preIndex);

            String paramName = keyRule.substring(start + 1, end);
            paramNames.add(paramName);

            preIndex = keyRule.indexOf("{", end + 1);
        }

        return paramNames;
    }

    /**
     * 将缓存Key规则中的参数名（包括大括号）替换为指定字符串
     *
     * @param keyRule     Key规则
     * @param replacement 代替的字符串
     * @return 替换后的字符串
     */
    public static String replaceParams(String keyRule, String replacement) {
        if (!keyRule.contains("{") && !keyRule.contains("}")) {
            return keyRule;
        }

        int preIndex = 0;
        while (preIndex != -1) {
            int start = keyRule.indexOf("{", preIndex);
            int end = keyRule.indexOf("}", preIndex);

            int len = end - start - 1;
            String regex = "\\{.{1," + len + "}\\}";
            keyRule = keyRule.replaceFirst(regex, replacement);

            preIndex = keyRule.indexOf("{", start + replacement.length());
        }
        return keyRule;
    }

    /**
     * 统计Key规则中左大括号个数
     *
     * @param keyRule Key规则
     * @return 左大括号个数
     */
    private static int countLeftBrace(String keyRule) {
        int sum = 0;
        while (keyRule.contains("{")) {
            keyRule = keyRule.replaceFirst("\\{", "");
            sum += 1;
        }
        return sum;
    }

    /**
     * 统计Key规则中右大括号个数
     *
     * @param keyRule Key规则
     * @return 右大括号个数
     */
    private static int countRightBrace(String keyRule) {
        int sum = 0;
        while (keyRule.contains("}")) {
            keyRule = keyRule.replaceFirst("\\}", "");
            sum += 1;
        }
        return sum;
    }

    // 测试
    public static void main(String[] args) {
//        String keyRule = "zjj"; // yes
        String keyRule = "zjj.{aa}xx{b}xx"; // yes
//        String keyRule = "zjj.{aa{a}a}"; // no
//        String keyRule = "zjj.{aa{a}"; // no
//        String keyRule = "zjj.{a}{"; // no
//        String keyRule = "zjj.{a}{"; // no
//        String keyRule = "zjj.{}{a}"; // no

//        validateKeyRule(keyRule);

        String newKeyRule = replaceParams(keyRule, "*");
//        System.out.println(newKeyRule);
    }

}
