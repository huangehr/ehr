package com.yihu.ehr.dict.service;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by yww on 2016/5/6.
 */
public interface XIcd10DiagnoseRelationRepository extends PagingAndSortingRepository<Icd10DiagnoseRelation,String> {
    List<Icd10DiagnoseRelation> findByIcd10Id(String icd10Id);
    Icd10DiagnoseRelation findByIcd10IdAndId(String icd10Id,String id);
}
