package com.yihu.ehr.resource.dao.intf;

import com.yihu.ehr.resource.model.RsDimension;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by lyr on 2016/4/25.
 */
public interface DimensionDao extends PagingAndSortingRepository<RsDimension,String> {

     long countByCategoryId(String categoryId);

}
