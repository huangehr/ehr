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
public class CacheBizCommon {

    /**
     * 生成缓存的 key
     * 最终生成的key为：categoryCode + : + 填充值的keyRuleExpression
     *
     * @param keyRuleExpression Key规则表达式
     * @param ruleParams        Key规则参数
     * @param categoryCode      缓存分类编码
     * @return 缓存的 key
     * @throws Exception
     */
    public static String generateKey(String keyRuleExpression, String ruleParams, String categoryCode) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> paramNames = parseParamNames(keyRuleExpression);
        String key = keyRuleExpression;
        if (paramNames.size() != 0) {
            if (StringUtils.isEmpty(ruleParams)) {
                String errorMsg = "Key规则表达式中有占位命名参数，则规则参数不能为空。";
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, errorMsg);
            }

            Map<String, Object> paramValues = objectMapper.readValue(ruleParams, Map.class);
            for (int i = 0, length = paramNames.size(); i < length; i++) {
                String paramName = paramNames.get(i);
                Object paramValue = paramValues.get(paramName);
                if (paramValue == null) {
                    String errorMsg = "参数名 " + paramName + " 缺少值，或参数名错误，或Key规则不符合规范。";
                    throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, errorMsg);
                } else {
                    key = key.replace("{" + paramName + "}", paramValue.toString());
                }
            }
        }
        key = categoryCode + ":" + key;
        return key;
    }

    /**
     * 解析Key规则中的占位参数名。
     *
     * 例：规则 xxx{a}xx{b}{c},包含三个占位命名参数 a、b、c，
     * 命名参数用“{}”裹着。
     *
     * @param keyRuleExpression Key规则表达式
     * @return 占位参数名集合
     */
    public static List<String> parseParamNames(String keyRuleExpression) {
        List<String> paramNames = new ArrayList<>();

        if (!keyRuleExpression.contains("{")) {
            return paramNames;
        }

        int preIndex = 0;
        while (preIndex != -1) {
            int start = keyRuleExpression.indexOf("{", preIndex);
            int end = keyRuleExpression.indexOf("}", preIndex);
            String paramName = keyRuleExpression.substring(start + 1, end);
            paramNames.add(paramName);
            preIndex = keyRuleExpression.indexOf("{", end);
        }

        return paramNames;
    }

}
