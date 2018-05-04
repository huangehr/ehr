package com.yihu.ehr.pack.service;

import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Service - 档案统计分析
 * Created by progr1mmer on 2018/4/13.
 */
@Service
public class PackStatisticsService extends BaseJpaService {

    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

    /**
     * 统计最近七天采集总数 -> getCollectTocalCount
     * @return
     */
    public Map<String, Long> get7DaysTotalCount() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date now = DateUtils.addDays(calendar.getTime(), 1);
        Date before = DateUtils.addDays(now, -6);
        Map<String, Long> esData = elasticSearchUtil.dateHistogram("json_archives", "info", new ArrayList<>(0), before, now , "receive_date", DateHistogramInterval.days(1),"yyyy-MM-dd");
        return esData;
    }

    /**
     * 根据接收日期统计各个医院的数据解析情况 -> getRecieveOrgCount
     * @param date
     * @return
     * @throws Exception
     */
    public List<Object []> getOrgPackAnalyzeSituation(Date date) throws Exception {
        String sql = "SELECT full_name FROM organizations WHERE org_code = %s";
        Session session = currentSession();
        String dateStr = DateUtil.formatDate(date, DateUtil.DEFAULT_DATE_YMD_FORMAT);
        String sql1 = "SELECT org_code, COUNT(org_code) FROM json_archives WHERE archive_status = 0 OR archive_status = 1 AND receive_date BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 00:00:00' GROUP BY org_code";
        ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
        Map<String, Object[]> dataMap1 = new HashMap<>();
        while (resultSet1.next()) {
            Object[] tempArr = new Object[3];
            String code = resultSet1.getString("org_code");
            String name = (String) session.createSQLQuery(String.format(sql, code)).uniqueResult();
            double count = resultSet1.getDouble("COUNT(org_code)");
            tempArr[0] = code;
            tempArr[1] = name;
            tempArr[2] = count;
            dataMap1.put(code, tempArr);
        }
        String sql2 = "SELECT org_code, COUNT(org_code) FROM json_archives WHERE archive_status = 3 AND receive_date BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 00:00:00' GROUP BY org_code";
        ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
        Map<String, Object[]> dataMap2 = new HashMap<>();
        while (resultSet2.next()) {
            Object[] tempArr = new Object[3];
            String code = resultSet2.getString("org_code");
            String name = (String) session.createSQLQuery(String.format(sql, code)).uniqueResult();
            double count = resultSet2.getDouble("COUNT(org_code)");
            tempArr[0] = code;
            tempArr[1] = name;
            tempArr[2] = count;
            dataMap2.put(code, tempArr);
        }
        String sql3 = "SELECT org_code, COUNT(org_code) FROM json_archives WHERE receive_date BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 00:00:00' GROUP BY org_code";
        ResultSet resultSet3 = elasticSearchUtil.findBySql(sql3);
        Map<String, Object[]> dataMap3 = new HashMap<>();
        while (resultSet3.next()) {
            Object[] tempArr = new Object[3];
            String code = resultSet3.getString("org_code");
            String name = (String) session.createSQLQuery(String.format(sql, code)).uniqueResult();
            double count = resultSet3.getDouble("COUNT(org_code)");
            tempArr[0] = code;
            tempArr[1] = name;
            tempArr[2] = count;
            dataMap3.put(code, tempArr);
        }
        List<Object []> dataList = new ArrayList<>();
        for (String key : dataMap3.keySet()) {
            Object [] dataArr = new Object[6];
            dataArr[0] = dataMap3.get(key)[0]; //机构编码
            dataArr[1] = dataMap3.get(key)[1]; //机构名称
            if (dataMap1.containsKey(key)) { //待解析
                dataArr[2] = dataMap1.get(key)[2];
            } else {
                dataArr[2] = 0;
            }
            if (dataMap2.containsKey(key)) { //成功解析
                dataArr[3] = dataMap3.get(key)[2];
            } else {
                dataArr[3] = 0;
            }
            dataArr[4] = dataMap3.get(key)[2]; //总计
            if ((double)dataArr[4] != 0) { //成功率
                dataArr[5] = (double)dataArr[3] / (double)dataArr[4];
            } else {
                dataArr[5] = 0;
            }
            dataList.add(dataArr);
        }
        return dataList;
    }

    /**
     * 按事件类型统计最近七天采集总数 -> getCollectEventTypeCount
     * @param event_type
     * @return
     */
    public Map<String, Long> get7DaysTotalCountByEventType(int event_type) {
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("andOr", "and");
        filterMap.put("condition", "=");
        filterMap.put("field", "event_type");
        filterMap.put("value", event_type);
        List<Map<String, Object>> filterList = new ArrayList<>();
        filterList.add(filterMap);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date now = DateUtils.addDays(calendar.getTime(), 1);
        Date before = DateUtils.addDays(now, -6);
        Map<String, Long> esData = elasticSearchUtil.dateHistogram("json_archives", "info", filterList, before, now , "receive_date", DateHistogramInterval.days(1),"yyyy-MM-dd");
        return esData;
    }

    /**
     * 统计今天各类就诊事件总数 -> getCollectTodayEventTypeCount
     * @return
     * @throws Exception
     */
    public List<Object []> getTodayEventTypeCount() throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String now = dateFormat.format(new Date());
        String sql = "SELECT event_type, COUNT(*) FROM archive_relation WHERE event_date BETWEEN '" + now + " 00:00:00' AND '" +  now + " 00:00:00' GROUP BY event_type";
        ResultSet resultSet = elasticSearchUtil.findBySql(sql);
        List<Object []> dataList = new ArrayList<>();
        while (resultSet.next()) {
            Object [] dataArr = new Object[2];
            dataArr[0] = resultSet.getObject("event_type");
            dataArr[1] = resultSet.getObject("COUNT(*)");
            dataList.add(dataArr);
        }
        return dataList;
    }

    /**
     * 获取一段时间内数据解析情况 -> getArchivesCount
     * @param date
     * @return
     */
    public List<Object []> getPackResolveSituation (Date date) throws Exception {
        String dateStr = DateUtil.formatDate(date, DateUtil.DEFAULT_DATE_YMD_FORMAT);
        String sql1 = "SELECT COUNT(*) FROM json_archives WHERE archive_status = 0 OR archive_status = 1 AND receive_date BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 00:00:00'";
        ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
        String sql2 = "SELECT COUNT(*) FROM json_archives WHERE archive_status = 3 AND receive_date BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 00:00:00'";
        ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
        String sql3 = "SELECT COUNT(*) FROM json_archives WHERE receive_date BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 00:00:00'";
        ResultSet resultSet3 = elasticSearchUtil.findBySql(sql3);
        List<Object []> dataList = new ArrayList<>();
        resultSet1.next();
        resultSet2.next();
        resultSet3.next();
        Object [] dataArr1 = new Object[2];
        dataArr1[0] = "waiting";
        dataArr1[1] = resultSet1.getObject("COUNT(*)");
        dataList.add(dataArr1);
        Object [] dataArr2 = new Object[2];
        dataArr2[0] = "successful";
        dataArr2[1] = resultSet2.getObject("COUNT(*)");
        dataList.add(dataArr2);
        Object [] dataArr3 = new Object[2];
        dataArr3[0] = "total";
        dataArr3[1] = resultSet3.getObject("COUNT(*)");
        dataList.add(dataArr3);
        return dataList;
    }

    /**
     * 获取某天数据新增情况
     * @param date
     * @return
     */
    public List<Map<String,Object>> getArchivesInc(String date,String orgCode) {
        Date begin = DateUtil.parseDate(date, DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Date end = DateUtil.addDate(1, begin);
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT");
        sql.append(" 	sum(");
        sql.append(" 		CASE");
        sql.append(" 		WHEN event_type = 1 THEN");
        sql.append(" 			1");
        sql.append(" 		ELSE");
        sql.append(" 			0");
        sql.append(" 		END");
        sql.append(" 	) inpatient_total,");
        sql.append(" 	sum(");
        sql.append(" 		CASE");
        sql.append(" 		WHEN event_type = 0 THEN");
        sql.append(" 			1");
        sql.append(" 		ELSE");
        sql.append(" 			0");
        sql.append(" 		END");
        sql.append(" 	) oupatient_total,");
        sql.append(" 	inpatient_inc,");
        sql.append(" 	oupatient_inc,");
        sql.append(" 	ed");
        sql.append(" FROM");
        sql.append(" 	(");
        sql.append(" 		SELECT DISTINCT");
        sql.append(" 			t2.org_code,");
        sql.append(" 			t2.patient_id,");
        sql.append(" 			t2.event_no,");
        sql.append(" 			t2.event_type,");
        sql.append(" 			t3.ed,");
        sql.append(" 			t3.inpatient_inc,");
        sql.append(" 			t3.oupatient_inc");
        sql.append(" 		FROM");
        sql.append(" 			json_archives t2,");
        sql.append(" 			(");
        sql.append(" 				SELECT");
        sql.append(" 					sum(");
        sql.append(" 						CASE");
        sql.append(" 						WHEN event_type = 1 THEN");
        sql.append(" 							1");
        sql.append(" 						ELSE");
        sql.append(" 							0");
        sql.append(" 						END");
        sql.append(" 					) inpatient_inc,");
        sql.append(" 					sum(");
        sql.append(" 						CASE");
        sql.append(" 						WHEN event_type = 0 THEN");
        sql.append(" 							1");
        sql.append(" 						ELSE");
        sql.append(" 							0");
        sql.append(" 						END");
        sql.append(" 					) oupatient_inc,");
        sql.append(" 					ed");
        sql.append(" 				FROM");
        sql.append(" 					(");
        sql.append(" 						SELECT DISTINCT");
        sql.append(" 							org_code,");
        sql.append(" 							patient_id,");
        sql.append(" 							event_no,");
        sql.append(" 							event_type,");
        sql.append(" 							DATE(t.event_date) ed");
        sql.append(" 						FROM");
        sql.append(" 							json_archives t");
        sql.append(" 						WHERE");
        sql.append(" 							t.receive_date >= '" + DateUtil.toString(begin) +"'");
        sql.append(" 						AND t.receive_date < '" + DateUtil.toString(end) +"'");
        if(StringUtils.isNotEmpty(orgCode)&&!"null".equals(orgCode)){
            sql.append(" AND t.org_code='"+orgCode+"'");
        }
        sql.append(" 					) t1");
        sql.append(" 				GROUP BY");
        sql.append(" 					ed");
        sql.append(" 			) t3");
        sql.append(" 		WHERE");
        sql.append(" 			DATE(t2.event_date) = t3.ed ");
        if(StringUtils.isNotEmpty(orgCode)&&!"null".equals(orgCode)){
            sql.append(" AND t2.org_code='"+orgCode+"'");
        }
        sql.append(" 	) t4");
        sql.append(" GROUP BY");
        sql.append(" 	inpatient_inc,");
        sql.append(" 	oupatient_inc,");
        sql.append(" 	ed");
        sql.append(" 	order by ed");
        return jdbcTemplate.queryForList(sql.toString());
    }


    /**
     * 完整性分析
     * @param startDate
     * @param endDate
     * @param orgCode
     * @return
     */
    public Envelop getIntegrityAnalysis (String startDate, String endDate, String orgCode) {
        Date start = DateUtil.formatCharDateYMD(startDate);
        Date end = DateUtil.formatCharDateYMD(endDate);
        int day = (int) ((end.getTime() - start.getTime()) / (1000 * 3600 * 24)) + 1;
        Map<String, Object> resMap = new HashMap<>();
        List<Map<String, Map<String, Object>>> res = new ArrayList<>();
        int total = 0;
        int in_patient_total = 0;
        int out_patient_total = 0;
        int total_es = 0;
        int in_patient_total_es = 0;
        int out_patient_total_es = 0;
        int total_sc = 0;
        int in_patient_total_sc = 0;
        int out_patient_total_sc = 0;
        for (int i =0; i < day; i++){
            Date date = DateUtil.addDate(i,start);
            Map<String, Map<String, Object>> map = new HashMap<>();
            Map<String, Object> rate = new HashMap<>();
            //平台数据
            Map<String, Object> map1 = getPatientCount(DateUtil.toString(date), orgCode);
            total += Integer.parseInt(map1.get("total").toString());
            in_patient_total += Integer.parseInt(map1.get("inpatient_total").toString());
            out_patient_total += Integer.parseInt(map1.get("oupatient_total").toString());
            //医院数据
            Map<String,Object> map2 = countByEs(DateUtil.toString(date), orgCode);
            total_es += Integer.parseInt(map2.get("total").toString());
            in_patient_total_es += Integer.parseInt(map2.get("inpatient_total").toString());
            out_patient_total_es += Integer.parseInt(map2.get("oupatient_total").toString());
            //上传数据
            Map<String,Object> map3 = getPatientNum(DateUtil.toString(date), orgCode);
            total_sc+=Integer.parseInt(map3.get("total").toString());
            in_patient_total_sc += Integer.parseInt(map3.get("inpatient_total").toString());
            out_patient_total_sc += Integer.parseInt(map3.get("oupatient_total").toString());
            //平台与医院
            if (Integer.parseInt(map2.get("total").toString())!=0){
                rate.put("total_rate", (Double.parseDouble(map1.get("total").toString()) / Double.parseDouble(map2.get("total").toString())) * 100);
            } else{
                rate.put("total_rate", "0");
            }
            if (Integer.parseInt(map2.get("inpatient_total").toString()) != 0){
                rate.put("inpatient_rate", (Double.parseDouble(map1.get("inpatient_total").toString()) / Double.parseDouble(map2.get("inpatient_total").toString())) * 100);
            } else {
                rate.put("inpatient_rate", "0");
            }
            if (Integer.parseInt(map2.get("oupatient_total").toString()) != 0 ){
                rate.put("oupatient_rate", (Double.parseDouble(map1.get("oupatient_total").toString()) / Double.parseDouble(map2.get("oupatient_total").toString())) * 100);
            } else {
                rate.put("oupatient_rate", "0");
            }
            //平台与上传
            if (Integer.parseInt(map3.get("total").toString()) != 0){
                rate.put("total_rate_sc", (Double.parseDouble(map1.get("total").toString()) / Double.parseDouble(map3.get("total").toString())) * 100);
            } else {
                rate.put("total_rate_sc", "0");
            }
            if (Integer.parseInt(map3.get("inpatient_total").toString()) != 0){
                rate.put("inpatient_rate_sc", (Double.parseDouble(map1.get("inpatient_total").toString()) / Double.parseDouble(map3.get("inpatient_total").toString())) * 100);
            } else {
                rate.put("inpatient_rate_sc", "0");
            }
            if (Integer.parseInt(map3.get("oupatient_total").toString()) != 0){
                rate.put("oupatient_rate_sc", (Double.parseDouble(map1.get("oupatient_total").toString()) / Double.parseDouble(map3.get("oupatient_total").toString())) * 100);
            } else {
                rate.put("oupatient_rate_sc", "0");
            }
            map.put(DateUtil.toString(date),rate);
            res.add(map);
        }
        //平台总数
        resMap.put("total", total);
        resMap.put("inpatient_total", in_patient_total);
        resMap.put("oupatient_total", out_patient_total);
        //医院总数
        resMap.put("total_es", total_es);
        resMap.put("inpatient_total_es", in_patient_total_es);
        resMap.put("oupatient_total_es", out_patient_total_es);
        //上传总数
        resMap.put("total_sc", total_sc);
        resMap.put("inpatient_total_sc", in_patient_total_sc);
        resMap.put("oupatient_total_sc", out_patient_total_sc);
        //平台与医院比例
        if (total_es != 0){
            resMap.put("total_rate", ((double)total / (double)total_es) * 100);
        } else {
            resMap.put("total_rate", "0");
        }
        if (in_patient_total_es != 0){
            resMap.put("inpatient_rate", ((double)in_patient_total / (double)in_patient_total_es) * 100);
        } else {
            resMap.put("inpatient_rate", "0");
        }
        if (out_patient_total_es != 0) {
            resMap.put("oupatient_rate", ((double)out_patient_total / (double)out_patient_total_es)*100);
        } else {
            resMap.put("oupatient_rate", "0");
        }
        //平台与上传比例
        if( total_sc != 0){
            resMap.put("total_rate_sc", ((double)total / (double)total_sc)*100);
        } else {
            resMap.put("total_rate_sc", "0");
        }
        if (in_patient_total_sc != 0){
            resMap.put("inpatient_rate_sc", ((double)in_patient_total / (double)out_patient_total_sc)*100);
        } else {
            resMap.put("inpatient_rate_sc", "0");
        }
        if (out_patient_total_sc != 0){
            resMap.put("oupatient_rate_sc", ((double)out_patient_total / (double)out_patient_total_sc)*100);
        } else {
            resMap.put("oupatient_rate_sc", "0");
        }
        Envelop envelop = new Envelop();
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
    public Map<String,Object> getPatientCount(String date, String orgCode) {
        Date begin = DateUtil.parseDate(date, DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Date end = DateUtil.addDate(1, begin);
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT");
        sql.append("     IFNULL( SUM(CASE WHEN event_type = 1 THEN 1 ELSE 0 END),0) inpatient_total,");
        sql.append("     IFNULL( SUM(CASE WHEN event_type =0 THEN 1 ELSE 0 END),0) oupatient_total,");
        sql.append("     COUNT(1) total");
        sql.append(" FROM(");
        sql.append("     SELECT");
        sql.append("         event_type,");
        sql.append("         patient_id,");
        sql.append("         event_no");
        sql.append("     FROM");
        sql.append("         json_archives t");
        sql.append("     WHERE event_date >= '"+DateUtil.toString(begin)+"' ");
        sql.append("         AND event_date < '"+DateUtil.toString(end)+"'");
        if (StringUtils.isNotEmpty(orgCode)){
            sql.append("         AND org_code = '"+orgCode+"'");
        }
        sql.append("     GROUP BY patient_id,event_no");
        sql.append(" )t");
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString());
        if (list!=null && list.size()>0){
            return list.get(0);
        } else{
            return null;
        }
    }

    /**
     * 获取一段时间内数据解析情况
     * @param date
     * @param orgCode
     * @return
     */
    public Map<String,Object> getPatientNum(String date, String orgCode) {
        Date begin = DateUtil.parseDate(date, DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Date end = DateUtil.addDate(1, begin);
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT");
        sql.append("     IFNULL( SUM(CASE WHEN event_type = 1 THEN 1 ELSE 0 END),0) inpatient_total,");
        sql.append("     IFNULL( SUM(CASE WHEN event_type =0 THEN 1 ELSE 0 END),0) oupatient_total,");
        sql.append("     COUNT(1) total");
        sql.append("     FROM");
        sql.append("         json_archives t");
        sql.append("     WHERE event_date >= '"+DateUtil.toString(begin)+"' ");
        sql.append("         AND event_date < '"+DateUtil.toString(end)+"'");
        if(StringUtils.isNotEmpty(orgCode)){
            sql.append("         AND org_code = '"+orgCode+"'");
        }
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString());
        if(list!=null && list.size()>0){
            return list.get(0);
        }else{
            return null;
        }
    }

    /**
     * 从ES获取数据 - > getPatientCountEs
     * @param date
     * @param orgCode
     * @return
     */
    public Map<String,Object> countByEs (String date, String orgCode) {
        Map<String,Object> resMap = new HashMap<String,Object>();
        int total = 0;
        int inpatient_total = 0;
        int out_patient_total = 0;
        String filters = "event_date=" + date;
        if (StringUtils.isNotEmpty(orgCode)) {
            filters += ";org_code=" + orgCode;
        }
        List<Map<String, Object>> res = elasticSearchUtil.list("qc", "daily_report", filters);
        for (Map<String,Object> report : res){
            total += Integer.parseInt(report.get("HSI07_01_001").toString());
            inpatient_total += Integer.parseInt(report.get("HSI07_01_012").toString());
            out_patient_total += Integer.parseInt(report.get("HSI07_01_002").toString());
        }
        resMap.put("total",total);
        resMap.put("inpatient_total",inpatient_total);
        resMap.put("oupatient_total",out_patient_total);
        return resMap;
    }

    /**
     * 及时性分析
     * @param startDate
     * @param endDate
     * @param orgCode
     * @return
     */
    public Envelop getArchivesTime(String startDate, String endDate, String orgCode) {
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
        for (int i =0;i<day;i++){
            Date date = DateUtil.addDate(i,start);
            Map<String,Map<String,Object>> map = new HashMap<String,Map<String,Object>>();
            Map<String,Object> rate = new HashMap<String,Object>();
            //平台数据
            List<Map<String,Object>> list = getPatientCountTime(DateUtil.toString(date), orgCode);
            total+=(Integer.parseInt(list.get(0).get("total").toString())+Integer.parseInt(list.get(1).get("total").toString()));
            inpatient_total+=Integer.parseInt(list.get(1).get("total").toString());
            oupatient_total+=Integer.parseInt(list.get(0).get("total").toString());
            //医院数据
            Map<String,Object> map2 = countByEs(DateUtil.toString(date), orgCode);
            total_es+=Integer.parseInt(map2.get("total").toString());
            inpatient_total_es+=Integer.parseInt(map2.get("inpatient_total").toString());
            oupatient_total_es+=Integer.parseInt(map2.get("oupatient_total").toString());
            //平台与医院
            if(Integer.parseInt(map2.get("total").toString())!=0){
                rate.put("total_rate", ((double)(Integer.parseInt(list.get(0).get("total").toString())+Integer.parseInt(list.get(1).get("total").toString())) / Double.parseDouble(map2.get("total").toString()))*100);
            }else{
                rate.put("total_rate", "0");
            }
            if(Integer.parseInt(map2.get("inpatient_total").toString())!=0){
                rate.put("inpatient_rate", (Double.parseDouble(list.get(1).get("total").toString()) / Double.parseDouble(map2.get("inpatient_total").toString()))*100);
            }else{
                rate.put("inpatient_rate", "0");
            }
            if(Integer.parseInt(map2.get("oupatient_total").toString())!=0){
                rate.put("oupatient_rate", (Double.parseDouble(list.get(0).get("total").toString()) / Double.parseDouble(map2.get("oupatient_total").toString()))*100);
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
    public List<Map<String,Object>> getPatientCountTime(String date, String orgCode) {
        Date begin = DateUtil.parseDate(date, DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Date end = DateUtil.addDate(2, begin);
        Date end1 = DateUtil.addDate(2, begin);
        Date end2 = DateUtil.addDate(7, begin);
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT");
        sql.append("     IFNULL( SUM(CASE WHEN event_type =0 THEN 1 ELSE 0 END),0) total");
        sql.append(" FROM(");
        sql.append("     SELECT");
        sql.append("         event_type,");
        sql.append("         patient_id,");
        sql.append("         event_no");
        sql.append("     FROM");
        sql.append("         json_archives t");
        sql.append("     WHERE event_date >= '"+DateUtil.toString(begin)+"' ");
        sql.append("         AND event_date < '"+DateUtil.toString(end)+"'");
        sql.append("         AND receive_date >= '"+DateUtil.toString(begin)+"'");
        sql.append("         AND receive_date < '"+DateUtil.toString(end1)+"'");
        if(StringUtils.isNotEmpty(orgCode)){
            sql.append("         AND org_code = '"+orgCode+"'");
        }
        sql.append("     GROUP BY patient_id,event_no");
        sql.append(" )t");
        sql.append(" UNION ALL");
        sql.append(" SELECT");
        sql.append("     IFNULL( SUM(CASE WHEN event_type =1 THEN 1 ELSE 0 END),0) total");
        sql.append(" FROM(");
        sql.append("     SELECT");
        sql.append("         event_type,");
        sql.append("         patient_id,");
        sql.append("         event_no");
        sql.append("     FROM");
        sql.append("         json_archives t");
        sql.append("     WHERE event_date >= '"+DateUtil.toString(begin)+"' ");
        sql.append("         AND event_date < '"+DateUtil.toString(end)+"'");
        sql.append("         AND receive_date >= '"+DateUtil.toString(begin)+"'");
        sql.append("         AND receive_date < '"+DateUtil.toString(end2)+"'");
        if(StringUtils.isNotEmpty(orgCode)){
            sql.append("         AND org_code = '"+orgCode+"'");
        }
        sql.append("     GROUP BY patient_id,event_no");
        sql.append(" )t");
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString());
        return  list;
    }

    /**
     * 获取数据集数量 -> getDataSetCount
     * @param date
     * @param orgCode
     * @return
     */
    public List<Map<String, Object>> getDataSetCount (String date, String orgCode) throws Exception {
        Date today = DateUtil.parseDate(date, DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Date end = DateUtil.addDate(1, today);
        List<String> fields = new ArrayList<String>();
        fields.add("dataSet");
        fields.add("count");
        fields.add("row");
        String sql;
        if (StringUtils.isNotEmpty(orgCode)){
            sql = "SELECT dataSet, COUNT(dataSet) AS count, SUM(dataSetRow) AS row FROM qc/receive_data_set " +
                    " WHERE receiveTime >= '" + date + "' AND receiveTime < '" + DateUtil.toString(end) + "' AND orgCode='" + orgCode + "' GROUP BY dataSet";
        } else {
            sql = "SELECT dataSet, COUNT(dataSet) AS count, SUM(dataSetRow) AS row FROM qc/receive_data_set " +
                    " WHERE receiveTime >= '"+ date +"' AND receiveTime < '" + DateUtil.toString(end) + "' GROUP BY dataSet";
        }
        List<Map<String, Object>> list = elasticSearchUtil.findBySql(fields, sql);
        return list;
    }

    public Envelop getArchivesRight (String startDate, String endDate, String orgCode) throws Exception {
        Date start = DateUtil.formatCharDateYMD(startDate);
        Date end = DateUtil.formatCharDateYMD(endDate);
        int day = (int) ((end.getTime() - start.getTime()) / (1000 * 3600 * 24)) + 1;
        List<Map<String, Object>> dataSetCountList = new ArrayList<Map<String, Object>>();
        Map<String,Object> resMap = new HashMap<String,Object>();
        for (int i = 0; i < day; i++) {
            Map<String,Object> map = new HashMap<String,Object>();
            Date date = DateUtil.addDate(i,start);
            Map<String,Object> dataSetCount = getErrorDataSetCount(DateUtil.toString(date), orgCode);
            map.put(DateUtil.toString(date),dataSetCount);
            dataSetCountList.add(map);
        }
        List<Map<String, Object>> errorCodeList = getErrorCode(startDate, endDate, orgCode);
        List<Map<String, Object>> codeList = getCode(startDate, endDate, orgCode);
        resMap.put("dataSet", dataSetCountList);
        resMap.put("errorCode", errorCodeList);
        resMap.put("code", codeList);
        Envelop envelop = new Envelop();
        envelop.setObj(resMap);
        envelop.setSuccessFlg(true);
        return envelop;
    }

    /**
     * 获取数据集数量和环比 -> getErrorDataSetCount
     * @param date
     * @param orgCode
     * @return
     */
    public Map<String,Object> getErrorDataSetCount(String date, String orgCode) throws Exception {
        Map<String,Object> map = new HashMap<String,Object>();
        Date today = DateUtil.parseDate(date, DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Date yesterday = DateUtil.addDate(-1, today);
        Date end = DateUtil.addDate(1, today);
        List<String> fields = new ArrayList<String>();
        fields.add("count");
        String sql1;
        String sql2;
        if (StringUtils.isNotEmpty(orgCode)){
            sql1 = "select count(code) as count from qc/receive_data_element where " +
                    "receiveTime >= '" + date + "' and receiveTime < '" + DateUtil.toString(end)+ "' and orgCode = '"+orgCode+"'";
            sql2 = "select count(code) as count from qc/receive_data_element where " +
                    "receiveTime = '" + DateUtil.toString(yesterday) + "' and receiveTime < '" + date + "' and orgCode = '" + orgCode + "'";
        } else {
            sql1 = "select count(code) as count from qc/receive_data_element where " +
                    "receiveTime >= '" + date + "' and receiveTime < '" + DateUtil.toString(end) + "'";
            sql2 = "select count(code) as count from qc/receive_data_element where " +
                    "receiveTime >= '" + DateUtil.toString(yesterday) + "' and receiveTime < '"+date+"'";
        }
        double num1 = 0;
        double num2 = 0;
        List<Map<String, Object>> list1 = elasticSearchUtil.findBySql(fields, sql1);
        if (list1 != null && list1.size() > 0){
            num1 = (double)(list1.get(0).get("count"));
        }
        List<Map<String, Object>> list2 = elasticSearchUtil.findBySql(fields, sql2);
        if (list2 != null && list2.size() > 0){
            num2 = (double)(list2.get(0).get("count"));
        }
        map.put("count",num1);
        if (num2 != 0){
            map.put("rate", (num1-num2) / num2 * 100);
        } else {
            map.put("rate", 0);
        }
        return map;
    }

    /**
     * 错误数据按规则分类占比 -> getErrorCode
     * @param startDate
     * @param endDate
     * @param orgCode
     * @return
     */
    public List<Map<String,Object>> getErrorCode(String startDate, String endDate, String orgCode) throws Exception {
        Date today = DateUtil.parseDate(endDate, DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Date end = DateUtil.addDate(1, today);
        List<String> fields = new ArrayList<String>();
        fields.add("errorCode");
        fields.add("count");
        String sql;
        if (StringUtils.isNotEmpty(orgCode)) {
            sql = "select errorCode,count(errorCode) as count from qc/receive_data_element " +
                    "where receiveTime >= '" + startDate + "' and receiveTime <= '" + DateUtil.toString(end) + "' and orgCode = '" + orgCode + "' group by errorCode";
        } else {
            sql = "select errorCode,count(errorCode) as count from qc/receive_data_element " +
                    "where receiveTime >= '" + startDate + "' and receiveTime <= '" + DateUtil.toString(end) + "' group by errorCode";
        }
        List<Map<String, Object>> list  = elasticSearchUtil.findBySql(fields, sql);
        return list;
    }

    /**
     * 错误数据按数据元分类占比 -> getCode
     * @param startDate
     * @param endDate
     * @param orgCode
     * @return
     */
    public List<Map<String,Object>> getCode(String startDate, String endDate, String orgCode) throws Exception {
        Date today = DateUtil.parseDate(endDate, DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Date end = DateUtil.addDate(1, today);
        List<String> fields = new ArrayList<String>();
        fields.add("code");
        fields.add("count");
        String sql;
        if (StringUtils.isNotEmpty(orgCode)) {
            sql = "select code, count(code) as count from qc/receive_data_element " +
                    "where receiveTime >= '" + startDate + "' and receiveTime <= '" + DateUtil.toString(end) + "' and orgCode = '" + orgCode + "' group by code order by count desc";
        } else {
            sql = "select code, count(code) as count from qc/receive_data_element " +
                    "where receiveTime >= '" + startDate + "' and receiveTime <= '" + DateUtil.toString(end) + "' group by code order by count desc";
        }
        List<Map<String, Object>> list = elasticSearchUtil.findBySql(fields, sql);
        if (list.size() > 10){
            list = list.subList(0, 10);
        }
        return list;
    }

}
