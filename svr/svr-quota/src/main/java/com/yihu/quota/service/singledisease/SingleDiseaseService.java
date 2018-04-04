package com.yihu.quota.service.singledisease;

import com.yihu.quota.etl.extract.es.EsExtract;
import com.yihu.quota.etl.util.ElasticsearchUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by wxw on 2018/2/27.
 */
@Service
public class SingleDiseaseService {
    @Autowired
    private EsExtract esExtract;
    @Autowired
    private ElasticsearchUtil elasticsearchUtil;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static final String HEALTH_PROBLEM = "1"; // 健康问题
    public static final String AGE = "2"; // 年龄段分布
    public static final String SEX = "3"; // 性别
    /**
     * 热力图数据
     * @return
     * @throws Exception
     */
    public List<Map<String,String>>  getHeatMap(String condition) throws Exception {
        List<Map<String,String>> list = new ArrayList<>();
        String sql = "select addressLngLat from single_disease_personal_index where addressLngLat is not null";
        if (!StringUtils.isEmpty(condition)) {
            sql += " and " + condition;
        }
        List<Map<String, Object>> listData = parseIntegerValue(sql);
        Map<String, Object> map = new HashMap<>();
        if (null != listData && listData.size() > 0 && listData.get(0).size() > 0) {
            listData.forEach(item -> {
                map.put(item.get("addressLngLat") + "", 1);
            });
            map.forEach((k,v)->{
                Map<String, String> temp = new HashMap<>();
                // "k":"116.419787;39.930658"
                String lng = k.split(";")[0];
                String lat = k.split(";")[1];
                temp.put("lng", lng);
                temp.put("lat", lat);
                temp.put("count", v + "");
                list.add(temp);
            });
        }
        return list;
    }

    /**
     * 获取糖尿病患者数
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getNumberOfDiabetes(String condition) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("select town, count(*) from single_disease_personal_index where town is not null");
        if (!StringUtils.isEmpty(condition)) {
            sql.append(" and " + condition);
        }
        sql.append(" group by town");
        List<Map<String, Object>> list = parseIntegerValue(sql.toString());
        List<Map<String, Object>> dataList = fillNoDataColumn(list);
        return dataList;
    }

    /**
     * 补全福州各个区县的患病人数
     * @param dataList
     * @return
     */
    private List<Map<String, Object>> fillNoDataColumn(List<Map<String, Object>> dataList) {
        String townSql = "SELECT id as town, name as townName from address_dict where pid = 350100";
        Map<String, Object> dictMap = new HashMap<>();
        List<Map<String, Object>> dictDataList = jdbcTemplate.queryForList(townSql);
        if(null != dictDataList) {
            for(int i = 0 ; i < dictDataList.size();i++){
                if(null != dictDataList.get(i).get("town") && null != dictDataList.get(i).get("townName")){
                    dictMap.put(dictDataList.get(i).get("town").toString(),dictDataList.get(i).get("townName").toString());
                }
            }
        }

        List<Map<String, Object>> resultList = new ArrayList<>();
        for(String code : dictMap.keySet()){
            Map<String,Object> oneMap = new HashMap<>();
            String result = "0";
            for(Map<String,Object> map : dataList){
                if(map.get("town") != null && code.equals(map.get("town"))){
                    result = map.get("COUNT(*)").toString();
                    break;
                }
            }
            oneMap.put("townName",dictMap.get(code));
            oneMap.put("town",code);
            oneMap.put("result",result);
            resultList.add(oneMap);
        }
        return  resultList;
    }

    /**
     * 新增患者年趋势
     * @return
     */
    public Map<String, List<String>> getLineDataInfo(String condition) {
        StringBuffer sql = new StringBuffer();
        sql.append("select eventDate, count(*) from single_disease_personal_index");
        if (!StringUtils.isEmpty(condition)) {
            sql.append(" where " + condition);
        }
        sql.append(" group by date_histogram(field='eventDate','interval'='year')");
        List<Map<String, Object>> listData = parseIntegerValue(sql.toString());
        Map<String, List<String>> map = new HashMap<>();
        List<String> xData = new ArrayList<>();
        List<String> valueData = new ArrayList<>();
        if (null != listData && listData.size() > 0 && listData.get(0).size() > 0) {
            listData.forEach(one -> {
                // "date_histogram(field=eventDate,interval=year)":"2015-01-01 00:00:00",因为是获取年份，所以截取前4位
                xData.add((one.get("date_histogram(field=eventDate,interval=year)") + "").substring(0,4));
                valueData.add(one.get("COUNT(*)") + "");
            });
            map.put("xData", xData);
            map.put("valueData", valueData);
        }
        return map;
    }

    /**
     * 获取饼状图数据
     * @param type 健康状况、年龄段、性别
     * @return
     */
    public Map<String, Object> getPieDataInfo(String type, String condition) {
        Map<String, Object> map = new HashMap<>();
        if (HEALTH_PROBLEM.equals(type)) {
            map = getHealthProInfo(condition);
        } else if (AGE.equals(type)) {
            map = getAgeInfo(condition);
        } else if (SEX.equals(type)) {
            map = getGenderInfo(condition);
        }
        return map;
    }

    /**
     * 获取健康状况
     * @return
     */
    public Map<String, Object> getHealthProInfo(String condition) {
        StringBuffer sql = new StringBuffer();
        sql.append("select diseaseTypeName,count(*) from single_disease_personal_index");
        if (!StringUtils.isEmpty(condition)) {
            sql.append(" where " + condition);
        }
        sql.append(" group by diseaseTypeName");
        List<Map<String, Object>> listData = parseIntegerValue(sql.toString());
        Map<String, Object> map = new HashMap<>();
        List<String> legendData = new ArrayList<>();
        List<Map<String, Object>> seriesData = new ArrayList<>();
        if (null != listData && listData.size() > 0 && listData.get(0).size() > 0) {
            listData.forEach(one -> {
                Map<String, Object> myMap = new HashMap<>();
                legendData.add(one.get("diseaseTypeName") + "");
                myMap.put("name", one.get("diseaseTypeName") + "");
                myMap.put("value", one.get("COUNT(*)") + "");
                seriesData.add(myMap);
            });
            map.put("legendData", legendData);
            map.put("seriesData", seriesData);
        }
        return map;
    }

    /**
     * 获取年龄段数据
     * @return
     */
    public Map<String, Object> getAgeInfo(String condition) {
        StringBuffer sql = new StringBuffer();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) + 1;
        /*
        * 年龄段分为0-6、7-17、18-40、41-65、65以上
        * 首先获取当前年份，由于ES查询是左包含，右不包，所以当前年份需要+1
        * 下面为构造年龄段的算式，其中year-151限定了范围是66-150岁 即66以上，其他类似
        * */
        String range = "range(birthYear," + (year - 151) + "," + (year - 66) + "," + (year - 41) + "," + (year - 18) + "," + (year - 7) + "," + year + ")";
        sql.append("select count(*) from single_disease_personal_index where birthYear <> 0");
        if (!StringUtils.isEmpty(condition)) {
            sql.append(" and " + condition);
        }
        sql.append(" group by " + range);
        List<Map<String, Object>> listData = parseIntegerValue(sql.toString());
        Map<String, Object> map = new HashMap<>();
        List<String> legendData = new ArrayList<>();
        List<Map<String, Object>> seriesData = new ArrayList<>();
        if (null != listData && listData.size() > 0 && listData.get(0).size() > 0) {
            listData.forEach(one -> {
                String rangeName = one.get(range) + "";
                // rangeName："1978.0-2001.0"
                int first = (int) Double.parseDouble(rangeName.split("-")[0]);
                int last = (int) Double.parseDouble(rangeName.split("-")[1]);
                Integer result = last - first;
                // 转成相应的年龄段
                String keyName = exchangeInfo(result);
                Map<String, Object> myMap = new HashMap<>();
                legendData.add(keyName);
                myMap.put("name", keyName);
                myMap.put("value", one.get("COUNT(*)") + "");
                seriesData.add(myMap);
            });
            map.put("legendData", legendData);
            map.put("seriesData", seriesData);
        }
        return map;
    }

    private String exchangeInfo(Integer result) {
        String keyName = "";
        switch (result) {
            case 85:
                keyName = "66岁以上";
                break;
            case 25:
                keyName = "41-65岁";
                break;
            case 23:
                keyName = "18-40岁";
                break;
            case 11:
                keyName = "7-17岁";
                break;
            case 7:
                keyName = "0-6岁";
                break;
            default:
                break;
        }
        return keyName;
    }

    /**
     * 获取性别数据
     * @return
     */
    public Map<String, Object> getGenderInfo(String condition) {
        StringBuffer sql = new StringBuffer();
        sql.append("select sexName, count(*) from single_disease_personal_index");
        if (!StringUtils.isEmpty(condition)) {
            sql.append(" where " + condition);
        }
        sql.append(" group by sexName");
        List<Map<String, Object>> listData = parseIntegerValue(sql.toString());
        Map<String, Object> map = new HashMap<>();
        List<String> legendData = new ArrayList<>();
        List<Map<String, Object>> seriesData = new ArrayList<>();
        if (null != listData && listData.size() > 0 && listData.get(0).size() > 0) {
            listData.forEach(one -> {
                Map<String, Object> myMap = new HashMap<>();
                legendData.add(one.get("sexName") + "");
                myMap.put("name", one.get("sexName") + "");
                myMap.put("value", one.get("COUNT(*)") + "");
                seriesData.add(myMap);
            });
            map.put("legendData", legendData);
            map.put("seriesData", seriesData);
        }
        return map;
    }

    /**
     * 获取并发症数据
     * @return
     */
    public Map<String, List<String>> getSymptomDataInfo(String condition) {
        StringBuffer sql = new StringBuffer();
        sql.append("select symptomName, count(*) from single_disease_check_index where checkCode = 'CH001'");
        if (!StringUtils.isEmpty(condition)) {
            sql.append(" and " + condition);
        }
        sql.append(" group by symptomName");
        List<Map<String, Object>> listData = parseIntegerValue(sql.toString());
        Map<String, List<String>> map = new HashMap<>();
        List<String> xData = new ArrayList<>();
        List<String> valueData = new ArrayList<>();
        if (null != listData && listData.size() > 0 && listData.get(0).size() > 0) {
            listData.forEach(one -> {
                xData.add(one.get("symptomName") + "");
                valueData.add(one.get("COUNT(*)") + "");
            });
            map.put("xData", xData);
            map.put("valueData", valueData);
        }
        return map;
    }

    /**
     * 用药患者数分布
     * @return
     */
    public Map<String, List<String>> getMedicineDataInfo(String condition) {
        StringBuffer sql = new StringBuffer();
        sql.append("select medicineName, count(*) from single_disease_check_index where checkCode = 'CH004'");
        if (!StringUtils.isEmpty(condition)) {
            sql.append(" and " + condition);
        }
        sql.append(" group by medicineName");
        List<Map<String, Object>> listData = parseIntegerValue(sql.toString());
        Map<String, List<String>> map = new HashMap<>();
        List<String> xData = new ArrayList<>();
        List<String> valueData = new ArrayList<>();
        if (null != listData && listData.size() > 0 && listData.get(0).size() > 0) {
            listData.forEach(one -> {
                xData.add(one.get("medicineName") + "");
                valueData.add(one.get("COUNT(*)") + "");
            });
            map.put("xData", xData);
            map.put("valueData", valueData);
        }
        return map;
    }

    /**
     *  空腹血糖统计
     * @return
     */
    public Map<String, List<String>> getFastingBloodGlucoseDataInfo(String condition) {
        StringBuffer sql = new StringBuffer();
        sql.append("select fastingBloodGlucoseCode, count(*) from single_disease_check_index where checkCode = 'CH002'");
        if (!StringUtils.isEmpty(condition)) {
            //先把过滤条件忽略性别的过滤
            condition = changeCondition(condition);
            sql.append(" and " + condition);
        }
        sql.append(" group by fastingBloodGlucoseCode");
        List<Map<String, Object>> list = parseIntegerValue(sql.toString());
        Map<String, List<String>> map = new HashMap<>();
        List<String> xData = new LinkedList<>();
        // 获取横坐标
        xData.add("4.4~6.1mmol/L");
        xData.add("6.1~7mmol/L");
        xData.add("7.0mmol/L以上");
        Map<String,String> resultDataMap = new HashMap<>();
//        if (null != list && list.get(0).size() > 0) {
//            list.forEach(one -> {
//                String code =  one.get("fastingBloodGlucoseCode") + "";
//                String count = one.get("COUNT(*)") + "";
//                String gender = one.get("sexName") + "";
//                if(!code.equals("null") && StringUtils.isNotEmpty(code)){
//                    resultDataMap.put(code + "-"+ gender,count);
//                }
//                resultDataMap.put(code,count);
//            });
//
//            for(int i =1;i<4 ;i++){
//                if( !resultDataMap.containsKey(i + "-" + "男性")) {
//                    resultDataMap.put(i + "-" + "男性","0");
//                }
//                if( !resultDataMap.containsKey(i + "-" + "女性")) {
//                    resultDataMap.put(i + "-" + "女性","0");
//                }
//            }
//
//            List<String> valueData1 = new LinkedList<>();    // 存放第一个数据源 男生
//            List<String> valueData2 = new LinkedList<>();    // 存放第二个数据源 女生
//            map.put("xData", xData);
//            for(String key : resultDataMap.keySet()){
//                if(key.contains("男性")){
//                    valueData1.add(resultDataMap.get(key)+"");
//                }
//                if(key.contains("女性")){
//                    valueData2.add(resultDataMap.get(key)+"");
//                }
//            }
//            map.put("valueData1", valueData1);
//            map.put("valueData2", valueData2);
//        }

        //无性别输出
        List<String> valueData = new ArrayList<>();
        if (null != list && list.size() > 0 && list.get(0).size() > 0) {
            list.forEach(one -> {
                valueData.add(one.get("COUNT(*)") + "");
            });
            map.put("xData", xData);
            map.put("valueData", valueData);
        }
        return map;
    }

    /**
     * 糖耐量统计
     * @return
     */
    public Map<String, List<String>> getSugarToleranceDataInfo(String condition) {
        StringBuffer sql = new StringBuffer();
        sql.append("select sugarToleranceCode, count(*) from single_disease_check_index where checkCode = 'CH003'");
        if (!StringUtils.isEmpty(condition)) {
            //先把过滤条件忽略性别的过滤
            condition = changeCondition(condition);
            sql.append(" and " + condition);
        }
        sql.append(" group by sugarToleranceCode");
        List<Map<String, Object>> list = parseIntegerValue(sql.toString());
        Map<String, List<String>> map = new HashMap<>();
        List<String> xData = new LinkedList<>();
        // 获取横坐标
        xData.add("7.8 mmol/L以下");
        xData.add("7.8~11.1 mmol/L");
        xData.add("11.1 mmol/L以上");
//        Map<String,String> resultDataMap = new HashMap<>();
//        if (null != list && list.get(0).size() > 0) {
//            list.forEach(one -> {
//                String code =  one.get("sugarToleranceCode") + "";
//                String gender = one.get("sexName") + "";
//                String count = one.get("COUNT(*)") + "";
//                if(!code.equals("null") && StringUtils.isNotEmpty(code)){
//                    resultDataMap.put(code + "-"+ gender,count);
//                }
//            });
//
//            for(int i =1;i<4 ;i++){
//                if( !resultDataMap.containsKey(i + "-" + "男性")) {
//                    resultDataMap.put(i + "-" + "男性","0");
//                }
//                if( !resultDataMap.containsKey(i + "-" + "女性")) {
//                    resultDataMap.put(i + "-" + "女性","0");
//                }
//            }
//
//            List<String> valueData1 = new LinkedList<>();    // 存放第一个数据源 男生
//            List<String> valueData2 = new LinkedList<>();    // 存放第二个数据源 女生
//            map.put("xData", xData);
//            for(String key : resultDataMap.keySet()){
//                if(key.contains("男性")){
//                    valueData1.add(resultDataMap.get(key)+"");
//                }
//                if(key.contains("女性")){
//                    valueData2.add(resultDataMap.get(key)+"");
//                }
//            }
//            map.put("valueData1", valueData1);
//            map.put("valueData2", valueData2);
//        }

        //无性别输出
        List<String> valueData = new ArrayList<>();
        if (null != list && list.size() > 0 && list.get(0).size() > 0) {
            list.forEach(one -> {
                valueData.add(one.get("COUNT(*)") + "");
            });
            map.put("xData", xData);
            map.put("valueData", valueData);
        }
        return map;
    }

    /**
     * 对查询结果key包含count、sum的value去掉小数点
     * @param sql
     * @return
     */
    public List<Map<String, Object>> parseIntegerValue(String sql) {
        List<Map<String, Object>> listData = elasticsearchUtil.excuteDataModel(sql);
        List<Map<String, Object>> handleData = new ArrayList<>();
        listData.forEach(item -> {
            Map<String, Object> myMap = new HashMap<>();
            item.forEach((k,v) -> {
                if (k.contains("COUNT") || k.contains("SUM")) {
                    v = (int) Double.parseDouble(v + "");
                }
                myMap.put(k,v);
            });
            handleData.add(myMap);
        });
        return handleData;
    }

    /**
     * 采集出来的空腹血糖和糖耐量用户的性别基本都是未知（状态为0），所以先忽略性别的过滤
     * @param condition
     * @return
     */
    private String changeCondition(String condition) {
        StringBuffer buffer = new StringBuffer();
        String[] ands = condition.split("and");
        for (String and : ands) {
            if (!and.contains("sex")) {
                buffer.append(and.trim() + " and ");
            }
        }
        String newCondition = buffer.toString();
        return newCondition.substring(0, newCondition.length() - 4).trim();
    }
}
