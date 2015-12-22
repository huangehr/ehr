package com.yihu.ehr.std.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标准化版本比较时要忽略的属性.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.07.06 12:53
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DifferIgnored {
    boolean value() default true;
}
