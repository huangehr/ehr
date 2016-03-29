package com.yihu.ehr.extractor;

import com.yihu.ehr.profile.ProfileDataSet;
import com.yihu.ehr.util.DateFormatter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.*;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.10.13 9:23
 */
@Component
@ConfigurationProperties(prefix = "extractor.event")
public class EventExtractor extends KeyDataExtractor {
    private Map<String, String> dataSets = new HashMap<>();       // 事件数据集
    private List<String> metaData = new ArrayList<>();            // 事件时间数据元

    @Override
    public Object extract(ProfileDataSet profileDataSet, Filter filter) throws ParseException {
        if (filter == Filter.EventDate && dataSets.containsKey(profileDataSet.getCode())) {
            for (String key : profileDataSet.getRecordKeys()) {
                Map<String, String> record = profileDataSet.getRecord(key);
                for (String recordKey : record.keySet()) {
                    if (metaData.contains(recordKey)) {
                        String value = record.get(recordKey);
                        if (value != null) {
                            return DateFormatter.simpleDateTimeParse(value);
                        }
                    }
                }
            }
        }

        return null;
    }

    @PostConstruct
    public void postConstruct() {
        Set<String> keys = new HashSet<>(this.dataSets.keySet());
        for (String key : keys) {
            String value = this.dataSets.remove(key);

            key = key.substring(key.indexOf('.') + 1);
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
