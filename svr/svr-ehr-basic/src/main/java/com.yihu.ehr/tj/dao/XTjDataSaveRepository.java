package com.yihu.ehr.tj.dao;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.yihu.ehr.entity.tj.TjDataSave;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by Administrator on 2017/6/9.
 */
public interface XTjDataSaveRepository extends PagingAndSortingRepository<TjDataSave, Long> {
}
