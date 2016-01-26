package com.yihu.ehr.dict.service.common;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author CWS
 * @version 1.0
 * @created 2015.07.30 14:43
 */
public interface KeyTypeRepository extends PagingAndSortingRepository<KeyType, String> {
    @Query("select dict from KeyType dict where dict.dictId = 12 and dict.code = :code order by dict.sort asc")
    KeyType getKeyType(@Param("code") String code);
}
