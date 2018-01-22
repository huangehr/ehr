package com.yihu.ehr.analyze.service.pack;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 档案包数据集。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 11:13
 */
public class DataSetRecord {

    protected String version;
    private String code;
    private String patientId;
    private String eventNo;
    private String orgCode;
    private String eventType;
    private Date eventTime;
    private Date createTime;
    private boolean isMultiRecord = false;
    private boolean reUploadFlg = false;
    private Map<String, DataElementRecord> records = new TreeMap<>();

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getRowKeyPrefix() {
        return orgCode + "_" + patientId + "_" + eventNo + "_" + version;
    }

    public String genRowKey(String index) {
        Integer idx = Integer.valueOf(index);
        String sortFormat = "%s$%04d";
        return String.format(sortFormat, getRowKeyPrefix(), idx);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String dataSetCode) {
        this.code = dataSetCode;
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

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    public boolean isMultiRecord() {
        return isMultiRecord;
    }

    public void setMultiRecord(boolean multiRecord) {
        isMultiRecord = multiRecord;
    }

    public boolean isReUploadFlg() {
        return reUploadFlg;
    }

    public void setReUploadFlg(boolean reUploadFlg) {
        this.reUploadFlg = reUploadFlg;
    }

    public void addRecord(String recordKey, DataElementRecord record) {
        this.records.put(recordKey, record);
    }

    public Set<String> getRecordKeys() {
        return this.records.keySet();
    }

    public DataElementRecord getRecord(String recordKey) {
        return this.records.get(recordKey);
    }

    public Map<String, DataElementRecord> getRecords() {
        return records;
    }

    public int getRecordCount() {
        return records.size();
    }

    public void updateRecordKey(String origin, String newer) {
        DataElementRecord record = this.records.remove(origin);
        if (record != null) {
            this.records.put(newer, record);
        }
    }

}
