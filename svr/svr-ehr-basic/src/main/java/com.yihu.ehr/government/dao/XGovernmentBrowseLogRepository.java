package com.yihu.ehr.government.dao;

import com.yihu.ehr.entity.government.GovernmentBrowseLog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by wxw on 2017/11/3.
 */
public interface XGovernmentBrowseLogRepository extends PagingAndSortingRepository<GovernmentBrowseLog, Integer> {

    @Query(value = "select gbl.title from Government_Browse_Log gbl where gbl.user_Id = :userId order by gbl.create_Time desc limit 4", nativeQuery = true)
    List<String> findByUserId(@Param("userId") String userId);
}
