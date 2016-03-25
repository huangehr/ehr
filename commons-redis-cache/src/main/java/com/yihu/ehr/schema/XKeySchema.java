package com.yihu.ehr.schema;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.25 18:11
 */
public interface XKeySchema {
    String makeKey(String table, String value, String column);
}
