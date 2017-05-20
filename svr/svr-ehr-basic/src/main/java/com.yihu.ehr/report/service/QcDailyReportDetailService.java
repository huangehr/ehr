package com.yihu.ehr.report.service;

import com.yihu.ehr.entity.report.QcDailyReportDetail;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.report.dao.XQcDailyReportDetailRepository;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author janseny
 * @version 1.0
 * @created 2017/5/08
 */
@Service
@Transactional
public class QcDailyReportDetailService extends BaseJpaService<QcDailyReportDetail, XQcDailyReportDetailRepository> {

    //查询数据
    public List<Object> getQcDailyReportDetailData(String reportId,Date storageTime,String storageFlag) {
        Session session = currentSession();
        String hql = "select qc.id  from QcDailyReportDetail qc " +
                "where  qc.reportId=:reportId and TO_DAYS(:storageTime) - TO_DAYS(qc.storageTime) = 0  " ;
                if(storageFlag != null){
                    hql = hql + " and qc.storageFlag=:storageFlag ";
                }
        Query query = session.createQuery(hql);
        query.setString("reportId", reportId);
        query.setDate("storageTime", storageTime);
        if(storageFlag != null){
            query.setString("storageFlag", storageFlag);
        }
        List<Object> list = query.list();
        if(list.size()== 0) {
            return null;
        }else {
            return list;
        }

    }

}
