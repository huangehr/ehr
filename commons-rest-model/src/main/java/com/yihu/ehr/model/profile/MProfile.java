package com.yihu.ehr.model.profile;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.commons.lang.StringUtils;

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
    private String summary;
    private Date eventDate;

    private List<MDocument> documents = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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

    public List<MDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<MDocument> documents) {
        this.documents = documents;
    }

    public String getDemographicId() {
        return demographicId;
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
}
