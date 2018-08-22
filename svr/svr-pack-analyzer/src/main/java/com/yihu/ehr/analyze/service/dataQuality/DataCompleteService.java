package com.yihu.ehr.analyze.service.dataQuality;


import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  质控管理首页- 完整性逻辑类
 * @author HZY
 * @created 2018/8/17 11:24
 */
@Service
public class DataCompleteService extends DataQualityBaseService {

    private final static Logger logger = LoggerFactory.getLogger(DataCompleteService.class);
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Value("${quality.orgCode}")
    private String defaultOrgCode;
    @Value("${quality.cloud}")
    private String cloud;
    @Value("${quality.cloudName}")
    private String cloudName;


    /**
     * 【完整性】 获取区域数据 - 行政区号分组
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public  List<Map<String,Object>> areaDataComplete(String startDate, String endDate) throws Exception{
        String end = DateUtil.addDate(1, endDate,DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Map<String,Object> resMap = null;
        List<Map<String,Object>> list = new ArrayList<>();
        //机构数据
        List<Map<String,Object>> groupList = getOrgDataGroup(startDate,end);
        //平台接收数据量
        Map<String, Object> platformDataGroup = getPlatformDataGroup(startDate, end);
        // 计算
        for (Map<String,Object> map:groupList){
            resMap = new HashMap<String,Object>();
            Integer platPormNum = Integer.parseInt(platformDataGroup.get("org_area").toString());
            Integer orgNum = Integer.parseInt(map.get("count").toString());
            String rate = calRate(platPormNum,orgNum);
            resMap.put("org_area",map.get("org_area"));
            resMap.put("count",platPormNum);
            resMap.put("total",orgNum);
            resMap.put("rate",rate);
            list.add(resMap);
        }

        return list;
    }

    /**
     * 获取平台区域分组档案数据量 - （区域编码分组）
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public Map<String,Object> getPlatformDataGroup(String startDate,String endDate) throws Exception {
        Map<String,Object> resMap = new HashMap<>();
        //平台数据
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT SUM(row) as count ,org_area ");
        sql.append("FROM json_archives_qc/qc_dataset_detail");
        sql.append(" WHERE receive_date>='" + startDate + " 00:00:00' and receive_date<='" + endDate + " 23:59:59'");
        sql.append("GROUP BY org_area");
        List<String> field2 = new ArrayList<>();
        field2.add("count");
        field2.add("org_area");
        List<Map<String,Object>> platformList = elasticSearchUtil.findBySql(field2, sql.toString());
        for (Map<String,Object> map : platformList){
            resMap.put(map.get("org_area").toString(),map.get("count"));
        }
        return resMap;
    }

    /**
     *  获取机构采集- 区域分组数据量 - （区域编码分组）
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public  List<Map<String,Object>> getOrgDataGroup(String startDate,String endDate) throws Exception {
        //机构数据
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT SUM(HSI07_01_001) as count ,org_area ");
        sql.append("FROM qc/daily_report");
        sql.append(" WHERE create_date>='" + startDate + "T00:00:00Z' and create_date<='" + endDate + "T23:59:59Z'");
        sql.append("GROUP BY org_area");
        List<String> field = new ArrayList<>();
        field.add("count");
        field.add("org_area");
        List<Map<String,Object>> groupList = elasticSearchUtil.findBySql(field, sql.toString());
        return groupList;
    }


}
