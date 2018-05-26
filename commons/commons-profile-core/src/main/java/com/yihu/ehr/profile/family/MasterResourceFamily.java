package com.yihu.ehr.profile.family;

/**
 * 主资源HBase列族结构: |basic|resource|extension|。
 *
 * @author Sand
 * @created 2016.04.27 17:06
 */
public class MasterResourceFamily extends ResourceFamily {
    public static class BasicColumns {
        public static final String ProfileType = "profile_type";
        public static final String EventNo = "event_no";
        public static final String EventDate = "event_date";
        public static final String EventType = "event_type";
        public static final String CardId = "card_id";
        public static final String CardType = "card_type";
        public static final String PatientId = "patient_id";
        public static final String PatientName = "patient_name";
        public static final String DemographicId = "demographic_id";
        public static final String OrgCode = "org_code";
        public static final String OrgName = "org_name";
        public static final String OrgArea = "org_area";
        public static final String CdaVersion = "cda_version";
        public static final String CreateDate = "create_date";
        public static final String ClientId = "client_id";
        public static final String Diagnosis = "diagnosis";
        public static final String DiagnosisName = "diagnosis_name";
        public static final String HealthProblem = "health_problem";
        public static final String HealthProblemName = "health_problem_name";
        public static final String DeptCode = "dept_code";
    }

}
