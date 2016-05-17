package com.yihu.ehr.service.resource.stage1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.profile.annotation.Column;
import com.yihu.ehr.profile.annotation.Table;
import com.yihu.ehr.service.resource.stage1.extractor.EventExtractor;
import com.yihu.ehr.service.resource.stage1.extractor.KeyDataExtractor;
import com.yihu.ehr.profile.core.commons.EventType;
import com.yihu.ehr.profile.core.commons.ProfileType;
import com.yihu.ehr.service.resource.ProfileId;
import com.yihu.ehr.profile.family.MasterResourceFamily;
import com.yihu.ehr.service.resource.StdDataSet;
import com.yihu.ehr.service.util.ResourceStorageUtil;
import com.yihu.ehr.util.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.*;

/**
 * 在内存中构建一个临时的档案结构，将档案包的数据解析进去之后，再使用资源化工具入库。
 *
 * @author Sand
 * @created 2015.08.16 10:44
 */
@Table(ResourceStorageUtil.MasterTable)
public class StdPackModel {
    protected ObjectMapper objectMapper = SpringContext.getService("objectMapper");

    private ProfileId profileId;                        // 档案ID
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

    public StdPackModel() {
        cardId = "";
        orgCode = "";
        patientId = "";
        eventNo = "";

        setProfileType(ProfileType.Standard);
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

    public void setId(String id) {
        this.profileId = new ProfileId(id);
    }

    @Column(value = MasterResourceFamily.BasicColumns.ProfileType, family = MasterResourceFamily.Basic)
    public ProfileType getProfileType() {
        return profileType;
    }

    public void setProfileType(ProfileType profileType) {
        this.profileType = profileType;
    }

    @Column(value = MasterResourceFamily.BasicColumns.EventType, family = MasterResourceFamily.Basic)
    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    @Column(value = MasterResourceFamily.BasicColumns.CdaVersion, family = MasterResourceFamily.Basic)
    public String getCdaVersion() {
        return cdaVersion;
    }

    public void setCdaVersion(String cdaVersion) {
        this.cdaVersion = cdaVersion;
    }

    @Column(value = MasterResourceFamily.BasicColumns.CardId, family = MasterResourceFamily.Basic)
    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    @Column(value = MasterResourceFamily.BasicColumns.OrgCode, family = MasterResourceFamily.Basic)
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    @Column(value = MasterResourceFamily.BasicColumns.PatientId, family = MasterResourceFamily.Basic)
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @Column(value = MasterResourceFamily.BasicColumns.EventNo, family = MasterResourceFamily.Basic)
    public String getEventNo() {
        return eventNo;
    }

    public void setEventNo(String eventNo) {
        this.eventNo = eventNo;
    }

    @Column(value = MasterResourceFamily.BasicColumns.DemographicId, family = MasterResourceFamily.Basic)
    public String getDemographicId() {
        return demographicId;
    }

    public void setDemographicId(String demographicId) {
        this.demographicId = demographicId;
    }

    @Column(value = MasterResourceFamily.BasicColumns.EventDate, family = MasterResourceFamily.Basic)
    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date date) {
        this.eventDate = date;
    }

    @Column(value = MasterResourceFamily.BasicColumns.CreateDate, family = MasterResourceFamily.Basic)
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

    public String toJson() {
         return jsonFormat().toString();
    }

    protected ObjectNode jsonFormat() {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("id", getId().toString());
        root.put("cardId", this.getCardId());
        root.put("orgCode", this.getOrgCode());
        root.put("patientId", this.getPatientId());
        root.put("eventNo", this.getEventNo());
        root.put("cdaVersion", this.getCdaVersion());
        root.put("clientId", this.getClientId());
        root.put("eventDate", DateTimeUtils.utcDateTimeFormat(this.getEventDate()));
        root.put("createDate", DateTimeUtils.utcDateTimeFormat(this.getCreateDate()));
        root.put("eventType", this.getEventType().toString());
        root.put("profileType", this.getProfileType().toString());

        ObjectNode dataSetsNode = root.putObject("dataSets");
        for (String dataSetCode : dataSets.keySet()) {
            StdDataSet dataSet = dataSets.get(dataSetCode);
            dataSetsNode.putPOJO(dataSetCode, dataSet.toJson());
        }

        return root;
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
