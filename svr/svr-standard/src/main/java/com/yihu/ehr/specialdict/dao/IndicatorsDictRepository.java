package com.yihu.ehr.specialdict.dao;

import com.yihu.ehr.specialdict.model.IndicatorsDict;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface IndicatorsDictRepository extends PagingAndSortingRepository<IndicatorsDict, Long> {

    IndicatorsDict findByCode(String code);
    IndicatorsDict findByName(String name);

    @Query("select indicatorsDict from IndicatorsDict indicatorsDict  where indicatorsDict.id in (:ids)")
    List<IndicatorsDict> findByIds(@Param("ids") long[] ids);

    @Query("select indicatorsDict from IndicatorsDict indicatorsDict where 1=1")
    List<IndicatorsDict> findAllIndicatorsDict();
}
