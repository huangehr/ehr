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
public class Icd10IndicatorRelationService extends BaseJpaService<Icd10IndicatorRelation, XIcd10IndicatorRelationRepository> {

    @PersistenceContext
    protected EntityManager entityManager;
    @Autowired
    private XIcd10IndicatorRelationRepository icd10IndicatorRelaRepo;

    public boolean isExist(String icd10Id,String indicatorId){
        Icd10IndicatorRelation icd10IndicatorRelation = icd10IndicatorRelaRepo.findByIcd10IdAndIndicatorId(icd10Id, indicatorId);
        return  icd10IndicatorRelation != null;
    }

    public List<Icd10IndicatorRelation> getIcd10IndicatorRelationListByIcd10Id(String icd10Id){
        List<Icd10IndicatorRelation> icd10IndicatorRelations = icd10IndicatorRelaRepo.findByIcd10Id(icd10Id);
        if(icd10IndicatorRelations.size() == 0){
            return null;
        }
        return icd10IndicatorRelations;
    }

    public boolean isUsage(String indicatorId){
        List<Icd10IndicatorRelation> icd10IndicatorRelations = icd10IndicatorRelaRepo.findByIndicatorId(indicatorId);
        if(icd10IndicatorRelations.size() == 0){
            return false;
        }
        return true;
    }

    public Page<Icd10IndicatorRelation> getRelationList(String sorts, int page, int size) {
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));
        return icd10IndicatorRelaRepo.findAll(pageable);
    }

    public List<Icd10IndicatorRelation> getIcd10DrugRelationsByIcd10Ids(String[] icd10Ids) {
        return icd10IndicatorRelaRepo.findByIcd10Ids(icd10Ids);
    }
}