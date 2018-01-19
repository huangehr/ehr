package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsResourceCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by lyr on 2016/5/4.
 */
public interface RsResourceCategoryDao extends PagingAndSortingRepository<RsResourceCategory, String> {
      long countByPid(String pid);
      List<RsResourceCategory> findByPid(String pid);
      List<RsResourceCategory> findByCode(String code);
      @Query("SELECT rsResourceCategory FROM RsResourceCategory rsResourceCategory WHERE rsResourceCategory.code = :code AND rsResourceCategory.pid = :pid" )
      List<RsResourceCategory> findByCodeAndPid(@Param("code") String code, @Param("pid") String pid);
}
