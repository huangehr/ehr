package com.yihu.ehr.basic.portal.service;

import com.yihu.ehr.basic.portal.dao.PortalFeedbackRepository;
import com.yihu.ehr.basic.portal.model.PortalFeedback;
import com.yihu.ehr.basic.portal.model.SurveyTemplate;
import com.yihu.ehr.query.BaseJpaService;
import org.apache.commons.collections.map.HashedMap;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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

    public Map<String,Object> findByUserId(String userId,int page,int pageSize){
        Map<String,Object> map = new HashedMap();
        if (page <= 0) {
            page = 1;
        }
        if (pageSize <= 0) {
            pageSize = 15;
        }
        Query queryCount = currentSession().createSQLQuery("select count(1) from portal_feedback f where 1=1 f.user_id = " +userId);
        Query queryList = currentSession().createSQLQuery("SELECT * FROM portal_feedback f where 1=1 and f.user_id = " +userId).addEntity(PortalFeedback.class);
        int count = Integer.parseInt(queryCount.list().get(0).toString());
        map.put("count",count);
        queryList.setFirstResult(pageSize * (page - 1));
        queryList.setMaxResults(pageSize);
        List<PortalFeedback> list = queryList.list();
        map.put("data",list);
        return map;

    }
}