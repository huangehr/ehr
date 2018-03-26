package com.yihu.ehr.analyze.service.pack;

import com.fasterxml.jackson.databind.JsonNode;
import com.yihu.ehr.profile.util.DataSetUtil;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Map;

/**
 * 标准档案分析
 *
 * @author Airhead
 * @created 2018.01.16
 */
@Component
public class StdPackageAnalyzer extends PackageAnalyzer {

    public final static String StandardFolder = "standard";
    public final static String OriginFolder = "origin";


    @Override
    public void analyze(ZipPackage zipPackage) throws Exception {
        File root = zipPackage.getPackFile();

        //解析标准数据
        String standardPath = root.getAbsolutePath() + File.separator + StandardFolder;
        File standardFolder = new File(standardPath);
        parseFiles(zipPackage, standardFolder.listFiles(), false);

        //解析原始数据
        // 目前没用到原始数据，质控校验也无法判断，故注释掉。 -- by zjj 2018.3.22
        /*String originPath = root.getAbsolutePath() + File.separator + OriginFolder;
        File originFolder = new File(originPath);
        parseFiles(zipPackage, originFolder.listFiles(), true);*/
    }

    /**
     * 将标准和原始文件夹中的JSON文件转换为数据集，
     * 放入标准档案包中
     *
     * @param zipPackage 标准档案包中
     * @param files      文件夹
     * @param origin     是否为标准文件夹
     * @throws Exception
     * @throws IOException
     */
    private void parseFiles(ZipPackage zipPackage, File[] files, boolean origin) throws Exception, IOException {
        for (File file : files) {
            DataSetRecord dataSet = analyzeDataSet(file, origin);

            zipPackage.insertDataSet(dataSet.getCode(), dataSet);
        }
    }

    /**
     * 根据JSON文件生产数据集
     *
     * @param jsonFile
     * @param isOrigin
     * @return
     * @throws IOException
     */

    public DataSetRecord analyzeDataSet(File jsonFile, boolean isOrigin) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(jsonFile);
        if (jsonNode.isNull()) {
            throw new IOException("Invalid json file when generate data set");
        }

        DataSetRecord dataSet = new DataSetRecord();
        String code = jsonNode.path("code").asText();
        String patientId = jsonNode.path("patient_id").asText();
        String eventNo = jsonNode.path("event_no").asText();
        String orgCode = jsonNode.path("org_code").asText();
        String version = jsonNode.path("inner_version").asText();
        String createTime = jsonNode.path("create_date").asText();
        String eventTime = jsonNode.path("event_time").asText();
        String eventType = jsonNode.path("event_type").asText();
        boolean reUploadFlg = jsonNode.path("reUploadFlg").asBoolean();

        //验证档案基础数据的完整性，当其中某字段为空的情况下直接提示档案包信息缺失。
        StringBuilder errorMsg = new StringBuilder();
        if (StringUtils.isEmpty(code)) {
            errorMsg.append("dataSetCode is null;");
        }
        if (StringUtils.isEmpty(patientId)) {
            errorMsg.append("patientId is null;");
        }
        if (StringUtils.isEmpty(eventNo)) {
            errorMsg.append("eventNo is null;");
        }
        if (StringUtils.isEmpty(orgCode)) {
            errorMsg.append("orgCode is null;");
        }
        if (!StringUtils.isEmpty(errorMsg.toString())) {
            throw new RuntimeException(errorMsg.toString());
        }

        try {
            dataSet.setPatientId(patientId);
            dataSet.setEventNo(eventNo);
            dataSet.setVersion(version);
            String dataSetCode = isOrigin ? DataSetUtil.originDataSetCode(code) : code;
            dataSet.setCode(dataSetCode);
            dataSet.setOrgCode(orgCode);
            dataSet.setEventType(eventType);    //TODO:暂时无数据？
            dataSet.setEventTime(DateUtil.strToDate(eventTime));
            dataSet.setCreateTime(DateTimeUtil.simpleDateParse(createTime));
            dataSet.setReUploadFlg(reUploadFlg);

            JsonNode dataNode = jsonNode.get("data");
            if (dataNode.size() > 1) {
                dataSet.setMultiRecord(true);
            }

            for (int i = 0; i < dataNode.size(); ++i) {
                DataElementRecord record = new DataElementRecord();
                JsonNode recordNode = dataNode.get(i);
                Iterator<Map.Entry<String, JsonNode>> iterator = recordNode.fields();
                while (iterator.hasNext()) {
                    Map.Entry<String, JsonNode> item = iterator.next();
                    String metaData = item.getKey();
                    JsonNode node = item.getValue();
                    String value = node.asText();
                    value = value.equals("null") ? "" : value;

                    record.putMetaData(metaData, value);
                }

                dataSet.addRecord(Integer.toString(i), record);
            }
        } catch (NullPointerException e) {
            throw new RuntimeException("Null pointer occurs while generate data set, package cda version: " + version);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date time format.");
        }

        return dataSet;
    }
}
