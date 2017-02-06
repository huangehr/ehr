package com.yihu.ehr.user.dao;

import com.yihu.ehr.user.entity.Doctors;
import com.yihu.ehr.user.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 2017-02-04 add by hzp
 */
public interface XDoctorRepository extends PagingAndSortingRepository<Doctors, Long> {

     Doctors findByUserId(String userId) throws Exception;
}
