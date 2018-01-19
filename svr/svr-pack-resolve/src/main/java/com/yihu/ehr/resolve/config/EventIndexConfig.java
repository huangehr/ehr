package com.yihu.ehr.resolve.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 *  病人事件索引信息字段
 * @author HZY
 * @version 1.0
 * @created 2017.07.03 17:03
 */
@Component
@ConfigurationProperties(prefix = "ehr.eventIndex",locations = "application.yml")
public class EventIndexConfig {
    private List<String> eventNo = new ArrayList<>();
    private List<String> patientId = new ArrayList<>();
    private List<String> eventTime = new ArrayList<>();


    public List<String> getEventNo() {
        return eventNo;
    }

    public List<String> getPatientId() {
        return patientId;
    }

    public List<String> getEventTime() {
        return eventTime;
    }

}
