package com.yihu.ehr.dict.service;

import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public boolean isExist(String icd10Id,String drugId){
        Icd10DrugRelation icd10DrugRelation = icd10DrugRelaRepo.findByIcd10IdAndDrugId(icd10Id, drugId);
        return icd10DrugRelation != null;
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

    public Page<Icd10DrugRelation> getRelationList(String sorts, int page, int size) {
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));
        return icd10DrugRelaRepo.findAll(pageable);
    }

    public List<Icd10DrugRelation> getIcd10DrugRelationsByIcd10Ids(String[] icd10Ids) {
        return icd10DrugRelaRepo.findByIcd10Ids(icd10Ids);
    }
}