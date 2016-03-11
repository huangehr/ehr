package com.yihu.ehr.util.validate;

import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/3/9
 */
public class ValidateUtil {

    public static ValidateResult validate(Object dataModel)
            throws InvocationTargetException, IllegalAccessException {

        Class clz = dataModel.getClass();
        if (clz.getAnnotation(Valid.class) == null)
            return ValidateResult.success();
        Method[] methods = clz.getMethods();
        ValidateResult validateResult;
        for (Method method : methods) {
            if (!(validateResult = validateRequired(method, dataModel)).isRs())
                return validateResult;

            if (!(validateResult = validateLen(method, dataModel)).isRs())
                return validateResult;

            continue;
        }
        return ValidateResult.success();
    }


    public static ValidateResult validateLen(Method method, Object dataModel)
            throws InvocationTargetException, IllegalAccessException {

        Annotation annotation = method.getAnnotation(Length.class);
        if (annotation == null)
            return ValidateResult.success();
        Object obj = method.invoke(dataModel);
        Length length = ((Length) annotation);
        String str = "";
        if (!StringUtils.isEmpty(obj))
            str = obj.toString();

        if (str.length() < length.minLen() || str.length() > length.maxLen()) {
            return ValidateResult.failedOfOutLen(length.msg(), method.getName().substring(3));
        }
        return ValidateResult.success();
    }

    public static ValidateResult validateRequired(Method method, Object dataModel)
            throws InvocationTargetException, IllegalAccessException {
        Annotation annotation = method.getAnnotation(Required.class);
        if (annotation == null)
            return ValidateResult.success();
        Object obj = method.invoke(dataModel);
        if (StringUtils.isEmpty(obj)) {
            Required required = ((Required) annotation);
            return ValidateResult.failedOfNull(
                    formatValidateMsg(required.filedName(), required.msg(), Required.MSG)
                    , method.getName().substring(3));
        }
        return ValidateResult.success();
    }

    public static String formatValidateMsg(String filedName, String msg, String def) {
        if (StringUtils.isEmpty(msg))
            return filedName + def;
        return msg;
    }

}
