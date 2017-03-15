package com.yihu.ehr.portal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.portal.dao.XPortalResourcesRepository;
import com.yihu.ehr.portal.model.PortalResources;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * 资源接口实现类.
 * 2017-03-11 add by zhoujie
 */
@Service
@Transactional
public class PortalResourcesService extends BaseJpaService<PortalResources, XPortalResourcesRepository> {

    @Autowired
    XPortalResourcesRepository portalResourcesRepository;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * 根据ID获取资源接口.
     * @param portalResourcesId
     */
    public PortalResources getPortalResources(Long portalResourcesId) {
        PortalResources portalResources = portalResourcesRepository.findOne(portalResourcesId);
        return portalResources;
    }

    /**
     * 删除资源
     * @param portalResourcesId
     */
    public void deletePortalResources(Long portalResourcesId) {
        portalResourcesRepository.delete(portalResourcesId);
    }

    public List<PortalResources> getPortalResourcesTop10(){
        return portalResourcesRepository.getPortalResourcesTop10();
    }
}
