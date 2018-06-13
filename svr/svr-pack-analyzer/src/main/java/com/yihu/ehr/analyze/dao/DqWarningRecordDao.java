package com.yihu.ehr.analyze.dao;

import com.yihu.ehr.entity.quality.DqWarningRecord;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author yeshijie on 2018/6/12.
 */
public interface DqWarningRecordDao extends PagingAndSortingRepository<DqWarningRecord, String> {

}
