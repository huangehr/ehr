package com.yihu.ehr.service.resource.stage1.resolver;

import com.fasterxml.jackson.databind.JsonNode;
import com.yihu.ehr.constants.EventType;
import com.yihu.ehr.profile.family.MasterResourceFamily;
import com.yihu.ehr.profile.util.PackageDataSet;
import com.yihu.ehr.service.resource.stage1.StandardPackage;
import com.yihu.ehr.service.resource.stage1.PackModelFactory;
import com.yihu.ehr.profile.util.DataSetUtil;
import com.yihu.ehr.service.resource.stage1.extractor.KeyDataExtractor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * 标准档案包解析器.
 *
 * @author Sand
 * @created 2015.09.09 15:04
 */
@Component
public class StdPackageResolver extends PackageResolver {
    @Override
    public void resolve(StandardPackage profile, File root) throws IOException, Exception {
        File standardFolder = new File(root.getAbsolutePath() + File.separator + PackModelFactory.StandardFolder);
        parseFiles(profile, standardFolder.listFiles(), false);

        File originFolder = new File(root.getAbsolutePath() + File.separator + PackModelFactory.OriginFolder);
        parseFiles(profile, originFolder.listFiles(), true);
    }

    /**
     * 结构化档案包解析JSON文件中的数据。
     */
    private void parseFiles(StandardPackage profile, File[] files, boolean origin) throws Exception, IOException {
        for (File file : files) {
            PackageDataSet dataSet = generateDataSet(file, origin);

            String dataSetCode = origin ? DataSetUtil.originDataSetCode(dataSet.getCode()) : dataSet.getCode();
            dataSet.setCode(dataSetCode);

            // Extract key data from data set if exists
            if (!origin) {
                //就诊卡信息
                if (StringUtils.isEmpty(profile.getCardId())) {
                    Map<String,Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.CardInfo);
                    String cardId = (String) properties.get(MasterResourceFamily.BasicColumns.CardId);
                    if(!StringUtils.isEmpty(cardId))
                    {
                        profile.setCardId(cardId);
                        profile.setCardType((String) properties.get(MasterResourceFamily.BasicColumns.CardType));
                    }
                }

                //身份信息
                if (StringUtils.isEmpty(profile.getDemographicId()) || StringUtils.isEmpty(profile.getPatientName())) {
                    Map<String,Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.DemographicInfo);

                    String demographicId = (String) properties.get(MasterResourceFamily.BasicColumns.DemographicId);
                    if(!StringUtils.isEmpty(demographicId) &&StringUtils.isEmpty(profile.getDemographicId())) {
                        profile.setDemographicId(demographicId);
                    }

                    String patientName =(String) properties.get(MasterResourceFamily.BasicColumns.PatientName);
                    if(!StringUtils.isEmpty(patientName) &&StringUtils.isEmpty(profile.getPatientName())) {
                        profile.setPatientName(patientName);
                    }
                }

                //就诊事件信息
                if (profile.getEventDate() == null) {
                    Map<String,Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.EventInfo);
                    Date eventDate = (Date)properties.get(MasterResourceFamily.BasicColumns.EventDate);
                    if(eventDate!=null)
                    {
                        profile.setEventDate(eventDate);
                        profile.setEventType((EventType) properties.get(MasterResourceFamily.BasicColumns.EventType));
                    }
                }

                //门诊/住院诊断
                Map<String,Object> properties = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.Diagnosis);
                List<String> diagnosisList = (List<String>)properties.get(MasterResourceFamily.BasicColumns.Diagnosis);
                if(diagnosisList!=null && diagnosisList.size()>0) {
                    profile.setDiagnosisList(diagnosisList);
                }
            }

            profile.setPatientId(dataSet.getPatientId());
            profile.setEventNo(dataSet.getEventNo());
            profile.setOrgCode(dataSet.getOrgCode());
            profile.setCdaVersion(dataSet.getCdaVersion());
            profile.setCreateDate(dataSet.getCreateTime());
            profile.insertDataSet(dataSetCode, dataSet);
        }
    }


    /**
     * 生产数据集
     *
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
