package com.yihu.ehr.profile.config;

import com.yihu.ehr.profile.core.EventType;
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
    Map<String, String> eventType = new TreeMap<>();

    public Map<String, String> getEventType() {
        return eventType;
    }

    @PostConstruct
    public void postConstruct() {
        Set<String> keys = new HashSet<>(this.eventType.keySet());
        for (String key : keys) {
            String value = this.eventType.remove(key);

            key = key.replaceAll("^\\d{1,2}\\.", "");
            this.eventType.put(key, value);
        }
    }

    public String getCdaDocumentTypeId(String event){
        return eventType.get(event);
    }
}
