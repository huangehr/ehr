package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.RsMonitorTypeReportDao;
import com.yihu.ehr.resource.model.RsMonitorTypeReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 监测分类报表 Service
 *
 * @author jansney
 * @created 2017年11月7日15:23:14
 */
@Service
@Transactional
public class RsMonitorTypeReportService extends BaseJpaService<RsMonitorTypeReport, RsMonitorTypeReportDao> {

    @Autowired
    private RsMonitorTypeReportDao rsReportMonitorTypeDao;

    /**
     * 删除
     * @param rsMonitorTypeReport
     */
    public void deleteRsMonitorTypeReport(RsMonitorTypeReport rsMonitorTypeReport) {
        rsReportMonitorTypeDao.delete(rsMonitorTypeReport);
    }

    public RsMonitorTypeReport findRelation(String reportId,String monitorTypeId){
        return rsReportMonitorTypeDao.findRelation(Integer.valueOf(reportId),Integer.valueOf(monitorTypeId));
    }

}
