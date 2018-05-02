package com.yihu.ehr.basic.portal.dao;

import com.yihu.ehr.basic.portal.model.ProtalMessageRemind;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/21.
 */
public interface PortalMessageRemindRepository extends PagingAndSortingRepository<ProtalMessageRemind,Long> {

    @Query(value = "select p.* from portal_message_remind p order by p.create_date desc limit 10 ",nativeQuery = true)
    List<ProtalMessageRemind> getMessageRemindTop10();

    @Query(value = "SELECT * FROM portal_message_remind WHERE order_id = :orderId AND create_date >= :recentDate ORDER BY create_date DESC limit 1 ", nativeQuery = true)
    List<ProtalMessageRemind> getRecentOneByOrderId(@Param("orderId") String orderId, @Param("recentDate") Date recentDate);

}
