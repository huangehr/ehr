package com.yihu.ehr.basic.statistics.service;

import com.yihu.ehr.basic.report.dao.XJsonArchivesRepository;
import com.yihu.ehr.entity.report.JsonArchives;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.datetime.DateUtil;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ResourceStatisticService extends BaseJpaService<JsonArchives, XJsonArchivesRepository> {

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
    public List<Object> getRecieveOrgCount(String date) {
        Date begin = DateUtil.parseDate(date, DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Date end = DateUtil.addDate(1, begin);
        Session session = currentSession();
        String sql = "SELECT " +
                "org_code," +
                "(SELECT o.full_name FROM organizations o WHERE o.org_code=temp.org_code) org_name," +
                "waiting," +
                "successful," +
                "total," +
                "(successful / total)* 100 rate" +
                "FROM" +
                "(SELECT" +
                "t.org_code," +
                "SUM(CASE WHEN archive_status = 0 THEN 1 ELSE 0 END) waiting," +
                "SUM(CASE WHEN archive_status = 0 THEN 1 ELSE 0 END) successful," +
                "COUNT(t.archive_status = 3) total " +
                "FROM json_archives " +
                "WHERE receive_date >= " + DateUtil.toString(begin, DateUtil.DEFAULT_DATE_YMD_FORMAT) +
                " AND receive_date <" + DateUtil.toString(end, DateUtil.DEFAULT_DATE_YMD_FORMAT) +
                " GROUP BY) temp ORDER BY successful DESC";

        SQLQuery query = session.createSQLQuery(sql);
        return query.list();
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
}
