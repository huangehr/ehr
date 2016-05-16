package com.yihu.ehr.profile.memory.intermediate;

/**
 * 数据集表列族。
 *
 * @author Sand
 * @created 2016.04.27 16:56
 */
public class DataSetFamily {
    public static final String Basic = "basic";
    public static final String MetaData = "meta_data";
    public static final String Extension = "extension";

    // Basic列
    public static class BasicColumns {
        public static final String ProfileId = "archive_id";
        public static final String CdaVersion = "inner_version";
        public static final String OrgCode = "org_code";
        public static final String LastUpdateTime = "last_update_time";
    }
}
