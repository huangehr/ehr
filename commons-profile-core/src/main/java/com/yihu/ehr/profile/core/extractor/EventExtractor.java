package com.yihu.ehr.profile.core.extractor;

import com.yihu.ehr.profile.core.profile.DataRecord;
import com.yihu.ehr.profile.core.profile.EventType;
import com.yihu.ehr.profile.core.profile.StdDataSet;
import com.yihu.ehr.util.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.*;

/**
 * 事件时间与类型抽取器。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.10.13 9:23
 */
@Component
@ConfigurationProperties(prefix = "extractor.event")
public class EventExtractor extends KeyDataExtractor {
    private Map<String, String> dataSets = new HashMap<>();       // 事件界定数据集
    private List<String> metaData = new ArrayList<>();            // 事件时间数据元

    @Override
    public Object extract(StdDataSet dataSet, Filter filter) throws ParseException {
        if (dataSets.containsKey(dataSet.getCode())) {
            if (filter == Filter.EventDate) {
                for (String rowKey : dataSet.getRecordKeys()) {
                    DataRecord record = dataSet.getRecord(rowKey);
                    for (String metaDataCode : metaData) {
                        if (StringUtils.isNotEmpty(record.getMetaData(metaDataCode))) {
                            String value = record.getMetaData(metaDataCode);
                            return DateTimeUtils.simpleDateTimeParse(value);
                        }
                    }
                }
            } else if (filter == Filter.EventType){
                return EventType.valueOf(dataSets.get(dataSet.getCode()));
            }
        }

        return null;
    }

    @PostConstruct
    public void postConstruct() {
        Set<String> keys = new HashSet<>(this.dataSets.keySet());
        for (String key : keys) {
            String value = this.dataSets.remove(key);

            key = key.replaceAll("^\\d{1,2}\\.", "");
            this.dataSets.put(key, value);
        }
    }

    public Map<String, String> getDataSets() {
        return this.dataSets;
    }

    public List<String> getMetaData() {
        return this.metaData;
    }
}
