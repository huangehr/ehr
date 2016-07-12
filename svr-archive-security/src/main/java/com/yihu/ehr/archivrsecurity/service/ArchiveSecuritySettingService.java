package com.yihu.ehr.archivrsecurity.service;

import com.yihu.ehr.archivrsecurity.dao.model.ArchiveSecuritySetting;
import com.yihu.ehr.archivrsecurity.dao.repository.XArchiveSecuritySettingRepository;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author linaz
 * @created 2016.07.11 14:13
 */
@Service
@Transactional
public class ArchiveSecuritySettingService extends BaseJpaService<ArchiveSecuritySetting, XArchiveSecuritySettingRepository> {

    @Autowired
    XArchiveSecuritySettingRepository archiveSecuritySettingRepository;
    public void deleteByUserId(String userId) {
        archiveSecuritySettingRepository.deleteByUserId(userId);
    }

    public boolean ArchiveSecuritySettingAuthentication(String userId, String securityKey) {
        archiveSecuritySettingRepository.ArchiveSecuritySettingAuthentication(userId,securityKey);
        return true;
    }

    public ArchiveSecuritySetting findByUserId(String userId) {
        return archiveSecuritySettingRepository.findByUserId(userId);
    }
}
