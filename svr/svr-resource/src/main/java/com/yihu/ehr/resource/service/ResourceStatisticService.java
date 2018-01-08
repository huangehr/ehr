package com.yihu.ehr.resource.service;

import com.yihu.ehr.entity.dict.SystemDictEntry;
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

    public BigInteger getPatientArchiveCount() {
        Session session = currentSession();
        String sql = "SELECT COUNT(*) FROM archive_relation";
        Query query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        return (BigInteger)query.uniqueResult();
    }

    public BigInteger getMedicalResourcesCount() {
        Session session = currentSession();
        String sql = "SELECT COUNT(*) FROM organizations";
        Query query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        BigInteger count = (BigInteger) query.uniqueResult();
        String sql1 = "SELECT COUNT(*) FROM doctors";
        Query query1 = session.createSQLQuery(sql1);
        query1.setFlushMode(FlushMode.COMMIT);
        BigInteger count1 = (BigInteger) query1.uniqueResult();
        return count.add(count1);
    }

    public BigInteger getDemographicCount() {
        Session session = currentSession();
        String hql = "SELECT COUNT(*) FROM demographics";
        Query query = session.createSQLQuery(hql);
        query.setFlushMode(FlushMode.COMMIT);
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

    public List<SystemDictEntry> getSystemDictEntry(Long dictId) {
        Session session = currentSession();
        String hql = "SELECT systemDictEntry FROM SystemDictEntry systemDictEntry WHERE systemDictEntry.dictId = :dictId";
        Query query = session.createQuery(hql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setLong("dictId", dictId);
        return query.list();
    }

    public List<Object> getStatisticsDemographicsAgeCount() {
        Session session = currentSession();;
        String sql = "SELECT count(1), tt.age  from(  " +
                " SELECT t1.id ,  " +
                "  ELT(   CEIL(  FLOOR( TIMESTAMPDIFF(MONTH, STR_TO_DATE(t1.id ,'%Y%m%d'), CURDATE())/12) /10+1 ), " +
                " '0-1','1-10','11-20','21-30','31-40','41-50','51-60','61-70','71-80','81-90','> 90') as age from ( "+
                " SELECT CASE when length(id)=15  then CONCAT('19',substr(id ,7,6)) ELSE substr(id ,7,8) end  id  from demographics t )t1 "+
                " )tt WHERE tt.age is not null  GROUP BY tt.age";
        SQLQuery query = session.createSQLQuery(sql);
        return query.list();
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

}
