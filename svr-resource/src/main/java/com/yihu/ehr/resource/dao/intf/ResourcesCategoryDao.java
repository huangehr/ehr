package com.yihu.ehr.resource.dao.intf;

import com.yihu.ehr.resource.model.RsCategory;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by lyr on 2016/5/4.
 */
public interface ResourcesCategoryDao extends PagingAndSortingRepository<RsCategory,String> {
      long countByPid(String pid);

      List<RsCategory> findByPid(String pid);
}
