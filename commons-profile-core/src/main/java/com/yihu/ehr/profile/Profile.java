package com.yihu.ehr.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.util.DateFormatter;

import java.util.*;

/**
 * 健康档案。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 10:44
 */
public class Profile {
    private ObjectMapper objectMapper = SpringContext.getService("objectMapper");

    private ProfileId archiveID;                        // 健康档案ID
    private String cardId;                              // 就诊时用的就诊卡ID
    private String orgCode;                             // 机构代码
    private String orgName;                             // 机构名称
    private String patientId;                           // 身份证号
    private String eventNo;                             // 事件号
    private Date eventDate;                             // 事件时间，如挂号，出院体检时间
    private String demographicId;                       // 人口学ID
    private String summary;                             // 档案摘要

    private Date createDate;                            // EhrArchive创建时间，由JSON包中提取
    private String cdaVersion;

    // 档案包含的数据集, key 为数据集的表名, 标准数据情况下, 表名为数据集代码, 原始数据集情况下, 表名为"数据集代码_ORIGIN"
    private Map<String, ProfileDataSet> dataSets;

    public Profile() {
        this.cardId = "";
        this.orgCode = "";
        this.patientId = "";
        this.eventNo = "";
        this.dataSets = new TreeMap<>();
    }
    
    public String getId() {
        if (archiveID == null){
            if(orgCode.length() == 0 || patientId.length() == 0 || eventNo.length() == 0){
                throw new IllegalArgumentException("构建档案ID失败, 机构代码或病人索引为空.");
            }

            if(eventDate == null) throw new IllegalArgumentException("构建档案ID失败, 事件时间为空.");

            this.archiveID = ProfileId.get(orgCode, patientId, eventNo, eventDate);
        }

        return archiveID.toString();
    }

    public void setId(String archiveId){
        this.archiveID = new ProfileId(archiveId);
    }
    
    public Collection<ProfileDataSet> getDataSets() {
        return dataSets.values();
    }

    public String getDataSetsAsString() {
        ObjectMapper objectMapper = SpringContext.getService("objectMapper");
        ObjectNode rootNode = objectMapper.createObjectNode();

        for (String key : dataSets.keySet()) {
            Set<String> rowKeys = dataSets.get(key).getRecordKeys();
            String records = String.join(",", rowKeys);
            rootNode.put(key, records);
        }

        return rootNode.toString();
    }

    public void addDataSet(String dataSetCode, ProfileDataSet dataSet) {
        this.dataSets.put(dataSetCode, dataSet);
    }

    public void removeDataSet(String dataSetCode){
        this.dataSets.remove(dataSetCode);
    }

    public ProfileDataSet getDataSet(String dataSetCode) {
        return this.dataSets.get(dataSetCode);
    }

    public String getCdaVersion() {
        return cdaVersion;
    }

    public void setCdaVersion(String cdaVersion) {
        this.cdaVersion = cdaVersion;
    }

    public Set<String> getDataSetTables(){
        return this.dataSets.keySet();
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
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String toJson(){
        ObjectNode root = objectMapper.createObjectNode();
        root.put("id", getId().toString());
        root.put("card_id", cardId);
        root.put("org_code", orgCode);
        root.put("org_name", orgName);
        root.put("patient_id", patientId);
        root.put("event_no", eventNo);
        root.put("event_date", eventDate == null ? "" : DateFormatter.utcDateTimeFormat(eventDate));
        root.put("cda_version", cdaVersion);
        root.put("create_date", createDate == null ? "" : DateFormatter.utcDateTimeFormat(createDate));
        root.put("summary", summary);

        ArrayNode dataSetsNode = root.putArray("data_sets");
        for (String dataSetCode : dataSets.keySet()){
            ProfileDataSet dataSet = dataSets.get(dataSetCode);
            dataSetsNode.addPOJO(dataSet.toJson(false));
        }

        return root.toString();
    }
}
