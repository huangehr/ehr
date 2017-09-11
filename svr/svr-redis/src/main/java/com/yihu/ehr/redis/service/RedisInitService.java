package com.yihu.ehr.redis.service;

import com.yihu.ehr.redis.schema.*;
import com.yihu.ehr.util.id.ObjectVersion;
import com.yihu.ehr.util.log.LogService;
import jdk.nashorn.internal.scripts.JD;
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
public class RedisInitService {

    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private AddressDictKeySchema addressDictKeySchema;
    @Autowired
    private HealthProblemDictKeySchema healthProblemDictKeySchema;
    @Autowired
    private Icd10HpRelationKeySchema icd10HpRelationKeySchema;
    @Autowired
    private Icd10KeySchema icd10KeySchema;
    @Autowired
    private IndicatorsDictKeySchema indicatorsDictKeySchema;
    @Autowired
    private OrgKeySchema orgKeySchema;
    @Autowired
    private RsAdapterMetaKeySchema rsAdapterMetaKeySchema;
    @Autowired
    private RsAdapterDictKeySchema rsAdapterDictKeySchema;
    @Autowired
    private RsMetadataKeySchema rsMetadataKeySchema;
    @Autowired
    private StdCdaVersionKeySchema stdCdaVersionKeySchema;
    @Autowired
    private StdDataSetKeySchema stdDataSetKeySchema;
    @Autowired
    private StdMetaDataKeySchema stdMetaDataKeySchema;

    /**
     * 缓存行政地址Redis
     */
    public void cacheAddressDict() throws Exception {
        String sql = "SELECT id, name FROM address_dict";
        List<Map<String, Object>> list = jdbc.queryForList(sql);
        //清空相关Redis
        addressDictKeySchema.deleteAll();
        for(Map<String, Object> tempMap : list){
            addressDictKeySchema.set(String.valueOf(tempMap.get("id")), String.valueOf(tempMap.get("name")));
        }
    }

    /**
     * 缓存健康问题名称Redis
     */
    public boolean cacheHpName() {
        String sql = "select code,name from health_problem_dict";
        List<Map<String,Object>> list = jdbc.queryForList(sql);
        //清空相关Redis
        healthProblemDictKeySchema.deleteAll();
        for(Map<String,Object> map:list){
            healthProblemDictKeySchema.set((String) map.get("code"),(String)map.get("name"));
        }
        return true;
    }

    /**
     * 缓存Icd10健康问题
     * @param force
     * @return
     */
    public boolean cacheIcd10HpRelation(boolean force) {
        String icd10HpReSql = "SELECT hp_id, icd10_id FROM icd10_hp_relation";
        String icd10Sql = "SELECT code FROM icd10_dict WHERE id = icd10.id";
        String hpDictSql = "SELECT code, name FROM health_problem_dict WHERE id = hpDict.id";
        List<Map<String, Object>> icd10HpReList = jdbc.queryForList(icd10HpReSql);
        if(force) {
            //清空相关Redis
            icd10HpRelationKeySchema.deleteAll();
        }
        for(Map<String, Object> tempMap : icd10HpReList) {
            Map<String, Object> icd10Map = jdbc.queryForMap(icd10Sql.replace("icd10.id", tempMap.get("icd10_id").toString()));
            Map<String, Object> hpDictMap = jdbc.queryForMap(hpDictSql.replace("hpDict.id", tempMap.get("hp_id").toString()));
            icd10HpRelationKeySchema.set(icd10Map.get("code").toString(), hpDictMap.get("code") + "__" + hpDictMap.get("name"));
        }
        return true;
    }

    /**
     * 缓存ICD10 Redis
     */
    public boolean cacheIcd10() {
        String sql = "select t.hp_code,d.code,d.name from\n" +
                "(select r.icd10_id,group_concat(p.`code` separator ';') hp_code \n" +
                "from hp_icd10_relation r\n" +
                "left join health_problem_dict p on p.id = r.hp_id \n" +
                "group by icd10_id) t\n" +
                "left join icd10_dict d on t.icd10_id = d.id" ;
        List<Map<String,Object>> list = jdbc.queryForList(sql);
        //清空相关Redis
        icd10KeySchema.deleteAll();
        icd10KeySchema.deleteHpCode();
        for(Map<String,Object> map:list){
            icd10KeySchema.set((String) map.get("code"), (String)map.get("name"));
            icd10KeySchema.setHpCode((String) map.get("code"), (String)map.get("hp_code"));
        }
        return true;
    }

    /**
     * 缓存指标
     * @return
     */
    public boolean cacheIndicatorsDict() {
        String sql = "SELECT * FROM indicators_dict";
        List<Map<String, Object>> list = jdbc.queryForList(sql);
        //清空相关Redis
        indicatorsDictKeySchema.deleteAll();
        for(Map<String, Object> tempMap : list) {
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
    }


    /**
     * 缓存机构名称Redis
     */
    public boolean cacheOrgName() {
        String sql = "select org_code, full_name from organizations";
        List<Map<String,Object>> list = jdbc.queryForList(sql);
        //清空相关Redis
        orgKeySchema.deleteAll();
        for(Map<String,Object> map:list){
            orgKeySchema.set(String.valueOf(map.get("org_code")),String.valueOf(map.get("full_name")));
        }
        return true;
    }

    /**
     * 缓存机构区域Redis
     */
    public boolean cacheOrgArea() {
        String sql = "select org_code, administrative_division from organizations";
        List<Map<String,Object>> list = jdbc.queryForList(sql);
        //清空相关Redis
        orgKeySchema.deleteOrgArea();
        for(Map<String,Object> map:list){
            orgKeySchema.setOrgArea(String.valueOf(map.get("org_code")),String.valueOf(map.get("administrative_division")));
        }
        return true;
    }

    /**
     * 缓存机构Saas区域Redis
     */
    public boolean cacheOrgSaasArea() {
        String sql = "select org_code,saas_code from org_saas where type='1' order by org_code";
        List<Map<String,Object>> list = jdbc.queryForList(sql);
        //清空相关Redis
        orgKeySchema.deleteOrgSaasArea();
        String orgCode = "";
        String val = "";
        for(Map<String,Object> map:list){
            String orgCodeCurrent =  String.valueOf(map.get("org_code"));
            String saasCodeCurrent =  String.valueOf(map.get("saas_code"));
            if(!orgCode.equals(orgCodeCurrent) && !StringUtils.isEmpty(orgCode)) {
                orgKeySchema.setOrgSaasArea(orgCode,val);
                val = saasCodeCurrent;
                orgCode = orgCodeCurrent;
            }
            else{
                if(StringUtils.isEmpty(val)) {
                    orgCode = orgCodeCurrent;
                    val =  saasCodeCurrent;
                }
                else{
                    orgCode = orgCodeCurrent;
                    val += "," + saasCodeCurrent;
                }
            }
        }
        if(!StringUtils.isEmpty(orgCode)) {
            orgKeySchema.setOrgSaasArea(orgCode,val);
        }
        return true;
    }

    /**
     * 缓存机构Saas机构Redis
     */
    public boolean cacheOrgSaasOrg() {
        String sql = "select org_code, saas_code from org_saas where type='2' order by org_code";
        List<Map<String, Object>> list = jdbc.queryForList(sql);
        //清空相关Redis
        orgKeySchema.deleteOrgSaasOrg();
        String orgCode = "";
        String val = "";
        for(Map<String,Object> map:list){
            String orgCodeCurrent =  String.valueOf(map.get("org_code"));
            String saasCodeCurrent =  String.valueOf(map.get("saas_code"));
            if(!orgCode.equals(orgCodeCurrent) && !StringUtils.isEmpty(orgCode)) {
                orgKeySchema.setOrgSaasOrg(orgCode,val);
                val = saasCodeCurrent;
                orgCode = orgCodeCurrent;
            }
            else{
                if(StringUtils.isEmpty(val)) {
                    orgCode = orgCodeCurrent;
                    val =  saasCodeCurrent;
                }
                else{
                    orgCode = orgCodeCurrent;
                    val += "," + saasCodeCurrent;
                }
            }
        }
        if(!StringUtils.isEmpty(orgCode)) {
            orgKeySchema.setOrgSaasOrg(orgCode,val);
        }
        return true;
    }

    /**
     * 缓存版本
     * @param versions
     * @param force
     * @return
     */
    public boolean cacheVersions(String versions, boolean force) {
        String versionQuery = "SELECT version_name FROM std_cda_versions WHERE version = 'std.version.column'" ;
        String dataSetQuery = "SELECT id, code, name, multi_record FROM data.set.table";
        String metaDataQuery = "SELECT a.code AS data_set_code, b.inner_code, b.type, b.dict_id FROM data.set.table a, " +
                "meta.data.table b WHERE a.id = b.dataset_id";
        String dictEntryQuery = "SELECT t.dict_id, t.code, t.value FROM dict.entry.table t";
        for (String version : versions.split(",")) {
            if (!ObjectVersion.isValid(version)) {
                throw new IllegalArgumentException("无效版本号");
            }
            if(force) {
                stdCdaVersionKeySchema.delete(version);
                stdMetaDataKeySchema.deleteMetaDataDict(version, "*", "*");
                stdMetaDataKeySchema.deleteMetaDataType(version, "*", "*");
                stdMetaDataKeySchema.deleteDictEntryValue(version, "*", "*");
            }
            String dataSetTable = "std_data_set_" + version;
            String metaDataTable = "std_meta_data_" + version;
            String dictEntryTable = "std_dictionary_entry_" + version;
            //数据集
            List<Map<String,Object>> dataSetList = jdbc.queryForList(dataSetQuery.replace("data.set.table", dataSetTable));
            for (Map<String, Object> tempMap: dataSetList) {
                String id = tempMap.get("id").toString();
                String code = (String) tempMap.get("code");
                String name = (String) tempMap.get("name");
                boolean multiRecord = (boolean) tempMap.get("multi_record");
                stdDataSetKeySchema.setDataSetCode(version, id, code);
                stdDataSetKeySchema.setDataSetName(version, id, name);
                stdDataSetKeySchema.setDataSetNameByCode(version, code, name);
                stdDataSetKeySchema.setDataSetMultiRecord(version, code, multiRecord);
            }
            //数据元
            List<Map<String,Object>> metaDataList = jdbc.queryForList(metaDataQuery.replace("data.set.table", dataSetTable).replace("meta.data.table", metaDataTable));
            for(Map<String, Object> tempMap : metaDataList) {
                String dataSetCode = (String) tempMap.get("data_set_code");
                String innerCode = (String) tempMap.get("inner_code");
                String type = (String) tempMap.get("type");
                long dictId = (Integer) tempMap.get("dict_id");
                String metaDataTypeKey = stdMetaDataKeySchema.makeKey(version, dataSetCode, innerCode);
                if (!force && stdMetaDataKeySchema.hasKey(metaDataTypeKey)) {
                    LogService.getLogger().warn("Meta data duplicated: " + metaDataTypeKey);
                }
                stdMetaDataKeySchema.setMetaDataType(version, dataSetCode, innerCode, type);
                stdMetaDataKeySchema.setMetaDataDict(version, dataSetCode, innerCode, String.valueOf(dictId));
            }
            //字典项
            List<Map<String,Object>> dictEntryList = jdbc.queryForList(dictEntryQuery.replace("dict.entry.table", dictEntryTable));
            for (Map<String, Object> tempMap: dictEntryList) {
                String dictId = tempMap.get("dict_id").toString();
                String code = (String) tempMap.get("code");
                String value = (String) tempMap.get("value");
                stdMetaDataKeySchema.setDictEntryValue(version, dictId, code, value);
            }
            //版本名
            Map<String, Object> versionMap = jdbc.queryForMap(versionQuery.replace("std.version.column", version));
            String versionName = (String) versionMap.get("version_name");
            stdCdaVersionKeySchema.set(version, versionName);
        }
        return true;
    }

    /**
     * 缓存适配数据字典数据
     * @param id
     * @return
     */
    public boolean cacheAdapterDict(String id){
        String schemaSql = "SELECT adapter_version FROM rs_adapter_schema WHERE id = " + id;
        String metaSql = "SELECT dict_code, src_dict_entry_code, dict_entry_code, src_dict_entry_name FROM rs_adapter_dictionary WHERE schema_id = " + id;
        Map<String, Object> schemaMap = jdbc.queryForMap(schemaSql);
        List<Map<String, Object>> dictList = jdbc.queryForList(metaSql);
        //清空相关Redis
        rsAdapterDictKeySchema.deleteAll();
        for(Map<String, Object> dictMap : dictList) {
            if (StringUtils.isEmpty(dictMap.get("dict_code")) || StringUtils.isEmpty(dictMap.get("src_dict_entry_code"))) {
                continue;
            }
            rsAdapterDictKeySchema.setMetaData(schemaMap.get("adapter_version").toString(), dictMap.get("dict_code").toString(),
                    dictMap.get("src_dict_entry_code").toString(), dictMap.get("dict_entry_code").toString() + "&" + dictMap.get("src_dict_entry_name"));
        }
        return true;
    }

    /**
     * 缓存适配数据元数据
     * @param id
     * @return
     */
    public boolean cacheAdapterMetadata(String id){
        String schemaSql = "SELECT adapter_version FROM rs_adapter_schema WHERE id = " + id;
        String metaSql = "SELECT src_dataset_code, src_metadata_code, metadata_id FROM rs_adapter_metadata WHERE schema_id = " + id;
        Map<String, Object> schemaMap = jdbc.queryForMap(schemaSql);
        List<Map<String, Object>> metaList = jdbc.queryForList(metaSql);
        //清空相关Redis
        rsAdapterMetaKeySchema.deleteAll();
        for(Map<String, Object> metaMap : metaList) {
            if (StringUtils.isEmpty(metaMap.get("src_dataset_code")) || StringUtils.isEmpty(metaMap.get("metadata_id"))) {
                continue;
            }
            rsAdapterMetaKeySchema.setMetaData(schemaMap.get("adapter_version").toString(), metaMap.get("src_dataset_code").toString(),
                    metaMap.get("src_metadata_code").toString(), metaMap.get("metadata_id").toString());
        }
        return true;
    }

    /**
     * 缓存数据元字典（Dict_code不为空）
     * @return
     */
    public boolean cacheMetadata() {
        String sql = "SELECT id, dict_code FROM rs_metadata WHERE dict_code != NULL and dict_code != ''";
        //String sql1 = "SELECT a FROM RsMetadata a WHERE a.dictCode <> NULL AND a.dictCode <> ''";
        List<Map<String, Object>> metaList = jdbc.queryForList(sql);
        //清空相关Redis
        rsMetadataKeySchema.deleteAll();
        for(Map<String, Object> tempMap : metaList) {
            if(StringUtils.isEmpty(tempMap.get("dict_code"))) {
                continue;
            }
            rsMetadataKeySchema.set((String) tempMap.get("id"), (String) tempMap.get("dict_code"));
        }
        return true;
    }
}
