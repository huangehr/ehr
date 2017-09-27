package com.yihu.ehr.redis.service;



import com.yihu.ehr.redis.schema.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


/**
 * Redis管理
 * @author hzp add at 20170425
 */
@Service
public class RedisService {

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
    private RsAdapterDictKeySchema rsAdapterDictKeySchema;
    @Autowired
    private RsAdapterMetaKeySchema rsAdapterMetaKeySchema;
    @Autowired
    private RsMetadataKeySchema rsMetadataKeySchema;
    @Autowired
    private StdCdaVersionKeySchema stdVersionKeySchema;
    @Autowired
    private StdDataSetKeySchema stdDataSetKeySchema;
    @Autowired
    private StdMetaDataKeySchema stdMetaDataKeySchema;

    /**
     *获取地址redis
     * @return
     */
    public String getAddress(String key) {
        return addressDictKeySchema.get(key);
    }

    /**
     *获取健康问题redis
     * @return
     */
    public String getHealthProblem(String key) {
        return healthProblemDictKeySchema.get(key);
    }

    /**
     *获取ICD10健康问题 redis
     * @return
     */
    public String getIcd10HpRelation(String key) {
        return icd10HpRelationKeySchema.get(key);
    }

    /**
     *获取ICD10名称 redis
     */
    public String getIcd10Name(String key) {
        return icd10KeySchema.get(key);
    }

    /**
     *获取ICD10对应健康问题 redis
     */
    public String getHpCodeByIcd10(String key) {
        return icd10KeySchema.getHpCode(key);
    }

    /**
     * 获取指标 redis
     * @return
     */
    public String getIndicators(String key) {
        return indicatorsDictKeySchema.get(key);
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
     *获取机构Saas区域权限范围redis
     * @return
     */
    public String getOrgSaasArea(String key) {
        return orgKeySchema.getOrgSaasArea(key);
    }

    /**
     *获取机构Saas机构权限范围redis
     * @return
     */
    public String getOrgSaasOrg(String key) {
        String saasOrg = orgKeySchema.getOrgSaasOrg(key);
        //未设置权限，默认自身机构
        if(StringUtils.isEmpty(saasOrg)) {
            saasOrg = key;
        }
        return saasOrg;
    }

    //------------------------------------ 资源化相关 START -------------------------------------------------------
    /**
     *获取资源化字典映射 redis
     * @return
     */
    public String getRsAdapterDict(String cdaVersion, String dictCode, String srcDictEntryCode) {
        return rsAdapterDictKeySchema.getMetaData(cdaVersion,dictCode,srcDictEntryCode);
    }

    /**
     *获取资源化数据元映射 redis
     * @return
     */
    public String getRsAdapterMetaData(String cdaVersion, String dictCode, String srcDictEntryCode) {
        return rsAdapterMetaKeySchema.getMetaData(cdaVersion,dictCode,srcDictEntryCode);
    }

    /**
     *获取资源化数据元映射 redis
     * @return
     */
    public String getRsMetaData(String key) {
        return rsMetadataKeySchema.get(key);
    }
    //------------------------------------ 资源化相关 END -------------------------------------------------------

    //------------------------------------ 标准相关 START -------------------------------------------------------
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
    //------------------------------------ 标准相关 END -------------------------------------------------------
}
