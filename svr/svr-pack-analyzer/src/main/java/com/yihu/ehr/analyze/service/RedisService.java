package com.yihu.ehr.analyze.service;



import com.yihu.ehr.redis.schema.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Redis管理
 * @author hzp add at 20170425
 */
@Service
public class RedisService {

    @Autowired
    private HealthProblemDictKeySchema healthProblemDictKeySchema;
    @Autowired
    private Icd10KeySchema icd10KeySchema;
    @Autowired
    private OrgKeySchema orgKeySchema;
    @Autowired
    private RsAdapterDictKeySchema rsAdapterDictKeySchema;
    @Autowired
    private RsAdapterMetaKeySchema rsAdapterMetaKeySchema;
    @Autowired
    private RsMetadataKeySchema rsMetadataKeySchema;
    @Autowired
    private StdDataSetKeySchema stdDataSetKeySchema;
    @Autowired
    private StdMetaDataKeySchema stdMetaDataKeySchema;

    /**
     * 获取健康问题redis
     *
     * @return
     */
    public String getHealthProblem(String key) {
        return healthProblemDictKeySchema.get(key);
    }

    /**
     *获取ICD10对应健康问题 redis
     */
    public String getHpCodeByIcd10(String key) {
        return icd10KeySchema.getHpCode(key);
    }

    /**
     * 获取ICD10慢病信息
     * @param key
     * @return
     */
    public String getChronicInfo(String key) {
        return icd10KeySchema.getChronicInfo(key);
    }

    /**
     *获取机构名称redis
     * @return
     */
    public String getOrgName(String key) {
       return orgKeySchema.get(key);
    }

    /**
     *获取机构区域redis
     * @return
     */
    public String getOrgArea(String key) {
        return orgKeySchema.getOrgArea(key);
    }

    /**
     *获取资源化字典映射 redis
     * @return
     */
    public String getRsAdapterDict(String cdaVersion, String dictCode, String srcDictEntryCode) {
        return rsAdapterDictKeySchema.getMetaData(cdaVersion, dictCode, srcDictEntryCode);
    }

    /**
     *获取资源化数据元映射 redis
     * @return
     */
    public String getRsAdapterMetaData(String cdaVersion, String dictCode, String srcDictEntryCode) {
        return rsAdapterMetaKeySchema.getMetaData(cdaVersion, dictCode, srcDictEntryCode);
    }

    /**
     *获取资源化数据元映射 redis
     * @return
     */
    public String getRsMetaData(String key) {
        return rsMetadataKeySchema.get(key);
    }

    /**
     *获取标准数据集--主从表 redis
     */
    public Boolean getDataSetMultiRecord(String version, String code){
        return stdDataSetKeySchema.dataSetMultiRecord(version, code);
    }

    /**
     * 获取标准数据元对应类型 redis
     */
    public String getMetaDataType(String version, String dataSetCode, String innerCode) {

        return stdMetaDataKeySchema.metaDataType(version, dataSetCode , innerCode);
    }

    /**
     * 获取标准数据元对应字典 redis
     */
    public String getMetaDataDict(String version, String dataSetCode, String innerCode) {
        return stdMetaDataKeySchema.metaDataDict(version, dataSetCode,innerCode);
    }

    /**
     * 获取标准数据字典对应值 redis
     */
    public String getDictEntryValue(String version, String dictId, String entryCode) {

        return stdMetaDataKeySchema.dictEntryValue(version, dictId , entryCode);
    }
}
