package com.yihu.ehr.basic.quota.dao;

import com.yihu.ehr.entity.quota.TjQuotaDataSave;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/6/9.
 */
public interface XTjQuotaDataSaveRepository extends PagingAndSortingRepository<TjQuotaDataSave, Long> {

    @Query("select dataSave from TjQuotaDataSave dataSave where dataSave.quotaCode = :quotaCode order by dataSave.id desc ")
    List<TjQuotaDataSave> getByQuotaCode(@Param("quotaCode") String quotaCode);

    @Modifying
    @Query("delete from TjQuotaDataSave QuotaDataSave where QuotaDataSave.quotaCode = :quotaCode")
    int deleteByQuotaCode(@Param("quotaCode") String quotaCode);
}
