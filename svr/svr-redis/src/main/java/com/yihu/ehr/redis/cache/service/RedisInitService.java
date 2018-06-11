package com.yihu.ehr.redis.cache.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redis.schema.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Redis初始化管理
 * @author hzp add at 20170425
 */
@Service
public class RedisInitService extends BaseJpaService {

    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private HealthProblemDictKeySchema healthProblemDictKeySchema;
    @Autowired
    private Icd10KeySchema icd10KeySchema;
    @Autowired
    private OrgKeySchema orgKeySchema;
    @Autowired
    private RsAdapterMetaKeySchema rsAdapterMetaKeySchema;
    @Autowired
    private RsAdapterDictKeySchema rsAdapterDictKeySchema;
    @Autowired
    private RsMetadataKeySchema rsMetadataKeySchema;

    /**
     * 缓存健康问题名称Redis
     */
    public int cacheHpName() {
        String sql = "select code, name from health_problem_dict";
        List<Map<String,Object>> list = jdbc.queryForList(sql);
        //清空相关Redis
        healthProblemDictKeySchema.deleteAll();
        for (Map<String,Object> map : list){
            healthProblemDictKeySchema.set((String) map.get("code"), (String)map.get("name"));
        }
        return list.size();
    }

    /**
     * 缓存ICD10 Redis
     */
    public int cacheIcd10() {
        String sql = "select t.hp_code, d.code, d.name, d.chronic_flag, d.type from\n" +
                "(select r.icd10_id, group_concat(p.`code` separator ';') hp_code \n" +
                "from icd10_hp_relation r\n" +
                "left join health_problem_dict p on p.id = r.hp_id \n" +
                "group by icd10_id) t\n" +
                "left join icd10_dict d on t.icd10_id = d.id" ;
        List<Map<String,Object>> list = jdbc.queryForList(sql);
        //清空相关Redis
        icd10KeySchema.deleteAll();
        icd10KeySchema.deleteHpCode();
        icd10KeySchema.deleteChronic();
        list.forEach(item -> {
            icd10KeySchema.set((String) item.get("code"), (String)item.get("name"));
            icd10KeySchema.setHpCode((String) item.get("code"), (String)item.get("hp_code"));
            if (item.get("chronic_flag") != null) {
                if (item.get("type") != null) {
                    icd10KeySchema.setChronicInfo((String) item.get("code"), item.get("chronic_flag") + "-" + item.get("type"));
                } else {
                    icd10KeySchema.setChronicInfo((String) item.get("code"), item.get("chronic_flag") + "-0");
                }
            }
        });
        return list.size();
    }

    /**
     * 缓存机构名称Redis
     */
    public int cacheOrgName() {
        String sql = "select org_code, full_name from organizations";
        List<Map<String, Object>> list = jdbc.queryForList(sql);
        //清空相关Redis
        orgKeySchema.deleteAll();
        for (Map<String,Object> map:list){
            orgKeySchema.set(String.valueOf(map.get("org_code")), String.valueOf(map.get("full_name")));
        }
        return list.size();
    }

    /**
     * 缓存机构区域Redis
     */
    public int cacheOrgArea() {
        String sql = "select org_code, administrative_division from organizations";
        List<Map<String,Object>> list = jdbc.queryForList(sql);
        //清空相关Redis
        orgKeySchema.deleteOrgArea();
        for (Map<String,Object> map:list){
            orgKeySchema.setOrgArea(String.valueOf(map.get("org_code")), String.valueOf(map.get("administrative_division")));
        }
        return list.size();
    }

    /**
     * 缓存机构Saas区域Redis
     */
    public int cacheOrgSaasArea() {
        String sql = "select org_code,saas_code from org_saas where type='1' order by org_code";
        List<Map<String,Object>> list = jdbc.queryForList(sql);
        //清空相关Redis
        orgKeySchema.deleteOrgSaasArea();
        String orgCode = "";
        String val = "";
        for (Map<String,Object> map:list){
            String orgCodeCurrent =  String.valueOf(map.get("org_code"));
            String saasCodeCurrent =  String.valueOf(map.get("saas_code"));
            if (!orgCode.equals(orgCodeCurrent) && !StringUtils.isEmpty(orgCode)) {
                orgKeySchema.setOrgSaasArea(orgCode,val);
                val = saasCodeCurrent;
                orgCode = orgCodeCurrent;
            } else {
                if (StringUtils.isEmpty(val)) {
                    orgCode = orgCodeCurrent;
                    val =  saasCodeCurrent;
                } else {
                    orgCode = orgCodeCurrent;
                    val += "," + saasCodeCurrent;
                }
            }
        }
        if (!StringUtils.isEmpty(orgCode)) {
            orgKeySchema.setOrgSaasArea(orgCode,val);
        }
        return list.size();
    }

    /**
     * 缓存机构Saas机构Redis
     */
    public int cacheOrgSaasOrg() {
        String sql = "select org_code, saas_code from org_saas where type='2' order by org_code";
        List<Map<String, Object>> list = jdbc.queryForList(sql);
        //清空相关Redis
        orgKeySchema.deleteOrgSaasOrg();
        String orgCode = "";
        String val = "";
        for (Map<String,Object> map:list){
            String orgCodeCurrent =  String.valueOf(map.get("org_code"));
            String saasCodeCurrent =  String.valueOf(map.get("saas_code"));
            if (!orgCode.equals(orgCodeCurrent) && !StringUtils.isEmpty(orgCode)) {
                orgKeySchema.setOrgSaasOrg(orgCode,val);
                val = saasCodeCurrent;
                orgCode = orgCodeCurrent;
            } else {
                if (StringUtils.isEmpty(val)) {
                    orgCode = orgCodeCurrent;
                    val =  saasCodeCurrent;
                } else {
                    orgCode = orgCodeCurrent;
                    val += "," + saasCodeCurrent;
                }
            }
        }
        if (!StringUtils.isEmpty(orgCode)) {
            orgKeySchema.setOrgSaasOrg(orgCode,val);
        }
        return list.size();
    }

    /**
     * 缓存适配数据字典数据
     * @param id
     * @return
     */
    public int cacheAdapterDict(String id){
        String schemaSql = "SELECT adapter_version FROM rs_adapter_scheme WHERE id = " + id;
        String metaSql = "SELECT dict_code, src_dict_entry_code, dict_entry_code, src_dict_entry_name FROM rs_adapter_dictionary WHERE scheme_id = " + id;
        Map<String, Object> schemaMap = jdbc.queryForMap(schemaSql);
        List<Map<String, Object>> dictList = jdbc.queryForList(metaSql);
        //清空相关Redis
        rsAdapterDictKeySchema.deleteAll();
        for (Map<String, Object> dictMap : dictList) {
            if (StringUtils.isEmpty(dictMap.get("dict_code")) || StringUtils.isEmpty(dictMap.get("src_dict_entry_code"))) {
                continue;
            }
            rsAdapterDictKeySchema.setMetaData(schemaMap.get("adapter_version").toString(), dictMap.get("dict_code").toString(),
                    dictMap.get("src_dict_entry_code").toString(), dictMap.get("dict_entry_code").toString() + "&" + dictMap.get("src_dict_entry_name"));
        }
        return dictList.size();
    }

    /**
     * 缓存适配数据元数据
     * @param id
     * @return
     */
    public int cacheAdapterMetadata(String id){
        String schemaSql = "SELECT adapter_version FROM rs_adapter_scheme WHERE id = " + id;
        String metaSql = "SELECT src_dataset_code, src_metadata_code, metadata_id FROM rs_adapter_metadata WHERE scheme_id = " + id;
        Map<String, Object> schemaMap = jdbc.queryForMap(schemaSql);
        List<Map<String, Object>> metaList = jdbc.queryForList(metaSql);
        //清空相关Redis
        rsAdapterMetaKeySchema.deleteAll();
        for (Map<String, Object> metaMap : metaList) {
            if (StringUtils.isEmpty(metaMap.get("src_dataset_code")) || StringUtils.isEmpty(metaMap.get("metadata_id"))) {
                continue;
            }
            rsAdapterMetaKeySchema.setMetaData(schemaMap.get("adapter_version").toString(), metaMap.get("src_dataset_code").toString(),
                    metaMap.get("src_metadata_code").toString(), metaMap.get("metadata_id").toString());
        }
        return metaList.size();
    }

    /**
     * 缓存数据元字典（Dict_code不为空）
     * @return
     */
    public int cacheMetadataDict() {
        String sql = "SELECT id, dict_code FROM rs_metadata WHERE dict_code != NULL OR dict_code != ''";
        //String sql1 = "SELECT a FROM RsMetadata a WHERE a.dictCode <> NULL AND a.dictCode <> ''";
        List<Map<String, Object>> metaList = jdbc.queryForList(sql);
        //清空相关Redis
        rsMetadataKeySchema.deleteAll();
        for (Map<String, Object> tempMap : metaList) {
            if (StringUtils.isEmpty(tempMap.get("dict_code"))) {
                continue;
            }
            rsMetadataKeySchema.set((String) tempMap.get("id"), (String) tempMap.get("dict_code"));
        }
        return metaList.size();
    }


    //TODO ------------------- 未知用途 --------------------------
    @Autowired
    private IndicatorsDictKeySchema indicatorsDictKeySchema;

    /**
     * 缓存指标
     * @return
     */
    /*public boolean cacheIndicatorsDict() {
        String sql = "SELECT * FROM indicators_dict";
        List<Map<String, Object>> list = jdbc.queryForList(sql);
        indicatorsDictKeySchema.deleteAll();
        for (Map<String, Object> tempMap : list) {
            HashMap<String, String> map = new HashMap<>();
            map.put("id", String.valueOf(tempMap.get("id")));
            map.put("code", String.valueOf(tempMap.get("code")));
            map.put("name", String.valueOf(tempMap.get("name")));
            map.put("PhoneticCode", String.valueOf(tempMap.get("phonetic_code")));
            map.put("type", String.valueOf(tempMap.get("type")));
            map.put("unit", String.valueOf(tempMap.get("unit")));
            map.put("LowerLimit", String.valueOf(tempMap.get("lower_limit")));
            map.put("UpperLimit", String.valueOf(tempMap.get("upper_limit")));
            map.put("Description", String.valueOf(tempMap.get("description")));
            indicatorsDictKeySchema.set(String.valueOf(tempMap.get("code")), map);
        }
        return true;
    }*/

}
