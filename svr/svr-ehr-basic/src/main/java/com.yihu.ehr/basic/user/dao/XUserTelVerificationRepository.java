package com.yihu.ehr.basic.user.dao;

import com.yihu.ehr.entity.user.UserTelVerification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Cws
 * @version 1.0
 * @created 2018.02.02 14:24
 */
public interface XUserTelVerificationRepository extends PagingAndSortingRepository<UserTelVerification, String> {

    @Query("select a from UserTelVerification a  where a.telNo = :telNo and a.appId = :appId order by a.effectivePeriod desc")
    List<UserTelVerification> findByTelNoAndAppId(@Param("telNo") String telNo, @Param("appId") String appId);

    @Query("select a from UserTelVerification a  where a.telNo = :telNo and a.appId = :appId and a.verificationCode = :verificationCode order by a.effectivePeriod desc")
    List<UserTelVerification> ListUserTelVerificationByTelNoAndAppId(@Param("telNo") String telNo, @Param("appId") String appId, @Param("verificationCode") String verificationCode);
}
