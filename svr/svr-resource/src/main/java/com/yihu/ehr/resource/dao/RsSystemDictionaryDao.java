package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsSystemDictionary;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
public interface RsSystemDictionaryDao extends PagingAndSortingRepository<RsSystemDictionary,String> {
}