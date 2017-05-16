package com.yihu.ehr.resource.dao.intf;

import com.yihu.ehr.resource.model.RsAppResource;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by lyr on 2016/4/26.
 */
public interface AppResourceDao extends PagingAndSortingRepository<RsAppResource,String> {
    RsAppResource findByAppId(String appId);
}
