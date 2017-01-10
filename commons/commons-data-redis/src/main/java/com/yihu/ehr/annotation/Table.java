package com.yihu.ehr.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.28 15:29
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    String value() default "";
}
