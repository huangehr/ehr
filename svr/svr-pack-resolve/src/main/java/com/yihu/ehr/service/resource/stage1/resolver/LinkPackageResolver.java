package com.yihu.ehr.service.resource.stage1.resolver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.yihu.ehr.profile.exception.LegacyPackageException;
import com.yihu.ehr.profile.util.MetaDataRecord;
import com.yihu.ehr.service.resource.stage1.LinkPackageDataSet;
import com.yihu.ehr.service.resource.stage1.LinkPackage;
import com.yihu.ehr.service.resource.stage1.StandardPackage;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

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
    public List<StandardPackage> resolveDataSets(File root,String clinetId) throws Exception {
        return null;
    }

    @Override
    public void resolve(StandardPackage profile, File root) throws IOException, ParseException {
        LinkPackage linkPackModel = (LinkPackage) profile;

        File indexFile = new File(root.getAbsolutePath() + File.pathSeparator + "index/patient_index.json");
        parseFile(linkPackModel, indexFile);
    }

    private void parseFile(LinkPackage profile, File indexFile) throws IOException, ParseException {
        JsonNode jsonNode = objectMapper.readTree(indexFile);

        String patientId = jsonNode.get("patient_id").asText();
        String eventNo = jsonNode.get("event_no").asText();
        String orgCode = jsonNode.get("org_code").asText();
        String version = jsonNode.get("inner_version").asText();
        String eventDate = jsonNode.get("event_time").asText();
        String expireDate = jsonNode.get("expiry_date").asText();
        //if (version.equals("000000000000")) throw new LegacyPackageException("Package is collected via cda version 00000000000, ignored.");

        profile.setPatientId(patientId);
        profile.setEventNo(eventNo);
        profile.setOrgCode(orgCode);
        profile.setCdaVersion(version);
        profile.setEventDate(DateUtil.strToDate(eventDate));
        profile.setExpireDate(DateUtil.strToDate(expireDate));

        // dataset节点，存储数据集URL
        JsonNode dataSetNode = jsonNode.get("dataset");
        Iterator<String> fieldNames = dataSetNode.fieldNames();
        while (fieldNames.hasNext()) {
            String dataSetCode = fieldNames.next();
            String url = dataSetNode.get(dataSetCode).asText();

            LinkPackageDataSet dataSet = new LinkPackageDataSet();
            dataSet.setUrl(url);

            profile.insertDataSet(dataSetCode, dataSet);
        }

        // summary节点可能不存在
        JsonNode summaryNode = jsonNode.get("summary");
        if (summaryNode == null) return;

        fieldNames = summaryNode.fieldNames();
        while (fieldNames.hasNext()) {
            String dataSetCode = fieldNames.next();

            LinkPackageDataSet linkPackageDataSet = (LinkPackageDataSet)profile.getDataSet(dataSetCode);
            if (linkPackageDataSet == null) linkPackageDataSet = new LinkPackageDataSet();

            ArrayNode arrayNode = (ArrayNode) summaryNode.get(dataSetCode);
            for (int i = 0; i < arrayNode.size(); ++i){

                MetaDataRecord record = new MetaDataRecord();
                Iterator<String> metaDataCodes = arrayNode.get(i).fieldNames();
                while (metaDataCodes.hasNext()){
                    String metaDataCode = metaDataCodes.next();
                    record.putMetaData(metaDataCode, arrayNode.get(i).get(metaDataCode).asText());
                }

                linkPackageDataSet.addRecord(Integer.toString(linkPackageDataSet.getRecordCount()), record);
            }

            profile.insertDataSet(dataSetCode, linkPackageDataSet);
        }
    }
}
