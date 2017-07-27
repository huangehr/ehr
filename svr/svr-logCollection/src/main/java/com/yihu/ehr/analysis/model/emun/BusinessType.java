package com.yihu.ehr.analysis.model.emun;

/**
 * Created by Administrator on 2017/2/9.
 * 业务日志类型
 */
public enum BusinessType {
    Consult("0"),//咨询
    Guidance("1"), // 指导
    Article("2"), // 健康教育
    Followup("3"),// 随访
    Appointment("4"), // 预约
    Label("5"),// 标签
    Register("6"),// 注册
    Archive("7"),// 健康档案
    Sign("8");// 签约

    private String value;

    BusinessType(String s) {
        this.value = s;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
