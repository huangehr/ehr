package com.yihu.ehr.cache;

import com.yihu.ehr.redis.RedisClient;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;
import java.util.Set;

/**
 * 基础数据缓存。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.19 10:58
 */
public class FoundationDataRedisCache {
    static String OrgQuery = "SELECT org_code, short_name FROM organizations";

    public static void load() {
//        RedisClient redisClient = null;
//        XSQLGeneralDAO generalDAO = ServiceFactory.getService(Services.SQLGeneralDAO);
//        Session session = generalDAO.getCurrentSession();
//
//        try {
//            if (!session.isConnected()) {
//                session = generalDAO.openSession();
//            }
//
//            loadOrganization(redisClient, session);
//        } catch (Exception e) {
//            LogService.getLogger(FoundationDataRedisCache.class).error(e.getMessage());
//        } finally {
//            session.close();
//        }
    }

    public static void cleanAll() {
        cleanOrganization();
    }

    public static CachedOrganization getOrganization(String orgCode){
//        XRedisClient redisDAO = ServiceFactory.getService(Services.RedisClient);
//        return redisDAO.get(FoundationDataKeySchema.makeOrganizationKey(orgCode));
        return null;
    }

    static void loadOrganization(RedisClient redisClient, Session session) {
//        Query query = session.createSQLQuery(OrgQuery);
//        List<Object[]> orgList = query.list();
//        for (Object[] record : orgList) {
//            CachedOrganization org = new CachedOrganization((String) record[0], (String) record[1]);
//            redisClient.set(FoundationDataKeySchema.makeOrganizationKey(org.code), org);
//        }
    }

    static void cleanOrganization(){
        //clean(RedisNamespace.FoundationDataOrg);
    }

    private static void clean(String resourceNamespace){
//        RedisClient redisDAO = ServiceFactory.getService(Services.RedisClient);
//
//        Set<String> keys = redisDAO.keys(resourceNamespace + "*");
//        redisDAO.delete(keys);
    }
}
