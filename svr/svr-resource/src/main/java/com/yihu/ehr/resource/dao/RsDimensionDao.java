package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsDimension;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by lyr on 2016/4/25.
 */
public interface RsDimensionDao extends PagingAndSortingRepository<RsDimension, String> {

     long countByCategoryId(String categoryId);

}
