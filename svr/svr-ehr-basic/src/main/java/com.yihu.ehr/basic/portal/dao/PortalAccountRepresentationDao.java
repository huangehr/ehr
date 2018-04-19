package com.yihu.ehr.basic.portal.dao;

import com.yihu.ehr.basic.portal.model.PortalAccountRepresentation;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zhangdan on 2018/4/19.
 */
public interface PortalAccountRepresentationDao extends PagingAndSortingRepository<PortalAccountRepresentation,String>, JpaSpecificationExecutor<PortalAccountRepresentation> {
;

}
