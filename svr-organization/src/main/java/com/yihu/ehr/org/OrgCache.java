package com.yihu.ehr.org;

import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.redis.RedisClient;
import com.yihu.ehr.schema.OrgKeySchema;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 缓存机构数据。
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

    @Autowired
    OrgKeySchema keySchema;

    public MOrganization organization(String orgCode){
        String name = redisClient.get(keySchema.name(orgCode));
        if(StringUtils.isEmpty(name)) return null;

        MOrganization organization = new MOrganization();
        organization.setOrgCode(orgCode);
        organization.setFullName(name);

        return organization;
    }

    public List<MOrganization> organizations(){
        Set<String> keys = redisClient.keys(keySchema.name("*"));
        List<MOrganization> organizations = new ArrayList<>(keys.size());

        for (String key : keys){
            MOrganization organization = new MOrganization();
            organization.setOrgCode(key.split(":")[1]);
            organization.setFullName(redisClient.get(key));

            organizations.add(organization);
        }

        return organizations;
    }

    public void cacheData(boolean force) {
        if (force) clean();

        Session session = entityManager.unwrap(Session.class);

        try {
            Query query = session.createSQLQuery("select org_code, full_name from organizations");
            List<Object[]> orgList = query.list();
            for (Object[] record : orgList) {
                String code = (String) record[0];
                String name = (String) record[1];

                redisClient.set(keySchema.name(code), name);
            }
        } finally {
            if(null != session) session.close();
        }
    }

    public void clean() {
        redisClient.delete(keySchema.name("*"));
    }
}
