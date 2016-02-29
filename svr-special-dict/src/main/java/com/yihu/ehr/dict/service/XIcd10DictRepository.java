package com.yihu.ehr.dict.service;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XIcd10DictRepository extends PagingAndSortingRepository<Icd10Dict, String> {

    Icd10Dict findByCode(String code);
    Icd10Dict findByName(String name);

}
