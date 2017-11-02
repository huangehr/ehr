package com.yihu.ehr.service.resource.stage1.extractor;

import com.yihu.ehr.profile.util.PackageDataSet;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

/**
 * @author hzp
 * @version 2.0
 * @created 2017.04.23
 */
@Component
public class ExtractorChain {
    @Autowired
    private ApplicationContext context;

    public Map<String,Object> doExtract(PackageDataSet dataSet, KeyDataExtractor.Filter filter) throws Exception {

        Map<String,Object> re = new HashedMap();
        //就診卡号和就诊卡类型
        if(filter.equals(KeyDataExtractor.Filter.CardInfo)) {
            re = context.getBean(CardInfoExtractor.class).extract(dataSet);
        }
        //事件时间和事件类型
        else if(filter.equals(KeyDataExtractor.Filter.EventInfo)) {
            re = context.getBean(EventInfoExtractor.class).extract(dataSet);
        }
        //身份证和姓名
        else if(filter.equals(KeyDataExtractor.Filter.Identity)) {
            re = context.getBean(IdentityExtractor.class).extract(dataSet);
        }
        //门诊和住院诊断
        else if(filter.equals(KeyDataExtractor.Filter.Diagnosis)) {
            re = context.getBean(DiagnosisExtractor.class).extract(dataSet);
        }
        return re;
    }
}
