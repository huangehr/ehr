package com.yihu.ehr.basic.user.dao;

import com.yihu.ehr.basic.user.entity.UserTypeRoles;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface XUserTypeRolesRepository extends PagingAndSortingRepository<UserTypeRoles, String> {

    UserTypeRoles findById(int id);

    List<UserTypeRoles> findByTypeId(int typeId);

    @Modifying
    @Query("delete from UserTypeRoles r where r.typeId = :typeId")
    void deleteUserTypeRolesByTypeId(@Param("typeId") int typeId);

    @Query("select r from UserTypeRoles r where r.typeId = :typeId and r.clientId=:clientId")
    List<UserTypeRoles> ListUserTypeRolesByTypeIdAndClientId(@Param("typeId") int typeId,@Param("clientId")String clientId);

}
