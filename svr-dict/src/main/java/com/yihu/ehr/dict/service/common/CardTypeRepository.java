package com.yihu.ehr.dict.service.common;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author CWS
 * @version 1.0
 * @created 2015.07.30 14:43
 */
public interface CardTypeRepository extends PagingAndSortingRepository<CardType, String> {
    @Query("select dict from CardType dict where dict.dictId = 10 and dict.code = :code order by dict.sort asc")
    CardType getCardType(@Param("code") String code);
}
