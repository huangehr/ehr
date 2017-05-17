package com.yihu.ehr.report.service;

import com.yihu.ehr.entity.report.QcDailyReportMetadata;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.report.dao.XQcDailyReportMetaDataRepository;
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
public class QcDailyReportMetadataService extends BaseJpaService<QcDailyReportMetadata, XQcDailyReportMetaDataRepository> {

    public List<Object> getOrgMeataData(String orgCode, Date quotaDate) {
        Session session = currentSession();
        String hql = "select qc.createDate,qc.eventTime,qc.totalQty,qc.errorQty,qc.errCode" +
                "from QcDailyReportMetadata qc where  qc.orgCode=:orgCode and  TO_DAYS(:quotaDate) - TO_DAYS(qc.createDate) = 0 ";
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


    //查询昨天 和 去年当天的数据
    public List<Object> getDataMeataYesdayLastYearData(String orgCode, Date quotaDate) {
        Session session = currentSession();
        String hql = "select qc.createDate,qc.totalQty,qc.errorQty" +
                "from QcDailyReportMetadata qc where  qc.orgCode=:orgCode and ( TO_DAYS(:quotaDate) - TO_DAYS(qc.createDate) = 1 " +
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

}
