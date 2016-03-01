package com.yihu.ehr.dict.service;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XIcd10IndicatorRelationRepository extends PagingAndSortingRepository<Icd10IndicatorRelation, String> {

    List<Icd10IndicatorRelation> findByIcd10Id(String icd10Id);
    List<Icd10IndicatorRelation> findByIndicatorId(String indicatorId);

    Icd10IndicatorRelation findByIcd10IdAndIndicatorId(String icd10Id,String indicatorId);
}
