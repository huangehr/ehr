package com.yihu.ehr.dict.service;

import com.yihu.ehr.query.BaseJpaService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


/**
 * 用户公私钥管理
 *
 * @author CWS
 * @version 1.0
 * @created 02-6月-2015 17:38:05
 */
@Transactional
@Service
public class Icd10DrugRelationService extends BaseJpaService<Icd10DrugRelation, XIcd10DrugRelationRepository> {

    @PersistenceContext
    protected EntityManager entityManager;
    @Autowired
    private XIcd10DrugRelationRepository icd10DrugRelaRepo;

    //flag: 0 - create  1 - update
    public boolean isExist(String id, String icd10Id,String drugId, String flag){
        Icd10DrugRelation icd10DrugRelation = icd10DrugRelaRepo.findByIcd10IdAndDrugId(icd10Id, drugId);
        if(icd10DrugRelation != null){
            if(StringUtils.equals(flag,"1")){
                return (!icd10DrugRelation.getId().equals(id));
            }
            return true;
        }else{
            return false;
        }
    }

    public List<Icd10DrugRelation> getIcd10DrugRelationListByIcd10Id(String icd10Id){

        List<Icd10DrugRelation> icd10DrugRelations = icd10DrugRelaRepo.findByIcd10Id(icd10Id);
        if(icd10DrugRelations.size() == 0){
            return null;
        }
        return icd10DrugRelations;
    }

    public boolean isUsage(String drugId){
        List<Icd10DrugRelation> icd10DrugRelations = icd10DrugRelaRepo.findByDrugId(drugId);
        if(icd10DrugRelations.size() == 0){
            return false;
        }
        return true;
    }

}