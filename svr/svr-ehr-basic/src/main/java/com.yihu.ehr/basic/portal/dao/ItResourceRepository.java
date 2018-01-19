package com.yihu.ehr.basic.portal.dao;

import com.yihu.ehr.basic.portal.model.ItResource;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/17.
 */
public interface ItResourceRepository extends PagingAndSortingRepository<ItResource,Integer> {

    @Query("select resource from ItResource resource where resource.platformType = :platformType ")
    List<ItResource> searchByPlatformType(@Param("platformType") String platformType);




}
