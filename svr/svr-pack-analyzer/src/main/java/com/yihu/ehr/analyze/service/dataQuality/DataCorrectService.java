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
        map.put("rate", calRate(value,total));
        return map;
    }

        /* ******************************** 区域层级模块相关 ***********************************/

    /**
     *  获取市区域的下级区域质控情况
     * @param dataType 数据维度  （complete: 完整性，inTime:及时性，correct:准确性）
     * @param areaCode 上区域编码
     * @param start
     * @param end
     * @return
     */
    public List<Map<String,Object>> getAreaData(String dataType ,String areaCode,String start,String end) throws Exception {
        List<Map<String,Object>> list = new ArrayList<>();
        String dateStr = DateUtil.toString(new Date());
        if (StringUtils.isBlank(start)) {
            start = dateStr;
        }
        if (StringUtils.isBlank(end)) {
            end = dateStr;
        }

        switch (dataType) {
            case "complete" :
                list = areaDataComplete(start,end);//完整性
                break;
            case "inTime" :
                //及时性
                break;
            case "correct" :
                //准确性
                break;
                default:break;
        }

        return list;
    }

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



    /**
     *  批量更新es中的区域编码org_area
     *  (通过机构编码org_code 更新org_area）
     */
    public void bulkUpdateOrgArea(String index,String type){
        List<Map<String, Object>> result = elasticSearchUtil.list(index, type, "");
        List<Map<String, Object>> updateSourceList = new ArrayList<>();

        result.forEach(item -> {
            Map<String, Object> updateSource = new HashMap<>();
            updateSource.put("_id", item.get("_id"));
            String orgCode = (String) item.get("org_code");
            String orgArea = redisClient.get("organizations:" + orgCode + ":area");
            updateSource.put("org_area", orgArea);
            updateSourceList.add(updateSource);
        });
        elasticSearchUtil.bulkUpdate(index, type, updateSourceList);
    }


    public List<Map<String,Object>> getHospitalDataByGroup(String startDate, String endDate, String orgCode,String groupField) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT SUM(count) as count ,SUM(row) as row, dataset_name, dataset ");
        sql.append("FROM json_archives_qc/qc_dataset_detail");
        sql.append(" WHERE receive_date>='" + startDate + " 00:00:00' and receive_date<='" + endDate + " 23:59:59'");
        if (StringUtils.isNotEmpty(orgCode) && !"null".equals(orgCode)&&!cloud.equals(orgCode)){
            sql.append(" and org_code='" + orgCode +"'");
        }
        sql.append("GROUP BY dataset_name,dataset");
        List<String> field = new ArrayList<>();
        field.add("count");
        field.add("row");
        field.add("dataset_name");
        field.add("dataset");
        List<Map<String,Object>> list = elasticSearchUtil.findBySql(field, sql.toString());
        Map<String, Object> totalMap = new HashMap<>();
        totalMap.put("dataset","总计");
        totalMap.put("dataset_name","-");
        double rowTotal = 0;
        double countTotal = 0;
        for(Map<String,Object> map :list){
            map.put("name" ,map.get("dataset_name"));
            rowTotal += Double.valueOf(map.get("row").toString());
            countTotal += Double.valueOf(map.get("count").toString());
        }
        totalMap.put("row",rowTotal);
        totalMap.put("count",countTotal);
        list.add(0,totalMap);
        return list;
    }


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
    public Map<String,Object> getErrorPlatformData(String dateField,String start, String end, String orgArea) {
        Map<String,Object> resMap = new HashMap<>();
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
            if (StringUtils.isNotEmpty(orgArea)) {
                resMap.put("type","org_code");
                fields.add("org_code");
                sql1 = "SELECT org_code,count(DISTINCT event_no) as count from json_archives_qc/qc_metadata_info where org_area='" + orgArea + "' " +
                        dateField + ">='" + start + " 00:00:00' and "+ dateField + "<='" + end + " 23:59:59' and (qc_step=1 or qc_step=2) group by org_code";
            } else {
                resMap.put("type","org_area");
                fields.add("org_area");
                sql1 = "SELECT org_area,count(DISTINCT event_no) as count from json_archives_qc/qc_metadata_info where " +
                        dateField +">='" + start + " 00:00:00' and "+dateField+"<='" + end + " 23:59:59' and (qc_step=1 or qc_step=2) group by org_area";
            }

            List<Map<String, Object>> resultList = elasticSearchUtil.findBySql(fields, sql1);
            if (resultList != null && resultList.size() > 0) {
                for (Map<String,Object> map : resultList){
                    if (StringUtils.isNotEmpty(orgArea)) {
                        resMap.put(map.get("org_code").toString(),map.get("count"));
                    }else {
                        resMap.put(map.get("org_area").toString(),map.get("count"));
                    }
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
     * @param start
     * @param end
     * @param orgArea
     * @return
     */
    public List<Map<String, Object>> getOrgDataMap(String dateField,String start, String end, String orgArea) throws IOException {
        List<Map<String, Object>> resultList = new ArrayList<>();
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
                fields.add("org_area");
                sql1 = "SELECT org_area,sum(HSI07_01_001) as count from qc/daily_report where " +
                        dateField +">='" + start + "' and "+dateField+"<='" + end + "' group by org_area";
            }
            fields.add("count");
            resultList = elasticSearchUtil.findBySql(fields, sql1);

            if (resultList != null && resultList.size() > 0) {
                for (Map<String, Object> map1 : resultList) {
                    String name = "";
                    if (StringUtils.isNotEmpty(orgArea)) {
                        String code = (String) map1.get("org_code");
                        name = redisClient.get("organizations:" + code + ":name");
                    }else {
                        String code =  map1.get("org_area").toString();
                        name = redisClient.get("area:" + code + ":name");
                    }
                    map1.put("name",name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }


    @Override
    public List<Map<String, Object>> getAreaDataQuality(String startDate, String endDate) throws Exception {
        String end = DateUtil.addDate(1, endDate,DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Map<String,Object> resMap = null;
        List<Map<String,Object>> list = new ArrayList<>();
        //机构数据
        List<Map<String,Object>> groupList = getOrgDataMap("create_date",startDate,end,null);
        //平台接收数据量
        Map<String, Object> platformDataGroup = getErrorPlatformData("receive_date",startDate, end,null);
        // 计算
        for (Map<String,Object> map:groupList){
            resMap = new HashMap<String,Object>();
            String type = platformDataGroup.get("type").toString();
            double platPormErrorNum = 0;
            if ("org_area".equals(type)) {
                platPormErrorNum = Double.parseDouble(platformDataGroup.get(map.get("org_area")).toString());
                resMap.put("code",map.get("org_area"));
            }else {
                platPormErrorNum = Double.parseDouble(platformDataGroup.get(map.get("org_code")).toString());
                resMap.put("code",map.get("org_code"));
            }

            double orgNum = Double.parseDouble(map.get("count").toString());
            double platPormNum = orgNum - platPormErrorNum;
            String rate = calRate(platPormNum,orgNum);
            resMap.put("org_code",map.get("org_code"));
            resMap.put("org_area",map.get("org_area"));
            resMap.put("name",map.get("name"));
            resMap.put("count",platPormErrorNum);
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
        List<Map<String,Object>> groupList = getOrgDataMap("create_date",startDate,end,areaCode);
        //平台接收数据量
        Map<String, Object> platformDataGroup = getErrorPlatformData("receive_date",startDate, end,areaCode);
        // 计算
        for (Map<String,Object> map:groupList){
            resMap = new HashMap<String,Object>();
            String type = platformDataGroup.get("type").toString();
            double platPormErrorNum = 0;
            if ("org_area".equals(type)) {
                platPormErrorNum = Double.parseDouble(platformDataGroup.get(map.get("org_area")).toString());
                resMap.put("code",map.get("org_area"));
            }else {
                platPormErrorNum = Double.parseDouble(platformDataGroup.get(map.get("org_code")).toString());
                resMap.put("code",map.get("org_code"));
            }

            double orgNum = Double.parseDouble(map.get("count").toString());
            double platPormNum = orgNum - platPormErrorNum;
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


}
