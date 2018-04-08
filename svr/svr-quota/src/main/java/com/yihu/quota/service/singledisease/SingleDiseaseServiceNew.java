package com.yihu.quota.service.singledisease;

import com.yihu.quota.etl.extract.es.EsExtract;
import com.yihu.quota.etl.util.ElasticsearchUtil;
import com.yihu.quota.vo.DictModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by wxw on 2018/2/27.
 */
@Service
public class SingleDiseaseServiceNew {
    @Autowired
    private EsExtract esExtract;
    @Autowired
    private ElasticsearchUtil elasticsearchUtil;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static final String HEALTHPROBLEM = "1"; // 疾病类型
    public static final String AGE = "2"; // 年龄段分布
    public static final String SEX = "3"; // 性别
    public static final String SYMPTOM = "4"; // 并发症
    /**
     * 热力图数据
     * @return
     * @throws Exception
     */
    public List<Map<String,String>>  getHeatMap() throws Exception {
        List<Map<String,String>> list = new ArrayList<>();
        String sql = "select addressLngLat from single_disease_personal_index where addressLngLat is not null ";
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
    public List<Map<String, Object>> getNumberOfDiabetes() throws Exception {
        String sql = "select town, count(*) from single_disease_personal_index where town is not null group by town";
        List<Map<String, Object>> list = parseIntegerValue(sql);
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
    public Map<String, List<String>> getLineDataInfo() {
        String sql = "select eventDate, count(*) from single_disease_personal_index group by date_histogram(field='eventDate','interval'='year')";
        List<Map<String, Object>> listData = parseIntegerValue(sql);
        Map<String, List<String>> map = new HashMap<>();
        List<String> xData = new ArrayList<>();
        List<String> valueData = new ArrayList<>();
        if (null != listData && listData.get(0).size() > 0) {
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
     * @param code 字典code
     * @return
     */
    public Map<String, Object> getPieDataInfo(String type, String code) {
        Map<String, Object> map = new HashMap<>();
        if (HEALTHPROBLEM.equals(type)) {
            map = getHealthProInfo(code);
        } else if (AGE.equals(type)) {
            map = getAgeInfo();
        } else if (SEX.equals(type)) {
            map = getGenderInfo();
        }
        return map;
    }

    /**
     * 疾病类型
     * @return
     */
    public Map<String, Object> getHealthProInfo(String code) {
        String sql = "select diseaseTypeName,count(*) from single_disease_personal_index group by diseaseTypeName";
        List<Map<String, Object>> listData = parseIntegerValue(sql);
        Map<String, Object> map = new HashMap<>();
        List<String> legendData = new ArrayList<>();
        List<Map<String, Object>> seriesData = new ArrayList<>();
        if (null != listData && listData.get(0).size() > 0) {
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
     * 获取全市总人口数
     * @param healthMap
     * @param code
     * @return
     */
    public Map<String, Object> getHealthCountInfo(Map<String, Object> healthMap, String code) {
        String sql = "select code, value as name from system_dict_entries where dict_id = 158 and code = ?";
        List<DictModel> dictDatas = jdbcTemplate.query(sql, new BeanPropertyRowMapper(DictModel.class), code);

        healthMap.put("name", "健康人群");
        healthMap.put("value", null != dictDatas && dictDatas.size() > 0 ? dictDatas.get(0).getName() : "0");
        return healthMap;
    }

    /**
     * 获取年龄段数据
     * @return
     */
    public Map<String, Object> getAgeInfo() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) + 1;
        /*
        * 年龄段分为0-6、7-17、18-40、41-65、65以上
        * 首先获取当前年份，由于ES查询是左包含，右不包，所以当前年份需要+1
        * 下面为构造年龄段的算式，其中year-151限定了范围是66-150岁 即66以上，其他类似
        * */
        String range = "range(birthYear," + (year - 151) + "," + (year - 66) + "," + (year - 41) + "," + (year - 18) + "," + (year - 7) + "," + year + ")";
        String sql = "select count(*) from single_disease_personal_index where birthYear <> 0  group by " + range;
        List<Map<String, Object>> listData = parseIntegerValue(sql);
        Map<String, Object> map = new HashMap<>();
        List<String> legendData = new ArrayList<>();
        List<Map<String, Object>> seriesData = new ArrayList<>();
        if (null != listData && listData.get(0).size() > 0) {
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
    public Map<String, Object> getGenderInfo() {
        String sql = "select sexName, count(*) from single_disease_personal_index group by sexName";
        List<Map<String, Object>> listData = parseIntegerValue(sql);
        Map<String, Object> map = new HashMap<>();
        List<String> legendData = new ArrayList<>();
        List<Map<String, Object>> seriesData = new ArrayList<>();
        if (null != listData && listData.get(0).size() > 0) {
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
     * 数据查询
     * @return
     */
    public Map<String, List<String>> getDataInfo(String sql ,String xdataName) {
        List<Map<String, Object>> listData = parseIntegerValue(sql);
        Map<String, List<String>> map = new HashMap<>();
        List<String> xData = new ArrayList<>();
        List<String> valueData = new ArrayList<>();
        if (null != listData && listData.get(0).size() > 0) {
            listData.forEach(one -> {
                if(xdataName.contains("date_histogram")){
                    xData.add(one.get(xdataName).toString().substring(0,4) + "");
                }else {
                    xData.add(one.get(xdataName) + "");
                }
                valueData.add(one.get("count") + "");
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
    public Map<String, List<String>> getFastingBloodGlucoseDataInfo() {
        String sql = "select fastingBloodGlucoseCode, count(*) from single_disease_check_index where checkCode = 'CH002' group by fastingBloodGlucoseCode";
        List<Map<String, Object>> list = parseIntegerValue(sql);
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
        if (null != list && list.get(0).size() > 0) {
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
    public Map<String, List<String>> getSugarToleranceDataInfo() {
        String sql = "select sugarToleranceCode, count(*) from single_disease_check_index where checkCode = 'CH003' group by sugarToleranceCode";
        List<Map<String, Object>> list = parseIntegerValue(sql);
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
        if (null != list && list.get(0).size() > 0) {
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
                if (k.contains("COUNT") || k.contains("SUM") || k.contains("count")) {
                    v = (int) Double.parseDouble(v + "");
                }
                myMap.put(k,v);
            });
            handleData.add(myMap);
        });
        return handleData;
    }



    /**
     * 获取饼状图数据
     * @param type 健康状况、年龄段、性别
     * @param groupName 分组字段
     * @return
     */
    public Map<String, Object> getPieDataInfoBySql(String type, String sql,String groupName) {
        Map<String, Object> map = new HashMap<>();
        if (HEALTHPROBLEM.equals(type)) {
            map = getHealthProInfoBySql(sql, groupName);
        } else if (AGE.equals(type)) {
            map = getAgeInfoBySql(sql);
        } else if (SEX.equals(type)) {
            map = getHealthProInfoBySql(sql, groupName);
        }else if (SYMPTOM.equals(type)) {
            map = getHealthProInfoBySql(sql, groupName);
        }
        return map;
    }

    /**
     * @return
     */
    public Map<String, Object> getHealthProInfoBySql(String sql,String groupName) {
        List<Map<String, Object>> listData = parseIntegerValue(sql);
        Map<String, Object> map = new HashMap<>();
        List<String> legendData = new ArrayList<>();
        List<Map<String, Object>> seriesData = new ArrayList<>();
        if (null != listData && listData.get(0).size() > 0) {
            listData.forEach(one -> {
                Map<String, Object> myMap = new HashMap<>();
                legendData.add(one.get(groupName) + "");
                myMap.put("name", one.get(groupName) + "");
                myMap.put("value", one.get("count") + "");
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
    public Map<String, Object> getAgeInfoBySql(String sql) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) + 1;
        /*
        * 年龄段分为0-6、7-17、18-40、41-65、65以上
        * 首先获取当前年份，由于ES查询是左包含，右不包，所以当前年份需要+1
        * 下面为构造年龄段的算式，其中year-151限定了范围是66-150岁 即66以上，其他类似
        * */
        String range = "range(birthYear," + (year - 151) + "," + (year - 66) + "," + (year - 41) + "," + (year - 18) + "," + (year - 7) + "," + year + ")";
//        String sql = "select count(*) from single_disease_personal_index where birthYear <> 0  group by " + range;
        List<Map<String, Object>> listData = parseIntegerValue(sql);
        Map<String, Object> map = new HashMap<>();
        List<String> legendData = new ArrayList<>();
        List<Map<String, Object>> seriesData = new ArrayList<>();
        if (null != listData && listData.get(0).size() > 0) {
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


}
