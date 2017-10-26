package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsResourceCategory;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by lyr on 2016/5/4.
 */
public interface RsResourceCategoryDao extends PagingAndSortingRepository<RsResourceCategory, String> {
      long countByPid(String pid);
      List<RsResourceCategory> findByPid(String pid);
      List<RsResourceCategory> findByCode(String code);
}
