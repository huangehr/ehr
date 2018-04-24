package com.yihu.ehr.basic.apps.dao;

import com.yihu.ehr.entity.resource.RsAppResourceMetadata;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by progr1mmer on 2018/4/8.
 */
public interface RsAppResourceMetadataDao  extends PagingAndSortingRepository<RsAppResourceMetadata, String> {
    void deleteByAppId(String appId);
}
