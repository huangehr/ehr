package com.yihu.ehr.resource.dao.intf;

import com.yihu.ehr.resource.model.RsResourcesQuery;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by Sxy on 2017/08/04.
 */
public interface ResourcesDefaultQueryDao extends PagingAndSortingRepository<RsResourcesQuery, String> {

    RsResourcesQuery findById(String id);
    RsResourcesQuery findByResourcesId(String resourcesId);
    void deleteByResourcesId(String resourcesId);
}
