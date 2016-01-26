package com.yihu.ehr.dict.service.common;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author CWS
 * @version 1.0
 * @created 2015.07.30 14:43
 */
public interface CardStatusRepository extends PagingAndSortingRepository<CardStatus, String> {
    @Query("select dict from CardStatus dict where dict.dictId = 9 and dict.code = :code order by dict.sort asc")
    CardStatus getCardStatus(@Param("code") String code);
}
