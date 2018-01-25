package com.yihu.ehr.basic.statistics.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.basic.report.dao.XJsonArchivesRepository;
import com.yihu.ehr.basic.statistics.feign.DailyReportClient;
import com.yihu.ehr.entity.report.JsonArchives;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ResourceStatisticService extends BaseJpaService<JsonArchives, XJsonArchivesRepository> {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DailyReportClient dailyReportClient;
    @Autowired
    protected ObjectMapper objectMapper;
    //统计最近七天采集总数
    public List<Object> getCollectTocalCount() {
        Session session = currentSession();
        String sql = "SELECT count(1), date_format(receive_date, '%Y-%m-%d') AS date FROM json_archives " +
                " WHERE DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(receive_date) GROUP BY date_format(receive_date, '%Y-%m-%d')";
        SQLQuery query = session.createSQLQuery(sql);
        return query.list();
    }


    /**
     * getRecieveOrgCount 根据接收日期统计各个医院的数据解析情况
     *
     * @param date
     * @return
     */
    public List<Map<String, Object>> getRecieveOrgCount(String date) {
        Date begin = DateUtil.parseDate(date, DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Date end = DateUtil.addDate(1, begin);
        String sql = "SELECT " +
                " org_code," +
                " (SELECT o.full_name FROM organizations o WHERE o.org_code=temp.org_code) org_name," +
                " waiting," +
                " successful," +
                " total," +
                " (successful / total)* 100 rate" +
                " FROM " +
                " (SELECT " +
                " org_code," +
                " SUM(CASE WHEN archive_status = 0 THEN 1 ELSE 0 END) waiting," +
                " SUM(CASE WHEN archive_status = 3 THEN 1 ELSE 0 END) successful," +
                " COUNT(1) total " +
                " FROM json_archives " +
                " WHERE receive_date >= '" + DateUtil.toString(begin, DateUtil.DEFAULT_DATE_YMD_FORMAT) +"'"+
                " AND receive_date <'" + DateUtil.toString(end, DateUtil.DEFAULT_DATE_YMD_FORMAT)+"'" +
                " GROUP BY org_code) temp ORDER BY successful DESC";

        return jdbcTemplate.queryForList(sql);
    }


    //统计最近七天采集门诊、住院各总数
    public List<Object> getCollectEventTypeCount(int eventType) {
        Session session = currentSession();
        String sql = "SELECT count(1), date_format(receive_date, '%Y-%m-%d') AS date, event_type FROM json_archives " +
                "WHERE DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(receive_date) AND  event_type=:eventType  GROUP BY date_format(receive_date, '%Y-%m-%d'), event_type;";
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter("eventType", eventType);
        return query.list();
    }


    //统计今天门诊、住院各总数
    public List<Object> getCollectTodayEventTypeCount() {
        Session session = currentSession();
        String sql = "SELECT count(1), event_type FROM json_archives " +
                "WHERE DATE_SUB(CURDATE(), INTERVAL 1 DAY) < date(event_date) GROUP BY event_type";
        SQLQuery query = session.createSQLQuery(sql);
        return query.list();
    }

    /**
     * 获取一段时间内数据解析情况
     * @param date
     * @return
     */
    public List<Map<String,Object>> getArchivesCount(String date) {
        Date begin = DateUtil.parseDate(date, DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Date end = DateUtil.addDate(1, begin);
        String sql = "SELECT " +
                " SUM(CASE WHEN archive_status = 0 THEN 1 ELSE 0 END) waiting," +
                " SUM(CASE WHEN archive_status = 3 THEN 1 ELSE 0 END) successful," +
                " COUNT(1) total " +
                " FROM json_archives t  " +
                " WHERE receive_date >= '" + DateUtil.toString(begin) +"'"+
                " AND receive_date <'" + DateUtil.toString(end) +"'";
        return jdbcTemplate.queryForList(sql);
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
    public Envelop getArchivesFull(String startDate, String endDate, String orgCode) {
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
            total_sc+=Integer.parseInt(map3.get("total").toString());
            inpatient_total_sc+=Integer.parseInt(map3.get("inpatient_total").toString());
            oupatient_total_sc+=Integer.parseInt(map3.get("oupatient_total").toString());
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
        if(StringUtils.isNotEmpty(orgCode)){
            sql.append("         AND org_code = '"+orgCode+"'");
        }
        sql.append("     GROUP BY patient_id,event_no");
        sql.append(" )t");

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString());
        if(list!=null && list.size()>0){
            return list.get(0);
        }else{
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
            String filter = objectMapper.writeValueAsString(list);
            Envelop envelop = dailyReportClient.list(filter);
            List<Map<String, Object>> res = envelop.getDetailModelList();
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
        for(int i =0;i<day;i++){
            Date date = DateUtil.addDate(i,start);
            Map<String,Map<String,Object>> map = new HashMap<String,Map<String,Object>>();
            Map<String,Object> rate = new HashMap<String,Object>();
            //平台数据
            List<Map<String,Object>> list = getPatientCountTime(DateUtil.toString(date), orgCode);
            total+=(Integer.parseInt(list.get(0).get("total").toString())+Integer.parseInt(list.get(1).get("total").toString()));
            inpatient_total+=Integer.parseInt(list.get(1).get("total").toString());
            oupatient_total+=Integer.parseInt(list.get(0).get("total").toString());
            //医院数据
            Map<String,Object> map2 = getPatientCountEs(DateUtil.toString(date), orgCode);
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
}
