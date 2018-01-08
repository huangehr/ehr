package com.yihu.ehr.basic.user.dao;

import com.yihu.ehr.basic.user.entity.Doctors;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * 2017-02-04 add by hzp
 */
public interface XDoctorRepository extends PagingAndSortingRepository<Doctors, Long> {

     Doctors findByUserId(String userId) throws Exception;

     Doctors findByCode(String code);
     Doctors findByIdCardNo(String idCardNo);

     @Query("select count(d.id) from Doctors d where d.roleType =:roleType")
     int getStatisticsCityDoctorByRoleType(@Param("roleType") String roleType);

}
