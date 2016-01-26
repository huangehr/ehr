package com.yihu.ehr.dict.service.common;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author CWS
 * @version 1.0
 * @created 2015.07.30 14:43
 */
public interface AppStatusRepository extends PagingAndSortingRepository<AppStatus, String> {
    @Query("select dict from AppStatus dict where dict.dictId = 2 and dict.code = :code order by dict.sort asc")
    AppStatus getAppStatus(@Param("code") String code);
}
