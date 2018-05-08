package com.yihu.ehr.profile.family;

/**
 * 子资源列族结构: |basic|resource|extension|。
 *
 * @author Sand
 * @created 2016.04.27 16:56
 */
public class SubResourceFamily extends ResourceFamily {
    // Basic列族
    public static class BasicColumns {
        public final static String ProfileId = "profile_id";
        public final static String OrgCode = "org_code";
        public final static String OrgArea = "org_area";
        public final static String EventType = "event_type";
        public final static String EventNo = "event_no";
        public final static String EventDate = "event_date";
    }
}
