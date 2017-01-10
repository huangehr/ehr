package com.yihu.ehr.profile.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.lang.SpringContext;

import java.util.*;

/**
 * 档案包数据集。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 11:13
 */
public class PackageDataSet {
    protected String code;
    protected String name;
    protected String patientId;
    protected String eventNo;
    protected String orgCode;
    protected String cdaVersion;
    protected Date eventTime;
    protected Date createTime;
    protected boolean isMultiRecord = false;

    protected Map<String, MetaDataRecord> records = new TreeMap<>();

    public String getCode() {
        return code;
    }

    public void setCode(String dataSetCode) {
        this.code = dataSetCode;
    }

    public String getCdaVersion() {
        return cdaVersion;
    }

    public void setCdaVersion(String cdaVersion) {
        this.cdaVersion = cdaVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Set<String> getRecordKeys() {
        return this.records.keySet();
    }

    public void addRecord(String recordKey, MetaDataRecord record) {
        this.records.put(recordKey, record);
    }

    public MetaDataRecord getRecord(String recordKey) {
        return this.records.get(recordKey);
    }

    public Map<String, MetaDataRecord> getRecords() {
        return records;
    }

    public int getRecordCount(){
        return records.size();
    }

    public void updateRecordKey(String origin, String newer) {
        MetaDataRecord record = this.records.remove(origin);
        if (record != null) {
            this.records.put(newer, record);
        }
    }

    public JsonNode toJson() {
        ObjectMapper objectMapper = SpringContext.getService(ObjectMapper.class);

        return dataSetBody(dataSetHeader(objectMapper));
    }

    protected ObjectNode dataSetHeader(ObjectMapper objectMapper){
        ObjectNode root = objectMapper.createObjectNode();
        root.put("code", code);
        root.put("patient_id", patientId);
        root.put("event_no", eventNo);
        root.put("org_code", orgCode);

        return root;
    }

    protected ObjectNode dataSetBody(ObjectNode root){
        ObjectNode records = root.putObject("records");
        for (String rowKey : this.records.keySet()) {
            MetaDataRecord record = this.records.get(rowKey);
            if (record == null){
                records.put(rowKey, (String)null);
            }else {
                ObjectNode metaData = records.putObject(rowKey);

                Set<String> codes = record.getMetaDataCodes();
                for (String code : codes) {
                    metaData.put(code, record.getMetaData(code));
                }
            }
        }

        return root;
    }

    public boolean isMultiRecord() {
        return isMultiRecord;
    }

    public void setMultiRecord(boolean multiRecord) {
        isMultiRecord = multiRecord;
    }
}
