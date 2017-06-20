package com.yihu.ehr.analysis.etl.transform;

import com.yihu.ehr.analysis.etl.BusinessTypeEnum;
import com.yihu.ehr.analysis.entity.UserPortrait;
import com.yihu.ehr.analysis.etl.ILogTransform;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 咨询日志提取分析
 *
 * Created by lyr-pc on 2017/2/17.
 */
public class ConsultLogTransform implements ILogTransform {

    private final BusinessTypeEnum logType = BusinessTypeEnum.consult;

    @Override
    public List<UserPortrait> transform(JSONObject log) {
        JSONObject logData = log.getJSONObject("data");
        JSONObject businessData = logData.getJSONObject("data");
        List<UserPortrait> labelInfoList = new ArrayList<>();
        // 慢病分析
        labelInfoList.addAll(transformGxyTnbMb(businessData));

        return labelInfoList;
    }

    @Override
    public int getLogType() {
        return logType.ordinal();
    }

    @Override
    public String getLogTypeName() {
        return logType.name();
    }

    /**
     * 慢病分析
     *
     * @param log
     * @return
     */
    public List<UserPortrait> transformGxyTnbMb(JSONObject log) {
        String symptoms = log.getString("symptoms");
        List<UserPortrait> userPortraits = new ArrayList<>();

        if(symptoms.contains("糖尿病")) {
            UserPortrait labelInfo = new UserPortrait();

            labelInfo.setUserCode(log.getString("patient"));
            labelInfo.setCategory("1002");
            labelInfo.setSubCategory("1002001");
            labelInfo.setValue("糖尿病");
            labelInfo.setCzrq(new Date());

            userPortraits.add(labelInfo);
        }

        if(symptoms.contains("高血压")) {
            UserPortrait labelInfo = new UserPortrait();

            labelInfo.setUserCode(log.getString("patient"));
            labelInfo.setCategory("1002");
            labelInfo.setSubCategory("1002001");
            labelInfo.setValue("高血压");
            labelInfo.setCzrq(new Date());

            userPortraits.add(labelInfo);
        }

        if(symptoms.contains("慢病")) {
            UserPortrait labelInfo = new UserPortrait();

            labelInfo.setUserCode(log.getString("patient"));
            labelInfo.setCategory("1002");
            labelInfo.setSubCategory("1002001");
            labelInfo.setValue("慢病人群");
            labelInfo.setCzrq(new Date());

            userPortraits.add(labelInfo);
        }

        return userPortraits;
    }
}
