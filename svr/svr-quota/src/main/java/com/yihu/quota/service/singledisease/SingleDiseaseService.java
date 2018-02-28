package com.yihu.quota.service.singledisease;

import com.yihu.quota.etl.extract.es.EsExtract;
import com.yihu.quota.etl.util.ElasticsearchUtil;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public String getHeatMap(String quotaCode) throws Exception {
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
        String points = JSONArray.fromObject(list).toString();
        return points;
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
}
