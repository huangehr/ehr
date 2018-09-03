package com.yihu.ehr.analyze.service.dataQuality;


import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.*;

/**
 * 质控管理首页- 基础逻辑类
 *
 * @author HZY
 * @created 2018/8/17 11:24
 */
@Service
public abstract class DataQualityBaseService extends BaseJpaService {

    @Value("${quality.orgCode}")
    private String defaultOrgCode;
    @Value("${quality.cloud}")
    private String cloud;
    @Value("${quality.cloudName}")
    private String cloudName;
    @Autowired
    protected ElasticSearchUtil elasticSearchUtil;

    /**
     * 获取市级下的区县质控信息 - 【区域级别】
     *
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public abstract List<Map<String, Object>> getAreaDataQuality(Integer dataLevel,String startDate, String endDate) throws Exception;

    /**
     * 获取区县下的机构质控信息 - 【机构级别】
     *
     * @param areaCode  区域编码
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public abstract List<Map<String, Object>> getOrgDataQuality(Integer dataLevel,String areaCode, String startDate, String endDate) throws Exception;

    /**
     *  生成总计map
     * @param name  机构名称/区域名称
     * @param platformNum
     * @param orgNum
     * @return
     */
    public Map<String,Object> genTotalData(String name ,double platformNum,double orgNum){
        Map<String,Object> totalMap = new HashMap<>();
        totalMap.put("code", "");
        totalMap.put("name", name);
        totalMap.put("count", platformNum);
        totalMap.put("total", orgNum);
        double rate = calDoubleRate(platformNum, orgNum);
        totalMap.put("rate", rate + "%");
        return totalMap;
    }

    /**
     * 通过map中的rate字段降序排列，并添加% 号
     * @param list
     */
    public void rateComparator(List<Map<String, Object>> list ){
        comparator(list,"rate",1);
        list.forEach(map->{
            map.put("rate",map.get("rate") + "%");
        });
    }

    /**
     * 根据map中的字段排序
     * @param list           map集合
     * @param orderField    排序字段
     * @param order          排序顺序  0：正序，1：降序
     */
    public void comparator(List<Map<String, Object>> list ,String orderField,int order){
        Collections.sort(list, new Comparator<Map<String, Object>>() {
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                if (order==0) {
                    return getDoubleValue(o1.get(orderField)).compareTo(getDoubleValue(o2.get(orderField)));
                }else if (order==1) {
                    return getDoubleValue(o2.get(orderField)).compareTo(getDoubleValue(o1.get(orderField)));
                }else {
                    return 0;
                }
            }
        });

    }

    /**
     * 百分比计算（不带单位）
     * @param molecular
     * @param denominator
     * @return
     */
    public Double calDoubleRate(double molecular, double denominator) {
        if (molecular == 0) {
            return 0.00;
        } else if (denominator == 0) {
            return 100.00;
        }

        BigDecimal b = new BigDecimal((molecular/denominator) *100) ;
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    /**
     * 获取douuble值
     *
     * @param objValue
     * @return
     */
    public Double getDoubleValue(Object objValue) {
        double value = 0;
        try {
            if (objValue != null)
                value = Double.parseDouble(objValue.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 获取医院日报数据量
     * @param start
     * @param orgCode
     * @return
     */
    public Map<String,Object> getHospitalDataCount(String start,String end, String orgCode) {
        long starttime = System.currentTimeMillis();
        Map<String,Object> resMap = new HashMap<String,Object>();
        int total=0;
        int inpatient_total=0;
        int oupatient_total=0;
        int exam_total=0;
        String dateStr = DateUtil.toString(new Date());
        if (StringUtils.isBlank(start)) {
            start = dateStr;
        }
        if (StringUtils.isBlank(end)) {
            end = dateStr;
        }

        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("create_date>=" + start + "T00:00:00Z;");
            stringBuilder.append("create_date<=" + end + "T23:59:59Z;");
            if (StringUtils.isNotEmpty(orgCode)) {
                stringBuilder.append("org_code?" + orgCode);
            }
            List<Map<String, Object>> res = elasticSearchUtil.list("qc","daily_report", stringBuilder.toString());
            if(res!=null && res.size()>0){
                for(Map<String,Object> report : res){
                    total+=Integer.parseInt(report.get("HSI07_01_001").toString());
                    inpatient_total+=Integer.parseInt(report.get("HSI07_01_012").toString());
                    oupatient_total+=Integer.parseInt(report.get("HSI07_01_002").toString());
                    exam_total+=Integer.parseInt(report.get("HSI07_01_004").toString());
                }
            }
            resMap.put("total",total);
            resMap.put("inpatient_total",inpatient_total);
            resMap.put("oupatient_total",oupatient_total);
            resMap.put("exam_total",exam_total);
            long endtime = System.currentTimeMillis();
            System.out.println("获取医院上传数据量耗时：" + (endtime - starttime) + "ms");
            return resMap;
        }catch (Exception e){
            e.printStackTrace();
            resMap.put("total",0);
            resMap.put("inpatient_total",0);
            resMap.put("oupatient_total",0);
            return resMap;
        }
    }

    /**
     * 获取有数据的医院
     * @param dateField  过滤时间字段
     * @param start       起始时间
     * @param end          截止时间
     * @return
     */
    public List<String> hasDataHospital(String dateField,String start,String end){
        //统计有数据的医院code
        String sqlOrg = "";
        List<String> list = new ArrayList<>();
         sqlOrg = "SELECT org_code FROM json_archives/info where "+ dateField +">= '" + start + " 00:00:00' AND "+dateField+"<='" + end + " 23:59:59' group by org_code ";
        try {
            ResultSet resultSetOrg = elasticSearchUtil.findBySql(sqlOrg);
            while (resultSetOrg.next()) {
                String orgCode = resultSetOrg.getString("org_code");
                if (StringUtils.isNotEmpty(orgCode)) {
                    list.add(orgCode);
                }
            }
        } catch (Exception e) {
            if (!"Error".equals(e.getMessage())) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 获取有数据的区县
     * @param dateField
     * @param start
     * @param end
     * @return
     */
    public List<String> hasDataArea(String dateField,String start,String end){
        //统计有数据的区域code
        String sqlOrg = "";
        List<String> list = new ArrayList<>();
        sqlOrg = "SELECT org_area FROM json_archives/info where "+ dateField +">= '" + start + " 00:00:00' AND "+dateField+"<='" + end + " 23:59:59' group by org_area ";
        try {
            ResultSet resultSetOrg = elasticSearchUtil.findBySql(sqlOrg);
            while (resultSetOrg.next()) {
                String orgCode = resultSetOrg.getString("org_code");
                if (StringUtils.isNotEmpty(orgCode)) {
                    list.add(orgCode);
                }
            }
        } catch (Exception e) {
            if (!"Error".equals(e.getMessage())) {
                e.printStackTrace();
            }
        }
        return list;
    }

}
