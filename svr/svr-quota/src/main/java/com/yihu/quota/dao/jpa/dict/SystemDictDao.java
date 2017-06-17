package com.yihu.quota.dao.jpa.dict;

import com.yihu.quota.model.jpa.dict.SystemDict;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by chenweida on 2017/6/1.
 */
public interface SystemDictDao extends PagingAndSortingRepository<SystemDict, Long>, JpaSpecificationExecutor<SystemDict> {
}
