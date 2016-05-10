package com.yihu.ehr.profile.core;

/**
 * @author Sand
 * @created 2016.05.05 18:08
 */
public class FileFamily {
    // 列族
    public static final String Basic = "basic";
    public static final String Files = "files";
    public static final String Extension = "extension";

    public static class BasicColumns {
        public static final String ProfileId = "archive_id";
        public static final String CdaVersion = "inner_version";
        public static final String OrgCode = "org_code";
        public static final String PatientId = "patient_id";
        public static final String EventNo = "event_no";
    }

    public static class FileColumns{
        public static final String CdaDocumentId = "cda_document_id";
        public static final String OriginUrl = "origin_url";
        public static final String ExpireDate = "expire_date";
        public static final String Files = "files";
    }

    /**
     * 获取列族.
     *
     * @return
     */
    public static String[] getFamilies() {
        return new String[]{
                FileFamily.Basic,
                FileFamily.Files,
                FileFamily.Extension
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
                    BasicColumns.ProfileId,
                    BasicColumns.CdaVersion,
                    BasicColumns.OrgCode
            };
        }

        return null;
    }
}
