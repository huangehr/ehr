package com.yihu.ehr.basic.portal.dao;

import com.yihu.ehr.basic.portal.model.PortalNotices;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * 2017-02-04 add by ysj
 */
public interface PortalNoticesRepository extends PagingAndSortingRepository<PortalNotices, Long> {

    @Query(value = "select p.* from portal_notices p order by p.release_date desc limit 10 ",nativeQuery = true)
    List<PortalNotices> getPortalNoticeTop10();

}
