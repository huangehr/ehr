package com.yihu.ehr.dict.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 字典项CRUD接口。
 *
 * @author CWS
 * @version 1.0
 * @created 2015.07.30 14:43
 */
interface XSystemDictEntryRepository extends PagingAndSortingRepository<SystemDictEntry, DictEntryKey> {
    @Query("select entry from SystemDictEntry entry where entry.dictId = :dictId")
    List<SystemDictEntry> getByDictId(@Param("dictId") Long dictId);

    @Modifying
    @Query("delete from SystemDictEntry entry where entry.dictId = :dictId")
    void deleteByDictId(@Param("dictId") long dictId);

    @Modifying
    @Query("delete from SystemDictEntry entry where entry.dictId = :dictId and entry.code = :code")
    void deleteByEntryCode(@Param("dictId") long dictId, @Param("code") String code);

    @Query("select max(entry.sort) from SystemDictEntry entry where entry.dictId = :dictId")
    Integer getNextEntrySort(@Param("dictId") long dictId);

    @Query("select entry from SystemDictEntry entry where entry.dictId = :dictId and entry.code like '%:code%'")
    List<SystemDictEntry> getEntryList(@Param("dictId") long dictId, @Param("code") String code);

    @Query("select entry from SystemDictEntry entry where entry.dictId = :dictId and entry.code = :code order by entry.sort asc")
    SystemDictEntry getConvertionalDict(@Param("dictId") long dictId, @Param("code") String code);

    @Query("select entry from SystemDictEntry entry where entry.dictId  =:dictId order by entry.sort asc")
    List<SystemDictEntry> getConventionalDictList(@Param("dictId") long dictId);

    @Query("select entry from SystemDictEntry entry where entry.dictId = :dictId and entry.code in ( :codes) order by entry.sort asc")
    List<SystemDictEntry> getConventionalDictList(@Param("dictId") long dictId, @Param("codes") String codes);

    @Query("select entry from SystemDictEntry entry where entry.dictId = :dictId order by entry.sort asc")
    List<SystemDictEntry> findAll(@Param("dictId") long dictId, Pageable pageable);
}

