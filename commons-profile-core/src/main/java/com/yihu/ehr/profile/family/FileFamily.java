package com.yihu.ehr.profile.family;

/**
 * @author Sand
 * @created 2016.05.05 18:08
 */
public class FileFamily extends ResourceFamily {
    public static class BasicColumns {
        public static final String OrgCode = "org_code";
        public static final String PatientId = "patient_id";
        public static final String EventNo = "event_no";
    }

    public static class ResourceColumns{
        public static final String CdaDocumentId = "cda_document_id";
        public static final String CdaDocumentName = "cda_document_name";
        public static final String FileList = "file_list";
    }
}
