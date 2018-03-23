package com.yihu.ehr.basic.portal.service;

import com.yihu.ehr.basic.portal.dao.PortalMessageTemplateRepository;
import com.yihu.ehr.basic.portal.model.PortalMessageTemplate;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author <a href="mailto:yhy23456@163.com">huiyang.yu</a>
 * @since 2018.03.21
 */
@Service
@Transactional
public class PortalMessageTemplateService extends BaseJpaService<PortalMessageTemplate, PortalMessageTemplateRepository> {

    private PortalMessageTemplateRepository portalMessageTemplateRepository;


    @Autowired
    public PortalMessageTemplateService(PortalMessageTemplateRepository portalMessageTemplateRepository) {
        this.portalMessageTemplateRepository = portalMessageTemplateRepository;
    }

    public PortalMessageTemplate getMessageTemplate(Long messageTemplateId) {
        return portalMessageTemplateRepository.findOne(messageTemplateId);
    }

    public void deletePortalMessageTemplate(Long messageTemplateId) {
        portalMessageTemplateRepository.delete(messageTemplateId);
    }
}
