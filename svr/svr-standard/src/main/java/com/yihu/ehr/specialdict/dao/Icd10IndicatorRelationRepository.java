package com.yihu.ehr.specialdict.dao;

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
public interface Icd10IndicatorRelationRepository extends PagingAndSortingRepository<Icd10IndicatorRelation, Long> {

    List<Icd10IndicatorRelation> findByIcd10Id(long icd10Id);
    List<Icd10IndicatorRelation> findByIndicatorId(long indicatorId);

    Icd10IndicatorRelation findByIcd10IdAndIndicatorId(long icd10Id, long indicatorId);

    @Query("select icd10IndicatorRelation from Icd10IndicatorRelation icd10IndicatorRelation  where icd10IndicatorRelation.icd10Id in (:icd10Ids)")
    List<Icd10IndicatorRelation> findByIcd10Ids(@Param("icd10Ids") long[] icd10Ids);
}
