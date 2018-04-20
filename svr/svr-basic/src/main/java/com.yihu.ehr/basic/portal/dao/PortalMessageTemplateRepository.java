package com.yihu.ehr.basic.portal.dao;

import com.yihu.ehr.basic.portal.model.ItResource;
import com.yihu.ehr.basic.portal.model.PortalMessageTemplate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author <a href="mailto:yhy23456@163.com">huiyang.yu</a>
 * @since 2018.03.21
 */
public interface PortalMessageTemplateRepository extends PagingAndSortingRepository<PortalMessageTemplate, Long> {
    @Query("select portalMessageTemplate from PortalMessageTemplate portalMessageTemplate where portalMessageTemplate.classification = :classification and  portalMessageTemplate.type = :type and  portalMessageTemplate.state = :state")
    List<PortalMessageTemplate> searchPortalMessageTemplate(@Param("classification") String classification,@Param("type") String type,@Param("state") String state);
}
