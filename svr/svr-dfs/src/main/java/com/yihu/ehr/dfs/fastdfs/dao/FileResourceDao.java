package com.yihu.ehr.dfs.fastdfs.dao;

import com.yihu.ehr.dfs.fastdfs.service.FileResource;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.12 8:53
 */
public interface FileResourceDao extends PagingAndSortingRepository<FileResource, String> {

    List<FileResource> findByObjectId(String objectId);

    List<FileResource> findByStoragePath(String storagePath);

    List<FileResource> findByObjectIdAndMime(String objectId, String mime);
}
