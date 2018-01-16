package com.yihu.ehr.portal.dao;

import com.yihu.ehr.portal.model.PortalNotices;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * 2017-02-04 add by ysj
 */
public interface XPortalNoticesRepository extends PagingAndSortingRepository<PortalNotices, Long> {

    @Query(value = "select p.* from portal_notices p order by p.release_date desc limit 10 ",nativeQuery = true)
    List<PortalNotices> getPortalNoticeTop10();

}
