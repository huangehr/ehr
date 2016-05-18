package com.yihu.ehr.resource.dao.intf;

import com.yihu.ehr.resource.model.RsDictionary;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
public interface RsDictionaryDao extends PagingAndSortingRepository<RsDictionary,String> {
}