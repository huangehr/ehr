package com.yihu.ehr.profile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.cache.*;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.util.DateFormatter;
import com.yihu.ehr.util.ObjectId;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 档案数据集。
 *
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

    private final static Pattern NumberPattern = Pattern.compile("([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])");

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

    public static ProfileDataSet parseJsonDataSet(JsonNode jsonNode, boolean isOrigin) {
        ProfileDataSet dataSet = new ProfileDataSet();

        try {
            assert jsonNode != null;

            String cdaVersion = jsonNode.get("inner_version").asText();
            String code = jsonNode.get("code").asText();
            String eventNo = jsonNode.get("event_no").asText();
            String patientId = jsonNode.get("patient_id").asText();
            String orgCode = jsonNode.get("org_code").asText();
            String eventDate = jsonNode.path("event_time").asText();        // 旧数据包可能不存在这个属性

            dataSet.setPatientId(patientId);
            dataSet.setEventNo(eventNo);
            dataSet.setCdaVersion(cdaVersion);
            dataSet.setCode(code);
            dataSet.setOrgCode(orgCode);

            JsonNode jsonRecords = jsonNode.get("data");
            for (int i = 0; i < jsonRecords.size(); ++i) {
                Map<String, String> record = new HashMap<>();

                JsonNode jsonRecord = jsonRecords.get(i);
                Iterator<Map.Entry<String, JsonNode>> iterator = jsonRecord.fields();
                while (iterator.hasNext()) {
                    Map.Entry<String, JsonNode> item = iterator.next();
                    String key = item.getKey();
                    if (key.equals("PATIENT_ID") || key.equals("EVENT_NO")) continue;

                    if (key.contains("HDSA00_01_017") && !item.getValue().asText().contains("null")) {
                        System.out.println(item.getValue().asText());
                    }

                    String[] standardizedMetaData = standardizeMetaData(
                            cdaVersion,
                            code,
                            key,
                            item.getValue().isNull() ? "" : item.getValue().asText(),
                            isOrigin);
                    if (standardizedMetaData != null) {
                        record.put(standardizedMetaData[0], standardizedMetaData[1]);
                        if (standardizedMetaData.length > 2)
                            record.put(standardizedMetaData[2], standardizedMetaData[3]);
                    }
                }

                dataSet.addRecord(new ObjectId((short) 0, BizObject.StdProfile).toString(), record);
            }
        } catch (NullPointerException ex) {
            throw new RuntimeException("Null pointer occurs in JsonFileParser.generateDataSet");
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
        }

        return dataSet;
    }

    /**
     * 翻译数据元。
     *
     * @param innerVersion
     * @param dataSetCode
     * @param isOriginDataSet
     * @param metaDataInnerCode
     * @param actualData
     * @return
     */
    private static String[] standardizeMetaData(String innerVersion,
                                               String dataSetCode,
                                               String metaDataInnerCode,
                                               String actualData,
                                               boolean isOriginDataSet) {
        actualData = (actualData == null) ? "" : actualData.trim();

        CachedMetaData metaData = StdDataRedisCache.getMetaData(innerVersion, dataSetCode, metaDataInnerCode);
        if (null == metaData) {
            String msg = "Meta data %1 of data set %2 is NOT found in version %3. Please check the meta data."
                    .replace("%1", metaDataInnerCode)
                    .replace("%2", dataSetCode)
                    .replace("%3", innerVersion);

            LogService.getLogger().warn(msg);
            return null;
        }

        // 仅对标准化数据集及有关联字典的数据元进行翻译
        if (!isOriginDataSet && actualData != null && actualData.length() > 0 && metaData.dictId > 0) {
            String[] tempQualifiers = StdObjectQualifierTranslator.splitInnerCodeAsCodeValue(metaDataInnerCode);

            String codeQualifier = StdObjectQualifierTranslator.toHBaseQualifier(tempQualifiers[0], metaData.type);
            String valueQualifier = StdObjectQualifierTranslator.toHBaseQualifier(tempQualifiers[1], metaData.type);

            String value = StdDataRedisCache.getDictEntryValue(innerVersion, metaData.dictId, actualData);

            return new String[]{codeQualifier, actualData, valueQualifier, value == null ? "" : value};
        } else {
            if (metaData.type.equals("D")) {
                actualData = actualData.length() <= 10 ? actualData : actualData.substring(0, actualData.lastIndexOf(' ')) + " 00:00:00";
            } else if (metaData.type.equals("DT")) {
                actualData = actualData.contains(".") ? actualData.substring(0, actualData.lastIndexOf('.')) : actualData;
            } else if (metaData.type.equals("N")) {
                Matcher matcher = NumberPattern.matcher(actualData);
                if (matcher.find()) {
                    actualData = matcher.group();
                } else {
                    actualData = "";
                }
            }

            return new String[]{StdObjectQualifierTranslator.toHBaseQualifier(metaDataInnerCode, metaData.type), actualData};
        }
    }
}
