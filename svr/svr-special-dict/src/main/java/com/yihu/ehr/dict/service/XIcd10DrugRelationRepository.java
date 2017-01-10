package com.yihu.ehr.dict.service;

import com.yihu.ehr.dict.model.Icd10DrugRelation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XIcd10DrugRelationRepository extends PagingAndSortingRepository<Icd10DrugRelation, Long> {

    List<Icd10DrugRelation> findByIcd10Id(String icd10Id);
    List<Icd10DrugRelation> findByDrugId(String drugId);

    Icd10DrugRelation findByIcd10IdAndDrugId(String icd10Id,String drugId);

    @Query("select icd10DrugRelation from Icd10DrugRelation icd10DrugRelation  where icd10DrugRelation.icd10Id in (:icd10Ids)")
    List<Icd10DrugRelation> findByIcd10Ids(@Param("icd10Ids") String[] icd10Ids);

}
