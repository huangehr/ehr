package com.yihu.ehr.service;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.12 8:53
 */
public interface XFileResourceRepository extends PagingAndSortingRepository<FileResource, String> {

    List<FileResource> findByObjectId(String objectId);
}
