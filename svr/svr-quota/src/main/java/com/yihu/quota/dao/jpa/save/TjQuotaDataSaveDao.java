package com.yihu.quota.dao.jpa.save;

import com.yihu.quota.model.jpa.save.TjQuotaDataSave;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by chenweida on 2017/6/1.
 */
public interface TjQuotaDataSaveDao extends PagingAndSortingRepository<TjQuotaDataSave, Long>, JpaSpecificationExecutor<TjQuotaDataSave> {

    TjQuotaDataSave findByQuotaCode(String quotaCode);

}
