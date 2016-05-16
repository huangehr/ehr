package com.yihu.ehr.resource.dao.intf;

import com.yihu.ehr.resource.model.RsResourceMetadata;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by lyr on 2016/4/25.
 */
public interface ResourceMetadataDao extends PagingAndSortingRepository<RsResourceMetadata,String>{
    void deleteByResourcesId(String resourcesId);

    List<RsResourceMetadata> findByResourcesId(String resourcesId);
}
