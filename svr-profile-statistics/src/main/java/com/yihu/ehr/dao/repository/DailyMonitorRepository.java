package com.yihu.ehr.dao.repository;

import com.yihu.ehr.dao.model.DailyMonitorFile;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by lyr on 2016/7/27.
 */
public interface DailyMonitorRepository extends PagingAndSortingRepository<DailyMonitorFile,String> {

    DailyMonitorFile findByMonitorDate(String monitorDate);
}
