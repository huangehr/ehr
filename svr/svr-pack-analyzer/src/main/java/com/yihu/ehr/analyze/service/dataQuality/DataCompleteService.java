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
 * 质控管理首页- 完整性逻辑类
 *
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
     *
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    @Override
    public List<Map<String, Object>> getAreaDataQuality(Integer dataLevel,String startDate, String endDate) throws Exception {
        String end = DateUtil.addDate(1, endDate, DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Map<String, Object> resMap = null;
        Map<String, Object> totalMap = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        //机构数据
        List<Map<String, Object>> groupList = dataCorrectService.getOrgDataMap(dataLevel,"create_date", startDate, end, null);
        //平台接收数据量
        Map<String, Object> platformDataGroup = getPlatformDataGroup(dataLevel,"receive_date", startDate, end, null);
        // 计算
        for (Map<String, Object> map : groupList) {
            resMap = new HashMap<String, Object>();
            String type = platformDataGroup.get("type").toString();
            String code = "";
            double platPormNum = 0;
            if ("org_area".equals(type)) {
                platPormNum = getDoubleValue(platformDataGroup.get(map.get("org_area")));
                code = map.get("org_area").toString();
            } else {
                platPormNum = getDoubleValue(platformDataGroup.get(map.get("org_code")));
                code = map.get("org_code").toString();
            }
            double orgNum = getDoubleValue(map.get("count"));
            double rate = calDoubleRate(platPormNum, orgNum);
            if ("".equals(code)) {
                totalMap.put("code",code);
                totalMap.put("name", map.get("name"));
                totalMap.put("count", platPormNum  + "%");
                totalMap.put("total", orgNum);
                totalMap.put("rate", rate);
            } else {
                resMap.put("code",code);
                resMap.put("name", map.get("name"));
                resMap.put("count", platPormNum);
                resMap.put("total", orgNum);
                resMap.put("rate", rate);
            }
            list.add(resMap);
        }
        //排序
        comparator(list);
        //添加总计
        list.add(0,totalMap);
        return list;
    }

    @Override
    public List<Map<String, Object>> getOrgDataQuality(Integer dataLevel,String areaCode, String startDate, String endDate) throws Exception {
        String end = DateUtil.addDate(1, endDate, DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Map<String, Object> resMap = null;
        Map<String, Object> totalMap = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        //机构数据
        List<Map<String, Object>> groupList = dataCorrectService.getOrgDataMap(dataLevel,"create_date", startDate, end, areaCode);
        //平台接收数据量
        Map<String, Object> platformDataGroup = getPlatformDataGroup(dataLevel,"receive_date", startDate, end, areaCode);
        // 计算
        for (Map<String, Object> map : groupList) {
            resMap = new HashMap<String, Object>();
            String type = platformDataGroup.get("type").toString();
            String code = "";
            double platPormNum = 0;
            if ("org_area".equals(type)) {
                platPormNum = getDoubleValue(platformDataGroup.get(map.get("org_area")));
                code = map.get("org_area").toString();
            } else {
                platPormNum = getDoubleValue(platformDataGroup.get(map.get("org_code")));
                code = map.get("org_code").toString();
            }
            double orgNum = getDoubleValue(map.get("count"));
            double rate = calDoubleRate(platPormNum, orgNum);

            if ("".equals(code)) {
                totalMap.put("code",code);
                totalMap.put("name", map.get("name"));
                totalMap.put("count", platPormNum + "%");
                totalMap.put("total", orgNum);
                totalMap.put("rate", rate);
            } else {
                resMap.put("code",code);
                resMap.put("name", map.get("name"));
                resMap.put("count", platPormNum);
                resMap.put("total", orgNum);
                resMap.put("rate", rate);
            }
            list.add(resMap);
        }

        //排序
        comparator(list);
        //添加总计
        list.add(0,totalMap);
        return list;
    }


    /**
     * 获取平台区域分组档案数据量 - （区域编码分组）
     * @param dataLevel 数据层级，：0：区域，1：机构
     * @param start
     * @param end
     * @return
     * @throws Exception
     */
    public Map<String, Object> getPlatformDataGroup(Integer dataLevel,String dateField, String start, String end, String orgArea) throws Exception {
        Map<String, Object> resMap = new HashMap<>();
        String dateStr = DateUtil.toString(new Date());
        double totalAreaCout = 0;//总计
        if (StringUtils.isBlank(start)) {
            start = dateStr;
        }
        if (StringUtils.isBlank(end)) {
            end = dateStr;
        }
        List<String> fields = new ArrayList<String>();
        fields.add("count");

        String sql1 = "";
        if (StringUtils.isNotEmpty(orgArea) ) {
            //添加标识，标识是机构数据
            resMap.put("type", "org_code");
            fields.add("org_code");
            sql1 = "SELECT count(DISTINCT event_no) as count ,org_code FROM json_archives/info where pack_type=1 and analyze_status=3 and org_area='" + orgArea + "' and " +
                    dateField + ">='" + start + " 00:00:00' and " + dateField + "<='" + end + " 23:59:59' group by org_code";
        } else if ( StringUtils.isEmpty(orgArea) && ( dataLevel ==0 && StringUtils.isEmpty(orgArea))){
            resMap.put("type", "org_area");
            fields.add("org_area");
            sql1 = "SELECT count(DISTINCT event_no) as count ,org_area FROM json_archives/info  where pack_type=1 and analyze_status=3 and " +
                    dateField + ">='" + start + " 00:00:00' and " + dateField + "<='" + end + " 23:59:59' group by org_area";
        }else if ( StringUtils.isEmpty(orgArea) && ( dataLevel ==1 && StringUtils.isEmpty(orgArea))){
            resMap.put("type", "org_code");
            fields.add("org_code");
            sql1 = "SELECT count(DISTINCT event_no) as count ,org_code FROM json_archives/info  where pack_type=1 and analyze_status=3 and " +
                    dateField + ">='" + start + " 00:00:00' and " + dateField + "<='" + end + " 23:59:59' group by org_code";
        }

        List<Map<String, Object>> resultList = elasticSearchUtil.findBySql(fields, sql1);
        for (Map<String, Object> map : resultList) {
            totalAreaCout += getDoubleValue(map.get("count"));
            if (StringUtils.isNotEmpty(orgArea) || ( dataLevel ==1 && StringUtils.isEmpty(orgArea))) {
                resMap.put(map.get("org_code").toString(), map.get("count"));
            } else {
                resMap.put(map.get("org_area").toString(), map.get("count"));
            }
        }
        if (!resMap.isEmpty()) {
            //总计
            if (dataLevel ==0) {
                //TODO 默认写上饶市，后面需要修改
                resMap.put("", totalAreaCout);
            } else {
                resMap.put("", totalAreaCout);
            }
        }
        return resMap;
    }


}
