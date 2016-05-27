package com.yihu.ehr.model.profile;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.yihu.ehr.constants.EventType;
import com.yihu.ehr.constants.ProfileType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.26 16:08
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MProfile {
    private String id;
    private String orgCode;
    private String orgName;
    private String demographicId;
    private String cdaVersion;
    private String clientId;
    private Date eventDate;
    private ProfileType profileType;
    private EventType eventType;

    private List<MProfileDocument> documents = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return StringUtils.isEmpty(orgName) ? "" : orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public List<MProfileDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<MProfileDocument> documents) {
        this.documents = documents;
    }

    public String getDemographicId() {
        return StringUtils.isEmpty(demographicId) ? "" : demographicId;
    }

    public void setDemographicId(String demographicId) {
        this.demographicId = demographicId;
    }

    public String getCdaVersion() {
        return cdaVersion;
    }

    public void setCdaVersion(String cdaVersion) {
        this.cdaVersion = cdaVersion;
    }

    public ProfileType getProfileType() {
        return profileType;
    }

    public void setProfileType(ProfileType profileType) {
        this.profileType = profileType;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
