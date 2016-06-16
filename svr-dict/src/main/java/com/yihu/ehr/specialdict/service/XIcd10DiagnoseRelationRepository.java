package com.yihu.ehr.specialdict.service;

import com.yihu.ehr.specialdict.model.Icd10DiagnoseRelation;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by yww on 2016/5/6.
 */
public interface XIcd10DiagnoseRelationRepository extends PagingAndSortingRepository<Icd10DiagnoseRelation,Long> {
    List<Icd10DiagnoseRelation> findByIcd10Id(long icd10Id);
    Icd10DiagnoseRelation findByIcd10IdAndId(long icd10Id, long id);
}
