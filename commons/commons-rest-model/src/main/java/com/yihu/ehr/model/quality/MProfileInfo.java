package com.yihu.ehr.model.quality;

public class MProfileInfo {

    private String data;//就诊日期
    private Integer inpatient;//住院人次数
    private Integer outpatient;//门诊人次数
    private Integer healExam;//体检人次数
    private Integer total;//就诊总人次

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getInpatient() {
        return inpatient;
    }

    public void setInpatient(Integer inpatient) {
        this.inpatient = inpatient;
    }

    public Integer getOutpatient() {
        return outpatient;
    }

    public void setOutpatient(Integer outpatient) {
        this.outpatient = outpatient;
    }

    public Integer getHealExam() {
        return healExam;
    }

    public void setHealExam(Integer healExam) {
        this.healExam = healExam;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
