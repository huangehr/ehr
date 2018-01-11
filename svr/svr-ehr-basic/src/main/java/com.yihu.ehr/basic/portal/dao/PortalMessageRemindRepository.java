package com.yihu.ehr.basic.portal.dao;

import com.yihu.ehr.basic.portal.model.ProtalMessageRemind;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/21.
 */
public interface PortalMessageRemindRepository extends PagingAndSortingRepository<ProtalMessageRemind,Long> {

    @Query(value = "select p.* from portal_message_remind p order by p.create_date desc limit 10 ",nativeQuery = true)
    List<ProtalMessageRemind> getMessageRemindTop10();

}
