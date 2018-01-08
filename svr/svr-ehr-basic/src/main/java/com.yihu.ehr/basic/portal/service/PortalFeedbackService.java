package com.yihu.ehr.basic.portal.service;

import com.yihu.ehr.basic.portal.dao.PortalFeedbackRepository;
import com.yihu.ehr.basic.portal.model.PortalFeedback;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 意见反馈接口实现类.
 * 2017-02-21 add by ysj
 */
@Service
@Transactional
public class PortalFeedbackService extends BaseJpaService<PortalFeedback, PortalFeedbackRepository> {

    @Autowired
    PortalFeedbackRepository portalFeedbackRepository;

    /**
     * 根据ID获取
     * @param portalFeedbackId
     */
    public PortalFeedback getPortalFeedback(Long portalFeedbackId) {
        PortalFeedback portalFeedback = portalFeedbackRepository.findOne(portalFeedbackId);
        return portalFeedback;
    }

    /**
     * 删除
     * @param portalFeedbackId
     */
    public void deletePortalFeedback(Long portalFeedbackId) {
        portalFeedbackRepository.delete(portalFeedbackId);
    }

}