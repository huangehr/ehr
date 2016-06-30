package com.yihu.ehr.specialdict.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.model.specialdict.MHealthProblemDict;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redis.RedisClient;
import com.yihu.ehr.schema.Icd10HpRelationKeySchema;
import com.yihu.ehr.specialdict.model.HealthProblemDict;
import com.yihu.ehr.specialdict.model.Icd10HpRelation;
import com.yihu.ehr.specialdict.model.IndicatorsDict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 用户公私钥管理
 *
 * @author CWS
 * @version 1.0
 * @created 02-6月-2015 17:38:05
 */
@Transactional
@Service
public class HealthProblemDictService extends BaseJpaService<HealthProblemDict, XHealthProblemDictRepository> {

    @PersistenceContext
    protected EntityManager entityManager;
    @Autowired
    private XHealthProblemDictRepository hpDictRepo;
    @Autowired
    private XIcd10HpRelationRepository hpIcd10ReRepo;

    @Autowired
    private IndicatorsDictService indicatorsDictService;

    public Page<HealthProblemDict> getDictList(String sorts, int page, int size) {
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));
        return hpDictRepo.findAll(pageable);
    }

    public boolean isCodeExist(String code){
        HealthProblemDict hpDict = hpDictRepo.findByCode(code);
        return hpDict != null;
    }

    public boolean isNameExist(String name){
        HealthProblemDict hpDict = hpDictRepo.findByName(name);
        return hpDict != null;
    }

    public HealthProblemDict createDict(HealthProblemDict dict) {
        dict.setName(dict.getName());
        hpDictRepo.save(dict);
        return dict;
    }

    public boolean isUsage(long id){
        boolean result = (hpIcd10ReRepo.findByHpId(id) != null);
        return result;
    }

    public HealthProblemDict findHpDictByCode(String code) {
        return hpDictRepo.findByCode(code);
    }
}