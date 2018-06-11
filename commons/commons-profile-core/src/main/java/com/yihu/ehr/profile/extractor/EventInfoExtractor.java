package com.yihu.ehr.profile.extractor;

import com.yihu.ehr.profile.EventType;
import com.yihu.ehr.profile.family.ResourceCells;
import com.yihu.ehr.profile.util.DataSetUtil;
import com.yihu.ehr.profile.model.PackageDataSet;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 事件时间与类型抽取器。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.10.13 9:23
 */
@Component
@ConfigurationProperties(prefix = "ehr.pack-extractor.event")
public class EventInfoExtractor extends KeyDataExtractor {

    //事件界定数据集
    private Map<String, String> dataSets = new HashMap<>();

    @Override
    public Map<String, Object> extract(PackageDataSet dataSet) throws Exception {
        Map<String,Object> properties = new HashMap<>();
        EventType eventType = null;
        if (dataSets.containsKey(dataSet.getCode())) {
            eventType = EventType.valueOf(dataSets.get(dataSet.getCode()));
        }
        properties.put(ResourceCells.EVENT_TYPE, eventType);
        return properties;
    }

    @PostConstruct
    public void postConstruct() {
        Set<String> keys = new HashSet<>(this.dataSets.keySet());
        for (String key : keys) {
            String value = this.dataSets.remove(key);
            key = key.replaceAll("^\\d{1,2}\\.", "");
            this.dataSets.put(key, value);
            if (!key.endsWith(DataSetUtil.OriginDataSetFlag)){
                this.dataSets.put(DataSetUtil.originDataSetCode(key), value);
            }
        }
    }

    public Map<String, String> getDataSets() {
        return this.dataSets;
    }
}
