package com.yihu.ehr.report.service;

import com.yihu.ehr.entity.report.QcDailyReport;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.report.dao.XQcDailyReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author janseny
 * @version 1.0
 * @created 2017/5/08
 */
@Service
@Transactional
public class QcDailyReportService extends BaseJpaService<QcDailyReport, XQcDailyReportRepository> {


}
