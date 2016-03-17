package com.yihu.ehr.cache;

import com.yihu.ehr.constants.RedisNamespace;
import com.yihu.ehr.util.StringBuilderUtil;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.09.19 12:02
 */
public class FoundationDataKeySchema {
    public static String makeOrganizationKey(String code){
        return new StringBuilderUtil("%1%2").arg(RedisNamespace.FoundationDataOrg).arg(code).toString();
    }
}
