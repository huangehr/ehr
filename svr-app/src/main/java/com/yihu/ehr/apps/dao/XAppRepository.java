package com.yihu.ehr.apps.dao;

import com.yihu.ehr.apps.service.App;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.List;

/**
 * App 操作接口。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XAppRepository extends PagingAndSortingRepository<App, String> {
    @Query("select app from Apps app where app.id = :id or app.name like '%:name%' or app.catlog")
    List<App> searchApps();
}
