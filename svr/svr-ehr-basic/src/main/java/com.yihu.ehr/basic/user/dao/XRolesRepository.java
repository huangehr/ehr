package com.yihu.ehr.basic.user.dao;

import com.yihu.ehr.basic.user.entity.Roles;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by yww on 2016/7/7.
 */
public interface XRolesRepository extends PagingAndSortingRepository<Roles,Long> {

    Roles findById(long id);

    List<Roles> findByAppId(String appId);

    @Query("select distinct r.appId from Roles r where r.id in (:roleIds)")
    List<String> findAppIdById(@Param("roleIds") List<Long> roleIds);
}
