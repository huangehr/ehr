package com.yihu.ehr.dict.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author CWS
 * @version 1.0
 * @created 2015.07.30 14:43
 */
public interface SettledWayRepository extends PagingAndSortingRepository<SettledWay, String> {
    @Query("select base from SettledWay base where base.dictId = 8 and base.code = :code order by base.sort asc")
    SettledWay getSettledWay(@Param("code") String code);
}
