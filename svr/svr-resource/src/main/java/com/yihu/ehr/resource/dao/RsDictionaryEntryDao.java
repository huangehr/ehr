package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsDictionaryEntry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
public interface RsDictionaryEntryDao extends PagingAndSortingRepository<RsDictionaryEntry,Integer> {

    List<RsDictionaryEntry> findByDictCode(String code);

    @Query("SELECT count(e) FROM RsDictionaryEntry e WHERE e.dictId=?1")
    int countByDictId(int dictId);

}