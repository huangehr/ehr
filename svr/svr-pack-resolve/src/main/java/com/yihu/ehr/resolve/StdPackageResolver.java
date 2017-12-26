package com.yihu.ehr.resolve;

import com.fasterxml.jackson.databind.JsonNode;
import com.yihu.ehr.constants.EventType;
import com.yihu.ehr.profile.family.MasterResourceFamily;
import com.yihu.ehr.profile.util.DataSetUtil;
import com.yihu.ehr.profile.util.PackageDataSet;
import com.yihu.ehr.resolve.model.stage1.StandardPackage;
import com.yihu.ehr.resolve.service.resource.stage1.PackModelFactory;
import com.yihu.ehr.resolve.service.resource.stage1.extractor.KeyDataExtractor;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 标准档案包解析器.
 * @author Sand
 * @created 2015.09.09 15:04
 */
@Component
public class StdPackageResolver extends PackageResolver {

    @Override
    public List<StandardPackage> resolveDataSets(File root, String clientId) throws Exception {
        return null;
    }

    @Override
    public void resolve(StandardPackage standardPackage, File root) throws IOException, Exception {
        //解析标准数据
        File standardFolder = new File(root.getAbsolutePath() + File.separator + PackModelFactory.StandardFolder);
        parseFiles(standardPackage, standardFolder.listFiles(), false);

        //解析原始数据
        //File originFolder = new File(root.getAbsolutePath() + File.separator + PackModelFactory.OriginFolder);
        //parseFiles(standardPackage, originFolder.listFiles(), true);
    }

    /**
     * 将标准和原始文件夹中的JSON文件转换为数据集，
     * 放入标准档案包中
     * @param standardPackage 标准档案包中
     * @param files 文件夹
     * @param origin 是否为标准文件夹
     * @throws Exception
     * @throws IOException
     */
    private void parseFiles(StandardPackage standardPackage, File[] files, boolean origin) throws Exception, IOException {
        List<PackageDataSet> packageDataSetList = new ArrayList<>(files.length);
        //新增补传判断---------------Start---------------
        for(File file : files) {
            PackageDataSet dataSet = generateDataSet(file, origin);
            packageDataSetList.add(dataSet);
            if(dataSet.isReUploadFlg()){
                standardPackage.setReUploadFlg(true);
            }
        }
        if(standardPackage.isReUploadFlg()) {
            for(PackageDataSet dataSet : packageDataSetList) {
                String dataSetCode = origin ? DataSetUtil.originDataSetCode(dataSet.getCode()) : dataSet.getCode();
                dataSet.setCode(dataSetCode);
                standardPackage.setEventDate(dataSet.getEventTime());
                standardPackage.setPatientId(dataSet.getPatientId());
                standardPackage.setEventNo(dataSet.getEventNo());
                standardPackage.setEventType(EventType.reUpload);
                standardPackage.setOrgCode(dataSet.getOrgCode());
                standardPackage.setCdaVersion(dataSet.getCdaVersion());
                standardPackage.setCreateDate(dataSet.getCreateTime());
                standardPackage.insertDataSet(dataSetCode, dataSet);
            }
            return;
        }
        //---------------End---------------
        for (PackageDataSet dataSet : packageDataSetList) {
            //将单个JSON文件转化为单个数据集
            String dataSetCode = origin ? DataSetUtil.originDataSetCode(dataSet.getCode()) : dataSet.getCode();
            dataSet.setCode(dataSetCode);
            // Extract key data from data set if exists
            if (!origin) {
                //就诊卡信息
                if (StringUtils.isEmpty(standardPackage.getCardId()) || StringUtils.isEmpty(standardPackage.getCardType())) {
                    Map<String, Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.CardInfo);
                    String cardId = (String) properties.get(MasterResourceFamily.BasicColumns.CardId);
                    String cardType = (String) properties.get(MasterResourceFamily.BasicColumns.CardType);
                    if(!StringUtils.isEmpty(cardId) && !StringUtils.isEmpty(cardType)) {
                        standardPackage.setCardId(cardId);
                        standardPackage.setCardType(cardType);
                    }
                }

                //身份信息
                if (StringUtils.isEmpty(standardPackage.getDemographicId()) || StringUtils.isEmpty(standardPackage.getPatientName())) {
                    Map<String, Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.Identity);
                    String demographicId = (String) properties.get(MasterResourceFamily.BasicColumns.DemographicId);
                    String patientName = (String) properties.get(MasterResourceFamily.BasicColumns.PatientName);
                    if(!StringUtils.isEmpty(demographicId) && !StringUtils.isEmpty(patientName)) {
                        standardPackage.setDemographicId(demographicId);
                        standardPackage.setPatientName(patientName);
                    }
                }

                //就诊事件信息
                if (standardPackage.getEventDate() == null || standardPackage.getEventType() == null) {
                    Map<String, Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.EventInfo);
                    Date eventDate = (Date) properties.get(MasterResourceFamily.BasicColumns.EventDate);
                    EventType eventType = (EventType) properties.get(MasterResourceFamily.BasicColumns.EventType);
                    if(eventDate != null && eventType != null) {
                        standardPackage.setEventDate(eventDate);
                        standardPackage.setEventType(eventType);
                    }
                }

                //门诊或住院诊断
                if(standardPackage.getDiagnosisList() == null || standardPackage.getDiagnosisList().size() <= 0 ) {
                    Map<String, Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.Diagnosis);
                    List<String> diagnosisList = (List<String>) properties.get(MasterResourceFamily.BasicColumns.Diagnosis);
                    if (diagnosisList != null && diagnosisList.size() > 0) {
                        standardPackage.setDiagnosisList(diagnosisList);
                    }
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
     * @param jsonFile
     * @param isOrigin
     * @return
     * @throws IOException
     */
    private PackageDataSet generateDataSet(File jsonFile, boolean isOrigin) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(jsonFile);
        if (jsonNode.isNull()) {
            throw new IOException("Invalid json file when generate data set");
        }
        PackageDataSet dataSet = dataSetResolverWithTranslator.parseStructuredJsonDataSet(jsonNode, isOrigin);
        return dataSet;
    }
}
