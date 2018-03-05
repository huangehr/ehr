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
public class SingleDiseaseService {
    @Autowired
    private EsExtract esExtract;
    @Autowired
    private ElasticsearchUtil elasticsearchUtil;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static final String HEALTHPROBLEM = "1"; // 健康问题
    public static final String AGE = "2"; // 年龄段分布
    public static final String SEX = "3"; // 性别
    /**
     * 热力图数据
     * @return
     * @throws Exception
     */
    public List<Map<String,String>>  getHeatMap() throws Exception {
        List<Map<String,String>> list = new ArrayList<>();
        String sql = "select addressLngLat, count(cardId) from single_disease_personal_index group by addressLngLat";
        List<Map<String, Object>> listData = parseIntegerValue(sql);
        Map<String, Object> map = new HashMap<>();
        if (null != listData && listData.get(0).size() > 0) {
            listData.forEach(item -> {
                map.put(item.get("addressLngLat") + "", item.get("COUNT(cardId)"));
            });
            map.forEach((k,v)->{
                Map<String, String> temp = new HashMap<>();
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
        String sql = "select town, count(*) from single_disease_personal_index group by town";
        List<Map<String, Object>> list = parseIntegerValue(sql);
        List<Map<String, Object>> dataList = fillNoDataColumn(list);
        return dataList;
    }

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
     * 获取健康状况
     * @return
     */
    public Map<String, Object> getHealthProInfo(String code) {
        String sql = "select disease, count(cardId) from single_disease_personal_index group by disease";
        List<Map<String, Object>> listData = parseIntegerValue(sql);
        Map<String, Object> map = new HashMap<>();
        List<String> legendData = new ArrayList<>();
        legendData.add("患病人群");
        legendData.add("健康人群");
        List<Map<String, Object>> seriesData = new ArrayList<>();
        if (null != listData && listData.get(0).size() > 0) {
            listData.forEach(one -> {
                Map<String, Object> myMap = new HashMap<>();
                myMap.put("name", "患病人群");
                myMap.put("value", one.get("COUNT(cardId)") + "");
                seriesData.add(myMap);
            });
        }
        // 获取健康人群人数
        Map<String, Object> healthMap = new HashMap<>();
        healthMap = getHealthCountInfo(healthMap, code);
        seriesData.add(healthMap);
        map.put("legendData", legendData);
        map.put("seriesData", seriesData);
        return map;
    }

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
        String range = "range(birthYear," + (year - 151) + "," + (year - 66) + "," + (year - 41) + "," + (year - 18) + "," + (year - 7) + "," + year + ")";
        String sql = "select count(birthYear) from single_disease_personal_index group by " + range;
        List<Map<String, Object>> listData = parseIntegerValue(sql);
        Map<String, Object> map = new HashMap<>();
        List<String> legendData = new ArrayList<>();
        List<Map<String, Object>> seriesData = new ArrayList<>();
        if (null != listData && listData.get(0).size() > 0) {
            listData.forEach(one -> {
                String rangeName = one.get(range) + "";
                int first = (int) Double.parseDouble(rangeName.split("-")[0]);
                int last = (int) Double.parseDouble(rangeName.split("-")[1]);
                Integer result = last - first;
                String keyName = exchangeInfo(result);
                Map<String, Object> myMap = new HashMap<>();
                legendData.add(keyName);
                myMap.put("name", keyName);
                myMap.put("value", one.get("COUNT(birthYear)") + "");
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
     * 获取并发症数据
     * @return
     */
    public Map<String, List<String>> getSymptomDataInfo() {
        String sql = "select symptomName, count(*) from single_disease_check_index where checkCode = 'CH001' group by symptomName";
        List<Map<String, Object>> listData = parseIntegerValue(sql);
        Map<String, List<String>> map = new HashMap<>();
        List<String> xData = new ArrayList<>();
        List<String> valueData = new ArrayList<>();
        if (null != listData && listData.get(0).size() > 0) {
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
    public Map<String, List<String>> getMedicineDataInfo() {
        String sql = "select medicineName, count(*) from single_disease_check_index where checkCode = 'CH004' group by medicineName";
        List<Map<String, Object>> listData = parseIntegerValue(sql);
        Map<String, List<String>> map = new HashMap<>();
        List<String> xData = new ArrayList<>();
        List<String> valueData = new ArrayList<>();
        if (null != listData && listData.get(0).size() > 0) {
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
    public Map<String, List<String>> getFastingBloodGlucoseDataInfo() {
        String firstSql = "select fastingBloodGlucoseName, count(*) from single_disease_check_index where checkCode = 'CH002' and sex=1 group by fastingBloodGlucoseName";
        String secondSql = "select fastingBloodGlucoseName, count(*) from single_disease_check_index where checkCode = 'CH002' and sex=2 group by fastingBloodGlucoseName";
        List<Map<String, Object>> firstListData = parseIntegerValue(firstSql);
        List<Map<String, Object>> secondListData = parseIntegerValue(secondSql);
        Map<String, List<String>> map = new HashMap<>();
        List<String> xData = new LinkedList<>();
        List<String> valueData1 = new LinkedList<>();    // 存放第一个数据源
        List<String> valueData2 = new LinkedList<>();    // 存放第二个数据源
        if (null != firstListData && firstListData.get(0).size() > 0) {
            firstListData.forEach(one -> {
                xData.add(one.get("fastingBloodGlucoseName") + "");   // 获取横坐标
                valueData1.add(one.get("COUNT(*)") + "");
            });
            map.put("valueData1", valueData1);
        }
        if (null != secondListData && secondListData.get(0).size() > 0) {
            secondListData.forEach(one -> {
                xData.add(one.get("fastingBloodGlucoseName") + "");
                valueData2.add(one.get("COUNT(*)") + "");
            });
            map.put("valueData2", valueData2);
        }
        if (xData.size() > 0) {
            Set<String> hash = new LinkedHashSet<>(xData);
            xData.clear();
            xData.addAll(hash);
            map.put("xData", xData);
        }
        return map;
    }

    /**
     * 糖耐量统计
     * @return
     */
    public Map<String, List<String>> getSugarToleranceDataInfo() {
        String firstSql = "select sugarToleranceName, count(*) from single_disease_check_index where checkCode = 'CH003' and sex=1 group by sugarToleranceName";
        String secondSql = "select sugarToleranceName, count(*) from single_disease_check_index where checkCode = 'CH003' and sex=2 group by sugarToleranceName";
        List<Map<String, Object>> firstListData = parseIntegerValue(firstSql);
        List<Map<String, Object>> secondListData = parseIntegerValue(secondSql);
        Map<String, List<String>> map = new HashMap<>();
        List<String> xData = new LinkedList<>();
        List<String> valueData1 = new LinkedList<>();    // 存放第一个数据源
        List<String> valueData2 = new LinkedList<>();    // 存放第二个数据源
        if (null != firstListData && firstListData.get(0).size() > 0) {
            firstListData.forEach(one -> {
                xData.add(one.get("sugarToleranceName") + "");   // 获取横坐标
                valueData1.add(one.get("COUNT(*)") + "");
            });
            map.put("valueData1", valueData1);
        }
        if (null != secondListData && secondListData.get(0).size() > 0) {
            secondListData.forEach(one -> {
                xData.add(one.get("sugarToleranceName") + "");
                valueData2.add(one.get("COUNT(*)") + "");
            });
            map.put("valueData2", valueData2);
        }
        if (xData.size() > 0) {
            Set<String> hash = new LinkedHashSet<>(xData);
            xData.clear();
            xData.addAll(hash);
            map.put("xData", xData);
        }
        return map;
    }

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

}
