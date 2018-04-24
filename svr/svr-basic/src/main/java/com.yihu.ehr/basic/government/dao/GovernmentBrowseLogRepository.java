package com.yihu.ehr.basic.government.dao;

import com.yihu.ehr.entity.government.GovernmentBrowseLog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by wxw on 2017/11/3.
 */
public interface GovernmentBrowseLogRepository extends PagingAndSortingRepository<GovernmentBrowseLog, Integer> {

    @Query(value = "select gbl.* from Government_Browse_Log gbl where gbl.user_Id = :userId order by gbl.create_Time desc limit 4", nativeQuery = true)
    List<GovernmentBrowseLog> findByUserId(@Param("userId") String userId);
}
