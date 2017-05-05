package com.yihu.ehr.specialdict.service;
import com.yihu.ehr.redis.schema.Icd10HpRelationKeySchema;
import com.yihu.ehr.specialdict.model.HealthProblemDict;
import com.yihu.ehr.specialdict.model.Icd10Dict;
import com.yihu.ehr.specialdict.model.Icd10HpRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author linaz
 * @created 2016.05.28 16:10
 */
@Transactional
@Service
public class Icd10HpRelationCacheService {

    @Autowired
    private Icd10HpRelationKeySchema keySchema;

    @Autowired
    private XIcd10HpRelationRepository icd10HpRelationRepository;

    @Autowired
    private XHealthProblemDictRepository healthProblemDictRepository;

    @Autowired
    private XIcd10DictRepository icd10DictRepository;

    /**
     * 缓存单个
     * @param icd10Id
     */
    public void cacheOne(long icd10Id) {
        List<Icd10HpRelation> icd10HpRelations = icd10HpRelationRepository.findByIcd10Id(icd10Id);

        for (Icd10HpRelation icd10HpRelation : icd10HpRelations) {
            long hpId = icd10HpRelation.getHpId();
            Icd10Dict icd10Dict = icd10DictRepository.findOne(icd10Id);
            HealthProblemDict healthProblemDict = healthProblemDictRepository.findOne(hpId);
            keySchema.set(icd10Dict.getCode(), healthProblemDict.getCode()+"__"+healthProblemDict.getName());
        }
    }

    /**
     * 缓存所有
     * @param force
     */
    public void cacheAll(boolean force) throws Exception {
        if (force) clean();
        List<Icd10HpRelation> icd10HpRelations = icd10HpRelationRepository.findAllIcd10HpRelation();
        for (Icd10HpRelation icd10HpRelation : icd10HpRelations) {
            long hpId = icd10HpRelation.getHpId();
            long icd10Id = icd10HpRelation.getIcd10Id();
            Icd10Dict icd10Dict = icd10DictRepository.findOne(icd10Id);
            HealthProblemDict healthProblemDict = healthProblemDictRepository.findOne(hpId);
            keySchema.set(icd10Dict.getCode(), healthProblemDict.getCode()+"__"+healthProblemDict.getName());
        }
    }


    /**
     * 获取单个缓存
     * @param icd10Id
     * @return
     */
    public HealthProblemDict healthProblemDict(String icd10Id) throws Exception {

        HealthProblemDict healthProblemDict = new HealthProblemDict();
        String codeAndName = keySchema.get(icd10Id);
        if(codeAndName!=null && codeAndName.contains("__")){
            healthProblemDict.setCode(codeAndName.split("__")[0]);
            healthProblemDict.setName(codeAndName.split("__")[1]);
        }
        return healthProblemDict;
    }

    /**
     * 获取所有缓存
     * @return
     */
    public List<HealthProblemDict> healthProblemDictList() throws Exception{
        Map<String,Object> map = keySchema.getAll();
        List<HealthProblemDict> healthProblemDictList = new ArrayList<>();

        for (String key : map.keySet()){
            HealthProblemDict healthProblemDict = new HealthProblemDict();
            healthProblemDict.setCode(key.split(":")[1]);
            String codeAndName = String.valueOf(map.get(key));
            if(codeAndName!=null && codeAndName.contains("__"))
            {
                healthProblemDict.setCode(codeAndName.split("__")[0]);
                healthProblemDict.setName(codeAndName.split("__")[1]);
                healthProblemDictList.add(healthProblemDict);
            }
        }
        return healthProblemDictList;
    }

    /**
     * 清楚所有缓存
     */
    public void clean() throws Exception {
        keySchema.deleteAll();
    }

}
