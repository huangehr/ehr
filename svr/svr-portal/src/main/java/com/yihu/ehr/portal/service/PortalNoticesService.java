package com.yihu.ehr.portal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.portal.dao.XPortalNoticesRepository;
import com.yihu.ehr.portal.model.PortalNotices;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 通知公告接口实现类.
 * 2017-02-04 add by ysj
 */
@Service
@Transactional
public class PortalNoticesService extends BaseJpaService<PortalNotices, XPortalNoticesRepository> {

    @Autowired
    XPortalNoticesRepository portalNoticesRepository;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * 根据ID获取通知公告接口.
     * @param portalNoticeId
     */
    public PortalNotices getPortalNotice(Long portalNoticeId) {
        PortalNotices portalNotices = portalNoticesRepository.findOne(portalNoticeId);
        return portalNotices;
    }

    /**
     * 删除通知公告
     * @param portalNoticeId
     */
    public void deletePortalNotice(Long portalNoticeId) {
        portalNoticesRepository.delete(portalNoticeId);
    }
}