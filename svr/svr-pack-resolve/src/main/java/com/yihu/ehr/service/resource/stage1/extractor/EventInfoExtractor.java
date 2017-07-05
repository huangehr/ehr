package com.yihu.ehr.service.resource.stage1.extractor;

import com.yihu.ehr.constants.EventType;
import com.yihu.ehr.profile.family.MasterResourceFamily;
import com.yihu.ehr.profile.util.DataSetUtil;
import com.yihu.ehr.profile.util.MetaDataRecord;
import com.yihu.ehr.profile.util.PackageDataSet;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
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
    private Map<String, String> dataSets = new HashMap<>();       // 事件界定数据集
    private List<String> metaData = new ArrayList<>();            // 事件时间数据元

    @Override
    public Map<String,Object> extract(PackageDataSet dataSet) throws Exception {
        Map<String,Object> properties = new HashedMap();

        Date eventDate = null;
        EventType eventType = null;
        if (dataSets.containsKey(dataSet.getCode())) {
            for (String rowKey : dataSet.getRecordKeys()) {
                MetaDataRecord record = dataSet.getRecord(rowKey);

                //获取就诊时间
                if(eventDate == null) {
                    for (String metaDataCode : metaData) {
                        String value = record.getMetaData(metaDataCode);
                        if (StringUtils.isNotEmpty(value)) {
                            eventDate = DateUtil.strToDate(value);
                        }

                        if(eventDate != null) {
                            eventType = EventType.valueOf(dataSets.get(dataSet.getCode()));
                            break;
                        }
                    }
                }
            }

        }

        properties.put(MasterResourceFamily.BasicColumns.EventDate,eventDate);
        properties.put(MasterResourceFamily.BasicColumns.EventType,eventType);

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

    public List<String> getMetaData() {
        return this.metaData;
    }
}
