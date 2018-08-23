package com.yihu.ehr.analyze.service.dataQuality;


import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

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
    @Autowired
    private DataCorrectService dataCorrectService;


    /**
     * 【完整性】 获取区域数据 - 行政区号分组
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    @Override
    public  List<Map<String,Object>> getAreaDataQuality(String startDate, String endDate) throws Exception{
        String end = DateUtil.addDate(1, endDate,DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Map<String,Object> resMap = null;
        List<Map<String,Object>> list = new ArrayList<>();
        //机构数据
        List<Map<String,Object>> groupList = dataCorrectService.getOrgDataMap("create_date",startDate,end,null);
        //平台接收数据量
        Map<String, Object> platformDataGroup = getPlatformDataGroup("receive_date",startDate, end ,null);
        // 计算
        for (Map<String,Object> map:groupList){
            resMap = new HashMap<String,Object>();
            String type = platformDataGroup.get("type").toString();
            double platPormNum = 0;
            if ("org_area".equals(type)) {
                platPormNum = Double.parseDouble(platformDataGroup.get(map.get("org_area")).toString());
                resMap.put("code",map.get("org_area"));
            }else {
                platPormNum = Double.parseDouble(platformDataGroup.get(map.get("org_code")).toString());
                resMap.put("code",map.get("org_code"));
            }
            double orgNum = Double.parseDouble(map.get("count").toString());
            String rate = calRate(platPormNum,orgNum);

            resMap.put("name",map.get("name"));
            resMap.put("count",platPormNum);
            resMap.put("total",orgNum);
            resMap.put("rate",rate);
            list.add(resMap);
        }

        return list;
    }

    @Override
    public List<Map<String, Object>> getOrgDataQuality(String areaCode, String startDate, String endDate) throws Exception {
        String end = DateUtil.addDate(1, endDate,DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Map<String,Object> resMap = null;
        List<Map<String,Object>> list = new ArrayList<>();
        //机构数据
        List<Map<String,Object>> groupList = dataCorrectService.getOrgDataMap("create_date",startDate,end,areaCode);
        //平台接收数据量
        Map<String, Object> platformDataGroup = getPlatformDataGroup("receive_date",startDate, end,areaCode);
        // 计算
        for (Map<String,Object> map:groupList){
            resMap = new HashMap<String,Object>();
            String type = platformDataGroup.get("type").toString();
            double platPormNum = 0;
            if ("org_area".equals(type)) {
                platPormNum = Double.parseDouble(platformDataGroup.get(map.get("org_area")).toString());
                resMap.put("code",map.get("org_area"));
            }else {
                platPormNum = Double.parseDouble(platformDataGroup.get(map.get("org_code")).toString());
                resMap.put("code",map.get("org_code"));
            }
            double orgNum = Double.parseDouble(map.get("count").toString());
            String rate = calRate(platPormNum,orgNum);
            resMap.put("code",map.get("code"));
            resMap.put("name",map.get("name"));
            resMap.put("count",platPormNum);
            resMap.put("total",orgNum);
            resMap.put("rate",rate);
            list.add(resMap);
        }

        return list;
    }


    /**
     * 获取平台区域分组档案数据量 - （区域编码分组）
     * @param start
     * @param end
     * @return
     * @throws Exception
     */
    public Map<String,Object> getPlatformDataGroup(String dateField,String start,String end,String orgArea) throws Exception {
        Map<String,Object> resMap = new HashMap<>();
        String dateStr = DateUtil.toString(new Date());
        if (StringUtils.isBlank(start)) {
            start = dateStr;
        }
        if (StringUtils.isBlank(end)) {
            end = dateStr;
        }
        List<String> fields = new ArrayList<String>();
        fields.add("count");

        String sql1 = "";
        if (StringUtils.isNotEmpty(orgArea)) {
            //添加标识，标识是机构数据
            resMap.put("type","org_code");
            fields.add("org_code");
            sql1 = "SELECT SUM(row) as count ,org_code FROM json_archives_qc/qc_dataset_detail where org_area='" + orgArea + "' and " +
                    dateField + ">='" + start + " 00:00:00' and "+ dateField + "<='" + end + " 23:59:59' group by org_code";
        } else {
            resMap.put("type","org_area");
            fields.add("org_area");
            sql1 = "SELECT SUM(row) as count ,org_area FROM json_archives_qc/qc_dataset_detail  where " +
                    dateField +">='" + start + " 00:00:00' and "+dateField+"<='" + end + " 23:59:59' group by org_area";
        }

        List<Map<String, Object>> resultList = elasticSearchUtil.findBySql(fields, sql1);
        for (Map<String,Object> map : resultList){
            if (StringUtils.isNotEmpty(orgArea)) {
                resMap.put(map.get("org_code").toString(),map.get("count"));
            }else {
                resMap.put(map.get("org_area").toString(),map.get("count"));
            }
        }
        return resMap;
    }



}
