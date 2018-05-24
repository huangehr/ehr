package com.yihu.ehr.profile.extractor;

import com.yihu.ehr.profile.family.MasterResourceFamily;
import com.yihu.ehr.profile.util.MetaDataRecord;
import com.yihu.ehr.profile.util.PackageDataSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 科室信息抽取
 * Created by progr1mmer on 2018/5/24.
 */
@Component
@ConfigurationProperties(prefix = "ehr.pack-extractor.dept")
public class DeptExtractor extends KeyDataExtractor {

    //界定数据集
    private List<String> dataSets = new ArrayList<String>();
    //科室代码界定数据元
    private List<String> metaData = new ArrayList<>();

    @Override
    public Map<String, Object> extract(PackageDataSet dataSet) throws Exception {
        Map<String,Object> properties = new HashMap<>();
        String deptCode = null;
        if (dataSets.contains(dataSet.getCode())) {
            for (String rowKey : dataSet.getRecordKeys()) {
                MetaDataRecord record = dataSet.getRecord(rowKey);
                for (String metaDataCode : metaData) {
                    String _deptCode = record.getMetaData(metaDataCode);
                    if (!StringUtils.isEmpty(_deptCode)) {
                        deptCode = _deptCode;
                    }
                }
            }
        }
        properties.put(MasterResourceFamily.BasicColumns.DeptCode, deptCode);
        return properties;
    }

    public List<String> getDataSets() {
        return dataSets;
    }

    public List<String> getMetaData() {
        return metaData;
    }
}
