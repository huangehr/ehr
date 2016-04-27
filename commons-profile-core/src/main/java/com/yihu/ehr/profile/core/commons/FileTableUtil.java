package com.yihu.ehr.profile.core.commons;

/**
 * @author linaz
 * @created 2016.04.15
 */
public class FileTableUtil {
    public static final String Table = "UnStructuredDocuments";

    // 列族
    public enum Family {
        Basic("basic"),
        Document("document"),
        Extension("extension");

        private String family;

        Family(String family) {
            this.family = family;
        }

        public String toString() {
            return family;
        }
    }

    // 列
    public enum BasicQualifier {
        ProfileId("archive_id"),
        InnerVersion("inner_version"),
        LastUpdateTime("last_update_time");

        private String qualifier;

        BasicQualifier(String qualifier) {
            this.qualifier = qualifier;
        }

        public String toString() {
            return qualifier;
        }
    }


    // 列
    public enum DocumentQualifier{
        OrgCode("org_code"),
        PatientId("patient_id"),
        EventNo("event_no"),
        EventDate("event_date"),
        CdaVersion("inner_version"),
        DocumentsJson("documents_json");
        private String qualifier;
        DocumentQualifier(String qualifier){
            this.qualifier = qualifier;
        }
        public String toString(){
            return qualifier;
        }
    }

    public static String[] getQualifiers(Family family) {
        if (family == Family.Basic) {
            return new String[]{
                    BasicQualifier.ProfileId.toString(),
                    BasicQualifier.InnerVersion.toString(),
                    BasicQualifier.LastUpdateTime.toString()
            };
        }else if(family == Family.Document){
            return new String[]{
                    DocumentQualifier.OrgCode.toString(),
                    DocumentQualifier.PatientId.toString(),
                    DocumentQualifier.EventNo.toString(),
                    DocumentQualifier.EventDate.toString(),
                    DocumentQualifier.CdaVersion.toString(),
                    DocumentQualifier.DocumentsJson.toString(),
            };
        }

        return null;
    }
}
