package com.yihu.ehr.resolve;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.yihu.ehr.profile.EventType;
import com.yihu.ehr.profile.extractor.ExtractorChain;
import com.yihu.ehr.profile.extractor.KeyDataExtractor;
import com.yihu.ehr.profile.family.ResourceCells;
import com.yihu.ehr.profile.model.MetaDataRecord;
import com.yihu.ehr.profile.model.PackageDataSet;
import com.yihu.ehr.profile.exception.IllegalJsonDataException;
import com.yihu.ehr.profile.exception.IllegalJsonFileException;
import com.yihu.ehr.resolve.model.stage1.StandardPackage;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * 即时交互档案数据解析器.
 *
 * @author HZY
 * @created 2018.01.04 14:40
 */
@Component
public class ImmediateDataResolver {

    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected ExtractorChain extractorChain;

    public void resolve(StandardPackage standardPackage, String data) throws Exception {
        //解析标准数据
        parseData(standardPackage, data);
    }

    /**
     * 解析及时交互档案的json数据
     *
     * @param standardPackage 标准档案数据类
     * @param data            即时交互json数据
     * @throws Exception
     * @throws IOException
     */
    private void parseData(StandardPackage standardPackage, String data) throws Exception {
        //解析数据集数据
        JsonNode dataNode = objectMapper.readValue(data, JsonNode.class);
        if (dataNode.isNull()) {
            throw new IllegalJsonFileException("Invalid json file when generate data set");
        }

        JsonNode eventTypeNode = dataNode.get("event_type");

        if (eventTypeNode == null){
            throw new IllegalJsonDataException("Not event_type in json data when generate data set");
        }

        int eventType = dataNode.get("event_type").asInt();

        List<PackageDataSet> packageDataSetList = parseStructuredImmediateJson(dataNode);

        if (packageDataSetList != null) {
            for (PackageDataSet dataSet : packageDataSetList) {
                if (dataSet.isReUploadFlg()) {
                    standardPackage.setReUploadFlg(true);
                }
            }
        }

        if (standardPackage.isReUploadFlg()) {
            for (PackageDataSet dataSet : packageDataSetList) {
                dataSet.setCode(dataSet.getCode());
                standardPackage.setEventTime(dataSet.getEventTime());
                standardPackage.setPatientId(dataSet.getPatientId());
                standardPackage.setEventNo(dataSet.getEventNo());
                standardPackage.setOrgCode(dataSet.getOrgCode());
                standardPackage.setCdaVersion(dataSet.getCdaVersion());
                standardPackage.setCreateDate(dataSet.getCreateTime());
                standardPackage.insertDataSet(dataSet.getCode(), dataSet);
            }
            return;
        }

        for (PackageDataSet dataSet : packageDataSetList) {
            //将单个JSON文件转化为单个数据集
            String dataSetCode = dataSet.getCode();
            dataSet.setCode(dataSetCode);
            // Extract key data from data set if exists
            //就诊卡信息
            if (StringUtils.isEmpty(standardPackage.getCardId()) || StringUtils.isEmpty(standardPackage.getCardType())) {
                Map<String, Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.CardInfo);
                String cardId = (String) properties.get(ResourceCells.CARD_ID);
                String cardType = (String) properties.get(ResourceCells.CARD_TYPE);
                if (!StringUtils.isEmpty(cardId)) {
                    standardPackage.setCardId(cardId);
                }
                if (!StringUtils.isEmpty(cardType)) {
                    standardPackage.setCardType(cardType);
                }
            }

            //身份信息
            if (StringUtils.isEmpty(standardPackage.getDemographicId()) || StringUtils.isEmpty(standardPackage.getPatientName())) {
                Map<String, Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.Identity);
                String demographicId = (String) properties.get(ResourceCells.DEMOGRAPHIC_ID);
                String patientName = (String) properties.get(ResourceCells.PATIENT_NAME);
                if (!StringUtils.isEmpty(demographicId)) {
                    standardPackage.setDemographicId(demographicId);
                }
                if (!StringUtils.isEmpty(patientName)) {
                    standardPackage.setPatientName(patientName);
                }
            }

            //就诊事件信息
            if (standardPackage.getEventTime() == null || standardPackage.getEventType() == null) {
                Date eventDate = dataSet.getEventTime();
                EventType mEventType = EventType.create(eventType);
                if (eventDate != null) {
                    standardPackage.setEventTime(eventDate);
                }
                if (mEventType != null) {
                    standardPackage.setEventType(mEventType);
                }
            }

            //门诊或住院诊断
            if (standardPackage.getDiagnosisCode() == null || standardPackage.getDiagnosisCode().size() <= 0) {
                Map<String, Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.Diagnosis);
                Set<String> diagnosisList = (Set<String>) properties.get(ResourceCells.DIAGNOSIS);
                if (diagnosisList != null && diagnosisList.size() > 0) {
                    standardPackage.setDiagnosisCode(diagnosisList);
                }
            }

            standardPackage.setPatientId(dataSet.getPatientId());
            standardPackage.setEventNo(dataSet.getEventNo());
            standardPackage.setOrgCode(dataSet.getOrgCode());
            standardPackage.setCdaVersion(dataSet.getCdaVersion());
            standardPackage.setCreateDate(dataSet.getCreateTime());
            standardPackage.insertDataSet(dataSetCode, dataSet);
        }
    }

    /**
     * 结构化 - 即时交互档案数据集处理
     * @param jsonNode
     * @return
     */
    private List<PackageDataSet> parseStructuredImmediateJson(JsonNode jsonNode) {
        List<PackageDataSet> packageDataSetList = new ArrayList<>();

        //获取就诊事件索引信息
        String patientId = jsonNode.get("patient_id") == null ? "" : jsonNode.get("patient_id").asText();
        String eventNo = jsonNode.get("event_no") == null ? "" : jsonNode.get("event_no").asText();
        String orgCode = jsonNode.get("org_code") == null ? "" : jsonNode.get("org_code").asText();
        String version = jsonNode.get("inner_version") == null ? "" : jsonNode.get("inner_version").asText();
        String createTime = jsonNode.get("create_date") == null ? "" : jsonNode.get("create_date").asText();
        String eventTime = jsonNode.path("event_time") == null ? "" : jsonNode.path("event_time").asText();
        boolean reUploadFlg = jsonNode.path("reUploadFlg") == null ? false : jsonNode.path("reUploadFlg").asBoolean();

        //验证档案基础数据的完整性，当其中某字段为空的情况下直接提示档案信息缺失。
        StringBuilder errorMsg = new StringBuilder();
        if (StringUtils.isEmpty(patientId)){
            errorMsg.append("patientId is null;");
        }
        if (StringUtils.isEmpty(eventNo) ){
            errorMsg.append("eventNo is null;");
        }
        if (StringUtils.isEmpty(orgCode)){
            errorMsg.append("orgCode is null;");
        }
        if (null == jsonNode.get("data")){
            errorMsg.append("dataSets is null;");
        }
        if (!StringUtils.isEmpty(errorMsg.toString())){
            throw new IllegalJsonDataException(errorMsg.toString());
        }

        //获取档案中数据集json数据
        JsonNode dataNode = jsonNode.get("data");
        Iterator<Map.Entry<String, JsonNode>> fields = dataNode.fields();
        while (fields.hasNext()){
            //遍历标准数据集数据
            Map.Entry<String, JsonNode> next = fields.next();
            String dataSetCode = next.getKey();
            ArrayNode dataSets = (ArrayNode) next.getValue();
            PackageDataSet dataSet = new PackageDataSet();
            dataSet.setPatientId(patientId);
            dataSet.setEventNo(eventNo);
            dataSet.setCdaVersion(version);
            dataSet.setCode(dataSetCode);
            dataSet.setOrgCode(orgCode);
            try {
                dataSet.setEventTime(DateUtil.strToDate(eventTime));
                dataSet.setCreateTime(DateTimeUtil.simpleDateParse(createTime));
            } catch (ParseException e) {
                throw new IllegalJsonDataException("Invalid date time format.");
            }
            dataSet.setReUploadFlg(reUploadFlg);
            for (int i = 0; i < dataSets.size(); i ++) {
                MetaDataRecord record = new MetaDataRecord();
                JsonNode recordNode = dataSets.get(i);
                Iterator<Map.Entry<String, JsonNode>> iterator = recordNode.fields();
                while (iterator.hasNext()) {
                    Map.Entry<String, JsonNode> item = iterator.next();
                    String metaDataKey = item.getKey();
                    String metaDataValue = item.getValue().asText().equals("null") ? "" : item.getValue().asText();
                    record.putMetaData(metaDataKey, metaDataValue);
                }
                dataSet.addRecord(Integer.toString(i), record);
            }
            packageDataSetList.add(dataSet);
        }
        return packageDataSetList;
    }
}
