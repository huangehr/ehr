package com.yihu.ehr.analyze.service.pack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.elasticsearch.ElasticSearchPool;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.InternalCardinality;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private JdbcTemplate jdbcTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

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
     * 获取某天数据新增情况
     * @param date
     * @return
     */
    public List<Map<String,Object>> getArchivesInc(String date,String orgCode) throws Exception{
        List<Map<String, Object>> list = getIncCount(date, orgCode);
        if(!list.isEmpty()){
            for(Map<String, Object> map : list){
                if(map.get("inpatient_inc")==null){
                    map.put("inpatient_inc",0);
                }
                if(map.get("oupatient_inc")==null){
                    map.put("oupatient_inc",0);
                }
                map.putAll(getPatientCount(map.get("ed").toString(),orgCode));
            }
        }
        return list;
    }


    public List<Map<String, Object>> getIncCount(String date,String orgCode) throws Exception{
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("andOr", "and");
        map.put("condition", ">=");
        map.put("field", "receive_date");
        map.put("value", ""+date+" 00:00:00");
        list.add(map);
        map = new HashMap<>();
        map.put("andOr", "and");
        map.put("condition", "<");
        map.put("field", "receive_date");
        map.put("value", ""+date+" 23:59:59");
        if(StringUtils.isNotEmpty(orgCode)&&!"null".equals(orgCode)){
            map = new HashMap<>();
            map.put("andOr", "and");
            map.put("condition", "=");
            map.put("field", "org_code");
            map.put("value", orgCode);
        }
        list.add(map);
        TransportClient transportClient = elasticSearchPool.getClient();
        try {
            List<Map<String, Object>> resultList = new ArrayList<>();
            SearchRequestBuilder builder = transportClient.prepareSearch("json_archives");
            builder.setTypes("info");
            builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
            builder.setQuery(elasticSearchUtil.getQueryBuilder(list));
            DateHistogramBuilder dateHistogramBuilder = new DateHistogramBuilder("date");
            dateHistogramBuilder.field("event_date");
            dateHistogramBuilder.interval(DateHistogramInterval.DAY);
            dateHistogramBuilder.format("yyyy-MM-dd");
            dateHistogramBuilder.minDocCount(0);
            AggregationBuilder terms = AggregationBuilders.terms("terms").field("event_type");
            CardinalityBuilder cardinality = AggregationBuilders.cardinality("cardinality").field("event_no");
            terms.subAggregation(cardinality);
            dateHistogramBuilder.subAggregation(terms);
            builder.addAggregation(dateHistogramBuilder);
            builder.setSize(0);
            builder.setExplain(true);
            SearchResponse response = builder.get();
            Histogram histogram = response.getAggregations().get("date");
            histogram.getBuckets().forEach(item1 -> {
                Map<String, Object> temp = new HashMap<>();
                temp.put("ed", item1.getKeyAsString());
                LongTerms term = item1.getAggregations().get("terms");
                term.getBuckets().forEach(item2 -> {
                    InternalCardinality internalCard = item2.getAggregations().get("cardinality");
                    System.out.println(internalCard.getProperty("value"));
                    if((Long)item2.getKey()==0){
                        temp.put("inpatient_inc",internalCard.getProperty("value"));
                    }else{
                        temp.put("oupatient_inc",internalCard.getProperty("value"));
                    }
                });
                resultList.add(temp);
            });
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
        return envelop;
    }
    /**
     * 平台就诊人数 去重复
     * @param dateStr
     * @param orgCode
     * @return
     */
    public Map<String,Object> getPatientCount(String dateStr, String orgCode) throws Exception{
        List<String> fields = new ArrayList<String>();
        fields.add("patient_id");
        fields.add("event_no");
        String sql1 ="";
        String sql2 ="";
        String sql3 ="";
        if(StringUtils.isNotEmpty(orgCode)){
            sql1 = "SELECT patient_id,event_no FROM json_archives WHERE event_type=1 AND org_code='"+orgCode+"' AND event_date BETWEEN" +
                    " '" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59' GROUP BY patient_id,event_no";

            sql2 = "SELECT patient_id,event_no FROM json_archives WHERE event_type=0 AND org_code='"+orgCode+"' AND event_date BETWEEN " +
                    "'" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59' GROUP BY patient_id,event_no";

            sql3 = "SELECT patient_id,event_no FROM json_archives WHERE org_code='"+orgCode+"' AND event_date BETWEEN " +
                    "'" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59' GROUP BY patient_id,event_no";
        }else{
            sql1 = "SELECT patient_id,event_no FROM json_archives WHERE event_type=1 AND event_date " +
                    "BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59' GROUP BY patient_id,event_no";

            sql2 = "SELECT patient_id,event_no FROM json_archives WHERE event_type=0 AND event_date " +
                    "BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59' GROUP BY patient_id,event_no";

            sql3 = "SELECT patient_id,event_no FROM json_archives WHERE event_date " +
                    "BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59' GROUP BY patient_id,event_no";
        }

        List<Map<String,Object>> list1 = elasticSearchUtil.findBySql(fields,sql1);
        List<Map<String,Object>> list2 = elasticSearchUtil.findBySql(fields,sql2);
        List<Map<String,Object>> list3= elasticSearchUtil.findBySql(fields,sql3);
        Map<String,Object> map = new HashMap<>();
        map.put("inpatient_total",list1.size());
        map.put("oupatient_total",list2.size());
        map.put("total",list3.size());
        return map;
    }


    /**
     * 平台就诊人数
     * @param dateStr
     * @param orgCode
     * @return
     */
    public Map<String,Object> getPatientNum(String dateStr, String orgCode) throws Exception{
        String sql1 ="";
        String sql2 ="";
        String sql3 ="";
        if(StringUtils.isNotEmpty(orgCode)){
            sql1 = "SELECT COUNT(*) FROM json_archives WHERE event_type=1 AND org_code='"+orgCode+"' AND event_date " +
                    "BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59'";

            sql2 = "SELECT COUNT(*) FROM json_archives WHERE event_type=0 AND org_code='"+orgCode+"' AND event_date " +
                    "BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59'";

            sql3 = "SELECT COUNT(*) FROM json_archives WHERE org_code='"+orgCode+"' AND event_date " +
                    "BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59'";
        }else{
            sql1 = "SELECT COUNT(*) FROM json_archives WHERE event_type=1 AND event_date " +
                    "BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59'";

            sql2 = "SELECT COUNT(*) FROM json_archives WHERE event_type=0 AND event_date " +
                    "BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59'";

            sql3 = "SELECT COUNT(*) FROM json_archives WHERE event_date " +
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
        return map;
    }
    /**
     * 从ES获取数据
     * @param date
     * @param orgCode
     * @return
     */
    public Map<String,Object> getPatientCountEs(String date, String orgCode) {
        Map<String,Object> resMap = new HashMap<String,Object>();
        int total=0;
        int inpatient_total=0;
        int oupatient_total=0;
        try {
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("andOr", "and");
            map.put("condition", "=");
            map.put("field", "event_date");
            map.put("value", date);
            list.add(map);
            if (StringUtils.isNotEmpty(orgCode)) {
                map = new HashMap<String, Object>();
                map.put("andOr", "and");
                map.put("condition", "=");
                map.put("field", "org_code");
                map.put("value", orgCode);
                list.add(map);
            }
            List<Map<String, Object>> res = elasticSearchUtil.list("qc","daily_report",list);
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
            total+=(Integer.parseInt(list.get("inpatient_total").toString())+Integer.parseInt(list.get("oupatient_total").toString()));
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
        return envelop;
    }

    /**
     * 获取一段时间内数据解析情况
     * @param date
     * @param orgCode
     * @return
     */
    public Map<String,Object> getPatientCountTime(String date, String orgCode) throws Exception{
        Date begin = DateUtil.parseDate(date, DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Date end = DateUtil.addDate(2, begin);
        Date end1 = DateUtil.addDate(2, begin);
        Date end2 = DateUtil.addDate(7, begin);

        List<String> fields = new ArrayList<String>();
        fields.add("patient_id");
        fields.add("event_no");
        String sql1 ="";
        String sql2 ="";
        if(StringUtils.isNotEmpty(orgCode)){
            sql1 = "SELECT patient_id,event_no FROM json_archives WHERE event_type=1 AND org_code='"+orgCode+"' AND event_date BETWEEN" +
                    " '" + DateUtil.toString(begin) + " 00:00:00' AND '" +  DateUtil.toString(end) + " 00:00:00' AND receive_date BETWEEN"+
                    " '" + DateUtil.toString(begin) + " 00:00:00' AND '" +  DateUtil.toString(end2) + " 00:00:00' GROUP BY patient_id,event_no";

            sql2 = "SELECT patient_id,event_no FROM json_archives WHERE event_type=0 AND org_code='"+orgCode+"' AND event_date BETWEEN " +
                    " '" + DateUtil.toString(begin) + " 00:00:00' AND '" +  DateUtil.toString(end) + " 00:00:00' AND receive_date BETWEEN"+
                    " '" + DateUtil.toString(begin) + " 00:00:00' AND '" +  DateUtil.toString(end1) + " 00:00:00' GROUP BY patient_id,event_no";
        }else{
            sql1 = "SELECT patient_id,event_no FROM json_archives WHERE event_type=1  AND event_date BETWEEN" +
                    " '" + DateUtil.toString(begin) + " 00:00:00' AND '" +  DateUtil.toString(end) + " 00:00:00' AND receive_date BETWEEN"+
                    " '" + DateUtil.toString(begin) + " 00:00:00' AND '" +  DateUtil.toString(end2) + " 00:00:00' GROUP BY patient_id,event_no";

            sql2 = "SELECT patient_id,event_no FROM json_archives WHERE event_type=0  AND event_date BETWEEN " +
                    " '" + DateUtil.toString(begin) + " 00:00:00' AND '" +  DateUtil.toString(end) + " 00:00:00' AND receive_date BETWEEN"+
                    " '" + DateUtil.toString(begin) + " 00:00:00' AND '" +  DateUtil.toString(end1) + " 00:00:00' GROUP BY patient_id,event_no";
        }

        List<Map<String,Object>> list1 = elasticSearchUtil.findBySql(fields,sql1);
        List<Map<String,Object>> list2 = elasticSearchUtil.findBySql(fields,sql2);
        Map<String,Object> map = new HashMap<>();
        map.put("inpatient_total",list1.size());
        map.put("oupatient_total",list2.size());
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
}
