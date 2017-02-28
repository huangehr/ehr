package com.yihu.ehr.portal.dao;

import com.yihu.ehr.portal.model.ItResource;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/17.
 */
public interface XItResourceRepository extends PagingAndSortingRepository<ItResource,Integer> {

    @Query("select resource from ItResource resource where resource.platformType = :platformType ")
    List<ItResource> searchByPlatformType(@Param("platformType") String platformType);




}
