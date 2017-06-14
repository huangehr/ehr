package com.yihu.ehr.tj.dao;

import com.yihu.ehr.entity.tj.TjQuotaDataSave;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/6/9.
 */
public interface XTjQuotaDataSaveRepository extends PagingAndSortingRepository<TjQuotaDataSave, Long> {

    @Query("select dataSave from TjQuotaDataSave dataSave where dataSave.quotaCode = :quotaCode")
    List<TjQuotaDataSave> getByQuotaCode(@Param("quotaCode") String quotaCode);
}
