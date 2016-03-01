package com.yihu.ehr.dict.service;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XHpIcd10RelationRepository extends PagingAndSortingRepository<HpIcd10Relation, String> {

    List<HpIcd10Relation> findByIcd10Id(String icd10Id);
    List<HpIcd10Relation> findByHpId(String hpId);

    HpIcd10Relation findByIcd10IdAndHpId(String icd10Id,String hpId);
}
