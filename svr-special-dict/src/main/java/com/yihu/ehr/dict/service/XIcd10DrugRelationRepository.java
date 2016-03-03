package com.yihu.ehr.dict.service;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XIcd10DrugRelationRepository extends PagingAndSortingRepository<Icd10DrugRelation, String> {

    List<Icd10DrugRelation> findByIcd10Id(String icd10Id);
    List<Icd10DrugRelation> findByDrugId(String drugId);

    Icd10DrugRelation findByIcd10IdAndDrugId(String icd10Id,String drugId);
}
