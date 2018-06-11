package com.yihu.ehr.profile.extractor;

import com.yihu.ehr.profile.family.ResourceCells;
import com.yihu.ehr.profile.model.MetaDataRecord;
import com.yihu.ehr.profile.model.PackageDataSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 门诊/住院诊断抽取器。
 *
 * @author hzp
 * @created 2017.06.01
 */
@Component
@ConfigurationProperties(prefix = "ehr.pack-extractor.diagnosis")
public class DiagnosisExtractor extends KeyDataExtractor {
    //界定数据集
    private List<String> dataSets = new ArrayList<String>();
    //诊断代码数据元
    private List<String> codeMetaData = new ArrayList<>();
    //诊断名称数据元
    private List<String> nameMetaData = new ArrayList<>();

    @Override
    public Map<String, Object> extract(PackageDataSet dataSet) throws Exception {
        Map<String,Object> properties = new HashMap<>();
        Set<String> diagnosis = new HashSet<>();
        Set<String> diagnosisName = new HashSet<>();
        if (dataSets.contains(dataSet.getCode())) {
            for (String rowKey : dataSet.getRecordKeys()) {
                MetaDataRecord record = dataSet.getRecord(rowKey);
                //获取门诊或住院诊断代码
                for (String metaDataCode : codeMetaData) {
                    String icd10Code = record.getMetaData(metaDataCode);
                    if (!StringUtils.isEmpty(icd10Code)) {
                        diagnosis.add(icd10Code);
                    }
                }
                //获取门诊或住院诊断名称
                for (String metaDataCode : nameMetaData) {
                    String value = record.getMetaData(metaDataCode);
                    if (!StringUtils.isEmpty(value)) {
                        diagnosisName.add(value);
                    }
                }
            }
        }
        properties.put(ResourceCells.DIAGNOSIS, diagnosis);
        properties.put(ResourceCells.DIAGNOSIS_NAME, diagnosisName);
        return properties;
    }

    public List<String> getDataSets() {
        return this.dataSets;
    }

    public List<String> getCodeMetaData() {
        return codeMetaData;
    }

    public List<String> getNameMetaData() {
        return nameMetaData;
    }
}
