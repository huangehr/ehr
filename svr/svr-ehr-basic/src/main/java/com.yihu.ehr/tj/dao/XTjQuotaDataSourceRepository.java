package com.yihu.ehr.tj.dao;

import com.yihu.ehr.entity.tj.TjQuotaDataSource;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/6/9.
 */
public interface XTjQuotaDataSourceRepository extends PagingAndSortingRepository<TjQuotaDataSource, Long> {
    @Query("select dataSource from TjQuotaDataSource dataSource where dataSource.quotaCode = :quotaCode order by dataSource.id desc ")
    List<TjQuotaDataSource> getByQuotaCode(@Param("quotaCode") String quotaCode);

    @Modifying
    @Query("delete from TjQuotaDataSource QuotaDataSource where QuotaDataSource.quotaCode = :quotaCode")
    int deleteByQuotaCode(@Param("quotaCode") String quotaCode);
}
