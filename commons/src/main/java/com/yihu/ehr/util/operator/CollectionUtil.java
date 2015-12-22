package com.yihu.ehr.util.operator;

import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;

/**
 * Hibernate Criteria utils
 *
 * @since 1.0.6
 */
public class CollectionUtil {

    public static boolean isEmpty(Collection coll) {

        return CollectionUtils.isEmpty(coll);
    }
}
