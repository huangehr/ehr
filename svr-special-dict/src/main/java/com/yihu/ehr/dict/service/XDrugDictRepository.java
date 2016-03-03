package com.yihu.ehr.dict.service;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XDrugDictRepository extends PagingAndSortingRepository<DrugDict, String> {

    DrugDict findByCode(String code);
    DrugDict findByName(String name);
}
