package com.yihu.ehr.dict.service;

import com.yihu.ehr.dict.model.IndicatorsDict;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XIndicatorsDictRepository extends PagingAndSortingRepository<IndicatorsDict, Long> {

    IndicatorsDict findByCode(String code);
    IndicatorsDict findByName(String name);

    @Query("select indicatorsDict from IndicatorsDict indicatorsDict  where indicatorsDict.id in (:ids)")
    List<IndicatorsDict> findByIds(@Param("ids") String[] ids);
}
