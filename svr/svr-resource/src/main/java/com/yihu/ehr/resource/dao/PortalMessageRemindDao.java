package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.ProtalMessageRemind;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author ZDM
 * @vsrsion 1.0
 * Created at 2018/4/20.
 */
public interface PortalMessageRemindDao extends PagingAndSortingRepository<ProtalMessageRemind,Long> {

    @Query(value = "select p.* from portal_message_remind p order by p.create_date desc limit 10 ",nativeQuery = true)
    List<ProtalMessageRemind> getMessageRemindTop10();

}
