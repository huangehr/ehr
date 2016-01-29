package com.yihu.ehr.dict.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author CWS
 * @version 1.0
 * @created 2015.07.30 14:43
 */
public interface XSystemDictRepository extends PagingAndSortingRepository<SystemDict, Long> {

//    @Query("select dict from SystemDict dict where 1=1")
//    AdapterType getAdapterType(@Param("code") String code);
//    @Query("select dict from SystemDict dict where dict.code = :code")
//    SystemDict getByCode(String code);
}

