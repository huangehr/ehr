package com.yihu.ehr.specialdict.service;

import com.yihu.ehr.specialdict.model.Icd10IndicatorRelation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XIcd10IndicatorRelationRepository extends PagingAndSortingRepository<Icd10IndicatorRelation, Long> {

    List<Icd10IndicatorRelation> findByIcd10Id(String icd10Id);
    List<Icd10IndicatorRelation> findByIndicatorId(String indicatorId);

    Icd10IndicatorRelation findByIcd10IdAndIndicatorId(String icd10Id, String indicatorId);

    @Query("select icd10IndicatorRelation from Icd10IndicatorRelation icd10IndicatorRelation  where icd10IndicatorRelation.icd10Id in (:icd10Ids)")
    List<Icd10IndicatorRelation> findByIcd10Ids(@Param("icd10Ids") String[] icd10Ids);
}
