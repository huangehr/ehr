package com.yihu.ehr.specialdict.service;

import com.yihu.ehr.specialdict.model.Icd10HpRelation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XIcd10HpRelationRepository extends PagingAndSortingRepository<Icd10HpRelation, Long> {

    List<Icd10HpRelation> findByIcd10Id(String icd10Id);

    List<Icd10HpRelation> findByHpId(String hpId);

    Icd10HpRelation findByIcd10IdAndHpId(String icd10Id, String hpId);

    @Query("select icd10HpRelation from Icd10HpRelation icd10HpRelation where 1=1")
    List<Icd10HpRelation> findAllIcd10HpRelation();
}
