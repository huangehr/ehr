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
public class Icd10DictService extends BaseJpaService<Icd10Dict, XIcd10DictRepository> {

    @PersistenceContext
    protected EntityManager entityManager;
    @Autowired
    private XIcd10DictRepository icd10DictRepo;
    @Autowired
    private XHpIcd10RelationRepository hpIcd10ReRepo;
    @Autowired
    private XIcd10DrugRelationRepository icd10DrugReRepo;
    @Autowired
    private XIcd10IndicatorRelationRepository icd10IndicatorReRepo;

    //flag: 0 - create  1 - update
    public boolean isCodeExist(String id, String code, String flag){
        Icd10Dict icd10Dict = icd10DictRepo.findByCode(code);
        if(icd10Dict != null){
            if(StringUtils.equals(flag,"1")){
                return (!icd10Dict.getId().equals(id));
            }
            return true;
        }else{
            return false;
        }
    }
    public boolean isNameExist(String id, String name, String flag){
        Icd10Dict icd10Dict = icd10DictRepo.findByName(name);
        if(icd10Dict != null){
            if(StringUtils.equals(flag,"1")){
                return (!icd10Dict.getId().equals(id));
            }
            return true;
        }else{
            return false;
        }
    }

    /*public boolean isNameExist(String name){
        String fields = "name = " + name;
        long result = getCount(fields);
        return result != 0 ;
    }*/

    public boolean isUsage(String id){
        boolean result = ((hpIcd10ReRepo.findByIcd10Id(id) != null)||(icd10DrugReRepo.findByIcd10Id(id) != null)||(icd10IndicatorReRepo.findByIcd10Id(id) != null));
        return result;
    }

}