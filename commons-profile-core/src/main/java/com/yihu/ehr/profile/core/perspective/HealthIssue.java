package com.yihu.ehr.profile.core.perspective;


/**
 * 健康问题。
 *
 * @author Sand
 * @created 2016.04.25 16:55
 */
public class HealthIssue {
    String diseaseId;
    String diseaseName;
    int duration;                   // 病龄
    int treatmentTimes;             // 就诊次数（包括随访，门诊及住院）
    int residentTimes;              // 住院次数

}
