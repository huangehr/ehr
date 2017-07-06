package com.yihu.quota.dao.jpa.save;

import com.yihu.quota.model.jpa.save.TjDataSave;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by chenweida on 2017/6/1.
 */
public interface TjDataSaveDao extends PagingAndSortingRepository<TjDataSave, Long>, JpaSpecificationExecutor<TjDataSave> {
}
