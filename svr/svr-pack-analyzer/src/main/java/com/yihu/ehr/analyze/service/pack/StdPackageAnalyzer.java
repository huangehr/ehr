package com.yihu.ehr.analyze.service.pack;

import com.fasterxml.jackson.databind.JsonNode;
import com.yihu.ehr.analyze.model.ZipPackage;
import com.yihu.ehr.analyze.service.RedisService;
import com.yihu.ehr.profile.EventType;
import com.yihu.ehr.profile.exception.AnalyzerException;
import com.yihu.ehr.profile.exception.IllegalJsonFileException;
import com.yihu.ehr.profile.extractor.KeyDataExtractor;
import com.yihu.ehr.profile.family.ResourceCells;
import com.yihu.ehr.profile.model.PackageDataSet;
import com.yihu.ehr.solr.SolrUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
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
    @Autowired
    private SolrUtil solrUtil;

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

            //补传标识
            if (dataSet.isReUploadFlg()) {
                zipPackage.setReUploadFlg(dataSet.isReUploadFlg());
                if(zipPackage.getEventType()==null && StringUtils.isNotBlank(dataSet.getOrgCode()) && StringUtils.isNotBlank(dataSet.getEventNo()) && dataSet.getEventTime()!=null){
                    //拼接出rowkey
                    String rowkey = dataSet.getOrgCode()+"_"+dataSet.getEventNo()+"_"+dataSet.getEventTime().getTime();
                    String q= "rowkey:"+rowkey;
                    //目前只有结构化档案有做质控,后期如果对非结构化等做质控,查询表名需要做判断
                    SolrDocumentList healthProfile = solrUtil.query("HealthProfile", q, null, 0, 1);
                    if (healthProfile != null && healthProfile.getNumFound() > 0) {
                        for (SolrDocument doc : healthProfile) {
                            String eventType = (String) doc.getFieldValue("event_type");
                            zipPackage.setEventType(EventType.create(eventType));
                        }
                    }else{
                        throw new IllegalJsonFileException("Please upload the complete package(" + rowkey + ") first !");
                    }

                }
            }

            //就诊事件信息
            if (zipPackage.getEventDate() == null || zipPackage.getEventType() == null) {
                Map<String, Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.EventInfo);
                EventType eventType = (EventType) properties.get(ResourceCells.EVENT_TYPE);
                if (eventType != null) {
                    zipPackage.setEventType(eventType);
                }
            }
            //门诊或住院诊断
            if (zipPackage.getDiagnosisCode().size() <= 0 || zipPackage.getDiagnosisName().size() <= 0) {
                Map<String, Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.Diagnosis);
                Set<String> diagnosisCode = (Set<String>) properties.get(ResourceCells.DIAGNOSIS);
                Set<String> diagnosisName = (Set<String>) properties.get(ResourceCells.DIAGNOSIS_NAME);
                if (diagnosisCode.size() > 0) {
                    zipPackage.setDiagnosisCode(diagnosisCode);
                }
                if (diagnosisName.size() > 0) {
                    zipPackage.setDiagnosisName(diagnosisName);
                }
            }

            //科室信息
            if (zipPackage.getDeptCode() == null) {
                Map<String, Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.Dept);
                String deptCode = (String) properties.get(ResourceCells.DEPT_CODE);
                if (StringUtils.isNotEmpty(deptCode)) {
                    zipPackage.setDeptCode(deptCode);
                }
            }

            zipPackage.insertDataSet(dataSet.getCode(), dataSet);
            zipPackage.setPatientId(dataSet.getPatientId());
            zipPackage.setEventNo(dataSet.getEventNo());
            zipPackage.setOrgCode(dataSet.getOrgCode());
            String orgName = redisService.getOrgName(dataSet.getOrgCode());
            if (StringUtils.isEmpty(orgName)) {
                throw new AnalyzerException("Can not get org name for " + dataSet.getOrgCode() + ", forget to cache?");
            }
            zipPackage.setOrgName(orgName);
            String orgArea = redisService.getOrgArea(dataSet.getOrgCode());
            if (StringUtils.isEmpty(orgArea)) {
                throw new AnalyzerException("Can not get org area for " + dataSet.getOrgCode() + ", forget to cache?");
            }
            zipPackage.setOrgArea(orgArea);
            zipPackage.setCdaVersion(dataSet.getCdaVersion());
            zipPackage.setEventDate(dataSet.getEventTime());
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
