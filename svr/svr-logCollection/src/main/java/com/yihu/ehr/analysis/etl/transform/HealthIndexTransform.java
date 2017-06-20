package com.yihu.ehr.analysis.etl.transform;

import com.yihu.ehr.analysis.etl.ETLConstantData;
import com.yihu.ehr.analysis.entity.UserPortrait;
import com.yihu.ehr.analysis.etl.BusinessTypeEnum;
import com.yihu.ehr.analysis.etl.ILogTransform;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lyr-pc on 2017/2/28.
 */
public class HealthIndexTransform implements ILogTransform {

    private final BusinessTypeEnum logType = BusinessTypeEnum.index;

    @Override
    public List<UserPortrait> transform(JSONObject log) throws Exception {
        JSONObject logData = log.getJSONObject("data");
        JSONObject businessData = logData.getJSONObject("data");
        List<UserPortrait> labelInfoList = new ArrayList<>();
        String type = businessData.has("type") ? businessData.get("type").toString() : "";

        if (type.equals("1")) {
            labelInfoList.addAll(transformXueTang(businessData));
        } else if (type.equals("2")) {
            labelInfoList.addAll(transformXueYa(businessData));
        } else if (type.equals("3")) {

        } else if (type.equals("4")) {

        }

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
     * 血糖分析
     *
     * @param log
     * @return
     */
    public List<UserPortrait> transformXueTang(JSONObject log) {
        List<UserPortrait> portraits = new ArrayList<>();
        boolean higher = false, lower = false;

        // 早餐前
        String value1 = log.has("value1") ? log.get("value1").toString() : "";
        // 早餐后
        String value2 = log.has("value2") ? log.get("value2").toString() : "";
        // 午餐前
        String value3 = log.has("value3") ? log.get("value3").toString() : "";
        // 午餐后
        String value4 = log.has("value4") ? log.get("value4").toString() : "";
        // 晚餐前
        String value5 = log.has("value5") ? log.get("value5").toString() : "";
        // 晚餐后
        String value6 = log.has("value6") ? log.get("value6").toString() : "";
        // 睡觉前
        String value7 = log.has("value7") ? log.get("value7").toString() : "";

        int v1 = StringUtils.isEmpty(value1) ? 0 : ETLConstantData.xueTangBefore(Double.parseDouble(value1));
        int v2 = StringUtils.isEmpty(value2) ? 0 : ETLConstantData.xueTangAfter(Double.parseDouble(value2));
        int v3 = StringUtils.isEmpty(value3) ? 0 : ETLConstantData.xueTangBefore(Double.parseDouble(value3));
        int v4 = StringUtils.isEmpty(value4) ? 0 : ETLConstantData.xueTangAfter(Double.parseDouble(value4));
        int v5 = StringUtils.isEmpty(value5) ? 0 : ETLConstantData.xueTangBefore(Double.parseDouble(value5));
        int v6 = StringUtils.isEmpty(value6) ? 0 : ETLConstantData.xueTangAfter(Double.parseDouble(value6));
        int v7 = StringUtils.isEmpty(value7) ? 0 : ETLConstantData.xueTangBefore(Double.parseDouble(value7));

        if (v1 == 1 || v2 == 1 || v3 == 1 || v4 == 1 || v5 == 1 || v6 == 1 || v7 == 1) {
            higher = true;
        }
        if (v1 == -1 || v2 == -1 || v3 == -1 || v4 == -1 || v5 == -1 || v6 == -1 || v7 == -1) {
            lower = true;
        }

        if (higher) {
            UserPortrait labelInfo = new UserPortrait();

            labelInfo.setUserCode(log.getString("user"));
            labelInfo.setCategory("1002");
            labelInfo.setSubCategory("1002002");
            labelInfo.setValue("血糖偏高");
            labelInfo.setCzrq(new Date());

            portraits.add(labelInfo);
        }

        if (lower) {
            UserPortrait labelInfo = new UserPortrait();

            labelInfo.setUserCode(log.getString("user"));
            labelInfo.setCategory("1002");
            labelInfo.setSubCategory("1002002");
            labelInfo.setValue("血糖偏低");
            labelInfo.setCzrq(new Date());

            portraits.add(labelInfo);
        }

        return portraits;
    }

    /**
     * 血压分析
     *
     * @param log
     * @return
     */
    public List<UserPortrait> transformXueYa(JSONObject log) {
        List<UserPortrait> portraits = new ArrayList<>();

        // 收缩压
        String value1 = log.has("value1") ? log.get("value1").toString() : "";
        // 舒张压
        String value2 = log.has("value2") ? log.get("value2").toString() : "";

        int v1 = StringUtils.isEmpty(value1) ? 0 : ETLConstantData.ssy(Double.parseDouble(value1));
        int v2 = StringUtils.isEmpty(value2) ? 0 : ETLConstantData.szy(Double.parseDouble(value2));

        if (v1 == 1 || v2 == 1) {
            UserPortrait labelInfo = new UserPortrait();

            labelInfo.setUserCode(log.getString("user"));
            labelInfo.setCategory("1002");
            labelInfo.setSubCategory("1002003");
            labelInfo.setValue("血压偏高");
            labelInfo.setCzrq(new Date());

            portraits.add(labelInfo);
        }

        if (v1 == -1 || v2 == -1) {
            UserPortrait labelInfo = new UserPortrait();

            labelInfo.setUserCode(log.getString("user"));
            labelInfo.setCategory("1002");
            labelInfo.setSubCategory("1002003");
            labelInfo.setValue("血压偏低");
            labelInfo.setCzrq(new Date());

            portraits.add(labelInfo);
        }

        return portraits;
    }

    public static void main(String[] args) throws Exception {
        HealthIndexTransform transform = new HealthIndexTransform();
        List<UserPortrait> portraits = transform.transform(new JSONObject("{\"logType\":8,\"caller\":\"\",\"time\":\"2017-03-03\",\"data\":{\"patient\":\"test_P20160828002\",\"businessType\":\"9\",\"data\":{\"id\":1,\"user\":\"test_P20160828002\",\"intervene\":\"\",\"value1\":\"11111\",\"value2\":\"11111\",\"value3\":\"\",\"value4\":\"\",\"value5\":\"\",\"value6\":\"\",\"type\":1,\"sort_date\":\"2016\\/8\\/24 00:00:00\",\"czrq\":\"2017\\/3\\/1 11:14:55\",\"del\":\"1\",\"device_sn\":\"\",\"idcard\":\"\"}}}"));
        portraits.addAll(transform.transform(new JSONObject("{\"logType\":8,\"caller\":\"\",\"time\":\"2017-03-03\",\"data\":{\"patient\":\"test_P20160828002\",\"businessType\":\"9\",\"data\":{\"id\":1,\"user\":\"test_P20160828002\",\"intervene\":\"\",\"value1\":\"11111\",\"value2\":\"11111\",\"value3\":\"\",\"value4\":\"\",\"value5\":\"\",\"value6\":\"\",\"type\":2,\"sort_date\":\"2016\\/8\\/24 00:00:00\",\"czrq\":\"2017\\/3\\/1 11:14:55\",\"del\":\"1\",\"device_sn\":\"\",\"idcard\":\"\"}}}")));
        System.out.println(new JSONArray(portraits).toString());
    }
}
