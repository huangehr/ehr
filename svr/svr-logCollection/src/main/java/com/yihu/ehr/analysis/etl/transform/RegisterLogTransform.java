package com.yihu.ehr.analysis.etl.transform;

import com.yihu.ehr.analysis.etl.BusinessTypeEnum;
import com.yihu.ehr.analysis.etl.ETLConstantData;
import com.yihu.ehr.analysis.etl.util.IdcardUtil;
import com.yihu.ehr.analysis.etl.ILogTransform;
import com.yihu.ehr.analysis.entity.UserPortrait;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 注册日志信息提取分析
 * <p>
 * Created by lyr-pc on 2017/2/17.
 */
public class RegisterLogTransform implements ILogTransform {

    private final BusinessTypeEnum logType = BusinessTypeEnum.register;

    @Override
    public List<UserPortrait> transform(JSONObject log) throws Exception {
        JSONObject logData = log.getJSONObject("data");
        JSONObject businessData = logData.getJSONObject("data");
        List<UserPortrait> labelInfoList = new ArrayList<>();

        // 性别
        UserPortrait sexInfo = tranformSex(businessData);
        if (sexInfo != null) labelInfoList.add(sexInfo);

        // 年龄
        List<UserPortrait> ageInfo = tranformAge(businessData);
        if (ageInfo != null && ageInfo.size() > 0) labelInfoList.addAll(ageInfo);

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
     * 获取性别信息
     *
     * @param log
     * @return
     */
    private UserPortrait tranformSex(JSONObject log) throws Exception {
        String idcard = log.getString("idcard");
        UserPortrait labelInfo = new UserPortrait();
        int sex = 3;
        if (StringUtils.isNotEmpty(idcard)) {
            sex = IdcardUtil.getSexForIdcard(idcard);
        } else {
            return null;
        }

        labelInfo.setUserCode(log.getString("code"));
        labelInfo.setCategory("1001");
        labelInfo.setSubCategory("1001001");
        labelInfo.setValue(ETLConstantData.sexName(sex));
        labelInfo.setCzrq(new Date());

        return labelInfo;
    }

    /**
     * 获取性别信息
     *
     * @param log
     * @return
     */
    private List<UserPortrait> tranformAge(JSONObject log) throws Exception {
        String idcard = log.getString("idcard");

        int age = 0;
        if (StringUtils.isNotEmpty(idcard)) {
            age = IdcardUtil.getAgeFromIdcard(idcard);
        } else {
            return null;
        }

        UserPortrait labelAgeInfo = new UserPortrait();
        labelAgeInfo.setUserCode(log.getString("code"));
        labelAgeInfo.setCategory("1001");
        labelAgeInfo.setSubCategory("1001002");
        labelAgeInfo.setValue(String.valueOf(age));
        labelAgeInfo.setCzrq(new Date());


        UserPortrait labelInfo = new UserPortrait();
        labelInfo.setUserCode(log.getString("code"));
        labelInfo.setCategory("1001");
        labelInfo.setSubCategory("1001003");
        labelInfo.setValue(ETLConstantData.ageInterval(age));
        labelInfo.setCzrq(new Date());

        return Arrays.asList(labelAgeInfo, labelInfo);
    }


    /**
     * 获取城市信息
     *
     * @param log
     * @return
     */
    private UserPortrait transformCity(JSONObject log) {
        String city = log.has("city") ? log.get("city").toString() : "";
        String idcard = log.getString("idcard");

        if(StringUtils.isEmpty(city)) {
            city = idcard.substring(0, 5);
        }

        UserPortrait labelAgeInfo = new UserPortrait();
        labelAgeInfo.setUserCode(log.getString("code"));
        labelAgeInfo.setCategory("1001");
        labelAgeInfo.setSubCategory("1001004");
        labelAgeInfo.setValue(ETLConstantData.cityName(city));
        labelAgeInfo.setCzrq(new Date());

        return labelAgeInfo;
    }
}