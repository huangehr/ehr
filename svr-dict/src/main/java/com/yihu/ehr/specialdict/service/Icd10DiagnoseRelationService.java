package com.yihu.ehr.specialdict.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.specialdict.model.Icd10DiagnoseRelation;
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
 * Created by yww on 2016/5/6.
 */
@Service
@Transactional
public class Icd10DiagnoseRelationService extends BaseJpaService<Icd10DiagnoseRelation,XIcd10DiagnoseRelationRepository> {
    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    private XIcd10DiagnoseRelationRepository icd10DiagnoseReRepo;

    public Page<Icd10DiagnoseRelation> getRelationList(String sorts,int page,int size){
        Pageable pageable = new PageRequest(page,size,parseSorts(sorts));
        return icd10DiagnoseReRepo.findAll(pageable);
    }

    public List<Icd10DiagnoseRelation> getIcd10DiagnoseRelationListByIcd10Id(long icd10Id){
        List<Icd10DiagnoseRelation> icd10DiagnoseRelations = icd10DiagnoseReRepo.findByIcd10Id(icd10Id);
        if(icd10DiagnoseRelations.size() == 0){
            return null;
        }
        return icd10DiagnoseRelations;
    }

}
