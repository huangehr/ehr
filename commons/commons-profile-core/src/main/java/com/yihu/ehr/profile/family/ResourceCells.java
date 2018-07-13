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
    public static final String PROFILE_ID = "profile_id"; //档案主索引
    public static final String PROFILE_TYPE = "profile_type"; //档案类型
    public static final String EVENT_NO = "event_no"; //事件号
    public static final String EVENT_DATE = "event_date"; //事件时间
    public static final String EVENT_TYPE = "event_type"; //事件类型
    public static final String CARD_ID = "card_id"; //就诊卡号
    public static final String CARD_TYPE = "card_type"; //就诊卡类型
    public static final String PATIENT_ID = "patient_id"; //病人ID
    public static final String PATIENT_NAME = "patient_name"; //病人姓名
    public static final String DEMOGRAPHIC_ID = "demographic_id"; //身份证号码
    public static final String ORG_CODE = "org_code"; //机构编码
    public static final String ORG_NAME = "org_name"; //机构名称
    public static final String ORG_AREA = "org_area"; //机构地区
    public static final String CDA_VERSION = "cda_version"; //CDA版本
    public static final String CREATE_DATE = "create_date"; //创建时间
    public static final String DIAGNOSIS = "diagnosis"; //ICD10诊断代码
    public static final String DIAGNOSIS_NAME = "diagnosis_name"; //ICD10诊断名称
    public static final String HEALTH_PROBLEM = "health_problem"; //健康问题诊断代码
    public static final String HEALTH_PROBLEM_NAME = "health_problem_name"; //健康问题诊断名称
    public static final String DEPT_CODE = "dept_code"; //科室编码
    public static final String SUB_ROWKEYS = "sub_rowkeys"; //细表索引
    public static final String PATIENT_AGE = "patient_age"; //就诊年龄
    public static final String PATIENT_SEX = "patient_sex"; //患者性别

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
                        PATIENT_AGE,
                        PATIENT_SEX,
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
                        EVENT_DATE,
                        PATIENT_AGE,
                        PATIENT_SEX,
                        PATIENT_ID,
                        DEPT_CODE
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
