package com.yihu.ehr.redis.service;



import com.yihu.ehr.entity.geography.GeographyDict;
import com.yihu.ehr.redis.feign.XGeographyClient;
import com.yihu.ehr.redis.schema.AddressDictKeySchema;
import com.yihu.ehr.redis.schema.HealthProblemDictKeySchema;
import com.yihu.ehr.redis.schema.Icd10KeySchema;
import com.yihu.ehr.redis.schema.OrgKeySchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

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

        //清空相关Redis
        addressDictKeySchema.deleteAll();

        for(GeographyDict geographyDict:list){
            addressDictKeySchema.set(String.valueOf(geographyDict.getId()), geographyDict.getName());
        }
    }

    /******************************** 缓存健康问题Redis ******************************************************/
    @Autowired
    HealthProblemDictKeySchema healthProblemDictKeySchema;
    @Autowired
    Icd10KeySchema icd10KeySchema;
    /**
     * 缓存健康问题名称Redis
     */
    public boolean cacheHpName()
    {
        String sql = "select code,name from health_problem_dict";

        //清空相关Redis
        healthProblemDictKeySchema.deleteAll();

        List<Map<String,Object>> list = jdbc.queryForList(sql);
        for(Map<String,Object> map:list){
            healthProblemDictKeySchema.set((String) map.get("code"),(String)map.get("name"));
        }
        return true;
    }

    /**
     * 缓存ICD10 Redis
     */
    public boolean cacheIcd10()
    {
        String sql = "select t.hp_code,d.code,d.name from\n" +
                "(select r.icd10_id,group_concat(p.`code` separator ';') hp_code \n" +
                "from hp_icd10_relation r\n" +
                "left join health_problem_dict p on p.id = r.hp_id \n" +
                "group by icd10_id) t\n" +
                "left join icd10_dict d on t.icd10_id = d.id" ;

        //清空相关Redis
        icd10KeySchema.deleteAll();
        icd10KeySchema.deleteHpCode();

        List<Map<String,Object>> list = jdbc.queryForList(sql);
        for(Map<String,Object> map:list){
            icd10KeySchema.set((String) map.get("code"),(String)map.get("name"));
            icd10KeySchema.setHpCode((String) map.get("code"),(String)map.get("hp_code"));
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

        //清空相关Redis
        orgKeySchema.deleteAll();

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

        //清空相关Redis
        orgKeySchema.deleteOrgArea();

        List<Map<String,Object>> list = jdbc.queryForList(sql);
        for(Map<String,Object> map:list){
            orgKeySchema.setOrgArea(String.valueOf(map.get("org_code")),String.valueOf(map.get("administrative_division")));
        }
        return true;
    }

    /**
     * 缓存机构Saas区域Redis
     */
    public boolean cacheOrgSaasArea()
    {
        String sql = "select org_code,saas_code from org_saas where type='1' order by org_code";

        //清空相关Redis
        orgKeySchema.deleteOrgSaasArea();

        List<Map<String,Object>> list = jdbc.queryForList(sql);

        String orgCode = "";
        String val = "";
        for(Map<String,Object> map:list){
            String orgCodeCurrent =  String.valueOf(map.get("org_code"));
            String saasCodeCurrent =  String.valueOf(map.get("saas_code"));
            if(!orgCode.equals(orgCodeCurrent) && !StringUtils.isEmpty(orgCode))
            {
                orgKeySchema.setOrgSaasArea(orgCode,val);
                val = saasCodeCurrent;
                orgCode = orgCodeCurrent;
            }
            else{
                if(StringUtils.isEmpty(val))
                {
                    orgCode = orgCodeCurrent;
                    val =  saasCodeCurrent;
                }
                else{
                    orgCode = orgCodeCurrent;
                    val += "," + saasCodeCurrent;
                }
            }
        }

        if(!StringUtils.isEmpty(orgCode))
        {
            orgKeySchema.setOrgSaasArea(orgCode,val);
        }

        return true;
    }

    /**
     * 缓存机构Saas机构Redis
     */
    public boolean cacheOrgSaasOrg()
    {
        String sql = "select org_code,saas_code from org_saas where type='2' order by org_code";

        //清空相关Redis
        orgKeySchema.deleteOrgSaasOrg();

        List<Map<String,Object>> list = jdbc.queryForList(sql);

        String orgCode = "";
        String val = "";
        for(Map<String,Object> map:list){
            String orgCodeCurrent =  String.valueOf(map.get("org_code"));
            String saasCodeCurrent =  String.valueOf(map.get("saas_code"));
            if(!orgCode.equals(orgCodeCurrent) && !StringUtils.isEmpty(orgCode))
            {
                orgKeySchema.setOrgSaasOrg(orgCode,val);
                val = saasCodeCurrent;
                orgCode = orgCodeCurrent;
            }
            else{
                if(StringUtils.isEmpty(val))
                {
                    orgCode = orgCodeCurrent;
                    val =  saasCodeCurrent;
                }
                else{
                    orgCode = orgCodeCurrent;
                    val += "," + saasCodeCurrent;
                }
            }
        }

        if(!StringUtils.isEmpty(orgCode))
        {
            orgKeySchema.setOrgSaasOrg(orgCode,val);
        }

        return true;
    }
}
