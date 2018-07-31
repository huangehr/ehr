package com.yihu.ehr.basic.user.dao;

import com.yihu.ehr.basic.user.entity.UserType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface XUserTypeRepository extends PagingAndSortingRepository<UserType, String> {

    UserType findById(int id);

    UserType findByCode(String code);

    @Modifying
    @Query("update UserType userType set userType.activeFlag = :activeFlag where userType.id = :id")
    void changeFlag(@Param("id") int id, @Param("activeFlag") String activeFlag);
}
