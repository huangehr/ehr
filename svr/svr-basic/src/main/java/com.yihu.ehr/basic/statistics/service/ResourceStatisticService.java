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
