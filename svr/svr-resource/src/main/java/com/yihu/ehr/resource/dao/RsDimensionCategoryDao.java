package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsDimensionCategory;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by lyr on 2016/4/25.
 */
public interface RsDimensionCategoryDao extends PagingAndSortingRepository<RsDimensionCategory,String> {
     int countByPid(String pid);
}
