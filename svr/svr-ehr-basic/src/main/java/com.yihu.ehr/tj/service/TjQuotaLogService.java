package com.yihu.ehr.tj.service;

import com.yihu.ehr.entity.tj.TjQuotaLog;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.tj.dao.XTjQuotaLogRepository;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author janseny
 * @version 1.0
 * @created 2017/5/08
 */
@Service
@Transactional
public class TjQuotaLogService extends BaseJpaService<TjQuotaLog, XTjQuotaLogRepository> {

    public List<TjQuotaLog> searchQuotaLogByParams(Map<String, Object> args) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String quotaCode = (String) args.get("quotaCode");
        Integer page = (Integer) args.get("page");
        Integer pageSize = (Integer) args.get("pageSize");

        Date startTime = (Date) args.get("startDate");
        Date endTime = (Date) args.get("endDate");

        String hql = "from TjQuotaLog where 1=1";
        if (!StringUtils.isEmpty(quotaCode)) {
            hql += " and quotaCode = :quotaCode";
        }
        if (!StringUtils.isEmpty(startTime)) {
            hql += " and startTime >= :startTime";
        }
        if (!StringUtils.isEmpty(endTime)) {
            hql += " and endTime < :endTime";
        }
        hql += " order by startTime desc";
        Query query = session.createQuery(hql);
        if (!StringUtils.isEmpty(quotaCode)) {
            query.setString("quotaCode", quotaCode);
        }
        if (!StringUtils.isEmpty(startTime)) {
            query.setDate("startTime",startTime);
        }
        if (!StringUtils.isEmpty(endTime)) {
            query.setDate("endTime", endTime);
        }
        query.setMaxResults(pageSize);
        query.setFirstResult((page - 1) * pageSize);
        List<TjQuotaLog> tjQuotaLogs = query.list();
        return tjQuotaLogs;
    }

    public Integer searchQuotaLogByParamsTotalCount(Map<String, Object> args) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String quotaCode = (String) args.get("quotaCode");
        Integer page = (Integer) args.get("page");
        Integer pageSize = (Integer) args.get("pageSize");

        Date startTime = (Date) args.get("startDate");
        Date endTime = (Date) args.get("endDate");
        String hql = "select count(*) from TjQuotaLog where 1=1";
        if (!StringUtils.isEmpty(quotaCode)) {
            hql += " and quotaCode = :quotaCode";
        }
        if (!StringUtils.isEmpty(startTime)) {
            hql += " and startTime >= :startTime";
        }
        if (!StringUtils.isEmpty(endTime)) {
            hql += " and endTime < :endTime";
        }
        Query query = session.createQuery(hql);
        if (!StringUtils.isEmpty(quotaCode)) {
            query.setString("quotaCode", quotaCode);
        }
        if (!StringUtils.isEmpty(startTime)) {
            query.setDate("startTime",startTime);
        }
        if (!StringUtils.isEmpty(endTime)) {
            query.setDate("endTime", endTime);
        }
        return ((Long)query.list().get(0)).intValue();
    }
}
