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
public class Icd10IndicatorRelationService extends BaseJpaService<Icd10IndicatorRelation, XIcd10IndicatorRelationRepository> {

    @PersistenceContext
    protected EntityManager entityManager;
    @Autowired
    private XIcd10IndicatorRelationRepository icd10IndicatorRelaRepo;

    //flag: 0 - create  1 - update
    public boolean isExist(String id, String icd10Id,String indicatorId, String flag){
        Icd10IndicatorRelation icd10IndicatorRelation = icd10IndicatorRelaRepo.findByIcd10IdAndIndicatorId(icd10Id, indicatorId);
        if(icd10IndicatorRelation != null){
            if(StringUtils.equals(flag,"1")){
                return (!icd10IndicatorRelation.getId().equals(id));
            }
            return true;
        }else{
            return false;
        }
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
}