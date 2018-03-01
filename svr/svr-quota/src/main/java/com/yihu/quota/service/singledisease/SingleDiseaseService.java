package com.yihu.quota.service.singledisease;

import com.yihu.quota.etl.extract.es.EsExtract;
import com.yihu.quota.etl.util.ElasticsearchUtil;
import net.sf.json.JSONArray;
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

    /**
     * 热力图数据
     * @param quotaCode
     * @return
     * @throws Exception
     */
    public List<Map<String,String>>  getHeatMap(String quotaCode) throws Exception {
        List<Map<String,String>> list = new ArrayList<>();
        String sql = "select town, sum(result) from medical_service_index where quotaCode ='" + quotaCode +"' group by town";
        List<Map<String, Object>> listData = elasticsearchUtil.excuteDataModel(sql);
        Map<String, Object> map = new HashMap<>();
        listData.forEach(item -> {
            map.put(item.get("town") + "", item.get("SUM(result)"));
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
        return list;
    }

    /**
     * 获取糖尿病患者数
     * @param quotaCode
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getNumberOfDiabetes(String quotaCode) throws Exception {
        String sql = "select town, sum(result) from medical_service_index where quotaCode = '" + quotaCode + "' group by town";
        List<Map<String, Object>> list = elasticsearchUtil.excuteDataModel(sql);
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
                    result = map.get("SUM(result)").toString();
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
     * @param quotaCode
     * @return
     */
    public Map<String, List<String>> getLineDataInfo(String quotaCode) {
        String sql = "select year, sum(result) from medical_service_index where quotaCode = '" + quotaCode + "' group by year";
        List<Map<String, Object>> listData = elasticsearchUtil.excuteDataModel(sql);
        Collections.reverse(listData);
        Map<String, List<String>> map = new HashMap<>();
        List<String> xData = new ArrayList<>();
        List<String> valueData = new ArrayList<>();
        listData.forEach(one -> {
            xData.add(one.get("year") + "");
            valueData.add(one.get("SUM(result)") + "");
        });
        map.put("xData", xData);
        map.put("valueData", valueData);
        return map;
    }

    /**
     * 获取饼状图数据
     * @param quotaCode
     * @return
     */
    public Map<String, Object> getPieDataInfo(String quotaCode) {
        String sql = "select slaveKey1Name, sum(result) from medical_service_index where quotaCode = '" + quotaCode + "' group by slaveKey1Name";
        List<Map<String, Object>> listData = elasticsearchUtil.excuteDataModel(sql);
        Map<String, Object> map = new HashMap<>();
        List<String> legendData = new ArrayList<>();
        List<Map<String, Object>> seriesData = new ArrayList<>();
        listData.forEach(one -> {
            Map<String, Object> myMap = new HashMap<>();
            legendData.add(one.get("slaveKey1Name") + "");
            myMap.put("name", one.get("slaveKey1Name") + "");
            myMap.put("value", one.get("SUM(result)") + "");
            seriesData.add(myMap);
        });
        map.put("legendData", legendData);
        map.put("seriesData", seriesData);
        return map;
    }

    /**
     * 获取柱状图数据（多数据源）
     * @param quotaCode
     * @return
     */
    public Map<String, List<String>> getSingleBarDataInfo(String quotaCode) {
        String sql = "select slaveKey1Name, sum(result) from medical_service_index where quotaCode = '" + quotaCode + "' group by slaveKey1Name";
        List<Map<String, Object>> listData = elasticsearchUtil.excuteDataModel(sql);
        Map<String, List<String>> map = new HashMap<>();
        List<String> xData = new ArrayList<>();
        List<String> valueData = new ArrayList<>();
        listData.forEach(one -> {
            xData.add(one.get("slaveKey1Name") + "");
            valueData.add(one.get("SUM(result)") + "");
        });
        map.put("xData", xData);
        map.put("valueData", valueData);
        return map;
    }

    /**
     * 获取柱状图数据（多数据源）
     * @param quotaCode
     * @return
     */
    public Map<String, List<String>> getMultipleBarDataInfo(String quotaCode) {
        String firstSql = "select slaveKey2Name, sum(result) from medical_service_index where quotaCode = '" + quotaCode + "' and slaveKey2=1 group by slaveKey1Name,slaveKey2Name";
        String secondSql = "select slaveKey2Name, sum(result) from medical_service_index where quotaCode = '" + quotaCode + "' and slaveKey2=2 group by slaveKey1Name,slaveKey2Name";
        List<Map<String, Object>> firstListData = elasticsearchUtil.excuteDataModel(firstSql);
        List<Map<String, Object>> secondListData = elasticsearchUtil.excuteDataModel(secondSql);
        Map<String, List<String>> map = new HashMap<>();
        List<String> xData = new LinkedList<>();
        List<String> valueData1 = new LinkedList<>();    // 存放第一个数据源
        List<String> valueData2 = new LinkedList<>();    // 存放第二个数据源
        firstListData.forEach(one -> {
            xData.add(one.get("slaveKey1Name") + "");   // 获取横坐标
            valueData1.add(one.get("SUM(result)") + "");
        });
        secondListData.forEach(one -> {
            xData.add(one.get("slaveKey1Name") + "");
            valueData2.add(one.get("SUM(result)") + "");
        });
        Set<String> hash = new LinkedHashSet<>(xData);
        xData.clear();
        xData.addAll(hash);
        map.put("xData", xData);
        map.put("valueData1", valueData1);
        map.put("valueData2", valueData2);
        return map;
    }
}
