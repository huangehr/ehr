package com.yihu.ehr.basic.apps.dao;

import com.yihu.ehr.entity.resource.RsAppResource;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by progr1mmer on 2018/4/8.
 */
public interface RsAppResourceDao  extends PagingAndSortingRepository<RsAppResource, String> {
    void deleteByAppId(String appId);
}
