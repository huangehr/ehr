package com.yihu.ehr.dict.service;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XIndicatorsDictRepository extends PagingAndSortingRepository<IndicatorsDict, String> {

    IndicatorsDict findByCode(String code);
    IndicatorsDict findByName(String name);

}
