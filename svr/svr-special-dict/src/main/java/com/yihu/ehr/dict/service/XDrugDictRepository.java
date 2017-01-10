package com.yihu.ehr.dict.service;

import com.yihu.ehr.dict.model.DrugDict;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XDrugDictRepository extends PagingAndSortingRepository<DrugDict, Long> {

    DrugDict findByCode(String code);
    DrugDict findByName(String name);

    @Query("select drugDict from DrugDict drugDict  where drugDict.id in (:ids)")
    List<DrugDict> findByIds(@Param("ids") String[] ids);

}
