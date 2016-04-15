package com.yihu.ehr.profile.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.lang.SpringContext;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 健康档案基类。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 10:44
 */
public class Profile {
    protected ObjectMapper objectMapper = SpringContext.getService("objectMapper");

    protected ProfileId profileId;                        // 健康档案ID
    protected String cardId;                              // 就诊时用的就诊卡ID
    protected String orgCode;                             // 机构代码
    protected String orgName;                             // 机构名称
    protected String patientId;                           // 身份证号
    protected String eventNo;                             // 事件号
    protected String demographicId;                       // 人口学ID
    protected String summary;                             // 档案摘要
    protected Date eventDate;                             // 事件时间，如挂号，出院体检时间
    protected Date createDate;                            // 档案创建时间，由JSON包中提取
    protected String cdaVersion;

    public String getId() {
        if (profileId == null){
            if(StringUtils.isEmpty(orgCode)){
                throw new IllegalArgumentException("Build profile id failed, organization code is empty.");
            }

            if (StringUtils.isEmpty(patientId) || StringUtils.isEmpty(eventNo)){
                throw new IllegalArgumentException("Build profile id failed, patient index is empty.");
            }

            if(eventDate == null){
                throw new IllegalArgumentException("Build profile id failed, unable to get event time.");
            }

            this.profileId = ProfileId.create(orgCode, patientId, eventNo, eventDate);
        }

        return profileId.toString();
    }

    public void setId(String archiveId){
        this.profileId = new ProfileId(archiveId);
    }

    public String getCdaVersion() {
        return cdaVersion;
    }

    public void setCdaVersion(String cdaVersion) {
        this.cdaVersion = cdaVersion;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode){
        this.orgCode = orgCode;
    }

    public String getOrgName(){
        return orgName;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId){
        this.patientId = patientId;
    }

    public String getEventNo(){
        return eventNo;
    }

    public void setEventNo(String eventNo){
        this.eventNo = eventNo;
    }

    public String getDemographicId(){
        return demographicId;
    }

    public void setDemographicId(String demographicId) {
        this.demographicId = demographicId;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date date){
        this.eventDate = date;
    }

    public String getSummary(){
        return summary;
    }

    public void setSummary(String summary){
        this.summary = summary;
    }

    public Date getCreateDate() {
        if (createDate == null) createDate = new Date();

        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String toJson(){
        return "";
    }

    public ProfileType getType(){
        return null;
    }
}
