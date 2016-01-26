package com.yihu.ehr.dict.service.common;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by zqb on 2015/12/21.
 */
public interface StdSourceTypeRepository extends PagingAndSortingRepository<StdSourceType, String> {
    @Query("select dict from StdSourceType dict where dict.dictId = 22 and dict.code = :code order by dict.sort asc")
    StdSourceType getStdSourceType(@Param("code") String code);

    @Query("select dict from StdSourceType dict where dict.dictId = 22 and dict.code in ( :codes) order by dict.sort asc")
    List<StdSourceType> getStdSourceTypeList(@Param("codes") String[] codes);
}
