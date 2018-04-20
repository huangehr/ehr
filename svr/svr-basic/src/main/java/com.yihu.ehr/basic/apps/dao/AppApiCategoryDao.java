package com.yihu.ehr.basic.apps.dao;

import com.yihu.ehr.entity.api.AppApiCategory;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Dao - Api业务类别
 * Created by progr1mmer on 2018/3/14.
 */
public interface AppApiCategoryDao extends PagingAndSortingRepository<AppApiCategory, Integer> {
}
