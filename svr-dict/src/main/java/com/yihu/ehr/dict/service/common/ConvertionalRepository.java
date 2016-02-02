package com.yihu.ehr.dict.service.common;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author CWS
 * @version 1.0
 * @created 2015.07.30 14:43
 */
public interface ConvertionalRepository extends PagingAndSortingRepository<ConventionalDict, String> {
    @Query("select dict from ConventionalDict dict where dict.dictId = :dictId and dict.code = :code order by dict.sort asc")
    ConventionalDict getConvertionalDict(@Param("dictId") long dictId,@Param("code") String code);


    @Query("select dict from ConventionalDict dict where dict.dictId  =:dictId order by dict.sort asc")
    List<ConventionalDict> getConventionalDictList(@Param("dictId") long dictId);


    @Query("select dict from ConventionalDict dict where dict.dictId = :dictId and dict.code in ( :codes) order by dict.sort asc")
    List<ConventionalDict> getConventionalDictList(@Param("dictId") long dictId,@Param("codes") String[] codes);
}

