package com.yihu.ehr.service;

import com.yihu.ehr.dao.model.ArchiveSecuritySetting;
import com.yihu.ehr.dao.repository.XArchiveSecuritySettingRepository;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author linaz
 * @created 2016.07.11 14:13
 */
@Service
@Transactional
public class ArchiveSecuritySettingService extends BaseJpaService<ArchiveSecuritySetting, XArchiveSecuritySettingRepository> {

    public void deleteByUserId(String userId) {
    }

    public boolean ArchiveSecuritySettingAuthentication(long userId, long securityKey) {
        return true;
    }

    public ArchiveSecuritySetting findByUserId(long userId) {
        return null;
    }
}
