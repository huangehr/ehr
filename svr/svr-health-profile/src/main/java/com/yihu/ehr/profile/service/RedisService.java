package com.yihu.ehr.profile.service;



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
     * 获取ICD10名称 redis
     */
    public String getIcd10Name(String key) {
        return icd10KeySchema.get(key);
    }

    /**
     * 获取ICD10慢病信息
     * @param key
     * @return
     */
    public String getChronicInfo(String key) {
        return icd10KeySchema.getChronicInfo(key);
    }

}
