package com.yihu.ehr.portal.service;

import com.yihu.ehr.portal.dao.XPortalSettingRepository;
import com.yihu.ehr.portal.model.PortalSetting;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 门户配置表接口实现类.
 * 2017-02-21 add by ysj
 */
@Service
@Transactional
public class PortalSettingService extends BaseJpaService<PortalSetting, XPortalSettingRepository> {

    @Autowired
    XPortalSettingRepository portalSettingRepository;

    /**
     * 根据ID获取
     * @param portalSettingId
     */
    public PortalSetting getPortalSetting(Long portalSettingId) {
        PortalSetting portalSetting = portalSettingRepository.findOne(portalSettingId);
        return portalSetting;
    }

    /**
     * 删除
     * @param portalSettingId
     */
    public void deletePortalSetting(Long portalSettingId) {
        portalSettingRepository.delete(portalSettingId);
    }
}