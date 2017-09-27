package com.yihu.ehr.dict.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 字典项DAO。
 *
 * @author Sand
 * @version 1.0
 * @created 2017.01.30 14:43
 */
public interface XSystemDictEntryRepository extends JpaRepository<SystemDictEntry, DictEntryKey> {
    List<SystemDictEntry> findByDictId(long dictId);

    Page<SystemDictEntry> findByDictId(long dictId, Pageable pageable);

    Page<SystemDictEntry> findByDictIdAndValueLike(long dictId, String value, Pageable pageable);

    @Modifying
    void deleteByDictId(long dictId);

    /**
     * 获取字典项下一排序号.
     *
     * @param dictId
     * @return
     */
    @Query("select max(entry.sort) from SystemDictEntry entry where entry.dictId = :dictId")
    Integer getNextEntrySN(@Param("dictId") long dictId);

    /**
     * 批量获取字典项列表.
     *
     * @param dictId
     * @param codes
     * @return
     */
    @Query("select entry from SystemDictEntry entry where entry.dictId = :dictId and entry.code in (:codes) order by entry.sort asc")
    List<SystemDictEntry> findByDictIdAndCodes(@Param("dictId") long dictId, @Param("codes") String[] codes);

    List<SystemDictEntry> findByDictIdAndValue(@Param("dictId") long dictId, @Param("value") String value);

    List<SystemDictEntry> findByDictIdAndCode(@Param("dictId") long dictId, @Param("code") String code);
}

