package com.yihu.ehr.analyze.service.pack;

import com.fasterxml.jackson.databind.JsonNode;
import com.yihu.ehr.analyze.service.RedisService;
import com.yihu.ehr.profile.EventType;
import com.yihu.ehr.profile.exception.IllegalJsonDataException;
import com.yihu.ehr.profile.exception.IllegalJsonFileException;
import com.yihu.ehr.profile.extractor.KeyDataExtractor;
import com.yihu.ehr.profile.family.MasterResourceFamily;
import com.yihu.ehr.profile.util.DataSetParser;
import com.yihu.ehr.profile.util.MetaDataRecord;
import com.yihu.ehr.profile.util.PackageDataSet;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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

    @Autowired
    private RedisService redisService;

    @Override
    public void analyze(ZipPackage zipPackage) throws Exception {
        File root = zipPackage.getPackFile();
        //解析标准数据
        String standardPath = root.getAbsolutePath() + File.separator + StandardFolder;
        File standardFolder = new File(standardPath);
        parseFiles(zipPackage, standardFolder.listFiles(), false);
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
    private void parseFiles(ZipPackage zipPackage, File[] files, boolean origin) throws Exception {
        for (File file : files) {
            PackageDataSet dataSet = analyzeDataSet(file, origin);
            //就诊事件信息
            if (zipPackage.getEventDate() == null || zipPackage.getEventType() == null) {
                Map<String, Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.EventInfo);
                Date eventDate = (Date) properties.get(MasterResourceFamily.BasicColumns.EventDate);
                EventType eventType = (EventType) properties.get(MasterResourceFamily.BasicColumns.EventType);
                if (eventDate != null) {
                    zipPackage.setEventDate(eventDate);
                }
                if (eventType != null) {
                    zipPackage.setEventType(eventType);
                }
            }

            //门诊或住院诊断
            if (zipPackage.getDiagnosisList() == null
                    || zipPackage.getDiagnosisList().size() <= 0
                    || zipPackage.getDiagnosisNameList() == null
                    || zipPackage.getDiagnosisNameList().size() <= 0) {
                Map<String, Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.Diagnosis);
                Set<String> diagnosisList = (Set<String>) properties.get(MasterResourceFamily.BasicColumns.Diagnosis);
                Set<String> diagnosisNameList = (Set<String>) properties.get(MasterResourceFamily.BasicColumns.DiagnosisName);
                if (diagnosisList.size() > 0) {
                    zipPackage.setDiagnosisList(diagnosisList);
                }
                if (diagnosisNameList.size() > 0) {
                    zipPackage.setDiagnosisNameList(diagnosisNameList);
                }
            }

            //科室信息
            if (zipPackage.getDeptCode() == null) {
                Map<String, Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.Dept);
                String deptCode = (String) properties.get(MasterResourceFamily.BasicColumns.DeptCode);
                if (StringUtils.isNotEmpty(deptCode)) {
                    zipPackage.setDeptCode(deptCode);
                }
            }
            zipPackage.insertDataSet(dataSet.getCode(), dataSet);
            zipPackage.setPatientId(dataSet.getPatientId());
            zipPackage.setEventNo(dataSet.getEventNo());
            zipPackage.setOrgCode(dataSet.getOrgCode());
            zipPackage.setOrgName(redisService.getOrgName(dataSet.getOrgCode()));
            zipPackage.setOrgArea(redisService.getOrgArea(dataSet.getOrgCode()));
            zipPackage.setCdaVersion(dataSet.getCdaVersion());
        }
    }

    /**
     * 根据JSON文件生产数据集
     * @param jsonFile
     * @param isOrigin
     * @return
     * @throws IOException
     */
    private PackageDataSet analyzeDataSet(File jsonFile, boolean isOrigin) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(jsonFile);
        if (jsonNode.isNull()) {
            throw new IllegalJsonFileException("Invalid json file when generate data set");
        }
        PackageDataSet dataSet = dataSetParser.parseStructuredJsonDataSet(jsonNode, isOrigin);
        return dataSet;
    }
}
