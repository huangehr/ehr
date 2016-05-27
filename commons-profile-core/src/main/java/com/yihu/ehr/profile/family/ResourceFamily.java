package com.yihu.ehr.profile.family;

/**
 * @author Sand
 * @created 2016.05.17 10:33
 */
public class ResourceFamily {
    public static final String Basic = "basic";
    public static final String Data = "d";
    public static final String Extension = "ext";

    /**
     * 获取列族.
     *
     * @return
     */
    public static String[] getFamilies() {
        return new String[]{
                ResourceFamily.Basic,
                ResourceFamily.Data,
                ResourceFamily.Extension
        };
    }
}
