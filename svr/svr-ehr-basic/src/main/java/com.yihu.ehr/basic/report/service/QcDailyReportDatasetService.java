package com.yihu.ehr.basic.report.service;

import com.yihu.ehr.basic.report.dao.XQcDailyReportDatasetRepository;
import com.yihu.ehr.entity.report.QcDailyReportDataset;
import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author janseny
 * @version 1.0
 * @created 2017/5/08
 */
@Service
@Transactional
public class QcDailyReportDatasetService extends BaseJpaService<QcDailyReportDataset, XQcDailyReportDatasetRepository> {

    public int getListCount(String reportId) {
        Session session = currentSession();
        String hql = "select qcd from QcDailyReportDataset qcd where qcd.reportId =:reportId ";
        Query query = session.createQuery(hql);
        query.setString("reportId", reportId);
        List list = query.list();
        if(list != null){
            return list.size();
        }else{
            return 0;
        }

    }
}
