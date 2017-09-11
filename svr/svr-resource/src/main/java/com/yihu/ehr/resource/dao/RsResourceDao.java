package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsResource;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by lyr on 2016/4/25.
 */
public interface RsResourceDao extends PagingAndSortingRepository<RsResource, String> {

    RsResource findByCode(String code);
    RsResource findById(String id);
    long countByCategoryId(String categoryId);
    List<RsResource> findByCategoryId(String categoryId);
}
