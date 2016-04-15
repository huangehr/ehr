package com.yihu.ehr.profile.core.commons;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.lang.SpringContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 档案数据集。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 11:13
 */
public class DataSet {
    private String code;
    private String name;
    private String patientId;
    private String eventNo;
    private String orgCode;
    private String orgName;
    private String cdaVersion;

    private Map<String, Map<String, String>> records = new HashMap<>();


    public Set<String> getRecordKeys() {
        return this.records.keySet();
    }

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

    public String getOrgName() {
        return orgName;
    }
    public void setOrgName(String orgName) {
        this.orgName = orgName;
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

    public void addRecord(String recordKey, Map<String, String> record) {
        this.records.put(recordKey, record);
    }

    public Map<String, String> getRecord(String recordKey) {
        return this.records.get(recordKey);
    }

    public Map<String, Map<String, String>> getRecords() {
        return records;
    }

    public void updateRecordKey(String origin, String newer) {
        Map<String, String> record = this.records.remove(origin);
        if (record != null) {
            this.records.put(newer, record);
        }
    }

    public JsonNode toJson(boolean simplified) {
        ObjectMapper objectMapper = SpringContext.getService("objectMapper");

        if (simplified) {
            ObjectNode root = objectMapper.createObjectNode();
            ArrayNode rows = root.putArray(code);

            for (String rowKey : this.records.keySet()) {
                Map<String, String> dataRecord = this.records.get(rowKey);
                if (dataRecord == null) continue;

                ObjectNode row = rows.addObject();

                Set<String> qualifiers = new TreeSet<>(dataRecord.keySet());
                for (String qualifier : qualifiers) {
                    String innerCode = qualifier.substring(0, qualifier.lastIndexOf('_'));
                    row.put(innerCode, dataRecord.get(qualifier));
                }
            }

            return root;
        } else {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("code", code);
            root.put("patient_id", patientId);
            root.put("event_no", eventNo);
            root.put("org_code", orgCode);

            ArrayNode arrayNode = root.putArray("records");
            for (String rowKey : this.records.keySet()) {
                ObjectNode row = arrayNode.addObject();
                row.put("row_key", rowKey);

                Map<String, String> dataRecord = this.records.get(rowKey);
                if (dataRecord == null) continue;

                ObjectNode data = row.putObject("data");

                Set<String> qualifiers = new TreeSet<>(dataRecord.keySet());
                for (String qualifier : qualifiers) {
                    String innerCode = qualifier.substring(0, qualifier.lastIndexOf('_'));
                    data.put(innerCode, dataRecord.get(qualifier));
                }
            }

            return root;
        }
    }
}
