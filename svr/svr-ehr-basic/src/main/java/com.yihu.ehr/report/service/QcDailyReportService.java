package com.yihu.ehr.report.service;

import com.yihu.ehr.entity.report.QcDailyReport;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.report.dao.XQcDailyReportRepository;
import org.apache.commons.lang.StringUtils;
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
        String hql = "select qc.orgCode,qc.createDate,qc.totalOutpatientNum,qc.realOutpatientNum,qc.totalHospitalNum,qc.realHospitalNum,qc.id " +
                "from QcDailyReport qc where  qc.orgCode=:orgCode and  TO_DAYS(:quotaDate) - TO_DAYS(qc.createDate) = 0 ";
        Query query = session.createQuery(hql);
        query.setString("orgCode", orgCode);
        query.setDate("quotaDate", quotaDate);
        List<Object> list = query.list();
        Object reObject = null;
        if(list.size()== 0) {
            return null;
        }else {
            for(Object object :list){
                reObject = object;
            }
            return reObject;
        }

    }


    //查询昨天 和 去年当天的数据
    public List<Object> getYesterdayAndLastYearTodayData(String orgCode, Date quotaDate) {
        Session session = currentSession();
        String hql = "select qc.createDate,qc.realHospitalNum,qc.realOutpatientNum,qc.id " +
                "from QcDailyReport qc where  qc.orgCode=:orgCode and ( TO_DAYS(:quotaDate) - TO_DAYS(qc.createDate) = 1 " +
                "or TO_DAYS( :quotaDate) - TO_DAYS( qc.createDate) = 365 or TO_DAYS( :quotaDate) - TO_DAYS( qc.createDate) = 366 )";
        Query query = session.createQuery(hql);
        query.setString("orgCode", orgCode);
        query.setDate("quotaDate", quotaDate);
        List<Object> list = query.list();
        if(list.size()== 0) {
            return null;
        }else {
            return list;
        }

    }


    public int getOrgDailyReportDetailCount(String reportId,String acqFlag,String archiveType) {
        Session session = currentSession();
        String hql = "select count(1) from QcDailyReportDetail qcd where  qcd.reportId=:reportId";
        if(StringUtils.isNotEmpty(acqFlag)){
            hql = hql + " and qcd.acqFlag=:acqFlag";
        }
        if(StringUtils.isNotEmpty(archiveType)){
            hql = hql + " and qcd.archiveType =:archiveType";
        }
        Query query = session.createQuery(hql);
        query.setString("reportId", reportId);
        if(StringUtils.isNotEmpty(acqFlag)){
            query.setString("acqFlag", acqFlag);
        }
        if(StringUtils.isNotEmpty(archiveType)){
            query.setString("archiveType", archiveType);
        }
        return ((Long)query.list().get(0)).intValue();
    }


}
