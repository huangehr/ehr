package com.yihu.ehr.profile.family;

/**
 * 主资源HBase列族结构: |basic|resource|extension|。
 *
 * @author Sand
 * @created 2016.04.27 17:06
 */
public class MasterResourceFamily extends ResourceFamily {
    public static class BasicColumns {
        public static final String CardId = "card_id";
        public static final String OrgCode = "org_code";
        public static final String OrgName = "org_name";
        public static final String OrgArea = "org_area";
        public static final String PatientId = "patient_id";
        public static final String PatientName = "patient_name";  //新增add by hzp at 20170401
        public static final String CardType = "card_type";  //新增add by hzp at 20170411
        public static final String EventNo = "event_no";
        public static final String EventDate = "event_date";
        public static final String EventType = "event_type";
        public static final String ProfileType = "profile_type";
        public static final String DemographicId = "demographic_id";
        public static final String ClientId = "client_id";
        public static final String CreateDate = "create_date";
        public static final String CdaVersion = "cda_version";
    }

    /**
     * 获取指定族的列.
     *
     * @return
     */
    public static String[] getColumns(String family) {
        if (family.equals(Basic)) {
            return new String[]{
                    BasicColumns.CardId,
                    BasicColumns.CardType,
                    BasicColumns.OrgCode,
                    BasicColumns.PatientId,
                    BasicColumns.PatientName,
                    BasicColumns.EventNo,
                    BasicColumns.EventDate,
                    BasicColumns.EventType,
                    BasicColumns.ProfileType,
                    BasicColumns.DemographicId,
                    BasicColumns.ClientId,
                    BasicColumns.CreateDate,
                    BasicColumns.CdaVersion
            };
        }

        return null;
    }
}
