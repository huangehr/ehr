package com.yihu.ehr.basic.user.dao;

import com.yihu.ehr.entity.user.UserTelVerification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author Cws
 * @version 1.0
 * @created 2018.02.02 14:24
 */
public interface XUserTelVerificationRepository extends PagingAndSortingRepository<UserTelVerification, String> {

    @Query("select a from UserTelVerification a  where a.telNo = :telNo and a.appId = :appId")
    UserTelVerification findByTelNoAndAppId(@Param("telNo") String telNo, @Param("appId") String appId);
}
