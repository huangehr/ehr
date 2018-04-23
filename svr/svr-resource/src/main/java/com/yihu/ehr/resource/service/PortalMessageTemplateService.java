package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.PortalMessageTemplateDao;
import com.yihu.ehr.resource.model.PortalMessageTemplate;
import org.apache.solr.common.StringUtils;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.*;

/**
 * @author zdm
 * @since 2018.04.21
 */
@Service
@Transactional
@EnableFeignClients
public class PortalMessageTemplateService extends BaseJpaService<PortalMessageTemplate, PortalMessageTemplateDao> {

    @Autowired
    private PortalMessageTemplateDao portalMessageTemplateRepository;
    /**
     * 应用ID
     */
    @Value("${h5.clientId}")
    public String clientId;

    public List<PortalMessageTemplate> getMessageTemplate(String classification,String type,String state) {
        return portalMessageTemplateRepository.searchPortalMessageTemplate(classification,type,state);
    }

    /**
     * 根据身份证号码获取用户的id
     * @param id_card_no
     * @return
     */
    public String getUserIdByIdCardNo(String id_card_no) {
           Session session = currentSession();
        String sql = "SELECT id FROM users WHERE id_card_no = :id_card_no";
        Query query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setString("id_card_no", id_card_no);
        return (String)query.uniqueResult();

    }

}
