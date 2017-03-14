package com.yihu.ehr.portal.dao;

import com.yihu.ehr.portal.model.ItResource;
import com.yihu.ehr.portal.model.MessageRemind;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/21.
 */
public interface XMessageRemindRepository extends PagingAndSortingRepository<MessageRemind,Integer> {

    @Query("select obj from MessageRemind obj where obj.appId = :appId ")
    List<MessageRemind> searchByAppId(@Param("appId") String appId);

    List<MessageRemind> findByToUserId(String userId);

    MessageRemind findById(String id);

}
