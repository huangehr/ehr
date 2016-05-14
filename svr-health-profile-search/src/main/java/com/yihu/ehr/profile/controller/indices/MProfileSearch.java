package com.yihu.ehr.profile.controller.indices;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author Sand
 * @created 2016.04.29 10:05
 */
@ApiModel
class MProfileSearch {
    @ApiModelProperty(example = "412726195111306268")
    private String demographicId;

    @ApiModelProperty(example = "41872607-9")
    private String organizationCode;

    @ApiModelProperty(example = "10295435")
    private String patientId;

    @ApiModelProperty(example = "000622450")
    private String eventNo;

    @ApiModelProperty(example = "段廷兰")
    private String name;

    @ApiModelProperty(example = "11")
    private String telephone;

    @ApiModelProperty(example = "女")
    private String gender;

    @ApiModelProperty(example = "1951-11-30")
    private String birthday;

    @ApiModelProperty(example = "")
    private String cdaDocumentId;

    String getDemographicId() {
        return demographicId;
    }

    void setDemographicId(String demographicId) {
        this.demographicId = demographicId;
    }

    String getOrganizationCode() {
        return organizationCode;
    }

    void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    String getPatientId() {
        return patientId;
    }

    void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    String getEventNo() {
        return eventNo;
    }

    void setEventNo(String eventNo) {
        this.eventNo = eventNo;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getTelephone() {
        return telephone;
    }

    void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    String getGender() {
        return gender;
    }

    void setGender(String gender) {
        this.gender = gender;
    }

    String getBirthday() {
        return birthday;
    }

    void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCdaDocumentId() {
        return cdaDocumentId;
    }

    public void setCdaDocumentId(String cdaDocumentId) {
        this.cdaDocumentId = cdaDocumentId;
    }
}
