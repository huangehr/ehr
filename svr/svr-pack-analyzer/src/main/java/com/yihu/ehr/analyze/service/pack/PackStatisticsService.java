package com.yihu.ehr.analyze.service.pack;

import com.yihu.ehr.elasticsearch.ElasticSearchPool;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.*;

/**
 * 质控报表
 *
 * @author zhengwei
 * @created 2018.04.24
 */
@Service
public class PackStatisticsService extends BaseJpaService {

    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    private ElasticSearchPool elasticSearchPool;

    /**
     * getRecieveOrgCount 根据接收日期统计各个医院的数据解析情况
     *
     * @param dateStr
     * @return
     */
    public List<Map<String, Object>> getRecieveOrgCount(String dateStr) throws Exception{
        long starttime = System.currentTimeMillis();
        Session session = currentSession();
        String sql1 = "SELECT org_code, COUNT(org_code) FROM json_archives WHERE (archive_status = 0 OR archive_status = 1) AND receive_date BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59' GROUP BY org_code";
        ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
        Map<String, Object[]> dataMap1 = new HashMap<>();
        try {
            while (resultSet1.next()) {
                Object[] tempArr = new Object[3];
                String code = ObjectUtils.toString(resultSet1.getObject("org_code"));
                String name = (String) session.createSQLQuery("SELECT full_name FROM organizations WHERE org_code ='" + code + "'").uniqueResult();
                double count = resultSet1.getDouble("COUNT(org_code)");
                tempArr[0] = code;
                tempArr[1] = name;
                tempArr[2] = count;
                dataMap1.put(code, tempArr);
            }
        }catch (Exception e){
            dataMap1 = new HashMap<>();
        }
        String sql2 = "SELECT org_code, COUNT(org_code) FROM json_archives WHERE archive_status = 3 AND receive_date BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59' GROUP BY org_code";
        ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
        Map<String, Object[]> dataMap2 = new HashMap<>();
        try {
            while (resultSet2.next()) {
                Object[] tempArr = new Object[3];
                String code = ObjectUtils.toString(resultSet2.getObject(0));
                String name = (String) session.createSQLQuery("SELECT full_name FROM organizations WHERE org_code ='" + code + "'").uniqueResult();
                double count = resultSet2.getDouble("COUNT(org_code)");
                tempArr[0] = code;
                tempArr[1] = name;
                tempArr[2] = count;
                dataMap2.put(code, tempArr);
            }
        }catch (Exception e){
            dataMap2 = new HashMap<>();
        }
        String sql3 = "SELECT org_code, COUNT(org_code) FROM json_archives WHERE receive_date BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59' GROUP BY org_code";
        ResultSet resultSet3 = elasticSearchUtil.findBySql(sql3);
        Map<String, Object[]> dataMap3 = new HashMap<>();
        try {
            while (resultSet3.next()) {
                Object[] tempArr = new Object[3];
                String code = ObjectUtils.toString(resultSet3.getObject(0));
                String name = (String) session.createSQLQuery("SELECT full_name FROM organizations WHERE org_code ='" + code + "'").uniqueResult();
                double count = resultSet3.getDouble("COUNT(org_code)");
                tempArr[0] = code;
                tempArr[1] = name;
                tempArr[2] = count;
                dataMap3.put(code, tempArr);
            }
        }catch (Exception e){
            dataMap3 = new HashMap<>();
        }
        List<Map<String,Object>> dataList = new ArrayList<>();
        if(dataMap3!=null) {
            for (String key : dataMap3.keySet()) {
                Map<String, Object> dataArr = new HashMap<>();
                dataArr.put("org_code", dataMap3.get(key)[0]);
                dataArr.put("org_name", dataMap3.get(key)[1]);
                if (dataMap1!=null && dataMap1.containsKey(key)) { //待解析
                    dataArr.put("waiting", dataMap1.get(key)[2]);
                } else {
                    dataArr.put("waiting", 0);
                }
                if (dataMap2!=null && dataMap2.containsKey(key)) { //成功解析
                    dataArr.put("successful", dataMap2.get(key)[2]);
                } else {
                    dataArr.put("successful", 0);
                }
                dataArr.put("total", dataMap3.get(key)[2]);
                if ((double) dataMap3.get(key)[2] != 0) { //成功率
                    dataArr.put("rate", Double.parseDouble(dataArr.get("successful").toString()) / (double) dataMap3.get(key)[2] * 100);
                } else {
                    dataArr.put("rate", 0);
                }
                dataList.add(dataArr);
            }
        }
        long endtime = System.currentTimeMillis();
        System.out.println("各个医院的数据解析情况查询耗时：" + (endtime - starttime) + "ms");
        return dataList;
    }

    /**
     * 获取一段时间内数据解析情况
     * @param dateStr
     * @return
     */
    public List<Map<String,Object>> getArchivesCount(String dateStr) throws Exception {
        String sql1 = "SELECT COUNT(*) FROM json_archives WHERE (archive_status = 0 OR archive_status = 1) AND receive_date BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59'";
        ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
        String sql2 = "SELECT COUNT(*) FROM json_archives WHERE archive_status = 3 AND receive_date BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59'";
        ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
        String sql3 = "SELECT COUNT(*) FROM json_archives WHERE receive_date BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59'";
        ResultSet resultSet3 = elasticSearchUtil.findBySql(sql3);
        List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
        resultSet1.next();
        resultSet2.next();
        resultSet3.next();
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("waiting",resultSet1.getObject("COUNT(*)"));
        map.put("successful",resultSet2.getObject("COUNT(*)"));
        map.put("total",resultSet3.getObject("COUNT(*)"));
        dataList.add(map);
        return dataList;
    }

    /**
     * 业务分析
     * @param date
     * @return
     */
    public List<Map<String,Object>> getArchivesInc(String date,String orgCode) throws Exception{
        long starttime = System.currentTimeMillis();
        List<Map<String, Object>> list = getIncCount(date, orgCode);
        List<Map<String, Object>> res = new ArrayList<>();
        System.out.println("业务分析日期："+list.size());
        if(!list.isEmpty()){
            for(Map<String, Object> map : list){
                if(map.get("ed")!=null) {
                    map.putAll(getPatientCount(map.get("ed").toString(), orgCode));
                    map.putAll(getPatientCountInc(date, map.get("ed").toString(), orgCode));
                    res.add(map);
                }
            }
        }
        long endtime = System.currentTimeMillis();
        System.out.println("业务分析查询耗时：" + (endtime - starttime) + "ms");
        return res;
    }

    public List<Map<String, Object>> getIncCount(String date, String orgCode) throws Exception{
        long starttime = System.currentTimeMillis();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("receive_date>=" + date + " 00:00:00;");
        stringBuilder.append("receive_date<" + date + " 23:59:59;");
        if (StringUtils.isNotEmpty(orgCode) && !"null".equals(orgCode)){
            stringBuilder.append("org_code=" + orgCode);
        }
        TransportClient transportClient = elasticSearchPool.getClient();
        try {
            List<Map<String, Object>> resultList = new ArrayList<>();
            SearchRequestBuilder builder = transportClient.prepareSearch("json_archives");
            builder.setTypes("info");
            builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
            builder.setQuery(elasticSearchUtil.getQueryBuilder(stringBuilder.toString()));
            DateHistogramBuilder dateHistogramBuilder = new DateHistogramBuilder("date");
            dateHistogramBuilder.field("event_date");
            dateHistogramBuilder.interval(DateHistogramInterval.DAY);
            dateHistogramBuilder.format("yyyy-MM-dd");
            dateHistogramBuilder.minDocCount(0);
            builder.addAggregation(dateHistogramBuilder);
            builder.setSize(0);
            builder.setExplain(true);
            SearchResponse response = builder.get();
            Histogram histogram = response.getAggregations().get("date");
            histogram.getBuckets().forEach(item -> {
                Map<String, Object> temp = new HashMap<>();
                if(item.getDocCount()>0&&!"".equals(item.getKeyAsString())) {
                    temp.put("ed", item.getKeyAsString());
                }
                resultList.add(temp);
            });
            long endtime = System.currentTimeMillis();
            System.out.println("业务分析获取数据查询耗时：" + (endtime - starttime) + "ms");
            return resultList;
        } finally {
            elasticSearchPool.releaseClient(transportClient);
        }
    }
    /**
     * 完整性分析
     * @param startDate
     * @param endDate
     * @param orgCode
     * @return
     */
    public Envelop getArchivesFull(String startDate, String endDate, String orgCode) throws Exception{
        long starttime = System.currentTimeMillis();
        Envelop envelop = new Envelop();
        Date start = DateUtil.formatCharDateYMD(startDate);
        Date end = DateUtil.formatCharDateYMD(endDate);
        int day = (int) ((end.getTime() - start.getTime()) / (1000*3600*24))+1;
        Map<String,Object> resMap = new HashMap<String,Object>();
        List<Map<String,Map<String,Object>>> res = new ArrayList<Map<String,Map<String,Object>>>();
        int total=0;
        int inpatient_total=0;
        int oupatient_total=0;
        int total_es=0;
        int inpatient_total_es=0;
        int oupatient_total_es=0;
        int total_sc=0;
        int inpatient_total_sc=0;
        int oupatient_total_sc=0;
        for(int i =0;i<day;i++){
            Date date = DateUtil.addDate(i,start);
            Map<String,Map<String,Object>> map = new HashMap<String,Map<String,Object>>();
            Map<String,Object> rate = new HashMap<String,Object>();
            //平台数据
            Map<String,Object> map1 = getPatientCount(DateUtil.toString(date), orgCode);
            total+=Integer.parseInt(map1.get("total").toString());
            inpatient_total+=Integer.parseInt(map1.get("inpatient_total").toString());
            oupatient_total+=Integer.parseInt(map1.get("oupatient_total").toString());
            //医院数据
            Map<String,Object> map2 = getPatientCountEs(DateUtil.toString(date), orgCode);
            total_es+=Integer.parseInt(map2.get("total").toString());
            inpatient_total_es+=Integer.parseInt(map2.get("inpatient_total").toString());
            oupatient_total_es+=Integer.parseInt(map2.get("oupatient_total").toString());
            //上传数据
            Map<String,Object> map3 = getPatientNum(DateUtil.toString(date), orgCode);
            total_sc+= Integer.parseInt(map3.get("total").toString());
            inpatient_total_sc+= Integer.parseInt(map3.get("inpatient_total").toString());
            oupatient_total_sc+= Integer.parseInt(map3.get("oupatient_total").toString());
            //平台与医院
            if(Integer.parseInt(map2.get("total").toString())!=0){
                rate.put("total_rate", (Double.parseDouble(map1.get("total").toString()) / Double.parseDouble(map2.get("total").toString()))*100);
            }else{
                rate.put("total_rate", "0");
            }
            if(Integer.parseInt(map2.get("inpatient_total").toString())!=0){
                rate.put("inpatient_rate", (Double.parseDouble(map1.get("inpatient_total").toString()) / Double.parseDouble(map2.get("inpatient_total").toString()))*100);
            }else{
                rate.put("inpatient_rate", "0");
            }
            if(Integer.parseInt(map2.get("oupatient_total").toString())!=0){
                rate.put("oupatient_rate", (Double.parseDouble(map1.get("oupatient_total").toString()) / Double.parseDouble(map2.get("oupatient_total").toString()))*100);
            }else{
                rate.put("oupatient_rate", "0");
            }
            //平台与上传
            if(Integer.parseInt(map3.get("total").toString())!=0){
                rate.put("total_rate_sc", (Double.parseDouble(map1.get("total").toString()) / Double.parseDouble(map3.get("total").toString()))*100);
            }else{
                rate.put("total_rate_sc", "0");
            }
            if(Integer.parseInt(map3.get("inpatient_total").toString())!=0){
                rate.put("inpatient_rate_sc", (Double.parseDouble(map1.get("inpatient_total").toString()) / Double.parseDouble(map3.get("inpatient_total").toString()))*100);
            }else{
                rate.put("inpatient_rate_sc", "0");
            }
            if(Integer.parseInt(map3.get("oupatient_total").toString())!=0){
                rate.put("oupatient_rate_sc", (Double.parseDouble(map1.get("oupatient_total").toString()) / Double.parseDouble(map3.get("oupatient_total").toString()))*100);
            }else{
                rate.put("oupatient_rate_sc", "0");
            }
            map.put(DateUtil.toString(date),rate);
            res.add(map);
        }
        //平台总数
        resMap.put("total",total);
        resMap.put("inpatient_total",inpatient_total);
        resMap.put("oupatient_total",oupatient_total);
        //医院总数
        resMap.put("total_es",total_es);
        resMap.put("inpatient_total_es",inpatient_total_es);
        resMap.put("oupatient_total_es",oupatient_total_es);
        //上传总数
        resMap.put("total_sc",total_sc);
        resMap.put("inpatient_total_sc",inpatient_total_sc);
        resMap.put("oupatient_total_sc",oupatient_total_sc);
        //平台与医院比例
        if(total_es!=0){
            resMap.put("total_rate", ((double)total / (double)total_es)*100);
        }else{
            resMap.put("total_rate", "0");
        }
        if(inpatient_total_es!=0){
            resMap.put("inpatient_rate", ((double)inpatient_total / (double)inpatient_total_es)*100);
        }else{
            resMap.put("inpatient_rate", "0");
        }
        if(oupatient_total_es!=0){
            resMap.put("oupatient_rate", ((double)oupatient_total / (double)oupatient_total_es)*100);
        }else{
            resMap.put("oupatient_rate", "0");
        }
        //平台与上传比例
        if(total_sc!=0){
            resMap.put("total_rate_sc", ((double)total / (double)total_sc)*100);
        }else{
            resMap.put("total_rate_sc", "0");
        }
        if(inpatient_total_sc!=0){
            resMap.put("inpatient_rate_sc", ((double)inpatient_total / (double)inpatient_total_sc)*100);
        }else{
            resMap.put("inpatient_rate_sc", "0");
        }
        if(oupatient_total_sc!=0){
            resMap.put("oupatient_rate_sc", ((double)oupatient_total / (double)oupatient_total_sc)*100);
        }else{
            resMap.put("oupatient_rate_sc", "0");
        }
        envelop.setObj(resMap);
        envelop.setDetailModelList(res);
        envelop.setSuccessFlg(true);
        long endtime = System.currentTimeMillis();
        System.out.println("完整性查询耗时：" + (endtime - starttime) + "ms");
        return envelop;
    }

    /**
     * 平台就诊人数 新增
     * @param receiveDate
     * @param eventDate
     * @param orgCode
     * @return
     */
    public Map<String,Object> getPatientCountInc(String receiveDate, String eventDate, String orgCode) throws Exception{
        long starttime = System.currentTimeMillis();
        String sql1 ="";
        String sql2 ="";
        if(StringUtils.isNotEmpty(orgCode)){
            sql1 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE event_type=1 AND pack_type=1 AND org_code='"+orgCode+"' AND event_date BETWEEN" +
                    " '" + eventDate + " 00:00:00' AND '" +  eventDate + " 23:59:59' AND receive_date BETWEEN '" + receiveDate + " 00:00:00' AND '" +  receiveDate + " 23:59:59'";

            sql2 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE event_type=0 AND pack_type=1 AND org_code='"+orgCode+"' AND event_date BETWEEN " +
                    "'" + eventDate + " 00:00:00' AND '" +  eventDate + " 23:59:59' AND receive_date BETWEEN '" + receiveDate + " 00:00:00' AND '" +  receiveDate + " 23:59:59'";

        }else{
            sql1 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE event_type=1 AND pack_type=1 AND event_date " +
                    "BETWEEN '" + eventDate + " 00:00:00' AND '" +  eventDate + " 23:59:59' AND receive_date BETWEEN '" + receiveDate + " 00:00:00' AND '" +  receiveDate + " 23:59:59'";

            sql2 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE event_type=0 AND pack_type=1 AND event_date " +
                    "BETWEEN '" + eventDate + " 00:00:00' AND '" +  eventDate + " 23:59:59' AND receive_date BETWEEN '" + receiveDate + " 00:00:00' AND '" +  receiveDate + " 23:59:59'";
        }
        ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
        ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
        resultSet1.next();
        resultSet2.next();
        Map<String,Object> map = new HashMap<>();
        map.put("inpatient_inc",new Double(resultSet1.getObject("COUNT(DISTINCT event_no)").toString()).intValue());
        map.put("oupatient_inc",new Double(resultSet2.getObject("COUNT(DISTINCT event_no)").toString()).intValue());
        long endtime = System.currentTimeMillis();
        System.out.println("平台就诊人数 新增：" + (endtime - starttime) + "ms");
        return map;
    }

    /**
     * 平台就诊人数 去重复
     * @param dateStr
     * @param orgCode
     * @return
     */
    public Map<String,Object> getPatientCount(String dateStr, String orgCode) throws Exception{
        long starttime = System.currentTimeMillis();
        String sql1 ="";
        String sql2 ="";
        String sql3 ="";
        if(StringUtils.isNotEmpty(orgCode)){
            sql1 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE event_type=1 AND pack_type=1 AND org_code='"+orgCode+"' AND event_date BETWEEN" +
                    " '" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59'";

            sql2 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE event_type=0 AND pack_type=1 AND org_code='"+orgCode+"' AND event_date BETWEEN " +
                    "'" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59'";

            sql3 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE pack_type=1 AND org_code='"+orgCode+"' AND event_date BETWEEN " +
                    "'" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59'";
        }else{
            sql1 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE event_type=1 AND pack_type=1 AND event_date " +
                    "BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59'";

            sql2 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE event_type=0 AND pack_type=1 AND event_date " +
                    "BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59'";

            sql3 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE pack_type=1 AND event_date " +
                    "BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59'";
        }
        ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
        ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
        ResultSet resultSet3 = elasticSearchUtil.findBySql(sql3);
        resultSet1.next();
        resultSet2.next();
        resultSet3.next();
        Map<String,Object> map = new HashMap<>();
        map.put("inpatient_total",new Double(resultSet1.getObject("COUNT(DISTINCT event_no)").toString()).intValue());
        map.put("oupatient_total",new Double(resultSet2.getObject("COUNT(DISTINCT event_no)").toString()).intValue());
        map.put("total",new Double(resultSet3.getObject("COUNT(DISTINCT event_no)").toString()).intValue());
        long endtime = System.currentTimeMillis();
        System.out.println("平台就诊人数 去重复：" + (endtime - starttime) + "ms");
        return map;
    }


    /**
     * 平台就诊人数
     * @param dateStr
     * @param orgCode
     * @return
     */
    public Map<String,Object> getPatientNum(String dateStr, String orgCode) throws Exception{
        long starttime = System.currentTimeMillis();
        String sql1 ="";
        String sql2 ="";
        String sql3 ="";
        if(StringUtils.isNotEmpty(orgCode)){
            sql1 = "SELECT COUNT(*) FROM json_archives WHERE event_type=1 AND pack_type=1  AND org_code='"+orgCode+"' AND event_date " +
                    "BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59'";

            sql2 = "SELECT COUNT(*) FROM json_archives WHERE event_type=0 AND pack_type=1 AND org_code='"+orgCode+"' AND event_date " +
                    "BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59'";

            sql3 = "SELECT COUNT(*) FROM json_archives WHERE pack_type=1 AND org_code='"+orgCode+"' AND event_date " +
                    "BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59'";
        }else{
            sql1 = "SELECT COUNT(*) FROM json_archives WHERE event_type=1 AND pack_type=1 AND event_date " +
                    "BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59'";

            sql2 = "SELECT COUNT(*) FROM json_archives WHERE event_type=0 AND pack_type=1 AND event_date " +
                    "BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59'";

            sql3 = "SELECT COUNT(*) FROM json_archives WHERE pack_type=1 AND event_date " +
                    "BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59'";
        }

        ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
        ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
        ResultSet resultSet3 = elasticSearchUtil.findBySql(sql3);
        resultSet1.next();
        resultSet2.next();
        resultSet3.next();
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("inpatient_total",new Double(resultSet1.getObject("COUNT(*)").toString()).intValue());
        map.put("oupatient_total", new Double(resultSet2.getObject("COUNT(*)").toString()).intValue());
        map.put("total",new Double(resultSet3.getObject("COUNT(*)").toString()).intValue());
        long endtime = System.currentTimeMillis();
        System.out.println("平台就诊人数查询耗时：" + (endtime - starttime) + "ms");
        return map;
    }
    /**
     * 从质控包获取数据
     * @param date
     * @param orgCode
     * @return
     */
    public Map<String,Object> getPatientCountEs(String date, String orgCode) {
        long starttime = System.currentTimeMillis();
        Map<String,Object> resMap = new HashMap<String,Object>();
        int total=0;
        int inpatient_total=0;
        int oupatient_total=0;
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("event_date=" + date + ";");
            if (StringUtils.isNotEmpty(orgCode)) {
                stringBuilder.append("org_code?" + orgCode);
            }
            List<Map<String, Object>> res = elasticSearchUtil.list("qc","daily_report", stringBuilder.toString());
            if(res!=null && res.size()>0){
                for(Map<String,Object> report : res){
                    total+=Integer.parseInt(report.get("HSI07_01_001").toString());
                    inpatient_total+=Integer.parseInt(report.get("HSI07_01_012").toString());
                    oupatient_total+=Integer.parseInt(report.get("HSI07_01_002").toString());
                }
            }
            resMap.put("total",total);
            resMap.put("inpatient_total",inpatient_total);
            resMap.put("oupatient_total",oupatient_total);
            long endtime = System.currentTimeMillis();
            System.out.println("从质控包获取数据查询耗时：" + (endtime - starttime) + "ms");
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
     * 及时性分析
     * @param startDate
     * @param endDate
     * @param orgCode
     * @return
     */
    public Envelop getArchivesTime(String startDate, String endDate, String orgCode) throws Exception{
        long starttime = System.currentTimeMillis();
        Envelop envelop = new Envelop();
        Date start = DateUtil.formatCharDateYMD(startDate);
        Date end = DateUtil.formatCharDateYMD(endDate);
        int day = (int) ((end.getTime() - start.getTime()) / (1000*3600*24))+1;
        Map<String,Object> resMap = new HashMap<String,Object>();
        List<Map<String,Map<String,Object>>> res = new ArrayList<Map<String,Map<String,Object>>>();
        int total=0;
        int inpatient_total=0;
        int oupatient_total=0;
        int total_es=0;
        int inpatient_total_es=0;
        int oupatient_total_es=0;
        for(int i =0;i<day;i++){
            Date date = DateUtil.addDate(i,start);
            Map<String,Map<String,Object>> map = new HashMap<String,Map<String,Object>>();
            Map<String,Object> rate = new HashMap<String,Object>();
            //平台数据
            Map<String,Object> list = getPatientCountTime(DateUtil.toString(date), orgCode);
            total+=Integer.parseInt(list.get("total").toString());
            inpatient_total+=Integer.parseInt(list.get("inpatient_total").toString());
            oupatient_total+=Integer.parseInt(list.get("oupatient_total").toString());
            //医院数据
            Map<String,Object> map2 = getPatientCountEs(DateUtil.toString(date), orgCode);
            total_es+=Integer.parseInt(map2.get("total").toString());
            inpatient_total_es+=Integer.parseInt(map2.get("inpatient_total").toString());
            oupatient_total_es+=Integer.parseInt(map2.get("oupatient_total").toString());
            //平台与医院
            if(Integer.parseInt(map2.get("total").toString())!=0){
                rate.put("total_rate", ((double)(Integer.parseInt(list.get("inpatient_total").toString())+Integer.parseInt(list.get("oupatient_total").toString())) / Double.parseDouble(map2.get("total").toString()))*100);
            }else{
                rate.put("total_rate", "0");
            }
            if(Integer.parseInt(map2.get("inpatient_total").toString())!=0){
                rate.put("inpatient_rate", (Double.parseDouble(list.get("inpatient_total").toString()) / Double.parseDouble(map2.get("inpatient_total").toString()))*100);
            }else{
                rate.put("inpatient_rate", "0");
            }
            if(Integer.parseInt(map2.get("oupatient_total").toString())!=0){
                rate.put("oupatient_rate", (Double.parseDouble(list.get("oupatient_total").toString()) / Double.parseDouble(map2.get("oupatient_total").toString()))*100);
            }else{
                rate.put("oupatient_rate", "0");
            }
            map.put(DateUtil.toString(date),rate);
            res.add(map);
        }
        //平台总数
        resMap.put("total",total);
        resMap.put("inpatient_total",inpatient_total);
        resMap.put("oupatient_total",oupatient_total);
        //医院总数
        resMap.put("total_es",total_es);
        resMap.put("inpatient_total_es",inpatient_total_es);
        resMap.put("oupatient_total_es",oupatient_total_es);
        //平台与医院比例
        if(total_es!=0){
            resMap.put("total_rate", ((double)total / (double)total_es)*100);
        }else{
            resMap.put("total_rate", "0");
        }
        if(inpatient_total_es!=0){
            resMap.put("inpatient_rate", ((double)inpatient_total / (double)inpatient_total_es)*100);
        }else{
            resMap.put("inpatient_rate", "0");
        }
        if(oupatient_total_es!=0){
            resMap.put("oupatient_rate", ((double)oupatient_total / (double)oupatient_total_es)*100);
        }else{
            resMap.put("oupatient_rate", "0");
        }
        envelop.setObj(resMap);
        envelop.setDetailModelList(res);
        envelop.setSuccessFlg(true);
        long endtime = System.currentTimeMillis();
        System.out.println("及时性查询耗时：" + (endtime - starttime) + "ms");
        return envelop;
    }

    /**
     * 及时性获取数据
     * @param date
     * @param orgCode
     * @return
     */
    public Map<String,Object> getPatientCountTime(String date, String orgCode) throws Exception{
        long starttime = System.currentTimeMillis();
        Date begin = DateUtil.parseDate(date, DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Date end1 = DateUtil.addDate(2, begin);
        Date end2 = DateUtil.addDate(7, begin);

        String sql1 ="";
        String sql2 ="";
        String sql3 ="";
        if(StringUtils.isNotEmpty(orgCode)){
            sql1 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE event_type=1 AND pack_type=1 AND org_code='"+orgCode+"' AND event_date BETWEEN" +
                    " '" + date + " 00:00:00' AND '" +  date + " 23:59:59' AND receive_date BETWEEN"+
                    " '" + date + " 00:00:00' AND '" +  DateUtil.toString(end2) + " 23:59:59'";

            sql2 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE event_type=0 AND pack_type=1 AND org_code='"+orgCode+"' AND event_date BETWEEN " +
                    " '" + date + " 00:00:00' AND '" +  date + " 23:59:59' AND receive_date BETWEEN"+
                    " '" + date + " 00:00:00' AND '" +  DateUtil.toString(end1) + " 23:59:59'";
            sql3 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE pack_type=1 AND org_code='"+orgCode+"' AND event_date BETWEEN " +
                    " '" + date + " 00:00:00' AND '" +  date + " 23:59:59' AND receive_date BETWEEN"+
                    " '" + date + " 00:00:00' AND '" +  DateUtil.toString(end2) + " 23:59:59'";
        }else{
            sql1 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE event_type=1 AND pack_type=1 AND event_date BETWEEN" +
                    " '" + date + " 00:00:00' AND '" +  date + " 23:59:59' AND receive_date BETWEEN"+
                    " '" + date + " 00:00:00' AND '" +  DateUtil.toString(end2) + " 23:59:59' ";

            sql2 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE event_type=0 AND pack_type=1 AND event_date BETWEEN " +
                    " '" + date + " 00:00:00' AND '" +  date + " 23:59:59' AND receive_date BETWEEN"+
                    " '" + date + " 00:00:00' AND '" +  DateUtil.toString(end1) + " 23:59:59' ";
            sql3 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE  pack_type=1 AND event_date BETWEEN " +
                    " '" + date + " 00:00:00' AND '" +  date + " 23:59:59' AND receive_date BETWEEN"+
                    " '" + date + " 00:00:00' AND '" +  DateUtil.toString(end2) + " 23:59:59' ";
        }

        ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
        ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
        ResultSet resultSet3 = elasticSearchUtil.findBySql(sql3);
        Map<String,Object> map = new HashMap<>();
        resultSet1.next();
        resultSet2.next();
        resultSet3.next();
        map.put("inpatient_total",new Double(resultSet1.getObject("COUNT(DISTINCT event_no)").toString()).intValue());
        map.put("oupatient_total",new Double(resultSet2.getObject("COUNT(DISTINCT event_no)").toString()).intValue());
        map.put("total",new Double(resultSet3.getObject("COUNT(DISTINCT event_no)").toString()).intValue());
        long endtime = System.currentTimeMillis();
        System.out.println("及时性数据查询耗时：" + (endtime - starttime) + "ms");
        return map;
    }

    /**
     * 获取数据集数量
     * @param date
     * @param orgCode
     * @return
     */
    public Envelop getDataSetCount(String date, String orgCode) {
        Envelop envelop = new Envelop();
        Date today = DateUtil.parseDate(date, DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Date end = DateUtil.addDate(1, today);
        try{
            List<String> fields = new ArrayList<String>();
            fields.add("dataSet");
            fields.add("count");
            fields.add("row");
            String sql ="";
            if(StringUtils.isNotEmpty(orgCode)){
                sql = "select dataSet,count(dataSet) as count,sum(dataSetRow) as row from qc/receive_data_set " +
                        " where receiveTime >= '"+date+"' and receiveTime < '"+DateUtil.toString(end)+"'and orgCode='"+orgCode+"' group by dataSet";
            }else{
                sql = "select dataSet,count(dataSet) as count,sum(dataSetRow) as row from qc/receive_data_set " +
                        " where receiveTime >= '"+date+"' and receiveTime < '"+DateUtil.toString(end)+"' group by dataSet";
            }
            List<Map<String, Object>> list = elasticSearchUtil.findBySql(fields,sql);
            envelop.setDetailModelList(list);
            envelop.setSuccessFlg(true);
        }catch(Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    public Envelop getArchivesRight(String startDate, String endDate, String orgCode) {
        Envelop envelop = new Envelop();
        try{
            Date start = DateUtil.formatCharDateYMD(startDate);
            Date end = DateUtil.formatCharDateYMD(endDate);
            int day = (int) ((end.getTime() - start.getTime()) / (1000*3600*24))+1;
            List<Map<String, Object>> dataSetCountList = new ArrayList<Map<String, Object>>();
            Map<String,Object> resMap = new HashMap<String,Object>();
            for(int i =0;i<day;i++){
                Map<String,Object> map = new HashMap<String,Object>();
                Date date = DateUtil.addDate(i,start);
                Map<String,Object> dataSetCount = getErrorDataSetCount(DateUtil.toString(date), orgCode);
                map.put(DateUtil.toString(date),dataSetCount);
                dataSetCountList.add(map);
            }
            List<Map<String, Object>> errorCodeList = getErrorCode(startDate, endDate, orgCode);
            List<Map<String, Object>> codeList = getCode(startDate, endDate, orgCode);
            resMap.put("dataSet",dataSetCountList);
            resMap.put("errorCode",errorCodeList);
            resMap.put("code",codeList);
            envelop.setObj(resMap);
            envelop.setSuccessFlg(true);
        }catch(Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    /**
     * 获取数据集数量和环比
     * @param date
     * @param orgCode
     * @return
     */
    public Map<String,Object> getErrorDataSetCount(String date, String orgCode) {
        Map<String,Object> map = new HashMap<String,Object>();
        try{
            Date today = DateUtil.parseDate(date, DateUtil.DEFAULT_DATE_YMD_FORMAT);
            Date yesterday = DateUtil.addDate(-1, today);
            Date end = DateUtil.addDate(1, today);
            List<String> fields = new ArrayList<String>();
            fields.add("count");
            String sql1 ="";
            String sql2 ="";
            if(StringUtils.isNotEmpty(orgCode)){
                sql1 = "select count(code) as count from qc/receive_data_element where " +
                        " receiveTime>='"+date+"' and receiveTime<'"+DateUtil.toString(end)+"' and orgCode='"+orgCode+"'";
                sql2 = "select count(code) as count from qc/receive_data_element where " +
                        "   receiveTime='"+DateUtil.toString(yesterday)+"' and receiveTime<'"+date+"' and orgCode='"+orgCode+"'";
            }else{
                sql1 = "select count(code) as count from qc/receive_data_element where " +
                        " receiveTime>='"+date+"' and receiveTime<'"+DateUtil.toString(end)+"'";
                sql2 = "select count(code) as count from qc/receive_data_element where " +
                        " receiveTime>='"+DateUtil.toString(yesterday)+"' and receiveTime<'"+date+"'";
            }
            double num1=0;
            double num2=0;
            List<Map<String, Object>> list1 = elasticSearchUtil.findBySql(fields,sql1);
            if(list1!=null&&list1.size()>0){
                num1= (double)(list1.get(0).get("count"));
            }
            List<Map<String, Object>> list2 = elasticSearchUtil.findBySql(fields,sql2);
            if(list2!=null&&list2.size()>0){
                num2= (double)(list2.get(0).get("count"));
            }
            map.put("count",num1);
            if(num2!=0){
                map.put("rate",(num1-num2)/num2*100);
            }else{
                map.put("rate",0);
            }
        }catch(Exception e){
            e.printStackTrace();
            map.put("count",0);
            map.put("rate",0);
        }
        return map;
    }

    /**
     * 错误数据按规则分类占比
     * @param startDate
     * @param endDate
     * @param orgCode
     * @return
     */
    public List<Map<String,Object>> getErrorCode(String startDate, String endDate, String orgCode){
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Date today = DateUtil.parseDate(endDate, DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Date end = DateUtil.addDate(1, today);
        try {
            List<String> fields = new ArrayList<String>();
            fields.add("errorCode");
            fields.add("count");
            String sql = "";
            if (StringUtils.isNotEmpty(orgCode)) {
                sql = "select errorCode,count(errorCode) as count  from qc/receive_data_element " +
                        " where receiveTime>='"+startDate+"' and receiveTime<='"+DateUtil.toString(end)+"'and orgCode='"+orgCode+"' group by errorCode";
            } else {
                sql = "select errorCode,count(errorCode) as count  from qc/receive_data_element " +
                        " where receiveTime>='"+startDate+"' and receiveTime<='"+DateUtil.toString(end)+"' group by errorCode";
            }
            list = elasticSearchUtil.findBySql(fields, sql);
        } catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 错误数据按数据元分类占比
     * @param startDate
     * @param endDate
     * @param orgCode
     * @return
     */
    public List<Map<String,Object>> getCode(String startDate, String endDate, String orgCode){
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Date today = DateUtil.parseDate(endDate, DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Date end = DateUtil.addDate(1, today);
        try {
            List<String> fields = new ArrayList<String>();
            fields.add("code");
            fields.add("count");
            String sql = "";
            if (StringUtils.isNotEmpty(orgCode)) {
                sql = "select code , count(code) as count from qc/receive_data_element " +
                        " where receiveTime>='"+startDate+"' and receiveTime<='"+DateUtil.toString(end)+"'and orgCode='"+orgCode+"' group by code order by count desc ";
            } else {
                sql = "select code , count(code) as count from qc/receive_data_element " +
                        " where receiveTime>='"+startDate+"' and receiveTime<='"+DateUtil.toString(end)+"' group by code order by count desc ";
            }
            list = elasticSearchUtil.findBySql(fields, sql);
            if(list.size()>10){
                list = list.subList(0,10);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 及时率、完整率按天统计
     * @param date
     * @return
     */
    public Envelop getStasticByDay(String date){
        Envelop envelop = new Envelop();
        Map<String,Object> resMap = new HashMap<>();
        Date begin = DateUtil.parseDate(date, DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Date end2 = DateUtil.addDate(7, begin);
        try {
            String sql1 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE  pack_type=1 AND event_date " +
                    "BETWEEN '" + date + " 00:00:00' AND '" + date + " 23:59:59'";
            String sql2 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE pack_type=1 AND event_date BETWEEN " +
                    " '" + date + " 00:00:00' AND '" +  date + " 23:59:59' AND receive_date BETWEEN"+
                    " '" + date + " 00:00:00' AND '" +  DateUtil.toString(end2) + " 23:59:59' ";
            ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
            resultSet1.next();
            ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
            resultSet2.next();
            Map<String,Object> map = getPatientCountEs(date,"");
            //平台档案数据
            int count_pt = new Double(resultSet1.getObject("COUNT(DISTINCT event_no)").toString()).intValue();
            //质控包上传数据
            int count_zk = Integer.parseInt(map.get("total").toString());
            //及时性数据
            int count_js =  new Double(resultSet2.getObject("COUNT(DISTINCT event_no)").toString()).intValue();

            if(count_zk!=0){
                resMap.put("time_rate", String.format("%.2f", ((double)count_js / (double)count_zk)*100)+"%");
                resMap.put("full_rate", String.format("%.2f", ((double)count_pt / (double)count_zk)*100)+"%");
            }else{
                resMap.put("time_rate", "0.00%");
                resMap.put("full_rate", "0.00%");
            }
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            resMap.put("time_rate", "0.00%");
            resMap.put("full_rate", "0.00%");
            envelop.setSuccessFlg(false);
            e.printStackTrace();
        }
        envelop.setObj(resMap);
        return envelop;
    }

}
