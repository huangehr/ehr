package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsResourceMetadata;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by lyr on 2016/4/25.
 */
public interface RsResourceMetadataDao extends PagingAndSortingRepository<RsResourceMetadata, String>{
    void deleteByResourcesId(String resourcesId);

    List<RsResourceMetadata> findByResourcesId(String resourcesId);

    @Query(value="delete from RsResourceMetadata rm where rm.resourcesId in (:resourcesIds)",nativeQuery=true)
    void deleteByResourcesIds(
            @Param("resourcesIds") String[] resourcesIds);
}
