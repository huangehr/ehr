package com.yihu.ehr.service.resource.stage1.extractor;

import com.yihu.ehr.profile.family.MasterResourceFamily;
import com.yihu.ehr.profile.util.MetaDataRecord;
import com.yihu.ehr.profile.util.PackageDataSet;
import org.apache.commons.collections.map.HashedMap;
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
    // 界定数据集
    private List<String> dataSets = new ArrayList<String>();
    // 数据元
    private List<String> metaData = new ArrayList<>();

    @Override
    public Map<String,Object> extract(PackageDataSet dataSet) throws Exception {
        Map<String,Object> properties = new HashedMap();
        List<String> diagnosisList = new ArrayList<>();
        if (dataSets.contains(dataSet.getCode())) {
            for (String rowKey : dataSet.getRecordKeys()) {
                MetaDataRecord record = dataSet.getRecord(rowKey);
                //获取门诊或住院诊断
                for (String metaDataCode : metaData) {
                    String value = record.getMetaData(metaDataCode);
                    if (!StringUtils.isEmpty(value) && !diagnosisList.contains(value)) {
                        diagnosisList.add(value);
                    }
                }
            }
        }
        properties.put(MasterResourceFamily.BasicColumns.Diagnosis, diagnosisList);
        return properties;
    }

    public List<String> getDataSets() {
        return this.dataSets;
    }

    public List<String> getMetaData() {
        return this.metaData;
    }
}
