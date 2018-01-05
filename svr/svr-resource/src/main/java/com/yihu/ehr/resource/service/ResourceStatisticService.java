package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import org.apache.commons.lang.StringUtils;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Service - 数据资源中心
 * Created by Progr1mmer on 2018/01/05.
 */
@Service
public class ResourceStatisticService extends BaseJpaService {

    public BigInteger getDemographicCount() {
        Session session = currentSession();
        String hql = "SELECT COUNT(*) FROM demographics";
        Query query = session.createSQLQuery(hql);
        return (BigInteger) query.uniqueResult();
    }

    public BigInteger getUseCardCount() {
        Session session = currentSession();
        String hql = "SELECT COUNT(*) FROM user_cards";
        Query query = session.createSQLQuery(hql);
        query.setFlushMode(FlushMode.COMMIT);
        return (BigInteger)query.uniqueResult();
    }

    public List getOrgAreaIdGroup() {
        Session session = currentSession();
        String sql = "SELECT o.administrative_division, COUNT(*) " +
                "FROM archive_relation a " +
                "LEFT JOIN organizations o ON a.org_code = o.org_code " +
                "GROUP BY a.org_code, o.administrative_division";
        Query query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        return query.list();
    }

    public String getAreaNameById(int id) {
        Session session = currentSession();
        String sql = "SELECT name FROM address_dict WHERE id = :id";
        Query query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setInteger("id", id);
        return (String)query.uniqueResult();
    }

    public List getArchiveRelationDateGroup(Date before) {
        Session session = currentSession();
        String hql = "SELECT DATE_FORMAT(createDate, '%Y-%m-%d'), COUNT(*) FROM ArchiveRelation archiveRelation WHERE archiveRelation.createDate > :before GROUP BY DATE_FORMAT(createDate, '%Y-%m-%d')";
        Query query = session.createQuery(hql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setDate("before", before);
        return query.list();
    }

    public List getOrgAreaNameGroupByClazz(String clazz) {
        Session session = currentSession();
        String sql;
        if(StringUtils.isEmpty(clazz)) {
            sql = "SELECT a.name, COUNT(*) " +
                    "FROM organizations o " +
                    "LEFT JOIN address_dict a ON o.administrative_division = a.id " +
                    "GROUP BY o.administrative_division, a.name";
        }else {
            sql = "SELECT a.name, COUNT(*) " +
                    "FROM organizations o " +
                    "LEFT JOIN address_dict a ON o.administrative_division = a.id " +
                    "WHERE o.big_classification = :clazz " +
                    "GROUP BY o.administrative_division, a.name";
        }
        Query query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        if(!StringUtils.isEmpty(clazz)) {
            query.setString("clazz", clazz);
        }
        return query.list();
    }

    public List getMedicalAreaCountGroupByRole(String roleType) {
        Session session = currentSession();
        String sql;
        if("Doctor".equals(roleType)) {
            sql = "SELECT o.administrative_division, COUNT(*) " +
                    "FROM doctors d " +
                    "LEFT JOIN organizations o ON o.org_code = d.orgCode " +
                    "WHERE d.role_type = :roleType OR d.role_type IS NULL GROUP BY d.orgCode, o.administrative_division";
        }else {
            sql = "SELECT o.administrative_division, COUNT(*) " +
                    "FROM doctors d " +
                    "LEFT JOIN organizations o ON o.org_code = d.orgCode " +
                    "WHERE d.role_type = :roleType GROUP BY d.orgCode, o.administrative_division";
        }
        Query query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setString("roleType", roleType);
        return query.list();
    }

    public BigInteger getMedicalCountByRoleType(String roleType) {
        Session session = currentSession();
        String sql;
        if("Doctor".equals(roleType)) {
            sql = "SELECT COUNT(*) FROM doctors WHERE LENGTH(orgCode) > 0 AND (role_type = :roleType OR role_type IS NULL)";
        }else {
            sql = "SELECT COUNT(*) FROM doctors WHERE role_type = :roleType";
        }
        Query query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setString("roleType", roleType);
        return  (BigInteger) query.uniqueResult();
    }

    public BigInteger getJsonArchiveCount(String status) {
        Session session = currentSession();
        String sql = "SELECT COUNT(*) FROM json_archives WHERE archive_status = :status";
        Query query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setString("status", status);
        return (BigInteger)query.uniqueResult();
    }

    public List getJsonArchiveReceiveDateGroup(Date before) {
        Session session = currentSession();
        String hql = "SELECT DATE_FORMAT(receiveDate, '%Y-%m-%d'), COUNT(*) FROM JsonArchives jsonArchives WHERE jsonArchives.receiveDate > :before GROUP BY DATE_FORMAT(receiveDate, '%Y-%m-%d')";
        Query query = session.createQuery(hql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setDate("before", before);
        return query.list();
    }

    public List getJsonArchiveFinishDateGroup(Date before) {
        Session session = currentSession();
        String hql = "SELECT DATE_FORMAT(finishDate, '%Y-%m-%d'), COUNT(*) FROM JsonArchives jsonArchives WHERE jsonArchives.finishDate > :before GROUP BY DATE_FORMAT(finishDate, '%Y-%m-%d')";
        Query query = session.createQuery(hql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setDate("before", before);
        return query.list();
    }

    public BigInteger getArchiveRelationCountByEventType(String eventType) {
        Session session = currentSession();
        String sql = "SELECT COUNT(*) FROM archive_relation WHERE event_type = :eventType";
        Query query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setString("eventType", eventType);
        return (BigInteger)query.uniqueResult();
    }

    public String getOrgNameByCode(String orgCode) {
        Session session = currentSession();
        String sql = "SELECT full_name FROM organizations WHERE org_code = :orgCode";
        Query query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setString("orgCode", orgCode);
        return (String) query.uniqueResult();
    }

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
                "where DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(receive_date) and  event_type=:eventType  GROUP BY date_format(receive_date, '%Y-%c-%d'), event_type;";
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
