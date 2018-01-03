package com.yihu.ehr.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.28 15:29
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapping {
    /**
     * 列
     * @return
     */
    String value() default "";

    String key() default "";
}
