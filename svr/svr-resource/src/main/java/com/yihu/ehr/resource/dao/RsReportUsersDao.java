package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsReportUsers;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by wxw on 2018/7/31.
 */
public interface RsReportUsersDao extends PagingAndSortingRepository<RsReportUsers, Integer> {

    @Query("from RsReportUsers ru where ru.userId = :userId order by ru.sortNo desc")
    List<RsReportUsers> findByUserId(@Param("userId") String userId);

    @Modifying
    @Query("delete from RsReportUsers ru where ru.userId = :userId")
    int deleteByUserId(@Param("userId") String userId);
}
