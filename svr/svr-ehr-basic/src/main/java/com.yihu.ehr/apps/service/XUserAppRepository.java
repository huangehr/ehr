package com.yihu.ehr.apps.service;

import com.yihu.ehr.apps.model.AppApi;
import com.yihu.ehr.apps.model.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author yeshijie
 * @version 1.0
 * @created 2017年2月16日18:04:13
 */
public interface XUserAppRepository extends JpaRepository<UserApp, String> {

    @Query("select userApp from UserApp userApp where userApp.appId = :appId and userApp.userId = :userId" )
    UserApp findByAppIdAndUserId(@Param("appId") String appId, @Param("userId") String userId);

    @Modifying
//    @Query("update UserApp userApp set userApp.status = 1 where userApp.userId = :userId")
    void deleteByUserId(@Param("userId") String userId);
}
