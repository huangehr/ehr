package com.yihu.ehr.dict.service;

import com.yihu.ehr.query.BaseJpaService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


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
    private XHpIcd10RelationRepository hpIcd10ReRepo;

    //flag: 0 - create  1 - update
    public boolean isCodeExist(String id, String code, String flag){
        HealthProblemDict hpDict = hpDictRepo.findByCode(code);
        if(hpDict != null){
            if(StringUtils.equals(flag,"1")){
                return (!hpDict.getId().equals(id));
            }
            return true;
        }else{
            return false;
        }
    }
    public boolean isNameExist(String id, String name, String flag){
        HealthProblemDict hpDict = hpDictRepo.findByName(name);
        if(hpDict != null){
            if(StringUtils.equals(flag,"1")){
                return (!hpDict.getId().equals(id));
            }
            return true;
        }else{
            return false;
        }
    }

    public boolean isUsage(String id){
        boolean result = (hpIcd10ReRepo.findByHpId(id) != null);
        return result;
    }

}