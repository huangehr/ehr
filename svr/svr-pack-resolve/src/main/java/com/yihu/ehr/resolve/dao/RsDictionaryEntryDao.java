package com.yihu.ehr.resolve.dao;

import com.yihu.ehr.resolve.model.stage1.RsDictionaryEntry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author zdm
 * @created 2018.07.17
 */
public interface RsDictionaryEntryDao extends PagingAndSortingRepository<RsDictionaryEntry,Integer> {

    List<RsDictionaryEntry> findByDictCode(String code);

}