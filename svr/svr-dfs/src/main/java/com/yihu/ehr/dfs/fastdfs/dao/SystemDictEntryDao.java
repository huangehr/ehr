package com.yihu.ehr.dfs.fastdfs.dao;

import com.yihu.ehr.entity.dict.DictEntryKey;
import com.yihu.ehr.entity.dict.SystemDictEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 字典项DAO。
 *
 * @author Sand
 * @version 1.0
 * @created 2017.01.30 14:43
 */
public interface SystemDictEntryDao extends PagingAndSortingRepository<SystemDictEntry, DictEntryKey> {

    Page<SystemDictEntry> findByDictId(long dictId, Pageable pageable);
}

