package com.yihu.ehr.basic.portal.dao;

import com.yihu.ehr.basic.portal.model.ItResourceDownload;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by janseny on 2017/3/11.
 */
public interface ItResourceDownloadRepository extends PagingAndSortingRepository<ItResourceDownload, Long> {

    @Query(value = "select p.* from it_resource_download p order by p.upload_time desc",nativeQuery = true)
    List<ItResourceDownload> getAllPortalResources();
}
