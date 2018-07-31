package com.yihu.ehr.basic.user.dao;

import com.yihu.ehr.basic.user.entity.UserTypeRoles;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface XUserTypeRolesRepository extends PagingAndSortingRepository<UserTypeRoles, String> {

    UserTypeRoles findById(int id);

    List<UserTypeRoles> findByTypeId(int typeId);

}
