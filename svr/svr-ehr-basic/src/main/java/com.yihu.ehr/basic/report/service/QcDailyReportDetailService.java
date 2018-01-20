package com.yihu.ehr.basic.report.service;

import com.yihu.ehr.basic.report.dao.XQcDailyReportDetailRepository;
import com.yihu.ehr.entity.report.QcDailyReportDetail;
import com.yihu.ehr.query.BaseJpaService;
import org.apache.commons.lang.StringUtils;
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




    public List<QcDailyReportDetail> getQcDailyReportDetailList(String reportId, String archiveType,Date startDate,Date endDate,int page,int pageSize) {
        Session session = currentSession();
        String hql = "select qcd from QcDailyReportDetail qcd where 1=1  ";

        if (StringUtils.isNotEmpty(reportId) ){
            hql = hql + " and qcd.reportId =:reportId ";
        }
        if (StringUtils.isNotEmpty(archiveType) ){
            hql = hql + " and qcd.archiveType =:archiveType ";
        }
        if (startDate != null && endDate != null) {
            hql = hql + " and ( (DATE(qcd.acqTime) >=:startDate and  DATE(qcd.acqTime) <=:endDate) or (DATE(qcd.storageTime) >=:startDate and  DATE(qcd.storageTime) <=:endDate)) ";
        }
        Query query = session.createQuery(hql);
        if (StringUtils.isNotEmpty(reportId) ){
            query.setString("reportId", reportId);
        }
        if (StringUtils.isNotEmpty(archiveType) ){
            query.setString("archiveType", archiveType);
        }
        if (startDate != null && endDate != null) {
            query.setDate("startDate", startDate);
            query.setDate("endDate", endDate);
        }
        query.setFirstResult(page);
        query.setMaxResults(pageSize);
        return query.list();
    }


    public int getQcDailyReportDetailListCount(String reportId, String archiveType,Date startDate,Date endDate) {
        Session session = currentSession();
        String hql = "select qcd from QcDailyReportDetail qcd where 1=1  ";

        if (StringUtils.isNotEmpty(reportId) ){
            hql = hql + " and qcd.reportId =:reportId ";
        }
        if (StringUtils.isNotEmpty(archiveType) ){
            hql = hql + " and qcd.archiveType =:archiveType ";
        }
        if (startDate != null && endDate != null) {
            hql = hql + " and ( (DATE(qcd.acqTime) >=:startDate and  DATE(qcd.acqTime) <=:endDate) or (DATE(qcd.storageTime) >=:startDate and  DATE(qcd.storageTime) <=:endDate)) ";
        }
        Query query = session.createQuery(hql);
        if (StringUtils.isNotEmpty(reportId) ){
            query.setString("reportId", reportId);
        }
        if (StringUtils.isNotEmpty(archiveType) ){
            query.setString("archiveType", archiveType);
        }
        if (startDate != null && endDate != null) {
            query.setDate("startDate", startDate);
            query.setDate("endDate", endDate);
        }
        List list = query.list();
        if(list != null){
            return list.size();
        }else{
            return 0;
        }

    }



}
