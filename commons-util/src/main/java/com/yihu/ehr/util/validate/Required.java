package com.yihu.ehr.util.validate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/3/2
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Required {
    public static String MSG = "不能为空！";

    String filedName();
    String msg() default "";
}
