package com.yihu.ehr.redis.service;



import com.yihu.ehr.redis.schema.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    StdDataSetKeySchema stdDataSetKeySchema;
    @Autowired
    StdKeySchema stdKeySchema;
    @Autowired
    StdMetaDataKeySchema stdMetaDataKeySchema;
    @Autowired
    StdVersionKeySchema stdVersionKeySchema;

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


    /******************************************* 资源化相关Redis *******************************************************************/
    /**
     *获取指标 redis
     * @return
     */
    /*public String getIndicatorsRedis(String key)
    {
        return resourceAdaptionDictSchema.getMetaData(key);
    }*/


}
