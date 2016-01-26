package com.yihu.ehr.dict.service;

import com.yihu.ehr.dict.service.common.DictPk;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author CWS
 * @version 1.0
 * @created 2015.07.30 14:43
 */
public interface XSystemDictEntryRepository extends PagingAndSortingRepository<SystemDictEntry, DictPk> {


    @Query("select dict from SystemDictEntry dict where dict.code = :code")
    SystemDictEntry getByCode(@Param("code") String code);

    @Query("delete from SystemDictEntry dict where dict.dictId = :dictId")
    void deleteByDictId(long dictId);

    @Query("delete from SystemDictEntry dict where dict.dictId = :dictId and dict.code = :code")
    void deleteByDictIdAndCode(long dictId, String code);


}

