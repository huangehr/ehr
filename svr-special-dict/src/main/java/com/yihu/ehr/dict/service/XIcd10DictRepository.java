package com.yihu.ehr.dict.service;

import com.yihu.ehr.dict.model.Icd10Dict;
import com.yihu.ehr.model.specialdict.MIcd10Dict;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XIcd10DictRepository extends PagingAndSortingRepository<Icd10Dict, String> {

    Icd10Dict findByCode(String code);
    Icd10Dict findByName(String name);

    @Query("select icd10Dict from Icd10Dict icd10Dict  where icd10Dict.id in (:ids)")
    List<MIcd10Dict> findByIds(@Param("ids") String[] ids);
}
