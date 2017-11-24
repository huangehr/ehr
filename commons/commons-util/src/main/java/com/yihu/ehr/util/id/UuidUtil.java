package com.yihu.ehr.util.id;

import java.util.UUID;

/**
 * @author 张进军
 * @date 2017/11/16 08:58
 */
public class UuidUtil {

    public static String randomUUID () {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
