package com.yihu.ehr.specialdict.dao;

import com.yihu.ehr.specialdict.model.Icd10DrugRelation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface Icd10DrugRelationRepository extends PagingAndSortingRepository<Icd10DrugRelation, Long> {

    List<Icd10DrugRelation> findByIcd10Id(long icd10Id);
    List<Icd10DrugRelation> findByDrugId(long drugId);

    Icd10DrugRelation findByIcd10IdAndDrugId(long icd10Id, long drugId);

    @Query("select icd10DrugRelation from Icd10DrugRelation icd10DrugRelation  where icd10DrugRelation.icd10Id in (:icd10Ids)")
    List<Icd10DrugRelation> findByIcd10Ids(@Param("icd10Ids") long[] icd10Ids);

}
