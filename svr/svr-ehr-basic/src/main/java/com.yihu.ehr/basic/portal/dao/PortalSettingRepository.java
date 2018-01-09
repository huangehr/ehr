package com.yihu.ehr.basic.portal.dao;

import com.yihu.ehr.basic.portal.model.PortalSetting;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * 2017-02-21 add by ysj
 */
public interface PortalSettingRepository extends PagingAndSortingRepository<PortalSetting, Long> {

    @Query(value = "select p.* from portal_setting p  limit 10 ",nativeQuery = true)
    List<PortalSetting> getPortalSettingTop10();
}
