package com.yihu.ehr.basic.report.dao;

import com.yihu.ehr.entity.report.QcDailyReportDataset;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author janseny
 * @version 1.0
 * @created 2017/5/08
 */
public interface XQcDailyReportDatasetRepository  extends PagingAndSortingRepository<QcDailyReportDataset, String> {

}
