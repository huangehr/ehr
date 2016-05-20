package com.yihu.ehr.resource.dao.intf;

import com.yihu.ehr.resource.model.RsResource;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by lyr on 2016/4/25.
 */
public interface ResourcesDao extends PagingAndSortingRepository<RsResource,String> {

    RsResource findByCode(String code);

    long countByCategoryId(String categoryId);

    List<RsResource> findByCategoryId(String categoryId);
}
