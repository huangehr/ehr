package com.yihu.ehr.report.service;

import com.yihu.ehr.entity.report.QcDailyReport;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.report.dao.XQcDailyReportRepository;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
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
public class QcDailyReportService extends BaseJpaService<QcDailyReport, XQcDailyReportRepository> {

    @Autowired
    XQcDailyReportRepository xQcDailyReportRepository;

    public Object getOrgData(String orgCode, Date quotaDate) {
        Session session = currentSession();
        String hql = "select qc.orgCode,qc.createDate,qc.totalOutpatientNum,qc.realOutpatientNum,qc.totalHospitalNum,qc.realHospitalNum " +
                "from QcDailyReport qc where  qc.orgCode=:orgCode and  TO_DAYS(:quotaDate) - TO_DAYS(qc.createDate) = 0 ";
        Query query = session.createQuery(hql);
        query.setString("orgCode", orgCode);
        query.setDate("quotaDate", quotaDate);
        List<Object> dailyReportList = query.list();
        Object reObject = null;
        if(dailyReportList.size()== 0) {
            return null;
        }else {
            for(Object object :dailyReportList){
                reObject = object;
            }
            return reObject;
        }

    }


    //查询昨天 和 去年当天的数据
    public List<Object> getYesterdayAndLastYearTodayData(String orgCode, Date quotaDate) {
        Session session = currentSession();
        String hql = "select qc.createDate,qc.realHospitalNum,qc.realOutpatientNum " +
                "from QcDailyReport qc where  qc.orgCode=:orgCode and ( TO_DAYS(:quotaDate) - TO_DAYS(qc.createDate) = 1 " +
                "or TO_DAYS( :quotaDate) - TO_DAYS( qc.createDate) = 365 or TO_DAYS( :quotaDate) - TO_DAYS( qc.createDate) = 366 )";
        Query query = session.createQuery(hql);
        query.setString("orgCode", orgCode);
        query.setDate("quotaDate", quotaDate);
        List<Object> dailyReportList = query.list();
        if(dailyReportList.size()== 0) {
            return null;
        }else {
            return dailyReportList;
        }

    }

}
