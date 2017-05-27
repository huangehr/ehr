package com.yihu.ehr.report.service;

import com.yihu.ehr.entity.patient.ArchiveRelation;
import com.yihu.ehr.entity.report.QcDailyReportDetail;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.report.dao.XQcDailyReportDetailRepository;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author janseny
 * @version 1.0
 * @created 2017/5/08
 */
@Service
@Transactional
public class QcDailyStatisticsService extends BaseJpaService<ArchiveRelation, XQcDailyReportDetailRepository> {


    public long getQcDailyStatisticsStorage(String orgCode) {
        Session session = currentSession();
        String hql  = "select count(*)  from ArchiveRelation ar where ar.orgCode=:orgCode " ;
        Query query = session.createQuery(hql);
        query.setString("orgCode", orgCode);
        long count = (Long)query.uniqueResult();
        return  count;
    }

    public List getQcDailyStatisticsStorageGroupEventType(String orgCode) {
        Session session = currentSession();
        String hql  = "select count(*),ar.eventType  from ArchiveRelation ar where ar.orgCode=:orgCode group by ar.eventType " ;
        Query query = session.createQuery(hql);
        query.setString("orgCode", orgCode);
        List list = query.list();
        return  list;
    }


    public List getQcDailyStatisticsIdentify(String orgCode) {
        Session session = currentSession();
        String sql  = "select  case when  sum(ar.identify_flag=1) is null then 0 else sum(ar.identify_flag=1)  end as identifyNum ,  " +
                " sum(ar.identify_flag is null or ar.identify_flag=0) as noIdentifyNum ,ar.event_type as eventType  " +
                " from archive_relation ar where ar.org_code=:orgCode group by ar.event_type " ;
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter("orgCode", orgCode);
        List list = query.list();
        return  list;
    }

    public List getQcDailyStatisticsStorageByDate(String orgCode,String startDate,String endDate) {
        Session session = currentSession();
        String sql = " select  count(*),DATE(ar.event_date) as ed,ar.event_type as et, " +
                " case when sum(ar.identify_flag=1) is null then 0 else sum(ar.identify_flag=1) end as identifyNum, " +
                " case when sum(ar.identify_flag is null or ar.identify_flag=0) is null then 0 else sum(ar.identify_flag is null or ar.identify_flag=0) end as noIdentifyNum " +
                " from archive_relation ar" +
                " where ar.org_code=:orgCode and ( DATE(ar.event_date) >=:startDate and  DATE(ar.event_date) <=:endDate)" +
                "   GROUP BY ed,et ORDER BY ed DESC";

        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter("orgCode", orgCode);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.list();
    }





}
