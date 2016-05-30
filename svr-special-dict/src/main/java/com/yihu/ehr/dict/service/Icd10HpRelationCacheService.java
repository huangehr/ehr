package com.yihu.ehr.dict.service;

import com.yihu.ehr.dict.model.HealthProblemDict;
import com.yihu.ehr.dict.model.Icd10HpRelation;
import com.yihu.ehr.redis.RedisClient;
import com.yihu.ehr.schema.Icd10HpRelationKeySchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author linaz
 * @created 2016.05.28 16:10
 */
@Component
public class Icd10HpRelationCacheService {

    @Autowired
    private Icd10HpRelationKeySchema keySchema;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private XIcd10HpRelationRepository icd10HpRelationRepository;

    @Autowired
    private XHealthProblemDictRepository healthProblemDictRepository;

    /**
     * 缓存单个
     * @param icd10Id
     */
    public void cacheOne(String icd10Id) {
        List<Icd10HpRelation> icd10HpRelations = icd10HpRelationRepository.findByIcd10Id(icd10Id);

        for (Icd10HpRelation icd10HpRelation : icd10HpRelations) {
            String hpID = icd10HpRelation.getHpId();
            HealthProblemDict healthProblemDict = healthProblemDictRepository.findOne(hpID);
            String redisKey = keySchema.icd10HpRelation(icd10HpRelation.getIcd10Id());
            redisClient.set(redisKey, healthProblemDict.getCode()+"__"+healthProblemDict.getName());
        }
    }

    /**
     * 缓存所有
     * @param force
     */
    public void cacheAll(boolean force) {
        if (force) clean();
        List<Icd10HpRelation> icd10HpRelations = icd10HpRelationRepository.findAllIcd10HpRelation();
        for (Icd10HpRelation icd10HpRelation : icd10HpRelations) {
            String icd10Id = icd10HpRelation.getIcd10Id();
            String hpId = icd10HpRelation.getHpId();
            HealthProblemDict healthProblemDict = healthProblemDictRepository.findOne(hpId);

            String redisKey = keySchema.icd10HpRelation(icd10Id);
            redisClient.set(redisKey, healthProblemDict.getCode()+"__"+healthProblemDict.getName());
        }
    }


    /**
     * 获取单个缓存
     * @param icd10Id
     * @return
     */
    public HealthProblemDict healthProblemDict(String icd10Id) {
        String codeAndName = redisClient.get(keySchema.icd10HpRelation(icd10Id));
        HealthProblemDict healthProblemDict = new HealthProblemDict();
        healthProblemDict.setCode(codeAndName.split("__")[0]);
        healthProblemDict.setName(codeAndName.split("__")[1]);
        return healthProblemDict;
    }

    /**
     * 获取所有缓存
     * @return
     */
    public List<HealthProblemDict> healthProblemDictList(){
        Set<String> keys = redisClient.keys(keySchema.icd10HpRelation("*"));
        List<HealthProblemDict> healthProblemDictList = new ArrayList<>(keys.size());

        for (String key : keys){
            HealthProblemDict healthProblemDict = new HealthProblemDict();
            healthProblemDict.setCode(key.split(":")[1]);
            String codeAndName = redisClient.get(key);
            healthProblemDict.setCode(codeAndName.split("__")[0]);
            healthProblemDict.setName(codeAndName.split("__")[1]);
            healthProblemDictList.add(healthProblemDict);
        }
        return healthProblemDictList;
    }

    /**
     * 清楚所有缓存
     */
    public void clean() {
        redisClient.delete(keySchema.icd10HpRelation("*"));
    }

}
