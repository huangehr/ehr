package com.yihu.ehr.basic.apps.dao;

import com.yihu.ehr.entity.api.AppApiErrorCode;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Dao - Api错误码
 * Created by progr1mmer on 2018/3/15.
 */
public interface AppApiErrorCodeDao extends PagingAndSortingRepository<AppApiErrorCode, Integer> {

    void deleteByApiId(Integer apiId);

}
