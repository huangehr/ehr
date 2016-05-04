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
@ConfigurationProperties(prefix = "cda-document-type-options")
public class CdaDocumentTypeOptions {
    // 数据集代码与CDA类别ID之间的映射
    Map<Integer, String> eventType = new HashMap<>();

    public Map<Integer, String> getEventType() {
        return eventType;
    }

    @PostConstruct
    public void postConstruct() {
        Set<Integer> keys = new HashSet<>(this.eventType.keySet());
        /*for (String key : keys) {
            String value = this.eventType.remove(key);

            key = key.replaceAll("^\\d{1,2}\\.", "");
            this.eventType.put(key, value);
            if(!key.contains(DataSetUtil.OriginDataSetFlag)) this.eventType.put(DataSetUtil.originDataSetCode(key), value);
        }*/
    }

    public String getCdaDocumentTypeId(String eventType){
        return this.eventType.get(eventType);
    }
}
