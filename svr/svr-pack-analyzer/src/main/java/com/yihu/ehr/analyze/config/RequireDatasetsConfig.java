package com.yihu.ehr.analyze.config;

import com.yihu.ehr.profile.EventType;
import com.yihu.ehr.profile.exception.AnalyzerException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 就诊事件必要数据集配置
 * Created by progr1mmer on 2018/7/23.
 */
@Component
@ConfigurationProperties(prefix = "ehr.require-data-sets")
public class RequireDatasetsConfig {

    private List<String> clinic = new ArrayList<>();
    private List<String> resident = new ArrayList<>();
    private List<String> medicalExam = new ArrayList<>();

    public List<String> getRequireDataset(EventType eventType) {
        switch (eventType) {
            case Clinic:
                return clinic;
            case Resident:
                return resident;
            case MedicalExam:
                return medicalExam;
            default:
                throw new AnalyzerException("Unknown event type " + eventType);
        }
    }

    public List<String> getClinic() {
        return clinic;
    }

    public List<String> getResident() {
        return resident;
    }

    public List<String> getMedicalExam() {
        return medicalExam;
    }

}
