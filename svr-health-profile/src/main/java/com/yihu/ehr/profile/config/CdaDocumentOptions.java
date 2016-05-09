package com.yihu.ehr.profile.config;

import com.yihu.ehr.profile.util.DataSetUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.11 20:38
 */
@Component
@ConfigurationProperties(prefix = "cda-document")
public class CdaDocumentOptions {
    // 数据集代码与CDA类别ID之间的映射
    Map<String, String> primaryDataSet = new TreeMap<>();

    public Map<String, String> getPrimaryDataSet() {
        return primaryDataSet;
    }

    @PostConstruct
    public void postConstruct() {
        Set<String> keys = new HashSet<>(this.primaryDataSet.keySet());
        for (String key : keys) {
            String value = this.primaryDataSet.remove(key);

            key = key.replaceAll("^\\d{1,2}\\.", "");
            this.primaryDataSet.put(key, value);
            if(!key.contains(DataSetUtil.OriginDataSetFlag)) this.primaryDataSet.put(DataSetUtil.originDataSetCode(key), value);
        }
    }

    public boolean isPrimaryDataSet(String dataSetCode){
        if (StringUtils.isEmpty(dataSetCode)) return false;
        if (dataSetCode.startsWith("HDSA")) return false;

        return primaryDataSet.containsKey(dataSetCode);
    }

    public String getCdaDocumentTypeId(String dataSetCode){
        return primaryDataSet.get(dataSetCode);
    }
}
