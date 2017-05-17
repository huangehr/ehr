package com.yihu.ehr.report.service;

import com.yihu.ehr.entity.report.QcDailyReportDatasets;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.report.dao.XQcDailyReportDatasetsRepository;
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
public class QcDailyReportDatasetsService extends BaseJpaService<QcDailyReportDatasets, XQcDailyReportDatasetsRepository> {


    public Object getOrgDatasetsData(String orgCode, Date quotaDate) {
        Session session = currentSession();
        String hql = "select qc.orgCode,qc.createDate,qc.totalNum,qc.realNum,qc.eventTime " +
                "from QcDailyReportDatasets qc where  qc.orgCode=:orgCode and  TO_DAYS(:quotaDate) - TO_DAYS(qc.createDate) = 0 ";
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
    public List<Object> getDatasetsYesdayLastYearData(String orgCode,Date quotaDate, Date lasterYearQuotaDate) {
        Session session = currentSession();
        String hql = "select qc.createDate,qc.totalNum,qc.realNum " +
                "from QcDailyReportDatasets qc where  qc.orgCode=:orgCode and ( TO_DAYS(:quotaDate) - TO_DAYS(qc.createDate) = 1 " +
                "or TO_DAYS( :lasterYearQuotaDate) - TO_DAYS( qc.createDate) = 0 )";
        Query query = session.createQuery(hql);
        query.setString("orgCode", orgCode);
        query.setDate("quotaDate", quotaDate);
        query.setDate("lasterYearQuotaDate", lasterYearQuotaDate);
        List<Object> list = query.list();
        if(list.size()== 0) {
            return null;
        }else {
            return list;
        }
    }
}
