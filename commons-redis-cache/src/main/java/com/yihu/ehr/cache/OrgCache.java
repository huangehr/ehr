package com.yihu.ehr.cache;

import com.yihu.ehr.redis.RedisClient;
import com.yihu.ehr.util.log.LogService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;

/**
 * 基础数据缓存。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.19 10:58
 */
@Component
public class OrgCache {
    @Autowired
    RedisClient redisClient;

    @Autowired
    EntityManager entityManager;

    public void cacheOrg() {
        RedisClient redisClient = null;
        Session session = entityManager.unwrap(Session.class);

        Query query = session.createSQLQuery("select code, name from organizations");
        List<Object[]> orgList = query.list();
        for (Object[] record : orgList) {
            String code = (String) record[0];
            String name = (String) record[1];

            this.redisClient.set();
        } catch (Exception e) {
            LogService.getLogger(OrgCache.class).error(e.getMessage());
        } finally {
            session.close();
        }
    }

    public void cleanAll() {
        cleanOrganization();
    }

    public CachedOrganization getOrganization(String orgCode){
//        XRedisClient redisDAO = ServiceFactory.getService(Services.RedisClient);
//        return redisDAO.get(FoundationKeySchema.makeOrganizationKey(orgCode));
        return null;
    }

    static void cacheOrg(RedisClient redisClient, Session session) {
        Query query = session.createSQLQuery(OrgQuery);
        List<Object[]> orgList = query.list();
        for (Object[] record : orgList) {
            CachedOrganization org = new CachedOrganization((String) record[0], (String) record[1]);
            redisClient.set(FoundationKeySchema.makeOrganizationKey(org.code), org);
        }
    }

    static void cleanOrganization(){
        //clean(RedisNamespace.FoundationDataOrg);
    }

    private void clean(String resourceNamespace){
        RedisClient redisDAO = ServiceFactory.getService(Services.RedisClient);

        Set<String> keys = redisDAO.keys(resourceNamespace + "*");
        redisDAO.delete(keys);
    }
}
