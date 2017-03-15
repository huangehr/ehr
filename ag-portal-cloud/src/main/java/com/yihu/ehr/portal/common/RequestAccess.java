package com.yihu.ehr.portal.common;

import java.lang.annotation.*;

/**
 * 自定义注解 拦截Controller
 * @author hzp
 * @since 20170314
 */

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public  @interface RequestAccess {

    String value()  default "";

    boolean check() default true;
}