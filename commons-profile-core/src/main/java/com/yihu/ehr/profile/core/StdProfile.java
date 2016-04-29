package com.yihu.ehr.profile.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.profile.extractor.EventExtractor;
import com.yihu.ehr.profile.extractor.KeyDataExtractor;
import com.yihu.ehr.util.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.*;

/**
 * 标准健康档案（结构化）。
 *
 * @author Sand
 * @created 2015.08.16 10:44
 */
public class StdProfile {
    protected ObjectMapper objectMapper = SpringContext.getService("objectMapper");

    private ProfileId profileId;                        // 健康档案ID
    private String cardId;                              // 就诊时用的就诊卡ID
    private String orgCode;                             // 机构代码
    private String clientId;                            // 应用来源
    private String patientId;                           // 身份证号
    private String eventNo;                             // 事件号
    private Date eventDate;                             // 事件时间，如挂号，出院体检时间
    private String demographicId;                       // 人口学ID
    private Date createDate;                            // 包创建时间
    private String cdaVersion;
    private ProfileType profileType;
    private EventType eventType;

    protected Map<String, StdDataSet> dataSets = new TreeMap<>();

    public StdProfile(){
        cardId = "";
        orgCode = "";
        patientId = "";
        eventNo = "";

        this.setProfileType(ProfileType.Standard);
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

    public String getId() {
        if (profileId == null) {
            if (StringUtils.isEmpty(orgCode)) {
                throw new IllegalArgumentException("Build profile id failed, organization code is empty.");
            }

            if (StringUtils.isEmpty(patientId) || StringUtils.isEmpty(eventNo)) {
                throw new IllegalArgumentException("Build profile id failed, patient index is empty.");
            }

            if (eventDate == null) {
                throw new IllegalArgumentException("Build profile id failed, unable to get event date.");
            }

            this.profileId = ProfileId.get(orgCode, patientId, eventNo, eventDate);
        }

        return profileId.toString();
    }

    public void setId(String archiveId) {
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

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getEventNo() {
        return eventNo;
    }

    public void setEventNo(String eventNo) {
        this.eventNo = eventNo;
    }

    public String getDemographicId() {
        return demographicId;
    }

    public void setDemographicId(String demographicId) {
        this.demographicId = demographicId;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date date) {
        this.eventDate = date;
    }

    public Date getCreateDate() {
        if (createDate == null) createDate = new Date();

        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void insertDataSet(String dataSetCode, StdDataSet dataSet) {
        this.dataSets.put(dataSetCode, dataSet);
    }

    public StdDataSet getDataSet(String dataSetCode) {
        return this.dataSets.get(dataSetCode);
    }

    public Collection<StdDataSet> getDataSets() {
        return dataSets.values();
    }

    public String getDataSetIndices() {
        ObjectNode rootNode = objectMapper.createObjectNode();

        for (String dataSetCode : dataSets.keySet()) {
            Set<String> recordKeys = dataSets.get(dataSetCode).getRecordKeys();
            rootNode.put(dataSetCode, String.join(",", recordKeys));
        }

        return rootNode.toString();
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String toJson(){
        ObjectNode root = objectMapper.createObjectNode();
        root.put("id", getId().toString());
        root.put("card_id", this.getCardId());
        root.put("org_code", this.getOrgCode());
        root.put("patient_id", this.getPatientId());
        root.put("event_no", this.getEventNo());
        root.put("event_date", this.getEventDate() == null ? "" : DateTimeUtils.utcDateTimeFormat(this.getEventDate()));
        root.put("cda_version", this.getCdaVersion());
        root.put("create_date", this.getCreateDate() == null ? "" : DateTimeUtils.utcDateTimeFormat(this.getCreateDate()));
        root.put("event_type", this.getEventType().toString());

        ObjectNode dataSetsNode = root.putObject("data_sets");
        for (String dataSetCode : dataSets.keySet()){
            StdDataSet dataSet = dataSets.get(dataSetCode);
            dataSetsNode.putPOJO(dataSetCode, dataSet.toJson());
        }

        return root.toString();
    }

    public void regularRowKey() {
        for (String dataSetCode : dataSets.keySet()) {
            StdDataSet dataSet = dataSets.get(dataSetCode);

            int rowIndex = 0;
            String sortFormat = dataSet.getRecordCount() > 10 ? "%s$%03d" : "%s$%1d";
            String[] rowKeys = dataSet.getRecordKeys().toArray(new String[dataSet.getRecordCount()]);
            for (String rowKey : rowKeys) {
                dataSet.updateRecordKey(rowKey, String.format(sortFormat, getId(), rowIndex++));
            }
        }
    }

    public void determineEventType() throws ParseException {
        if (getEventType() != null) return;

        EventExtractor eventExtractor = SpringContext.getService(EventExtractor.class);
        for (String dataSetCode : dataSets.keySet()) {
            StdDataSet dataSet = dataSets.get(dataSetCode);
            EventType eventType = (EventType) eventExtractor.extract(dataSet, KeyDataExtractor.Filter.EventType);
            if (eventType != null) {
                setEventType(eventType);
                break;
            }
        }
    }
}
