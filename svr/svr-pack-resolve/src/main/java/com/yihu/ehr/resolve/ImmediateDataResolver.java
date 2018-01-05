package com.yihu.ehr.resolve;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.EventType;
import com.yihu.ehr.profile.family.MasterResourceFamily;
import com.yihu.ehr.profile.util.PackageDataSet;
import com.yihu.ehr.resolve.model.stage1.StandardPackage;
import com.yihu.ehr.resolve.service.resource.stage1.DataSetParserWithTranslator;
import com.yihu.ehr.resolve.service.resource.stage1.extractor.ExtractorChain;
import com.yihu.ehr.resolve.service.resource.stage1.extractor.KeyDataExtractor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 即时交互档案数据解析器.
 *
 * @author HZY
 * @created 2018.01.04 14:40
 */
@Component
public class ImmediateDataResolver {

    @Autowired
    protected DataSetParserWithTranslator dataSetResolverWithTranslator;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected ExtractorChain extractorChain;

    public void resolve(StandardPackage standardPackage, String data) throws IOException, Exception {
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
    private void parseData(StandardPackage standardPackage, String data) throws Exception, IOException {
        //解析数据集数据
        JsonNode dataNode = objectMapper.readValue(data, JsonNode.class);
        if (dataNode.isNull()) {
            throw new IOException("Invalid json file when generate data set");
        }

        int eventType = dataNode.get("event_type").asInt();

        List<PackageDataSet> packageDataSetList = dataSetResolverWithTranslator.parseStructuredImmediateJson(dataNode);

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
                standardPackage.setEventDate(dataSet.getEventTime());
                standardPackage.setPatientId(dataSet.getPatientId());
                standardPackage.setEventNo(dataSet.getEventNo());
                standardPackage.setEventType(EventType.reUpload);
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
                String cardId = (String) properties.get(MasterResourceFamily.BasicColumns.CardId);
                String cardType = (String) properties.get(MasterResourceFamily.BasicColumns.CardType);
                if (!StringUtils.isEmpty(cardId) && !StringUtils.isEmpty(cardType)) {
                    standardPackage.setCardId(cardId);
                    standardPackage.setCardType(cardType);
                }
            }

            //身份信息
            if (StringUtils.isEmpty(standardPackage.getDemographicId()) || StringUtils.isEmpty(standardPackage.getPatientName())) {
                Map<String, Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.Identity);
                String demographicId = (String) properties.get(MasterResourceFamily.BasicColumns.DemographicId);
                String patientName = (String) properties.get(MasterResourceFamily.BasicColumns.PatientName);
                if (!StringUtils.isEmpty(demographicId) && !StringUtils.isEmpty(patientName)) {
                    standardPackage.setDemographicId(demographicId);
                    standardPackage.setPatientName(patientName);
                }
            }

            //就诊事件信息
            if (standardPackage.getEventDate() == null || standardPackage.getEventType() == null) {
                Date eventDate = dataSet.getEventTime();
                EventType mEventType = EventType.create(eventType);
                if (eventDate != null && mEventType != null) {
                    standardPackage.setEventDate(eventDate);
                    standardPackage.setEventType(mEventType);
                }
            }

            //门诊或住院诊断
            if (standardPackage.getDiagnosisList() == null || standardPackage.getDiagnosisList().size() <= 0) {
                Map<String, Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.Diagnosis);
                List<String> diagnosisList = (List<String>) properties.get(MasterResourceFamily.BasicColumns.Diagnosis);
                if (diagnosisList != null && diagnosisList.size() > 0) {
                    standardPackage.setDiagnosisList(diagnosisList);
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
     * 根据JSON文件生产数据集
     *
     * @param jsonNode
     * @return
     * @throws IOException
     */
    private List<PackageDataSet> generateDataSet(JsonNode jsonNode) throws IOException {

        if (jsonNode.isNull()) {
            throw new IOException("Invalid json file when generate data set");
        }
        List<PackageDataSet> dataSet = dataSetResolverWithTranslator.parseStructuredImmediateJson(jsonNode);
        return dataSet;
    }
}
