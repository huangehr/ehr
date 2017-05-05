package com.yihu.ehr.redis.service;



import com.yihu.ehr.entity.geography.GeographyDict;
import com.yihu.ehr.redis.feign.XGeographyClient;
import com.yihu.ehr.redis.schema.AddressDictKeySchema;
import com.yihu.ehr.redis.schema.HealthProblemDictKeySchema;
import com.yihu.ehr.redis.schema.OrgKeySchema;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


/**
 * Redis初始化管理
 * @author hzp add at 20170425
 */
@Service
public class RedisInitService {

    @Autowired
    JdbcTemplate jdbc;

    /****************************** 缓存行政地址Redis ***********************************************/
    @Autowired
    XGeographyClient geographyClient;
    @Autowired
    AddressDictKeySchema addressDictKeySchema;

    /**
     * 缓存行政地址Redis
     */
    public void cacheAddressDict() throws Exception {
        List<GeographyDict> list =  geographyClient.getAllAddressDict();
        for(GeographyDict geographyDict:list){
            addressDictKeySchema.set(String.valueOf(geographyDict.getId()), geographyDict.getName());
        }
    }

    /******************************** 缓存健康问题Redis ******************************************************/
    @Autowired
    HealthProblemDictKeySchema healthProblemDictKeySchema;

    /**
     * 缓存健康问题Redis
     */
    public boolean cacheIcd10ByHpCode()
    {
        String sql = "select i.code,h.code hp_code \n" +
                "from hp_icd10_relation r,icd10_dict i,health_problem_dict h\n" +
                "where r.hp_id=h.id \n" +
                "and r.icd10_id=i.id\n" ;

        List<Map<String,Object>> list = jdbc.queryForList(sql);
        for(Map<String,Object> map:list){
            healthProblemDictKeySchema.set(String.valueOf(map.get("hp_code")),String.valueOf(map.get("code")));
        }
        return true;
    }


    /******************************** 机构信息Redis ******************************************************/
    @Autowired
    OrgKeySchema orgKeySchema;

    /**
     * 缓存机构名称Redis
     */
    public boolean cacheOrgName()
    {
        String sql = "select org_code, full_name from organizations";

        List<Map<String,Object>> list = jdbc.queryForList(sql);
        for(Map<String,Object> map:list){
            orgKeySchema.set(String.valueOf(map.get("org_code")),String.valueOf(map.get("full_name")));
        }
        return true;
    }

    /**
     * 缓存机构区域Redis
     */
    public boolean cacheOrgArea()
    {
        String sql = "select org_code, administrative_division from organizations";

        List<Map<String,Object>> list = jdbc.queryForList(sql);
        for(Map<String,Object> map:list){
            orgKeySchema.setOrgArea(String.valueOf(map.get("org_code")),String.valueOf(map.get("administrative_division")));
        }
        return true;
    }


}
