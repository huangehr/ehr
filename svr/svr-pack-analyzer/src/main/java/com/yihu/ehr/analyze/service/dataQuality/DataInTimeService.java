package com.yihu.ehr.analyze.service.dataQuality;


import com.yihu.ehr.analyze.dao.DqPaltformReceiveWarningDao;
import com.yihu.ehr.entity.quality.DqPaltformReceiveWarning;
import com.yihu.ehr.redis.client.RedisClient;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang.StringUtils;
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
 * 质控管理首页- 及时性逻辑类
 *
 * @author HZY
 * @created 2018/8/17 11:24
 */
@Service
public class DataInTimeService extends DataQualityBaseService {

    private final static Logger logger = LoggerFactory.getLogger(DataInTimeService.class);


    @Value("${quality.orgCode}")
    private String defaultOrgCode;
    @Value("${quality.cloud}")
    private String cloud;
    @Value("${quality.cloudName}")
    private String cloudName;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private DataCorrectService dataCorrectService;
    @Autowired
    private DqPaltformReceiveWarningDao dqPaltformReceiveWarningDao;


    /**
     * 获取及时接收档案数
     *
     * @param dateField 时间区间查询字段
     * @param start
     * @param end
     * @return
     */
    public Map<String, Object> getInTimeMap(Integer dataLevel,Integer eventType, String dateField, String start, String end, String orgArea) {
        Map<String, Object> resMap = new HashMap<>();
        double totalInTime = 0;
        //初始化 及时率预警信息
        DqPaltformReceiveWarning warning = dqPaltformReceiveWarningDao.findByOrgCode(defaultOrgCode);
        long starttime = System.currentTimeMillis();
        String sql0 = "";
        List<String> fields = new ArrayList<String>();
        fields.add("count");
        try {
            if (StringUtils.isNotEmpty(orgArea) ) {
                fields.add("org_code");
                sql0 = "SELECT  COUNT(DISTINCT event_no) as count,org_code FROM json_archives WHERE event_type=" + eventType + " AND pack_type=1 AND org_area='" + orgArea + "' AND " + dateField +
                        " BETWEEN '" + start + " 00:00:00' AND '" + end + " 23:59:59' and delay <=" + warning.getPeInTime() + " GROUP BY org_code";
            } else if (  dataLevel ==0 && StringUtils.isEmpty(orgArea)){
                fields.add("org_area");
                sql0 = "SELECT  COUNT(DISTINCT event_no) as count,org_area FROM json_archives WHERE event_type=" + eventType + " AND pack_type=1 AND " + dateField +
                        " BETWEEN '" + start + " 00:00:00' AND '" + end + " 23:59:59' and delay <=" + warning.getPeInTime() + " GROUP BY org_area";
            }else  if (  dataLevel ==1 && StringUtils.isEmpty(orgArea)){
                fields.add("org_code");
                sql0 = "SELECT  COUNT(DISTINCT event_no) as count,org_area FROM json_archives WHERE event_type=" + eventType + " AND pack_type=1 AND " + dateField +
                        " BETWEEN '" + start + " 00:00:00' AND '" + end + " 23:59:59' and delay <=" + warning.getPeInTime() + " GROUP BY org_code";

            }

            List<Map<String, Object>> resultSet0 = elasticSearchUtil.findBySql(fields, sql0);

            if (resultSet0 != null && resultSet0.size() > 0) {
                for (Map<String, Object> map : resultSet0) {
                    if (StringUtils.isNotEmpty(orgArea) || ( dataLevel ==1 && StringUtils.isEmpty(orgArea))) {
                        resMap.put(map.get("org_code").toString(), map.get("count"));
                    } else {
                        resMap.put(map.get("org_area").toString(), map.get("count"));
                    }
                }
            }
            logger.info("平台就诊及时人数 去重复：" + (System.currentTimeMillis() - starttime) + "ms");
        } catch (Exception e) {
            if (!"Error".equals(e.getMessage())) {
                e.printStackTrace();
            }
        }
        return resMap;
    }

    /**
     * 获取及时上传数
     *
     * @param dateField 时间区间查询字段
     * @param start
     * @param end
     * @return
     */
    public Map<String, Object> getInTimeMap(Integer dataLevel,String dateField, String start, String end, String orgArea) {
        Map<String, Object> resMap = new HashMap<>();
        double totalAreaCout = 0;//总计
        double totalInTime = 0;//就诊及时数
        //初始化 及时率预警信息
        DqPaltformReceiveWarning warning = dqPaltformReceiveWarningDao.findByOrgCode(defaultOrgCode);
        long starttime = System.currentTimeMillis();
        //获取及时数组
        Map<String, Object> outPatientInTimeMap = getInTimeMap(dataLevel,0, dateField, start, end, orgArea); //门诊及时数
        Map<String, Object> inPatientInTimeMap = getInTimeMap(dataLevel,1, dateField, start, end, orgArea); //住院及时数
        Map<String, Object> examInTimeMap = getInTimeMap(dataLevel,2, dateField, start, end, orgArea); //体检及时数

        for (String key : outPatientInTimeMap.keySet()) {
            double outpatientInTime = getDoubleValue(outPatientInTimeMap.get(key));
            double inpatientInTime = getDoubleValue(inPatientInTimeMap.get(key));
            double examInTime = getDoubleValue(examInTimeMap.get(key));
            totalInTime = outpatientInTime + inpatientInTime + examInTime; // //就诊及时性
            resMap.put(key, totalInTime);
            totalAreaCout += totalInTime;
        }
        //指定数据类型
        if (dataLevel ==0) {
            resMap.put("type", "org_area");
        } else {
            resMap.put("type", "org_code");
        }

        //总计
        if (!resMap.isEmpty()) {
            resMap.put("", totalAreaCout);
        }
        logger.info("平台就诊及时人数 去重复：" + (System.currentTimeMillis() - starttime) + "ms");


        return resMap;
    }


    /* ******************************** 区域层级模块相关 ***********************************/
    @Override
    public List<Map<String, Object>> getAreaDataQuality(Integer dataLevel,String startDate, String endDate) throws Exception {
        String end = DateUtil.addDate(1, endDate, DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Map<String, Object> resMap = null;
        List<Map<String, Object>> list = new ArrayList<>();
        double totalNum = 0;//平台总数
        double totalHospitalNum = 0;//医院总数
        //机构数据
        List<Map<String, Object>> groupList = dataCorrectService.getOrgDataMap(dataLevel,"create_date", startDate, end, null);
        //平台接收数据量
        Map<String, Object> platformDataGroup = getInTimeMap(dataLevel,"receive_date", startDate, end, null);
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
            if (!"".equals(code)) {
                resMap.put("code", code);
                resMap.put("name", map.get("name"));
                resMap.put("count", platPormNum);
                resMap.put("total", orgNum);
                resMap.put("rate", rate);
                list.add(resMap);
                totalNum += platPormNum;
            } else {
                totalHospitalNum = platPormNum;
            }
        }
        //排序
        comparator(list);
        //添加总计
        if (totalHospitalNum !=0) {
            Map<String, Object> totalMap = genTotalData("上饶市", totalNum, totalHospitalNum);
            list.add(0, totalMap);
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> getOrgDataQuality(Integer dataLevel,String areaCode, String startDate, String endDate) throws Exception {
        String end = DateUtil.addDate(1, endDate, DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Map<String, Object> resMap = null;
        List<Map<String, Object>> list = new ArrayList<>();
        double totalNum = 0;//平台总数
        double totalHospitalNum = 0;//医院总数
        //机构数据
        List<Map<String, Object>> groupList = dataCorrectService.getOrgDataMap(dataLevel,"create_date", startDate, end, areaCode);
        //平台接收数据量
        Map<String, Object> platformDataGroup = getInTimeMap(dataLevel,"receive_date", startDate, end, areaCode);
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
            if (!"".equals(code)) {
                resMap.put("code", code);
                resMap.put("name", map.get("name"));
                resMap.put("count", platPormNum);
                resMap.put("total", orgNum);
                resMap.put("rate", rate);
                list.add(resMap);
                totalNum += platPormNum;
            } else {
                totalHospitalNum = platPormNum;
            }
        }
        //排序
        comparator(list);
        //添加总计
        if (totalHospitalNum !=0) {
            Map<String, Object> totalMap = genTotalData("全部机构", totalNum, totalHospitalNum);
            list.add(0, totalMap);
        }
        return list;
    }


}
