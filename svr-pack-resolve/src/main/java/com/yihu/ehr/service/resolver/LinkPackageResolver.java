package com.yihu.ehr.service.resolver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.yihu.ehr.profile.core.DataRecord;
import com.yihu.ehr.profile.core.LinkDataSet;
import com.yihu.ehr.profile.core.LinkProfile;
import com.yihu.ehr.profile.core.StdProfile;
import com.yihu.ehr.util.DateTimeUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;

/**
 * 轻量级档案包解析器.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.09 15:04
 */
@Component
public class LinkPackageResolver extends PackageResolver {
    @Override
    public void resolve(StdProfile profile, File root) throws IOException, ParseException {
        LinkProfile linkProfile = (LinkProfile) profile;

        File indexFile = new File(root.getAbsolutePath() + File.pathSeparator + "index/patient_index.json");
        parseFile(linkProfile, indexFile);
    }

    private void parseFile(LinkProfile profile, File indexFile) throws IOException, ParseException {
        JsonNode jsonNode = objectMapper.readTree(indexFile);

        String patientId = jsonNode.get("patient_id").asText();
        String eventNo = jsonNode.get("event_no").asText();
        String orgCode = jsonNode.get("org_code").asText();
        String version = jsonNode.get("inner_version").asText();
        String eventDate = jsonNode.get("event_time").asText();
        String expireDate = jsonNode.get("expiry_date").asText();

        profile.setPatientId(patientId);
        profile.setEventNo(eventNo);
        profile.setOrgCode(orgCode);
        profile.setCdaVersion(version);
        profile.setEventDate(DateTimeUtils.utcDateTimeParse(eventDate));
        profile.setExpireDate(DateTimeUtils.utcDateTimeParse(expireDate));

        // dataset节点，存储数据集URL
        JsonNode dataSetNode = jsonNode.get("dataset");
        Iterator<String> fieldNames = dataSetNode.fieldNames();
        while (fieldNames.hasNext()) {
            String dataSetCode = fieldNames.next();
            String url = dataSetNode.get(dataSetCode).asText();

            LinkDataSet dataSet = new LinkDataSet();
            dataSet.setUrl(url);

            profile.insertDataSet(dataSetCode, dataSet);
        }

        // summary节点可能不存在
        JsonNode summaryNode = jsonNode.get("summary");
        if (summaryNode == null) return;

        fieldNames = summaryNode.fieldNames();
        while (fieldNames.hasNext()) {
            String dataSetCode = fieldNames.next();

            LinkDataSet linkDataSet = (LinkDataSet)profile.getDataSet(dataSetCode);
            if (linkDataSet == null) linkDataSet = new LinkDataSet();

            ArrayNode arrayNode = (ArrayNode) summaryNode.get(dataSetCode);
            for (int i = 0; i < arrayNode.size(); ++i){

                DataRecord record = new DataRecord();
                Iterator<String> metaDataCodes = arrayNode.get(i).fieldNames();
                while (metaDataCodes.hasNext()){
                    String metaDataCode = metaDataCodes.next();
                    record.putMetaData(metaDataCode, arrayNode.get(i).get(metaDataCode).asText());
                }

                linkDataSet.addRecord(Integer.toString(linkDataSet.getRecordCount()), record);
            }

            profile.insertDataSet(dataSetCode, linkDataSet);
        }
    }
}
