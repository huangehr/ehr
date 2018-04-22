package com.yihu.ehr.resource.service;

import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.entity.dict.SystemDictEntry;
import com.yihu.ehr.query.BaseJpaService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.*;

/**
 * Service - 数据资源中心
 * Created by Progr1mmer on 2018/01/05.
 */
@Service
public class ResourceCenterService extends BaseJpaService {

    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

    // ------------------------------- 统计相关 start ------------------------------------

    public BigInteger getMedicalResourcesCount() {
        Session session = currentSession();
        String sql = "SELECT COUNT(1) FROM organizations";
        Query query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        BigInteger count = (BigInteger) query.uniqueResult();
        String sql1 = "SELECT COUNT(1) FROM doctors";
        Query query1 = session.createSQLQuery(sql1);
        query1.setFlushMode(FlushMode.COMMIT);
        BigInteger count1 = (BigInteger) query1.uniqueResult();
        return count.add(count1);
    }

    public BigInteger getDemographicCount() {
        Session session = currentSession();
        String hql = "SELECT COUNT(1) FROM demographics";
        Query query = session.createSQLQuery(hql);
        query.setFlushMode(FlushMode.COMMIT);
        return (BigInteger) query.uniqueResult();
    }

    public BigInteger getUseCardCount() {
        Session session = currentSession();
        String sql = "SELECT COUNT(DISTINCT(owner_idcard)) FROM user_cards";
        Query query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        return (BigInteger)query.uniqueResult();
    }

    public List getOrgAreaIdGroup(int currentCityId) throws Exception {
        String sql1 = "SELECT org_code, COUNT(org_code) FROM archive_relation GROUP BY org_code";
        ResultSet resultSet = elasticSearchUtil.findBySql(sql1);
        Map<String, Double> esResult = new HashMap<>();
        while (resultSet.next()) {
            String code = resultSet.getString("org_code");
            double count = resultSet.getDouble("COUNT(org_code)");
            esResult.put(code, count);
        }
        Session session = currentSession();
        String sql = "SELECT o.org_code, o.administrative_division FROM organizations o " +
                "WHERE o.administrative_division IN (SELECT id FROM address_dict WHERE pid = :pid) ";
        Query query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setInteger("pid", currentCityId);
        List<Object []> list = query.list();
        List<Object []> result = new ArrayList<>();
        list.forEach(item -> {
            if (item.length == 2) {
                if (esResult.containsKey(item[0])) {
                    result.add(new Object[]{
                            item[1], esResult.get(item[0])
                    });
                }
            }
        });
        return result;
    }

    public String getAreaNameById(int id) {
        Session session = currentSession();
        String sql = "SELECT name FROM address_dict WHERE id = :id";
        Query query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setInteger("id", id);
        return (String)query.uniqueResult();
    }

    public List getOrgAreaNameGroupByClazz(String clazz, int currentCityId) {
        Session session = currentSession();
        String sql;
        if (StringUtils.isEmpty(clazz)) {
            sql = "SELECT a.name, COUNT(1) " +
                    "FROM organizations o " +
                    "LEFT JOIN address_dict a ON o.administrative_division = a.id WHERE org_type = 'Hospital' " +
                    "AND o.administrative_division IN (SELECT id FROM address_dict WHERE pid = :pid) " +
                    "GROUP BY o.administrative_division, a.name";
        } else {
            sql = "SELECT a.name, COUNT(1) " +
                    "FROM organizations o " +
                    "LEFT JOIN address_dict a ON o.administrative_division = a.id " +
                    "WHERE o.big_classification = :clazz  AND o.org_type = 'Hospital' " +
                    "AND o.administrative_division IN (SELECT id FROM address_dict WHERE pid = :pid) " +
                    "GROUP BY o.administrative_division, a.name";
        }
        Query query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setInteger("pid", currentCityId);
        if (!StringUtils.isEmpty(clazz)) {
            query.setString("clazz", clazz);
        }
        return query.list();
    }

    public List getMedicalAreaCountGroupByRole(String roleType, int currentCityId) {
        Session session = currentSession();
        String sql;
        if ("Doctor".equals(roleType)) {
            sql = "SELECT o.administrative_division, COUNT(1) " +
                    "FROM doctors d " +
                    "LEFT JOIN organizations o ON o.org_code = d.org_code " +
                    "WHERE d.role_type IN ('10', '11') " +
                    "AND o.administrative_division IN (SELECT id FROM address_dict WHERE pid = :pid) " +
                    "GROUP BY d.org_code, o.administrative_division";
        } else {
            sql = "SELECT o.administrative_division, COUNT(1) " +
                    "FROM doctors d " +
                    "LEFT JOIN organizations o ON o.org_code = d.org_code " +
                    "WHERE d.role_type = '8' " +
                    "AND o.administrative_division IN (SELECT id FROM address_dict WHERE pid = :pid) " +
                    "GROUP BY d.org_code, o.administrative_division";
        }
        Query query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setInteger("pid", currentCityId);
        return query.list();
    }

    public BigInteger getMedicalCountByRoleType(String roleType, int currentCityId) {
        Session session = currentSession();
        String sql;
        if ("Doctor".equals(roleType)) {
            sql = "SELECT COUNT(1) FROM doctors d " +
                    "LEFT JOIN organizations o ON o.org_code = d.org_code " +
                    "WHERE d.role_type IN ('10', '11') " +
                    "AND o.administrative_division IN (SELECT id FROM address_dict WHERE pid = :pid)";
        } else {
            sql = "SELECT COUNT(1) FROM doctors d " +
                    "LEFT JOIN organizations o ON o.org_code = d.org_code " +
                    "WHERE role_type = '8' " +
                    "AND o.administrative_division IN (SELECT id FROM address_dict WHERE pid = :pid)";
        }
        Query query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setInteger("pid", currentCityId);
        return  (BigInteger) query.uniqueResult();
    }

    public double getJsonArchiveTotalCount() throws Exception {
        String sql = "SELECT COUNT(*) FROM json_archives";
        ResultSet resultSet =  elasticSearchUtil.findBySql(sql);
        resultSet.next();
        return (double)resultSet.getObject("COUNT(*)");
    }

    public double getJsonArchiveCount(String status) throws Exception {
        String sql = "SELECT COUNT(*) FROM json_archives WHERE archive_status = " + status;
        ResultSet resultSet = elasticSearchUtil.findBySql(sql);
        resultSet.next();
        return (double)resultSet.getObject("COUNT(*)");
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
        Session session = currentSession();
        String sql = "SELECT count(1), tt.age  from(  " +
                " SELECT t1.id ,  " +
                "  ELT(   CEIL(  FLOOR( TIMESTAMPDIFF(MONTH, STR_TO_DATE(t1.id ,'%Y%m%d'), CURDATE())/12) /10+1 ), " +
                " '0-1','1-10','11-20','21-30','31-40','41-50','51-60','61-70','71-80','81-90','> 90') as age from ( "+
                " SELECT CASE when length(id)=15  then CONCAT('19',substr(id ,7,6)) ELSE substr(id ,7,8) end  id  from demographics t )t1 "+
                " )tt WHERE tt.age is not null  GROUP BY tt.age";
        SQLQuery query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        return query.list();
    }

    public List<Object[]> newStatisticsDemographicsAgeCount() {
        Session session = currentSession();;
        String sql = "SELECT count(1), tt.age ,tt.gender from( SELECT t1.id , gender, " +
                "ELT( CEIL( FLOOR( TIMESTAMPDIFF(MONTH, STR_TO_DATE(t1.id ,'%Y%m%d'), CURDATE())/12) /10+1 ), \n" +
                "'0-6','7-17','18-40','41-65','> 65') as age from ( " +
                " SELECT CASE when length(id)=15  then CONCAT('19',substr(id ,7,6)) ELSE substr(id ,7,8) end  id ,gender from demographics t )t1 " +
                " )tt WHERE tt.age is not null AND  gender IN ('1', '2') GROUP BY tt.age, tt.gender";
        SQLQuery query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        return query.list();
    }

    public List getJsonArchiveReceiveDateGroup(Date start, Date end) {
        List<Map<String, Long>> esData = elasticSearchUtil.dateHistogram("json_archives", "info", new ArrayList<>(0), start, end , "receive_date", DateHistogramInterval.days(1),"yyyy-MM-dd");
        return esData;
    }

    public List getJsonArchiveFinishDateGroup(Date start, Date end) {
        List<Map<String, Long>> esData = elasticSearchUtil.dateHistogram("json_archives", "info", new ArrayList<>(0), start, end , "finish_date", DateHistogramInterval.days(1),"yyyy-MM-dd");
        return esData;
    }

    public Integer getOrgAreaByCode(String orgCode, int currentCityId) {
        Session session = currentSession();
        String sql = "SELECT o.administrative_division FROM organizations o " +
                "WHERE o.org_code = :orgCode " +
                "AND O.administrative_division IN (SELECT id FROM address_dict WHERE pid = :pid)";
        Query query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setString("orgCode", orgCode);
        query.setInteger("pid", currentCityId);
        return (Integer) query.uniqueResult();
    }

    public String getOrgNameByCode(String orgCode) {
        Session session = currentSession();
        String sql = "SELECT full_name FROM organizations WHERE org_code = :orgCode";
        Query query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setString("orgCode", orgCode);
        return (String) query.uniqueResult();
    }

    public String getDeptNameByCode(String deptCode) {
        Session session = currentSession();
        String sql = "SELECT entry.value FROM std_dictionary_entry_59083976eebd entry " +
                "INNER JOIN std_dictionary_59083976eebd dict ON entry.dict_id = dict.id " +
                "WHERE dict.code = 'STD_DEPARTMENT' AND entry.code = :deptCode";
        Query query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setString("deptCode", deptCode);
        return (String) query.uniqueResult();
    }

    public int getCurrentCityId() {
        Session session = currentSession();
        String sql = "SELECT entry.value FROM system_dict_entries entry " +
                "INNER JOIN system_dicts dict ON dict.id = entry.dict_id " +
                "WHERE entry.code = 'CITY'";
        Query query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        return Integer.parseInt((String)query.uniqueResult());
    }

    public List getDistrict(int currentCityId) {
        Session session = currentSession();
        String sql = "SELECT id, name FROM address_dict WHERE pid = :pid";
        Query query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setInteger("pid", currentCityId);
        return query.list();
    }

    // ------------------------------- 统计相关 end ------------------------------------

    // ------------------------------- 大数据展示相关 start ------------------------------------

    public List<Object[]> findAppFeatureIdAndNameByAppIdAndCode(String code, String appId) {
        Session session = currentSession();
        String sql = "SELECT id, name FROM apps_feature WHERE code = :code AND app_id = :appId";
        SQLQuery query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setString("code", code);
        query.setString("appId", appId);
        return query.list();
    }

    public BigInteger getTotalViewCount(Integer dataSource) {
        Session session = currentSession();
        String sql;
        if (dataSource != null) {
            sql = "SELECT COUNT(1) FROM rs_resource WHERE data_source = :dataSource";
        } else {
            sql = "SELECT COUNT(1) FROM  rs_resource";
        }
        SQLQuery query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        if (dataSource != null) {
            query.setInteger("dataSource", dataSource);
        }
        return (BigInteger)query.uniqueResult();
    }

    public List<Object[]> getResourceCategoryIdAndNameList(){
        Session session = currentSession();
        String sql = "SELECT id, name FROM rs_resource_category";
        SQLQuery query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        return query.list();
    }

    public BigInteger countResourceByResourceCateIdAndDataSource (String cateId, Integer dataSource) {
        Session session = currentSession();
        String sql = "SELECT COUNT(1) FROM rs_resource WHERE category_id = :cateId AND data_source = :dataSource";
        SQLQuery query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setString("cateId", cateId);
        query.setInteger("dataSource", dataSource);
        return (BigInteger) query.uniqueResult();
    }

    public BigInteger getTotalReportCount() {
        Session session = currentSession();
        String sql;
        sql = "SELECT COUNT(1) FROM rs_report";
        SQLQuery query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        return (BigInteger)query.uniqueResult();
    }

    public List<Object[]> getReportCategoryIdAndNameList(){
        Session session = currentSession();
        String sql = "SELECT id, name FROM rs_report_category";
        SQLQuery query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        return query.list();
    }

    public BigInteger countReportByReportCateId (Integer cateId) {
        Session session = currentSession();
        String sql = "SELECT COUNT(1) FROM rs_report WHERE report_category_id = :cateId";
        SQLQuery query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setInteger("cateId", cateId);
        return (BigInteger) query.uniqueResult();
    }

    public BigInteger getTotalQuotaCount() {
        Session session = currentSession();
        String sql;
        sql = "SELECT COUNT(1) FROM tj_quota";
        SQLQuery query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        return (BigInteger)query.uniqueResult();
    }

    public List<Object[]> getQuotaCategoryIdAndNameList(){
        Session session = currentSession();
        String sql = "SELECT id, name FROM tj_quota_category";
        SQLQuery query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        return query.list();
    }

    public BigInteger countQuotaByQuotaCateId (Integer cateId) {
        Session session = currentSession();
        String sql = "SELECT COUNT(1) FROM tj_quota WHERE quota_type = :cateId";
        SQLQuery query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setInteger("cateId", cateId);
        return (BigInteger) query.uniqueResult();
    }

}
