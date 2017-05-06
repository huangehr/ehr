package com.yihu.ehr.redis.service;



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
    AddressDictKeySchema addressDictKeySchema;
    @Autowired
    HealthProblemDictKeySchema healthProblemDictKeySchema;
    @Autowired
    Icd10HpRelationKeySchema icd10HpRelationKeySchema;
    @Autowired
    IndicatorsDictKeySchema indicatorsDictKeySchema;
    @Autowired
    OrgKeySchema orgKeySchema;

    @Autowired
    ResourceAdaptionDictSchema resourceAdaptionDictSchema;
    @Autowired
    ResourceAdaptionKeySchema resourceAdaptionKeySchema;
    @Autowired
    ResourceMetadataSchema resourceMetadataSchema;

    @Autowired
    StdVersionKeySchema stdVersionKeySchema;
    @Autowired
    StdDataSetKeySchema stdDataSetKeySchema;
    @Autowired
    StdMetaDataKeySchema stdMetaDataKeySchema;

    /**
     *获取地址redis
     * @return
     */
    public String getAddressRedis(String key)
    {
        return addressDictKeySchema.get(key);
    }

    /**
     *获取健康问题redis
     * @return
     */
    public String getHealthProblemRedis(String key)
    {
        return healthProblemDictKeySchema.get(key);
    }

    /**
     *获取ICD10健康问题 redis
     * @return
     */
    public String getIcd10HpRelationRedis(String key)
    {
        return icd10HpRelationKeySchema.get(key);
    }

    /**
     *获取指标 redis
     * @return
     */
    public String getIndicatorsRedis(String key)
    {
        return indicatorsDictKeySchema.get(key);
    }

    /**
     *获取机构redis
     * @return
     */
    public String getOrgRedis(String key)
    {
       return orgKeySchema.get(key);
    }

    /**
     *获取机构redis
     * @return
     */
    public String getOrgAreaRedis(String key)
    {
        return orgKeySchema.getOrgArea(key);
    }

    /******************************************* 资源化相关Redis *******************************************************************/

    /**
     *获取资源化字典映射 redis
     * @return
     */
    public String getRsAdaptionDict(String cdaVersion, String dictCode, String srcDictEntryCode)
    {
        return resourceAdaptionDictSchema.getMetaData(cdaVersion,dictCode,srcDictEntryCode);
    }

    /**
     *获取资源化数据元映射 redis
     * @return
     */
    public String getRsAdaptionMetaData(String cdaVersion, String dictCode, String srcDictEntryCode)
    {
        return resourceAdaptionKeySchema.getMetaData(cdaVersion,dictCode,srcDictEntryCode);
    }

    /**
     *获取资源化数据元映射 redis
     * @return
     */
    public String getRsMetaData(String key)
    {
        return resourceMetadataSchema.get(key);
    }
    /******************************************* 标准相关Redis *******************************************************************/
    /**
     *获取标准版本 redis
     */
    public String getStdVersion(String key)
    {
        return stdVersionKeySchema.get(key);
    }

    /**
     *获取标准数据集代码 redis
     */
    public String getDataSetCode(String version, String id){
        return stdDataSetKeySchema.dataSetCode(version, id);
    }

    /**
     *获取标准数据集名称 redis
     */
    public String getDataSetName(String version, String id){
        return stdDataSetKeySchema.dataSetName(version, id);
    }

    /**
     *获取标准数据集名称 redis
     */
    public String getDataSetNameByCode(String version, String code){
        return stdDataSetKeySchema.dataSetNameByCode(version, code);
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

        return stdMetaDataKeySchema.metaDataType( version, dataSetCode , innerCode);
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

        return stdMetaDataKeySchema.dictEntryValue( version, dictId , entryCode);
    }
}
