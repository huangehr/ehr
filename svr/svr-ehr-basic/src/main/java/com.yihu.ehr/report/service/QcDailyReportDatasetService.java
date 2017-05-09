package com.yihu.ehr.report.service;

import com.yihu.ehr.entity.report.QcDailyReportDataset;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.report.dao.XQcDailReportDatasetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author janseny
 * @version 1.0
 * @created 2017/5/08
 */
@Service
@Transactional
public class QcDailyReportDatasetService extends BaseJpaService<QcDailyReportDataset, XQcDailReportDatasetRepository> {


}
