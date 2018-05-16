package com.yihu.ehr.resolve.util;


import java.util.UUID;

/**
 * Created by progr1mmer on 2018/5/15.
 */
public class LocalTempPathUtil {

    private final static String TEMP_PATH = System.getProperty("java.io.tmpdir") + java.io.File.separator;

    public static String getTempPath() {
        return TEMP_PATH;
    }

    public static String getTempPathWithUUIDSuffix() {
        StringBuilder path = new StringBuilder();
        path.append(TEMP_PATH).append(UUID.randomUUID()).append("_");
        return path.toString();
    }
}
