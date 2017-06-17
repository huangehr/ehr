package com.yihu.quota.dao.jpa.dict;

import com.yihu.quota.model.jpa.dict.SystemDictList;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by chenweida on 2017/6/1.
 */
public interface SystemDictListDao extends PagingAndSortingRepository<SystemDictList, Long>, JpaSpecificationExecutor<SystemDictList> {
}
