package com.yihu.ehr.portal.dao;

import com.yihu.ehr.portal.model.PortalSetting;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * 2017-02-21 add by ysj
 */
public interface XPortalSettingRepository extends PagingAndSortingRepository<PortalSetting, Long> {

    @Query(value = "select p.* from portal_setting p  limit 10 ",nativeQuery = true)
    List<PortalSetting> getPortalSettingTop10();
}
