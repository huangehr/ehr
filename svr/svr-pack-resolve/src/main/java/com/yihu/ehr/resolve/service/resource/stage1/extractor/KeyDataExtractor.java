package com.yihu.ehr.resolve.service.resource.stage1.extractor;

import com.yihu.ehr.profile.util.PackageDataSet;

/**
 * 关键数据提取器。如：身份证号，事件号，卡号等。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.10.13 9:12
 */
public abstract class KeyDataExtractor {

    public enum Filter{
        CardInfo,        //就診卡号和就诊卡类型
        Identity,   //身份证和姓名
        EventInfo,     //事件时间和事件类型
        Diagnosis     //门诊/住院诊断
    }

    public abstract Object extract(PackageDataSet dataSet) throws Exception;
}
