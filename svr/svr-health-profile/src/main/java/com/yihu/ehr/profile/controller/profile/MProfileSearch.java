package com.yihu.ehr.profile.controller.profile;

/**
 * @author Sand
 * @created 2016.04.29 10:05
 */
class MProfileSearch {
    private String demographicId;
    private String organizationCode;
    private String patientId;
    private String eventNo;
    private String name;
    private String telephone;
    private String gender;
    private String birthday;
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
