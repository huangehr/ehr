package com.yihu.quota.service.singledisease;

import com.yihu.quota.etl.extract.es.EsExtract;
import com.yihu.quota.etl.util.ElasticsearchUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(SingleDiseaseService.class);

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
        String sql = "select addressLngLat from singleDiseasePersonal where addressLngLat is not null";
        if (!StringUtils.isEmpty(condition) && !condition.contains("undefined")) {
            sql += " and " + condition;
        }
        log.info("sql = " + sql);
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
        sql.append("select town, count(*) from singleDiseasePersonal where town is not null");
        if (!StringUtils.isEmpty(condition)&& !condition.contains("undefined")) {
            sql.append(" and " + condition);
        }
        sql.append(" group by town");
        log.info("sql = " + sql.toString());
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
        sql.append("select eventDate, count(*) from singleDiseasePersonal");
        if (!StringUtils.isEmpty(condition)&& !condition.contains("undefined")) {
            sql.append(" where " + condition);
        }
        sql.append(" group by date_histogram(field='eventDate','interval'='year')");
        log.info("sql = " + sql.toString());
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
        if(condition.contains("undefined")){
            condition = "";
        }
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
        sql.append("select diseaseTypeName,count(*) from singleDiseasePersonal");
        if (!StringUtils.isEmpty(condition)) {
            sql.append(" where " + condition);
        }
        sql.append(" group by diseaseTypeName");
        log.info("sql = " + sql.toString());
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
        sql.append("select count(*) from singleDiseasePersonal where birthYear <> 0");
        if (!StringUtils.isEmpty(condition)) {
            sql.append(" and " + condition);
        }
        sql.append(" group by " + range);
        log.info("sql = " + sql.toString());
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
        sql.append("select sexName, count(*) from singleDiseasePersonal");
        if (!StringUtils.isEmpty(condition)) {
            sql.append(" where " + condition);
        }
        sql.append(" group by sexName");
        log.info("sql = " + sql.toString());
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
        sql.append("select symptomName, count(*) from singleDiseaseCheck where checkCode = 'CH001'");
        if (!StringUtils.isEmpty(condition)) {
            sql.append(" and " + condition);
        }
        sql.append(" group by symptomName");
        log.info("sql = " + sql.toString());
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
        sql.append("select medicineName, count(*) from singleDiseaseCheck where checkCode = 'CH004'");
        if (!StringUtils.isEmpty(condition)) {
            sql.append(" and " + condition);
        }
        sql.append(" group by medicineName");
        log.info("sql = " + sql.toString());
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
        sql.append("select fastingBloodGlucoseCode, count(*) from singleDiseaseCheck where checkCode = 'CH002'");
        if (!StringUtils.isEmpty(condition) && !condition.contains("undefined")) {
            //先把过滤条件忽略性别的过滤
            condition = changeCondition(condition);
            sql.append(" and " + condition);
        }
        sql.append(" group by fastingBloodGlucoseCode");
        log.info("sql = " + sql.toString());
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
        sql.append("select sugarToleranceCode, count(*) from singleDiseaseCheck where checkCode = 'CH003'");
        if (!StringUtils.isEmpty(condition) && !condition.contains("undefined")) {
            //先把过滤条件忽略性别的过滤
            condition = changeCondition(condition);
            sql.append(" and " + condition);
        }
        sql.append(" group by sugarToleranceCode");
        log.info("sql = " + sql.toString());
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

    /**
     * 获取糖尿病类型分析
     * @param type
     * @param filter
     * @return
     */
    public Map<String, List<String>> getDiseaseTypeAnalysisInfo(String type, String filter) {
        String sql = "";
        if ("1".equals(type)) {
            sql = "select count(*) from singleDiseasePersonal group by diseaseTypeName,date_histogram(field='eventDate','interval'='year') order by eventDate,diseaseType";
        } else {
            if(filter.isEmpty()){
                sql = "select count(*) from singleDiseasePersonal group by diseaseTypeName,date_histogram(field='eventDate','interval'='month')";
            }else {
                sql = "select count(*) from singleDiseasePersonal  where " + filter + " group by diseaseTypeName,date_histogram(field='eventDate','interval'='month')";
            }
        }
        log.info("sql = " + sql);
        List<Map<String, Object>> listData = parseIntegerValue(sql);
        Map<String, List<String>> map = new HashMap<>();
        List<String> xData = new ArrayList<>();
        List<String> valueData = new ArrayList<>();
        Set<String> hashSet = new TreeSet<>();
        Map<String, String> value = new HashMap<>();
        if (null != listData && listData.size() > 0 && listData.get(0).size() > 0) {
            if ("1".equals(type)) {
                listData.forEach(one -> {
                    hashSet.add((one.get("date_histogram(field=eventDate,interval=year)") + "").substring(0,4));
                    value.put((one.get("date_histogram(field=eventDate,interval=year)") + "").substring(0,4) + "-"+ one.get("diseaseTypeName"), one.get("COUNT(*)") + "");
                });
            } else {
                hashSet.clear();
                hashSet.addAll(getMonthInfo());
                listData.forEach(one -> {
                    value.put((one.get("date_histogram(field=eventDate,interval=month)") + "").substring(5,7) + "-"+ one.get("diseaseTypeName"), one.get("COUNT(*)") + "");
                });
            }
        }
        List<String> name = new ArrayList<>();
        name.add("I型糖尿病");
        name.add("II型糖尿病");
        name.add("妊娠糖尿病");
        name.add("其他糖尿病");
        if (hashSet.size() > 0 && value.size() > 0) {
            for (String year : hashSet) {
                for (int i = 1; i <= 4; i++) {
                    if (!value.containsKey(year + "-I型糖尿病")) {
                        value.put(year + "-I型糖尿病", "0");
                    }
                    if (!value.containsKey(year + "-II型糖尿病")) {
                        value.put(year + "-II型糖尿病", "0");
                    }
                    if (!value.containsKey(year + "-妊娠糖尿病")) {
                        value.put(year + "-妊娠糖尿病", "0");
                    }
                    if (!value.containsKey(year + "-其他糖尿病")) {
                        value.put(year + "-其他糖尿病", "0");
                    }
                }

            }
            List<String> list1 = new ArrayList<>(); // I型糖尿病
            List<String> list2 = new ArrayList<>(); // II型糖尿病
            List<String> list3 = new ArrayList<>(); // 妊娠糖尿病
            List<String> list4 = new ArrayList<>(); // 其他糖尿病
            for (String year : hashSet) {
                for (String key : value.keySet()) {
                    if (key.equals(year + "-I型糖尿病")) {
                        list1.add(value.get(year + "-I型糖尿病"));
                    }
                    if (key.equals(year + "-II型糖尿病")) {
                        list2.add(value.get(year + "-II型糖尿病"));
                    }
                    if (key.equals(year + "-妊娠糖尿病")) {
                        list3.add(value.get(year + "-妊娠糖尿病"));
                    }
                    if (key.equals(year + "-其他糖尿病")) {
                        list4.add(value.get(year + "-其他糖尿病"));
                    }
                }
            }
            xData.clear();
            xData.addAll(hashSet);
            map.put("name", name);
            map.put("xName", xData);
            map.put("type1", list1);
            map.put("type2", list2);
            map.put("type3", list3);
            map.put("type4", list4);
        }
        return map;
    }

    /**
     * 性别分析
     * @param type
     * @param filter
     * @return
     */
    public Map<String, List<String>> getSexAnalysisInfo(String type, String filter) {
        String sql = "";
        if ("1".equals(type)) {
            sql = "select count(*) from singleDiseasePersonal group by sexName,date_histogram(field='eventDate','interval'='year')";
        } else {
            if(filter.isEmpty()){
                sql = "select count(*) from singleDiseasePersonal group by sexName,date_histogram(field='eventDate','interval'='month')";
            }else {
                sql = "select count(*) from singleDiseasePersonal where " + filter + "group by sexName,date_histogram(field='eventDate','interval'='month')";
            }
        }
        log.info("sql = " + sql);
        List<Map<String, Object>> listData = parseIntegerValue(sql);
        Map<String, List<String>> map = new HashMap<>();
        List<String> xData = new ArrayList<>();
        Set<String> hashSet = new TreeSet<>();
        Map<String, String> value = new HashMap<>();
        if (null != listData && listData.size() > 0 && listData.get(0).size() > 0) {
            if ("1".equals(type)) {
                listData.forEach(one -> {
                    hashSet.add((one.get("date_histogram(field=eventDate,interval=year)") + "").substring(0,4));
                    value.put((one.get("date_histogram(field=eventDate,interval=year)") + "").substring(0,4) + "-"+ one.get("sexName"), one.get("COUNT(*)") + "");
                });
            } else {
                hashSet.clear();
                hashSet.addAll(getMonthInfo());
                listData.forEach(one -> {
                    value.put((one.get("date_histogram(field=eventDate,interval=month)") + "").substring(5,7) + "-"+ one.get("sexName"), one.get("COUNT(*)") + "");
                });
            }
        }
        List<String> name = new ArrayList<>();
        name.add("女性");
        name.add("男性");
        name.add("未知");
        if (hashSet.size() > 0 && value.size() > 0) {
            for (String year : hashSet) {
                for (int i = 1; i <= 3; i++) {
                    if (!value.containsKey(year + "-女性")) {
                        value.put(year + "-女性", "0");
                    }
                    if (!value.containsKey(year + "-男性")) {
                        value.put(year + "-男性", "0");
                    }
                    if (!value.containsKey(year + "-未知")) {
                        value.put(year + "-未知", "0");
                    }
                }

            }
            List<String> list1 = new ArrayList<>(); // 女性
            List<String> list2 = new ArrayList<>(); // 男性
            List<String> list3 = new ArrayList<>(); // 未知
            for (String year : hashSet) {
                for (String key : value.keySet()) {
                    if (key.equals(year + "-女性")) {
                        list1.add(value.get(year + "-女性"));
                    }
                    if (key.equals(year + "-男性")) {
                        list2.add(value.get(year + "-男性"));
                    }
                    if (key.equals(year + "-未知")) {
                        list3.add(value.get(year + "-未知"));
                    }
                }
            }
            xData.clear();
            xData.addAll(hashSet);
            map.put("name", name);
            map.put("xName", xData);
            map.put("type1", list1);
            map.put("type2", list2);
            map.put("type3", list3);
        }
        return map;
    }

    /**
     * 年龄段分析
     * @param type
     * @param filter
     * @return
     */
    public Map<String, List<String>> getAgeAnalysisInfo(String type, String filter) {
        String sql = "";
        Calendar calendar = Calendar.getInstance();
        int years = calendar.get(Calendar.YEAR) + 1;
        /*
        * 年龄段分为0-6、7-17、18-40、41-65、65以上
        * 首先获取当前年份，由于ES查询是左包含，右不包，所以当前年份需要+1
        * 下面为构造年龄段的算式，其中year-151限定了范围是66-150岁 即66以上，其他类似
        * */
        String range = "range(birthYear," + (years - 151) + "," + (years - 66) + "," + (years - 41) + "," + (years - 18) + "," + (years - 7) + "," + years + ")";
        if ("1".equals(type)) {
            sql = "select count(birthYear) from singleDiseasePersonal group by " + range + ",date_histogram(field='eventDate','interval'='year')";
        } else {
            if(filter.isEmpty()){
                sql = "select count(birthYear) from singleDiseasePersonal group by " + range + ",date_histogram(field='eventDate','interval'='month')";
            }else {
                sql = "select count(birthYear) from singleDiseasePersonal where " + filter + " group by " + range + ",date_histogram(field='eventDate','interval'='month')";
            }
        }
        log.info("sql = " + sql);
        List<Map<String, Object>> listData = parseIntegerValue(sql);
        Map<String, List<String>> map = new HashMap<>();
        List<String> xData = new ArrayList<>();
        Set<String> hashSet = new TreeSet<>();
        Map<String, String> value = new HashMap<>();
        if (null != listData && listData.size() > 0 && listData.get(0).size() > 0) {
            if ("1".equals(type)) {
                listData.forEach(one -> {
                    String rangeName = one.get(range) + "";
                    // rangeName："1978.0-2001.0"
                    int first = (int) Double.parseDouble(rangeName.split("-")[0]);
                    int last = (int) Double.parseDouble(rangeName.split("-")[1]);
                    Integer result = last - first;
                    // 转成相应的年龄段
                    String keyName = exchangeInfo(result);
                    hashSet.add((one.get("date_histogram(field=eventDate,interval=year)") + "").substring(0,4));
                    value.put((one.get("date_histogram(field=eventDate,interval=year)") + "").substring(0,4) + "-"+ keyName, one.get("COUNT(birthYear)") + "");
                });
            } else {
                hashSet.clear();
                hashSet.addAll(getMonthInfo());
                listData.forEach(one -> {
                    String rangeName = one.get(range) + "";
                    // rangeName："1978.0-2001.0"
                    int first = (int) Double.parseDouble(rangeName.split("-")[0]);
                    int last = (int) Double.parseDouble(rangeName.split("-")[1]);
                    Integer result = last - first;
                    // 转成相应的年龄段
                    String keyName = exchangeInfo(result);
                    String strDate = one.get("date_histogram(field=eventDate,interval=month)") + "";
                    if(StringUtils.isNotEmpty(strDate) && strDate.length() > 7){
                        value.put(strDate.substring(5, 7) + "-"+ keyName, one.get("COUNT(birthYear)") + "");
                    }
                });
            }
        }
        List<String> name = new ArrayList<>();
        name.add("0-6岁");
        name.add("7-17岁");
        name.add("18-40岁");
        name.add("41-65岁");
        name.add("66岁以上");
        if (hashSet.size() > 0 && value.size() > 0) {
            for (String year : hashSet) {
                for (int i = 1; i <= 5; i++) {
                    if (!value.containsKey(year + "-0-6岁")) {
                        value.put(year + "-0-6岁", "0");
                    }
                    if (!value.containsKey(year + "-7-17岁")) {
                        value.put(year + "-7-17岁", "0");
                    }
                    if (!value.containsKey(year + "-18-40岁")) {
                        value.put(year + "-18-40岁", "0");
                    }
                    if (!value.containsKey(year + "-41-65岁")) {
                        value.put(year + "-41-65岁", "0");
                    }
                    if (!value.containsKey(year + "-66岁以上")) {
                        value.put(year + "-66岁以上", "0");
                    }
                }

            }
            List<String> list1 = new ArrayList<>(); // 0-6岁
            List<String> list2 = new ArrayList<>(); // 7-17岁
            List<String> list3 = new ArrayList<>(); // 18-40岁
            List<String> list4 = new ArrayList<>(); // 41-65岁
            List<String> list5 = new ArrayList<>(); // 66岁以上
            for (String year : hashSet) {
                for (String key : value.keySet()) {
                    if (key.equals(year + "-0-6岁")) {
                        list1.add(value.get(year + "-0-6岁"));
                    }
                    if (key.equals(year + "-7-17岁")) {
                        list2.add(value.get(year + "-7-17岁"));
                    }
                    if (key.equals(year + "-18-40岁")) {
                        list3.add(value.get(year + "-18-40岁"));
                    }
                    if (key.equals(year + "-41-65岁")) {
                        list4.add(value.get(year + "-41-65岁"));
                    }
                    if (key.equals(year + "-66岁以上")) {
                        list5.add(value.get(year + "-66岁以上"));
                    }
                }
            }
            xData.clear();
            xData.addAll(hashSet);
            map.put("name", name);
            map.put("xName", xData);
            map.put("type1", list1);
            map.put("type2", list2);
            map.put("type3", list3);
            map.put("type4", list4);
            map.put("type5", list5);
        }
        return map;
    }

    /**
     *
     * @param i
     * @param type 1糖尿病类型 2性别 3年龄
     * @return
     */
    public String getNameByIdType(int i, String type) {
        String result = "";
        if ("1".equals(type)) {
            switch (i) {
                case 1:
                    result = "I型糖尿病";
                    break;
                case 2:
                    result = "II型糖尿病";
                    break;
                case 3:
                    result = "妊娠糖尿病";
                    break;
                case 4:
                    result = "其他糖尿病";
                    break;
                default:
                    break;
            }
        } else if ("2".equals(type)) {
            switch (i) {
                case 1:
                    result = "女性";
                    break;
                case 2:
                    result = "男性";
                    break;
                case 3:
                    result = "未知";
                    break;
                default:
                    break;
            }
        } else if ("3".equals(type)) {
            switch (i) {
                case 1:
                    result = "0-6岁";
                    break;
                case 2:
                    result = "7-17岁";
                    break;
                case 3:
                    result = "18-40岁";
                    break;
                case 4:
                    result = "41-65岁";
                    break;
                case 5:
                    result = "66岁以上";
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    public Set<String> getMonthInfo() {
        Set<String> hashSet = new TreeSet<>();
        hashSet.add("01");
        hashSet.add("02");
        hashSet.add("03");
        hashSet.add("04");
        hashSet.add("05");
        hashSet.add("06");
        hashSet.add("07");
        hashSet.add("08");
        hashSet.add("09");
        hashSet.add("10");
        hashSet.add("11");
        hashSet.add("12");
        return hashSet;
    }
}
