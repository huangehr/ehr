package com.yihu.ehr.basic.user.dao;

import com.yihu.ehr.basic.user.entity.UserType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface XUserTypeRepository extends PagingAndSortingRepository<UserType, String> {

    UserType findById(int id);

    @Query("select userType from UserType userType where userType.code = :code ")
    List<UserType> findByCode(@Param("code") String code);

    @Query("select userType from UserType userType  where userType.name = :name")
    List<UserType> findByName(@Param("name") String name);

    @Modifying
    @Query("update UserType userType set userType.activeFlag = :activeFlag where userType.id = :id")
    void changeFlag(@Param("id") int id, @Param("activeFlag") String activeFlag);
}
