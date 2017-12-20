package com.yihu.ehr.statistics.service;

import com.yihu.ehr.entity.report.JsonArchives;
import com.yihu.ehr.patient.dao.XJsonArchiveDao;
import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceStatisticService extends BaseJpaService<JsonArchives, XJsonArchiveDao> {

    //统计最近七天采集总数
    public List<Object> getCollectTocalCount() {
        Session session = currentSession();
        String sql = "SELECT count(1), date_format(receive_date, '%Y-%c-%d') as date FROM json_archives " +
                " where DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(receive_date) GROUP BY date_format(receive_date, '%Y-%c-%d')";
        SQLQuery query = session.createSQLQuery(sql);
        return query.list();
    }

    //统计最近七天采集门诊、住院各总数
    public List<Object> getCollectEventTypeCount(int eventType) {
        Session session = currentSession();
        String sql = "SELECT count(1), date_format(receive_date, '%Y-%c-%d') as date, event_type FROM json_archives " +
                "where DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(receive_date) and  event_type=:eventType  GROUP BY date_format(event_date, '%Y-%c-%d'), event_type;";
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter("eventType", eventType);
        return query.list();
    }

    //统计今天门诊、住院各总数
    public List<Object> getCollectTodayEventTypeCount() {
        Session session = currentSession();
        String sql = "SELECT count(1), event_type FROM json_archives " +
                "where DATE_SUB(CURDATE(), INTERVAL 1 DAY) < date(event_date) GROUP BY event_type";
        SQLQuery query = session.createSQLQuery(sql);
        return query.list();
    }
}
