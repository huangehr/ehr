package com.yihu.ehr.portal.service;

import com.yihu.ehr.portal.dao.XPortalStandardsRepository;
import com.yihu.ehr.portal.model.PortalStandards;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 标准规范管理接口实现类.
 * 2017-02-21 add by ysj
 */
@Service
@Transactional
public class PortalStandardsService extends BaseJpaService<PortalStandards, XPortalStandardsRepository> {

    @Autowired
    XPortalStandardsRepository portalStandardsRepository;

    /**
     * 根据ID获取
     * @param portalStandardsId
     */
    public PortalStandards getPortalStandards(Long portalStandardsId) {
        PortalStandards portalStandards = portalStandardsRepository.findOne(portalStandardsId);
        return portalStandards;
    }

    /**
     * 删除
     * @param portalStandardsId
     */
    public void deletePortalStandards(Long portalStandardsId) {
        portalStandardsRepository.delete(portalStandardsId);
    }
}