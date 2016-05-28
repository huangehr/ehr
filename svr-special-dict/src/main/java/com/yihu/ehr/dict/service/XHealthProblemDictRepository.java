package com.yihu.ehr.dict.service;

import com.yihu.ehr.dict.model.HealthProblemDict;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XHealthProblemDictRepository extends PagingAndSortingRepository<HealthProblemDict, String> {

    HealthProblemDict findByCode(String code);
    HealthProblemDict findByName(String name);

}
