package com.yihu.ehr.resource.dao.intf;

import com.yihu.ehr.resource.model.RsDictionaryEntry;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
public interface RsDictionaryEntryDao extends PagingAndSortingRepository<RsDictionaryEntry,String> {

    List<RsDictionaryEntry> findByDictCode(String code);

}