package com.yihu.ehr.profile.annotation;


import com.yihu.ehr.profile.core.ProfileFamily;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Sand
 * @created 2016.05.03 15:27
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String value();

    String family();
}
