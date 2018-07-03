package com.yihu.ehr.profile.family;

import com.yihu.ehr.profile.ProfileType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by progr1mmer on 2018/6/9.
 */
public class ResourceCells {

    public static final String ROWKEY = "rowkey";

    //Basic
    public static final String PROFILE_ID = "profile_id";
    public static final String PROFILE_TYPE = "profile_type";
    public static final String EVENT_NO = "event_no";
    public static final String EVENT_DATE = "event_date";
    public static final String EVENT_TYPE = "event_type";
    public static final String CARD_ID = "card_id";
    public static final String CARD_TYPE = "card_type";
    public static final String PATIENT_ID = "patient_id";
    public static final String PATIENT_NAME = "patient_name";
    public static final String DEMOGRAPHIC_ID = "demographic_id";
    public static final String ORG_CODE = "org_code";
    public static final String ORG_NAME = "org_name";
    public static final String ORG_AREA = "org_area";
    public static final String CDA_VERSION = "cda_version";
    public static final String CREATE_DATE = "create_date";
    public static final String DIAGNOSIS = "diagnosis";
    public static final String DIAGNOSIS_NAME = "diagnosis_name";
    public static final String HEALTH_PROBLEM = "health_problem";
    public static final String HEALTH_PROBLEM_NAME = "health_problem_name";
    public static final String DEPT_CODE = "dept_code";
    public static final String SUB_ROWKEYS = "sub_rowkeys";

    //RawFiles
    public static final String CDA_DOCUMENT_ID = "cda_document_id";
    public static final String CDA_DOCUMENT_NAME = "cda_document_name";
    public static final String FILE_LIST = "file_list";


    public static List<String> getMasterBasicCell(ProfileType profileType) {
        switch (profileType) {
            case Standard:
                return new ArrayList<>(Arrays.asList(
                        PROFILE_TYPE,
                        EVENT_NO,
                        EVENT_DATE,
                        EVENT_TYPE,
                        CARD_ID,
                        CARD_TYPE,
                        PATIENT_ID,
                        PATIENT_NAME,
                        DEMOGRAPHIC_ID,
                        ORG_CODE,
                        ORG_NAME,
                        ORG_AREA,
                        CDA_VERSION,
                        CREATE_DATE,
                        DEPT_CODE,
                        DIAGNOSIS,
                        DIAGNOSIS_NAME,
                        HEALTH_PROBLEM,
                        HEALTH_PROBLEM_NAME
                ));
            case File:
                return new ArrayList<>(Arrays.asList(
                        PROFILE_TYPE,
                        EVENT_NO,
                        EVENT_DATE,
                        EVENT_TYPE,
                        PATIENT_ID,
                        PATIENT_NAME,
                        DEMOGRAPHIC_ID,
                        ORG_CODE,
                        ORG_NAME,
                        ORG_AREA,
                        CDA_VERSION,
                        CREATE_DATE,
                        FILE_LIST,
                        SUB_ROWKEYS
                ));
            case Link:
                return new ArrayList<>(Arrays.asList(
                        PROFILE_TYPE,
                        EVENT_NO,
                        EVENT_DATE,
                        EVENT_TYPE,
                        PATIENT_ID,
                        PATIENT_NAME,
                        DEMOGRAPHIC_ID,
                        ORG_CODE,
                        ORG_NAME,
                        ORG_AREA,
                        CDA_VERSION,
                        CREATE_DATE,
                        FILE_LIST,
                        SUB_ROWKEYS
                ));
            case Simple:
                return new ArrayList<>();
            default:
                return null;
        }
    }

    public static List<String> getSubBasicCell(ProfileType profileType) {
        switch (profileType) {
            case Standard:
                return new ArrayList<>(Arrays.asList(
                        ORG_CODE,
                        ORG_NAME,
                        ORG_AREA,
                        EVENT_TYPE,
                        EVENT_NO,
                        EVENT_DATE
                ));
            case File:
                return new ArrayList<>(Arrays.asList(
                        ORG_CODE,
                        ORG_NAME,
                        ORG_AREA,
                        EVENT_TYPE,
                        EVENT_NO,
                        EVENT_DATE
                ));
            case Link:
                return new ArrayList<>(Arrays.asList(
                        ORG_CODE,
                        ORG_NAME,
                        ORG_AREA,
                        EVENT_TYPE,
                        EVENT_NO,
                        EVENT_DATE
                ));
            case Simple:
                return new ArrayList<>();
            default:
                return null;
        }
    }

}
