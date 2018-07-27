package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsResourceDefaultQuery;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by Sxy on 2017/08/04.
 */
public interface RsResourceDefaultQueryDao extends PagingAndSortingRepository<RsResourceDefaultQuery, String> {

    RsResourceDefaultQuery findById(String id);
    RsResourceDefaultQuery findByResourcesId(String resourcesId);
    void deleteByResourcesId(String resourcesId);
}
