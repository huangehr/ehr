package com.yihu.ehr.report.dao;

import com.yihu.ehr.entity.report.QcDailyReportDetail;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author janseny
 * @version 1.0
 * @created 2017/5/08
 */
public interface XQcDailyReportDetailRepository extends PagingAndSortingRepository<QcDailyReportDetail, Integer> {
}
