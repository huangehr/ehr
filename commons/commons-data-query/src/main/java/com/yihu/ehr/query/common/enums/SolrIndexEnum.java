package com.yihu.ehr.query.common.enums;

/**
 * Created by janseny on 2018/6/29.
 * store true 的索引
 */
public enum SolrIndexEnum {
    EHR_003905,// 就诊年龄（岁）   主表
    EHR_005013,// 住院年龄			主表
    EHR_000019,// 性别				主表
    EHR_000081,// 门诊科室 		主表
    EHR_000228,// 住院科室			主表
    EHR_000170,// 实际住院天数		主表
    EHR_000109,// 疾病					细表
    EHR_000131,// 药物名称（中药）     细表
    EHR_000175,// 住院费用金额(元)		细表
    EHR_000049,// 费用（门诊费用）		细表
    EHR_000293,// 诊断代   码 -医院	细表
    EHR_000045, //费用（门诊费用）		细表
    EHR_000051,//费用支付代码（门诊费用）细表
    EHR_000180;//费用支付代码（住院费用）细表

}
