package com.yihu.ehr.basic.portal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.basic.portal.dao.ItResourceDownloadRepository;
import com.yihu.ehr.basic.portal.model.ItResourceDownload;
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
public class ItResourceDownloadService extends BaseJpaService<ItResourceDownload, ItResourceDownloadRepository> {

    @Autowired
    ItResourceDownloadRepository portalResourcesRepository;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * 根据ID获取资源接口.
     * @param portalResourcesId
     */
    public ItResourceDownload getPortalResources(Long portalResourcesId) {
        ItResourceDownload portalResources = portalResourcesRepository.findOne(portalResourcesId);
        return portalResources;
    }

    /**
     * 删除资源
     * @param portalResourcesId
     */
    public void deletePortalResources(Long portalResourcesId) {
        portalResourcesRepository.delete(portalResourcesId);
    }

    public List<ItResourceDownload> getAllPortalResources(){
        return portalResourcesRepository.getAllPortalResources();
    }
}
