package com.yihu.ehr.profile.core.slice;

import java.util.Date;

/**
 * 诊断。
 *
 * @author Sand
 * @created 2016.04.25 16:55
 */
public class Diagnosis {
    String icd10Code;           // ICD10代码
    String icdName;             // ICD10名称
    String diseaseName;         // 疾病名称
    Date date;                  // 下诊断时间
}
