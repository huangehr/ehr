package com.yihu.ehr.basic.portal.service;

import com.yihu.ehr.basic.portal.dao.PortalSettingRepository;
import com.yihu.ehr.basic.portal.model.PortalSetting;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 门户配置表接口实现类.
 * 2017-02-21 add by ysj
 */
@Service
@Transactional
public class PortalSettingService extends BaseJpaService<PortalSetting, PortalSettingRepository> {

    @Autowired
    PortalSettingRepository portalSettingRepository;

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



    public List<PortalSetting> getPortalSettingTop10(){
        return portalSettingRepository.getPortalSettingTop10();
    }
}