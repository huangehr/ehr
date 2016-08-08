package com.yihu.ehr.medicalRecords.model.DTO;

/**
 * Created by hzp on 2016/8/4.
 */
public class DictDTO {
    private String code;
    private String name;
    private String phoneticCode;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneticCode() {
        return phoneticCode;
    }

    public void setPhoneticCode(String phoneticCode) {
        this.phoneticCode = phoneticCode;
    }
}
