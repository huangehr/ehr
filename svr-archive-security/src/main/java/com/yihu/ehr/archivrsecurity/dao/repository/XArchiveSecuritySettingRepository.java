package com.yihu.ehr.archivrsecurity.dao.repository;

import com.yihu.ehr.archivrsecurity.dao.model.ArchiveSecuritySetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author linaz
 * @created 2016.07.11 14:13
 */
public interface XArchiveSecuritySettingRepository extends JpaRepository<ArchiveSecuritySetting, Long> {

    @Modifying
    void deleteByUserId(String userId);

    @Modifying
    @Query("update ArchiveSecuritySetting ass set ass.securityKey = :securityKey where ass.userId = :userId")
    void ArchiveSecuritySettingAuthentication(@Param ("userId") String userId, @Param ("securityKey") String securityKey);

    ArchiveSecuritySetting findByUserId(String userId);
}

