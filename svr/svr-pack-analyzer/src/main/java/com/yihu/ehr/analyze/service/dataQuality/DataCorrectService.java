package com.yihu.ehr.analyze.service.dataQuality;


import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.entity.quality.DqPaltformReceiveWarning;
import com.yihu.ehr.redis.client.RedisClient;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.*;

/**
 *  质控管理首页- 准确性逻辑类
 * @author HZY
 * @created 2018/8/17 11:24
 */
@Service
public class DataCorrectService extends DataQualityBaseService {

    private final static Logger logger = LoggerFactory.getLogger(DataCorrectService.class);
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

    @Value("${quality.orgCode}")
    private String defaultOrgCode;
    @Value("${quality.cloud}")
    private String cloud;
    @Value("${quality.cloudName}")
    private String cloudName;
    @Autowired
    private RedisClient redisClient;


    /**
     *  获取及时上传数
     * @param dateField  时间区间查询字段
     * @param start
     * @param end
     * @param warning  预警信息
     * @return
     */
    public double getInTimeNum(String dateField,String start, String end, DqPaltformReceiveWarning warning) {
        double totalInTime = 0;
        //及时率
        try {
            long starttime = System.currentTimeMillis();
            String sql0 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE event_type=2 AND pack_type=1 AND " + dateField +
                    " BETWEEN '" + start + " 00:00:00' AND '" + end + " 23:59:59' and delay <=" + warning.getPeInTime();

            String sql1 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE event_type=1 AND pack_type=1 AND " + dateField +
                    " BETWEEN '" + start + " 00:00:00' AND '" + end + " 23:59:59' and delay <=" + warning.getHospitalInTime();

            String sql2 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE event_type=0 AND pack_type=1 AND " + dateField +
                    " BETWEEN '" + start + " 00:00:00' AND '" + end + " 23:59:59' and delay <=" + warning.getOutpatientInTime();

            ResultSet resultSet0 = elasticSearchUtil.findBySql(sql0);
            ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
            ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
            resultSet0.next();
            resultSet1.next();
            resultSet2.next();
            double outpatientInTime = new Double(resultSet2.getObject("COUNT(DISTINCT event_no)").toString());//门诊及时数
            double inpatientInTime = new Double(resultSet1.getObject("COUNT(DISTINCT event_no)").toString());//住院及时数
            double examInTime = new Double(resultSet0.getObject("COUNT(DISTINCT event_no)").toString());//体检及时数
            totalInTime = outpatientInTime + inpatientInTime + examInTime; // //就诊及时性
            logger.info("平台就诊及时人数 去重复：" + (System.currentTimeMillis() - starttime) + "ms");
        } catch (Exception e) {
            if (!"Error".equals(e.getMessage())) {
                e.printStackTrace();
            }
        }
        return totalInTime;
    }

    public Map<String, Object> genVisitMap(String typeField, double value, double total) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", typeField);
        map.put("value", value);
        map.put("rate", calDoubleRate(value,total));
        return map;
    }

        /* ******************************** 区域层级模块相关 ***********************************/

    /**
     * 获取平台准确性-档案错误数
     *  (1. orgArea为空时，根据区域分组查询
     *   2. orgArea不为空时，根据机构分组查询）
     * @param dateField  时间区间查询字段
     * @param start
     * @param end
     * @param orgArea
     * @return
     */
    public Map<String,Object> getErrorPlatformData(Integer dataLevel,String dateField,String start, String end, String orgArea) {
        Map<String,Object> resMap = new HashMap<>();
        double totalAreaCout = 0;
        try {
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
            if (StringUtils.isNotEmpty(orgArea) ) {
                resMap.put("type","org_code");
                fields.add("org_code");
                sql1 = "SELECT org_code,count(DISTINCT event_no) as count from json_archives_qc/qc_metadata_info where org_area='" + orgArea + "' and " +
                        dateField + ">='" + start + " 00:00:00' and "+ dateField + "<='" + end + " 23:59:59' and (qc_step=1 or qc_step=2) group by org_code";
            } else  if ( StringUtils.isEmpty(orgArea) && ( dataLevel ==0 && StringUtils.isEmpty(orgArea))){
                resMap.put("type","org_area");
                fields.add("org_area");
                sql1 = "SELECT org_area,count(DISTINCT event_no) as count from json_archives_qc/qc_metadata_info where " +
                        dateField +">='" + start + " 00:00:00' and "+dateField+"<='" + end + " 23:59:59' and (qc_step=1 or qc_step=2) group by org_area";
            }else if ( StringUtils.isEmpty(orgArea) && ( dataLevel ==1 && StringUtils.isEmpty(orgArea))){
                resMap.put("type","org_code");
                fields.add("org_code");
                sql1 = "SELECT org_code,count(DISTINCT event_no) as count from json_archives_qc/qc_metadata_info where " +
                        dateField +">='" + start + " 00:00:00' and "+dateField+"<='" + end + " 23:59:59' and (qc_step=1 or qc_step=2) group by org_code";
            }

            List<Map<String, Object>> resultList = elasticSearchUtil.findBySql(fields, sql1);
            if (resultList != null && resultList.size() > 0) {
                for (Map<String,Object> map : resultList){
                    totalAreaCout += getDoubleValue(map.get("count")) ;
                    if (StringUtils.isNotEmpty(orgArea) || ( dataLevel ==1 && StringUtils.isEmpty(orgArea))) {
                        resMap.put(map.get("org_code").toString(),map.get("count"));
                    }else {
                        resMap.put(map.get("org_area").toString(),map.get("count"));
                    }
                }
            }
            if (!resMap.isEmpty()) {
                //总计
                if (dataLevel ==0) {
                    // 默认上饶市，
                    resMap.put("", totalAreaCout);
                } else {
                    resMap.put("", totalAreaCout);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resMap;
    }


    /**
     * 获取医院总数据量
     *  (1. orgArea为空时，根据区域分组查询
     *   2. orgArea不为空时，根据机构分组查询）
     *   @param dataLevel 数据级别：  0：区县，1：机构
     * @param start
     * @param end
     * @param orgArea
     * @return
     */
    public List<Map<String, Object>> getOrgDataMap(Integer dataLevel,String dateField,String start, String end, String orgArea) throws IOException {
        List<Map<String, Object>> resultList = new ArrayList<>();
        double totalAreaCout = 0;
        try {
            String dateStr = DateUtil.toString(new Date());
            if (StringUtils.isBlank(start)) {
                start = dateStr;
            }
            if (StringUtils.isBlank(end)) {
                end = dateStr;
            }

            if ("create_date".equals(dateField)) {
                start = start + "T00:00:00Z";
                end = end + "T23:59:59Z";
            }else {
                start = start +" 00:00:00";
                end = end +" 23:59:59";
            }

            List<String> fields = new ArrayList<String>();
            String sql1 = "";
            if (StringUtils.isNotEmpty(orgArea)) {
                fields.add("org_code");
                sql1 = "SELECT org_code,sum(HSI07_01_001) as count from qc/daily_report where org_area='" + orgArea + "' and " +
                        dateField + ">='" + start + "' and "+ dateField + "<='" + end + "' group by org_code";
            } else {
                if (dataLevel == 0) {
                    fields.add("org_area");
                    sql1 = "SELECT org_area,sum(HSI07_01_001) as count from qc/daily_report where " +
                            dateField +">='" + start + "' and "+dateField+"<='" + end + "' group by org_area";
                }else {
                    fields.add("org_code");
                    sql1 = "SELECT org_code,sum(HSI07_01_001) as count from qc/daily_report where " +
                            dateField +">='" + start + "' and "+dateField+"<='" + end + "' group by org_code";
                }

            }
            fields.add("count");
            resultList = elasticSearchUtil.findBySql(fields, sql1);

            //设置机构，区域名称
            if (resultList != null && resultList.size() > 0) {
                for (Map<String, Object> map1 : resultList) {
                    String name = "";
                    if (StringUtils.isNotEmpty(orgArea) || ( dataLevel ==1 && StringUtils.isEmpty(orgArea))) {
                        String code = (String) map1.get("org_code");
                        name = redisClient.get("organizations:" + code + ":name");
                    }else {
                        String code =  map1.get("org_area").toString();
                        name = redisClient.get("area:" + code + ":name");
                        totalAreaCout += getDoubleValue( map1.get("count"));
                    }
                    map1.put("name",name);
                }
            }

            if (!resultList.isEmpty()) {
                Map<String, Object> total = new HashMap<>();
                if (dataLevel ==0) {
                    // 临时写死上饶市
                    total.put("name", "上饶市");
                    total.put("org_area", "");
                    total.put("", totalAreaCout);
                    total.put("count", totalAreaCout);
                } else {
                    total.put("name", "全部机构");
                    total.put("org_code", "");
                    total.put("count", totalAreaCout);
                }
                resultList.add(0, total);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }


    @Override
    public List<Map<String, Object>> getAreaDataQuality(Integer dataLevel,String startDate, String endDate) throws Exception {
        String end = DateUtil.addDate(1, endDate,DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Map<String,Object> resMap = null;
        List<Map<String,Object>> list = new ArrayList<>();
        double totalNum = 0;//平台总数
        double totalHospitalNum = 0;//医院总数
        //机构数据
        List<Map<String,Object>> groupList = getOrgDataMap(dataLevel,"create_date",startDate,end,null);
        //平台接收错误数据量
        Map<String, Object> platformErrorGroup = getErrorPlatformData(dataLevel,"receive_date",startDate, end,null);
        // 计算
        for (Map<String,Object> map:groupList){
            resMap = new HashMap<String,Object>();
            String type = platformErrorGroup.get("type").toString();
            double platPormErrorNum = 0;
            String code = "";
            if ("org_area".equals(type)) {
                platPormErrorNum = getDoubleValue(platformErrorGroup.get(map.get("org_area")));
                code = map.get("org_area").toString();
            }else {
                platPormErrorNum = getDoubleValue(platformErrorGroup.get(map.get("org_code")));
                code = map.get("org_code").toString();
            }

            double orgNum = getDoubleValue(map.get("count"));
            double platPormNum = orgNum - platPormErrorNum;
            double rate = calDoubleRate(platPormNum,orgNum);
            if (!"".equals(code)) {
                resMap.put("code", code);
                resMap.put("name", map.get("name"));
                resMap.put("count", platPormNum);
                resMap.put("total", orgNum);
                resMap.put("rate", rate);
                totalNum += platPormNum;
            } else {
                totalHospitalNum = platPormNum;
            }
            list.add(resMap);
        }
        //排序
        comparator(list);
        //添加总计
        Map<String, Object> totalMap = genTotalData("上饶市",totalNum,totalHospitalNum);
        list.add(0,totalMap);
        return list;
    }

    @Override
    public List<Map<String, Object>> getOrgDataQuality(Integer dataLevel,String areaCode, String startDate, String endDate) throws Exception {
        String end = DateUtil.addDate(1, endDate,DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Map<String,Object> resMap = null;
        List<Map<String,Object>> list = new ArrayList<>();
        double totalNum = 0;//平台总数
        double totalHospitalNum = 0;//医院总数
        //机构数据
        List<Map<String,Object>> groupList = getOrgDataMap(dataLevel,"create_date",startDate,end,areaCode);
        //平台接收数据量
        Map<String, Object> platformDataGroup = getErrorPlatformData(dataLevel,"receive_date",startDate, end,areaCode);
        // 计算
        for (Map<String,Object> map:groupList){
            resMap = new HashMap<String,Object>();
            String type = platformDataGroup.get("type").toString();
            double platPormErrorNum = 0;
            String code = "";
            if ("org_area".equals(type)) {
                platPormErrorNum = getDoubleValue(platformDataGroup.get(map.get("org_area")));
                code = map.get("org_area").toString();
            }else {
                platPormErrorNum = getDoubleValue(platformDataGroup.get(map.get("org_code")));
                code = map.get("org_code").toString();
            }

            double orgNum = getDoubleValue(map.get("count"));
            double platPormNum = orgNum - platPormErrorNum;
            double rate = calDoubleRate(platPormNum,orgNum);
            if (!"".equals(code)) {
                resMap.put("code", code);
                resMap.put("name", map.get("name"));
                resMap.put("count", platPormNum);
                resMap.put("total", orgNum);
                resMap.put("rate", rate);
                totalNum += platPormNum;
            } else {
                totalHospitalNum = platPormNum;
            }
            list.add(resMap);
        }
        //排序
        comparator(list);
        //添加总计
        Map<String, Object> totalMap = genTotalData("全部机构",totalNum,totalHospitalNum);
        list.add(0,totalMap);
        return list;
    }


}
