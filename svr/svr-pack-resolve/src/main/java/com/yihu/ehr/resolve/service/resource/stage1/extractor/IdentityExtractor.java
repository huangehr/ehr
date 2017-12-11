package com.yihu.ehr.resolve.service.resource.stage1.extractor;

import com.yihu.ehr.profile.family.MasterResourceFamily;
import com.yihu.ehr.profile.util.MetaDataRecord;
import com.yihu.ehr.profile.util.PackageDataSet;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 身份信息提取。
 *
 * @author hzp
 * @version 2.0
 * @created 2017.04.23
 */
@Component
@ConfigurationProperties(prefix = "ehr.pack-extractor.identity")
public class IdentityExtractor extends KeyDataExtractor {

    //包含身份信息的数据集
    private List<String> dataSets = new ArrayList<>();
    @Value("${ehr.pack-extractor.identity.meta-data.id-card-no}")
    private String IdCardNo;
    @Value("${ehr.pack-extractor.identity.meta-data.id-card-type}")
    private String IdCardType;
    @Value("${ehr.pack-extractor.identity.meta-data.patient-name}")
    private String PatientName;
    private static final String IdCardNoDictEntry = "01;02";    // 身份字典项代码：身份证号与护照

    @Override
    public Map<String,Object> extract(PackageDataSet dataSet) throws Exception {
        Map<String,Object> properties = new HashedMap();
        String demographicId = "";
        String patientName = "";
        //获取身份证和姓名
        if (dataSets.contains(dataSet.getCode())) {
            for (String key : dataSet.getRecordKeys()) {
                MetaDataRecord record = dataSet.getRecord(key);
                //获取身份证号码
                if(StringUtils.isEmpty(demographicId)) {
                    String val = record.getMetaData(IdCardType);
                    if (StringUtils.isEmpty(val)) {
                        // default as identity card no
                        demographicId = record.getMetaData(IdCardNo);
                    } else {
                        if (IdCardNoDictEntry.contains(val)) {
                            demographicId = record.getMetaData(IdCardNo);
                        }
                    }
                }
                //获取姓名
                if(StringUtils.isEmpty(patientName)) {
                    String val = record.getMetaData(PatientName);
                    if (val != null) {
                        patientName = val;
                    }
                }
            }
        }
        properties.put(MasterResourceFamily.BasicColumns.DemographicId, demographicId);
        properties.put(MasterResourceFamily.BasicColumns.PatientName, patientName);
        return properties;
    }

    public List<String> getDataSets() {
        return this.dataSets;
    }
}
