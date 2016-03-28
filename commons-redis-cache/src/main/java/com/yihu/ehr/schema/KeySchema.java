package com.yihu.ehr.schema;

import com.yihu.ehr.util.StringBuilderUtil;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.25 14:03
 */
public class KeySchema {
    protected static final String KeySchema = "%1:%2:%3";

    protected static String makeKey(String table, String key, String column){
        return new StringBuilderUtil(KeySchema)
                .arg(table)
                .arg(key)
                .arg(column)
                .toString();
    }
}
