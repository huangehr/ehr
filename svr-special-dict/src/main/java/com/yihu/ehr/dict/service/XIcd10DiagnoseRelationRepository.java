package com.yihu.ehr.dict.service;

import com.yihu.ehr.dict.model.Icd10DiagnoseRelation;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by yww on 2016/5/6.
 */
public interface XIcd10DiagnoseRelationRepository extends PagingAndSortingRepository<Icd10DiagnoseRelation,String> {
    List<Icd10DiagnoseRelation> findByIcd10Id(String icd10Id);
    Icd10DiagnoseRelation findByIcd10IdAndId(String icd10Id,String id);
}
