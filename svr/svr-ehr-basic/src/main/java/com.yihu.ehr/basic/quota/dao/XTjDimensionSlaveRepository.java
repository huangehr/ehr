package com.yihu.ehr.basic.quota.dao;

import com.yihu.ehr.entity.quota.TjDimensionSlave;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author janseny
 * @version 1.0
 * @created 2017/5/08
 */
public interface XTjDimensionSlaveRepository extends PagingAndSortingRepository<TjDimensionSlave, Long> {

    List<TjDimensionSlave> findByCode(String code);
}
