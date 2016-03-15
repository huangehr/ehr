package com.yihu.ehr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.cache.CachedDataSet;
import com.yihu.ehr.cache.CachedOrganization;
import com.yihu.ehr.cache.FoundationDataRedisCache;
import com.yihu.ehr.cache.StdDataRedisCache;
import com.yihu.ehr.lang.SpringContext;

import java.util.*;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 11:13
 */
public class ProfileDataSet {
    private String code;
    private String name;
    private String patientId;
    private String eventNo;
    private String orgCode;
    private String orgName;
    private String cdaVersion;
    private List<String> originDocUrls = new ArrayList<>();

    private Map<String, Map<String, String>> records;

    private ObjectMapper objectMapper = SpringContext.getService("objectMapper");

    public ProfileDataSet() {
        records = new HashMap<>();
    }
    
    public Set<String> getRecordKeys() {
        return this.records.keySet();
    }

    
    public String getCode() {
        return code;
    }

    public void setCode(String dataSetCode) {
        assert cdaVersion != null & cdaVersion.length() > 0;

        this.code = dataSetCode;

        String tempCode = StdObjectQualifierTranslator.isOriginDataSet(dataSetCode) ?
                dataSetCode.substring(0, dataSetCode.lastIndexOf(StdObjectQualifierTranslator.OriginDataSetFlag)) : dataSetCode;
        CachedDataSet dataSet = StdDataRedisCache.getDataSet(cdaVersion, tempCode);
        name = dataSet == null ? "" : dataSet.name;
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

    
    public String getOrgCode() {
        return orgCode;
    }

    
    public String getOrgName() {
        return orgName;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;

        CachedOrganization organization = FoundationDataRedisCache.getOrganization(orgCode);
        orgName = organization == null ? "" : organization.name;
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

    
    public void updateRecordKey(String origin, String newer) {
        Map<String, String> record = this.records.remove(origin);
        if (record != null) {
            this.records.put(newer, record);
        }
    }

    
    public List<String> getOriginDocumentURL() {
        return null;
    }

    public JsonNode toJson(boolean simplified) {
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
            root.put("name", name);
            root.put("patient_id", patientId);
            root.put("event_no", eventNo);
            root.put("org_code", orgCode);
            root.put("org_name", orgName);
            root.put("origin_doc_url", String.join(";", originDocUrls));

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
