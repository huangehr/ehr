package com.yihu.quota.dao.jpa;

import com.yihu.quota.model.jpa.TjQuotaGovProvision;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by wxw on 2018/3/6.
 */
public interface TjQuotaGovProvisionDao extends PagingAndSortingRepository<TjQuotaGovProvision, Long>{

    @Query("select sum(gp.population) from TjQuotaGovProvision gp where gp.administrativeDivision = :town and gp.year = :year ")
    Long getSumByDistrict(@Param("town") long town, @Param("year") String year);

    @Query("select sum(gp.population) from TjQuotaGovProvision gp where  gp.year = :year ")
    Long getSumByDistrict(@Param("year") String year);
}
