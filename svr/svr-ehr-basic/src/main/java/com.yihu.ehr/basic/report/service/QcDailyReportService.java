package com.yihu.ehr.basic.report.service;

import com.yihu.ehr.basic.report.dao.XQcDailyReportRepository;
import com.yihu.ehr.entity.report.QcDailyReport;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private QcDailyReportDetailService qcDailyReportDetailService;

    public List<Object> getOrgData(String orgCode, Date quotaDate) {
        Session session = currentSession();
        String hql = "select qc.orgCode,qc.createDate,qc.totalOutpatientNum,qc.realOutpatientNum,qc.totalHospitalNum,qc.realHospitalNum,qc.id " +
                "from QcDailyReport qc where  qc.orgCode=:orgCode and  TO_DAYS(:quotaDate) - TO_DAYS(qc.createDate) = 0 ";
        Query query = session.createQuery(hql);
        query.setString("orgCode", orgCode);
        query.setDate("quotaDate", quotaDate);
        List<Object> list = query.list();
        if (list.size() == 0) {
            return null;
        } else {
            return list;
        }

    }


    //查询昨天
    public List<Object> getYesterdayData(String orgCode, Date quotaDate) {
        Session session = currentSession();
        String hql = "select qc.createDate,qc.realHospitalNum,qc.realOutpatientNum,qc.id " +
                "from QcDailyReport qc where  qc.orgCode=:orgCode and  TO_DAYS(:quotaDate) - TO_DAYS(qc.createDate) = 1 ";
        Query query = session.createQuery(hql);
        query.setString("orgCode", orgCode);
        query.setDate("quotaDate", quotaDate);
        List<Object> list = query.list();
        if (list.size() == 0) {
            return null;
        } else {
            return list;
        }

    }

    //当天的数据
    public List<Object> getTodayData(String orgCode, Date quotaDate) {
        Session session = currentSession();
        String hql = "select qc.createDate,qc.realHospitalNum,qc.realOutpatientNum,qc.id " +
                "from QcDailyReport qc where  qc.orgCode=:orgCode and  TO_DAYS(:quotaDate) - TO_DAYS(qc.createDate) = 0 ";
        Query query = session.createQuery(hql);
        query.setString("orgCode", orgCode);
        query.setDate("quotaDate", quotaDate);
        List<Object> list = query.list();
        if (list.size() == 0) {
            return null;
        } else {
            return list;
        }

    }

    public int getOrgDailyReportDetailCount(String reportId, String timelyFlag, String archiveType, String storageFlag) {
        Session session = currentSession();
        String hql = "select count(1) from QcDailyReportDetail qcd where  qcd.reportId=:reportId";
        if (StringUtils.isNotEmpty(timelyFlag)) {
            hql = hql + " and qcd.timelyFlag=:timelyFlag";
        }
        if (StringUtils.isNotEmpty(archiveType)) {
            hql = hql + " and qcd.archiveType =:archiveType";
        }
        if (StringUtils.isNotEmpty(storageFlag)) {
            hql = hql + " and qcd.storageFlag =:storageFlag";
        }
        Query query = session.createQuery(hql);
        query.setString("reportId", reportId);
        if (StringUtils.isNotEmpty(archiveType)) {
            query.setString("archiveType", archiveType);
        }
        if (StringUtils.isNotEmpty(timelyFlag)) {
            query.setString("timelyFlag", timelyFlag);
        }
        if (StringUtils.isNotEmpty(storageFlag)) {
            query.setString("storageFlag", storageFlag);
        }
        return ((Long) query.list().get(0)).intValue();
    }

    public List<QcDailyReport> getData(String orgCode, Date quotaDate) {
        Session session = currentSession();
        String hql = "select qc " +
                "from QcDailyReport qc where  qc.orgCode=:orgCode and  TO_DAYS(:quotaDate) - TO_DAYS(qc.createDate) = 0 ";
        Query query = session.createQuery(hql);
        query.setString("orgCode", orgCode);
        query.setDate("quotaDate", quotaDate);
        List<QcDailyReport> list = query.list();
        if (list.size() == 0) {
            return null;
        } else {
            return list;
        }

    }
    /**
     * 更新数量
     * @param date
     */
    public void updateNum(String date){
        Session session = currentSession();
        String hql = "select qc from QcDailyReport qc where  TO_DAYS(:date) - TO_DAYS(qc.createDate) = 0 ";
        Query query = session.createQuery(hql);
        query.setDate("date", DateUtil.strToDate(date));
        List<QcDailyReport> list = query.list();
        for(QcDailyReport model : list){
            int outpatient = qcDailyReportDetailService.getQcDailyReportDetailListCount(model.getId(),"outpatient",null,null);
            int hospital = qcDailyReportDetailService.getQcDailyReportDetailListCount(model.getId(),"hospital",null,null);
            model.setRealOutpatientNum(outpatient);
            model.setTotalOutpatientNum(outpatient);
            model.setRealHospitalNum(hospital);
            model.setTotalHospitalNum(hospital);
            save(model);
        }
    }

}
