package com.yihu.ehr.user.dao;

import com.yihu.ehr.user.entity.Roles;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by yww on 2016/7/7.
 */
public interface XRolesRepository extends PagingAndSortingRepository<Roles,Long> {

    Roles findById(long id);

    List<Roles> findByAppId(String appId);

}
