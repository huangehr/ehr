package com.yihu.ehr.basic.statistics.service;

import com.yihu.ehr.basic.report.dao.XJsonArchivesRepository;
import com.yihu.ehr.entity.report.JsonArchives;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ResourceStatisticService extends BaseJpaService<JsonArchives, XJsonArchivesRepository> {
    @Autowired
    private JdbcTemplate jdbcTemplate;
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
                " SUM(CASE WHEN archive_status = 0 THEN 1 ELSE 0 END) num1," +
                " SUM(CASE WHEN archive_status = 3 THEN 1 ELSE 0 END) num2," +
                " COUNT(1)num3 " +
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
}
