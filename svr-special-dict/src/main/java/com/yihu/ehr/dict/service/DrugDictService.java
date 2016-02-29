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
public class DrugDictService extends BaseJpaService<DrugDict, XDrugDictRepository> {

    @PersistenceContext
    protected EntityManager entityManager;
    @Autowired
    private XDrugDictRepository drugDictRepo;
    @Autowired
    private XIcd10DrugRelationRepository icd10DrugReRepo;

    //flag: 0 - create  1 - update
    public boolean isCodeExist(String id, String code, String flag){
        DrugDict drugDict = drugDictRepo.findByCode(code);
        if(drugDict != null){
            if(StringUtils.equals(flag,"1")){
                return (!drugDict.getId().equals(id));
            }
            return true;
        }else{
            return false;
        }
    }
    public boolean isNameExist(String id, String name, String flag){
        DrugDict drugDict = drugDictRepo.findByName(name);
        if(drugDict != null){
            if(StringUtils.equals(flag,"1")){
                return (!drugDict.getId().equals(id));
            }
            return true;
        }else{
            return false;
        }
    }

    public boolean isUsage(String id){
        boolean result = (icd10DrugReRepo.findByDrugId(id) != null);
        return result;
    }
}