package com.yihu.ehr.profile.memory.intermediate;

/**
 * HBase数据库中健康档案表选项. 如列族名称等.
 * 健康档案列族结构: |basic|extension|
 * 健康事件列族结构: |basic|extension|
 * 数据集列族结构: |basic|meta_data|extension|
 * @author Sand
 * @created 2016.04.27 17:06
 */
public class ProfileFamily{
    // 列族
    public static final String Basic = "basic";
    public static final String Extension = "extension";

    public static class BasicColumns {
        public static final String CardId = "card_id";
        public static final String OrgCode = "org_code";
        public static final String PatientId = "patient_id";
        public static final String EventNo = "event_no";
        public static final String EventDate = "event_date";
        public static final String EventType = "event_type";
        public static final String ProfileType = "profile_type";
        public static final String DemographicId = "demographic_id";
        public static final String ClientId = "client_id";
        public static final String CreateDate = "create_date";
        public static final String DataSets = "data_sets";
        public static final String Files = "files";
        public static final String CdaVersion = "inner_version";
    }

    /**
     * 获取列族.
     *
     * @return
     */
    public static String[] getFamilies() {
        return new String[]{
                ProfileFamily.Basic,
                ProfileFamily.Extension
        };
    }

    /**
     * 获取指定族的列.
     *
     * @return
     */
    public static String[] getColumns(String family) {
        if (family.equals(ProfileFamily.Basic)) {
            return new String[]{
                    BasicColumns.CardId,
                    BasicColumns.OrgCode,
                    BasicColumns.PatientId,
                    BasicColumns.EventNo,
                    BasicColumns.EventDate,
                    BasicColumns.DemographicId,
                    BasicColumns.ClientId,
                    BasicColumns.CreateDate,
                    BasicColumns.DataSets,
                    BasicColumns.CdaVersion
            };
        }

        return null;
    }
}
