package com.yihu.ehr.user.dao;

import com.yihu.ehr.user.entity.Doctors;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 2017-02-04 add by hzp
 */
public interface XDoctorRepository extends PagingAndSortingRepository<Doctors, Long> {

     Doctors findByUserId(String userId) throws Exception;

     Doctors findByCode(String code);
     Doctors findByIdCardNo(String idCardNo);

}
